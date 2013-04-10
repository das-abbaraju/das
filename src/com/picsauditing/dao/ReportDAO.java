package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.actions.report.ManageReports;
import com.picsauditing.dao.mapper.ReportInfoMapper;
import com.picsauditing.dao.mapper.UserMapper;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.service.ReportInfo;
import com.picsauditing.service.ReportSearch;
import com.picsauditing.service.ReportSearchResults;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class ReportDAO extends PicsDAO {

	@Autowired
	private FeatureToggle featureToggle;

	private static final Logger logger = LoggerFactory.getLogger(ReportDAO.class);

	public ReportSearchResults runQuery(String sql) throws SQLException {
		Database database = new Database();
		List<BasicDynaBean> rows = queryDatabase(database, sql);
		return new ReportSearchResults(rows, database.getAllRows());
	}

	private List<BasicDynaBean> queryDatabase(Database database, String sql) throws SQLException {
		if (featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_READ_ONLY_DATASOURCE)) {
			return database.selectReadOnly(sql, true);
		}

		return database.select(sql, true);
	}

	static String getOrderBySort(String sortType) {
		String orderBy = Strings.EMPTY_STRING;

		if (ManageReports.ALPHA_SORT.equals(sortType)) {
			orderBy = "r.name";
		} else if (ManageReports.DATE_ADDED_SORT.equals(sortType)) {
			orderBy = "r.creationDate";
		} else if (ManageReports.LAST_VIEWED_SORT.equals(sortType)) {
			orderBy = "ru.lastViewedDate";
		} else {
			throw new IllegalArgumentException("Unexpected sort type '" + sortType + "'");
		}

		return orderBy;
	}

	// TODO remove this after next release
	@Transactional(propagation = Propagation.NEVER)
	public void truncateReportChildren() {
		System.out.println("WARNING: TRUNCATING REPORT TABLES");
		em.createNativeQuery("TRUNCATE TABLE report_column").executeUpdate();
		em.createNativeQuery("TRUNCATE TABLE report_filter").executeUpdate();
		em.createNativeQuery("TRUNCATE TABLE report_sort").executeUpdate();
	}

	public Report findById(int reportId) {
		return find(Report.class, reportId);
	}

	public void detach(Report newReport) {
		em.detach(newReport);
	}

	public List<ReportInfo> findByOwnerID(ReportSearch reportSearch) {
		SelectSQL sql = buildQueryForFindByOwnerId(reportSearch);

		try {
			return Database.select(sql.toString(), new ReportInfoMapper());
		} catch (SQLException e) {
			logger.error("Error while finding owned by reports for ownerID = "
					+ reportSearch.getPermissions().getUserId());
		}

		return Collections.EMPTY_LIST;
	}

	private SelectSQL buildQueryForFindByOwnerId(ReportSearch reportSearch) {
		SelectSQL sql = new SelectSQL("report r");

		sql.addField("r.id AS " + ReportInfoMapper.ID_FIELD);
		sql.addField("r.name AS " + ReportInfoMapper.NAME_FIELD);
		sql.addField("r.description AS " + ReportInfoMapper.DESCRIPTION_FIELD);
		sql.addField("r.creationDate AS " + ReportInfoMapper.CREATION_DATE_FIELD);
		sql.addField("ru.favorite AS " + ReportInfoMapper.FAVORITE_FIELD);
		sql.addField("1 AS " + ReportInfoMapper.EDITABLE_FIELD); // because if you own it, you can edit it
		sql.addField("ru.lastViewedDate AS " + ReportInfoMapper.LAST_VIEWED_DATE_FIELD);
		sql.addField("0 AS " + ReportInfoMapper.NUMBER_OF_TIMES_FAVORITED);
		sql.addField("u.id AS '" + UserMapper.USER_ID_FIELD + "'");
		sql.addField("u.name AS '" + UserMapper.USER_NAME_FIELD + "'");

		sql.addJoin("JOIN report_user ru ON ru.reportID = r.id AND ru.userID = " + reportSearch.getPermissions().getUserId());
		sql.addJoin("JOIN users u ON u.id = ru.userID");

		sql.addWhere("r.ownerID = " + reportSearch.getPermissions().getUserId());

		addOrderBy(sql, reportSearch);

		return sql;
	}

	public List<ReportInfo> findReportForSharedWith(ReportSearch reportSearch) {
		Permissions permissions = reportSearch.getPermissions();

		SelectSQL sql = buildQueryForFindSharedWith(reportSearch, permissions);

		try {
			return Database.select(sql.toString(), new ReportInfoMapper());
		} catch (SQLException e) {
			logger.error("Error while finding shared with reports for userID = " + permissions.getUserId());
		}
		return Collections.EMPTY_LIST;
	}

	private SelectSQL buildQueryForFindSharedWith(ReportSearch reportSearch, Permissions permissions) {
		SelectSQL sql = new SelectSQL("report r");

		sql.addField("r.id AS " + ReportInfoMapper.ID_FIELD);
		sql.addField("r.name AS " + ReportInfoMapper.NAME_FIELD);
		sql.addField("r.description AS " + ReportInfoMapper.DESCRIPTION_FIELD);
		sql.addField("r.creationDate AS " + ReportInfoMapper.CREATION_DATE_FIELD);
		sql.addField("ru.favorite AS " + ReportInfoMapper.FAVORITE_FIELD);
		sql.addField("MAX(rp.editable) AS " + ReportInfoMapper.EDITABLE_FIELD);
		sql.addField("ru.lastViewedDate AS " + ReportInfoMapper.LAST_VIEWED_DATE_FIELD);
		sql.addField("0 AS " + ReportInfoMapper.NUMBER_OF_TIMES_FAVORITED);
		sql.addField("u.id AS '" + UserMapper.USER_ID_FIELD + "'");
		sql.addField("u.name AS '" + UserMapper.USER_NAME_FIELD + "'");

		sql.addJoin("LEFT JOIN report_user ru ON r.id = ru.reportID AND ru.userID = " + permissions.getUserId());
		sql.addJoin("JOIN users u ON u.id = ru.userID");
		sql.addJoin("JOIN (SELECT reportID, editable FROM report_permission_user WHERE userID = "
				+ permissions.getUserId()
				+ " UNION SELECT reportID, editable FROM report_permission_user WHERE userID IN ("
				+ Strings.implode(permissions.getAllInheritedGroupIds()) + ") "
				+ " UNION SELECT reportID, 0 FROM report_permission_account WHERE accountID = "
				+ permissions.getAccountId() + ") rp ON rp.reportID = r.id");

		// do not return reports this user owns
		sql.addWhere("r.ownerID != u.id");
		sql.addGroupBy("r.id");
		addOrderBy(sql, reportSearch);
		return sql;
	}

	private void addOrderBy(SelectSQL sql, ReportSearch reportSearch) {
		if (Strings.isEmpty(reportSearch.getSortType()) || Strings.isEmpty(reportSearch.getSortDirection())) {
			return;
		}

		sql.addOrderBy(getOrderBySort(reportSearch.getSortType()) + Strings.SINGLE_SPACE
				+ reportSearch.getSortDirection());
	}
}