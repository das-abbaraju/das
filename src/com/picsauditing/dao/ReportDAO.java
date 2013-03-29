package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.actions.report.ManageReports;
import com.picsauditing.dao.mapper.ReportInfoMapper;
import com.picsauditing.dao.mapper.SharedWithMapper;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportElement;
import com.picsauditing.report.ReportJson;
import com.picsauditing.report.ReportPaginationParameters;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.service.ReportInfo;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.Strings;
import com.picsauditing.util.pagination.Paginatable;
import com.picsauditing.util.pagination.PaginationParameters;

@SuppressWarnings("unchecked")
public class ReportDAO extends PicsDAO implements Paginatable<Report> {

	@Autowired
	private FeatureToggle featureToggle;

	private static final Logger logger = LoggerFactory.getLogger(ReportPermissionUserDAO.class);

	public List<BasicDynaBean> runQuery(String sql, JSONObject json) throws SQLException {
		Database database = new Database();

		List<BasicDynaBean> rows = null;
		if (featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_READ_ONLY_DATASOURCE)) {
			rows = database.selectReadOnly(sql, true);
		} else {
			rows = database.select(sql, true);
		}

		// todo: move this outta here
		json.put(ReportJson.RESULTS_TOTAL, database.getAllRows());
		return rows;
	}

	@Override
	public List<Report> getPaginationResults(PaginationParameters parameters) {
		List<Report> reports = new ArrayList<Report>();
		try {
			SelectSQL sql = buildQueryForSearch(parameters);
			sql.setPageNumber(parameters.getPageSize(), parameters.getPage());

			Database db = new Database();
			List<BasicDynaBean> results = db.select(sql.toString(), false);

			for (BasicDynaBean bean : results) {
				int id = 0;
				if ((id = NumberUtils.toInt(bean.get("id").toString(), 0)) != 0) {
					Report report = this.find(Report.class, id);
					report.setNumTimesFavorited(NumberUtils.toInt(bean.get("numTimesFavorited").toString(), 0));
					reports.add(report);
				}
			}
		} catch (Exception e) {
			logger.error("Unexpected exception in getPaginationResults()");
		}

		return reports;
	}

	@Override
	public int getPaginationOverallCount(PaginationParameters parameters) {
		try {
			SelectSQL sql = buildQueryForSearch(parameters);
			sql.addField("count(r.id) AS count");

			Database db = new Database();
			List<BasicDynaBean> results = db.select(sql.toString(), false);

			return Database.toInt(results.get(0), "count");
		} catch (Exception e) {
			logger.error("Unexpected exception in getPaginationOverallCount()", e);
		}

		return -1;
	}

	private SelectSQL buildQueryForSearch(PaginationParameters parameters) {
		ReportPaginationParameters reportParams = (ReportPaginationParameters) parameters;

		// TODO escape properly
		String query = "\'%" + Strings.escapeQuotes(reportParams.getQuery()) + "%\'";

		SelectSQL selectSQL = ReportUserDAO.setupSqlForSearchFilterQuery(reportParams.getPermissions());
		selectSQL.addWhere("r.name LIKE " + query + " OR r.description LIKE " + query + " OR u.name LIKE " + query);

		return selectSQL;
	}

	public List<Report> findAllOrdered(Permissions permissions, String sort, String direction) {
		String orderBy = getOrderBySort(sort);

		SelectSQL subSql = new SelectSQL("report_permission_user rpu");
		subSql.addField("rpu.reportID");
		subSql.addWhere("rpu.userID = :userId OR rpu.userID IN ( :groupIds )");

		SelectSQL sql = new SelectSQL("report r");
		sql.addJoin("JOIN report_user ru ON ru.reportID = r.id AND ru.userID = :userId");
		sql.addWhere("r.id IN (" + subSql.toString() + ")");

		sql.addOrderBy(orderBy + " " + direction);

		Query query = em.createNativeQuery(sql.toString(), Report.class);
		query.setParameter("userId", permissions.getUserId());
		query.setParameter("groupIds", permissions.getAllInheritedGroupIds());

		return query.getResultList();
	}

	private String getOrderBySort(String sort) {
		String orderBy = "";

		if (sort.equals(ManageReports.ALPHA_SORT)) {
			orderBy = "r.name";
		} else if (sort.equals(ManageReports.DATE_ADDED_SORT)) {
			orderBy = "r.creationDate";
		} else if (sort.equals(ManageReports.LAST_VIEWED_SORT)) {
			orderBy = "ru.lastViewedDate";
		} else {
			throw new IllegalArgumentException("Unexpected sort type '" + sort + "'");
		}
		return orderBy;
	}

	public void remove(ReportElement row) {
		if (row != null) {
			em.remove(row);
		}
	}

	// TODO remove this after next release
	@Transactional(propagation = Propagation.NEVER)
	public void truncateReportChildren() {
		System.out.println("WARNING: TRUNCATING REPORT TABLES");
		em.createNativeQuery("TRUNCATE TABLE report_column").executeUpdate();
		em.createNativeQuery("TRUNCATE TABLE report_filter").executeUpdate();
		em.createNativeQuery("TRUNCATE TABLE report_sort").executeUpdate();
	}

	@Transactional(propagation = Propagation.NESTED)
	public ReportElement save(ReportElement o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public <E extends ReportElement> int remove(Class<E> clazz, String where) {
		Query query = em.createQuery("DELETE " + clazz.getName() + " t WHERE " + where);
		return query.executeUpdate();
	}

	@Transactional(propagation = Propagation.NESTED)
	public <E extends ReportElement> void save(List<E> reportElements) {
		for (ReportElement reportElement : reportElements) {
			save(reportElement);
		}
	}

	public Report findById(int reportId) {
		return find(Report.class, reportId);
	}

	public void detach(Report newReport) {
		em.detach(newReport);
	}

	public List<ReportInfo> findByOwnerID(int ownerId) {
		SelectSQL sql = new SelectSQL("report r");

		sql.addField("r.id AS id");
		sql.addField("r.name AS name");
		sql.addField("r.description AS description");
		sql.addField("r.creationDate AS creationDate");
		sql.addField("ru.favorite as favorite");
		sql.addField("1 as editable"); // because if you own it, you can edit it
		sql.addField("ru.lastViewedDate as lastViewedDate");
		sql.addField("u.id as 'users.id'");
		sql.addField("u.name as 'users.name'");

		sql.addJoin("JOIN report_user ru on ru.reportID = r.id");
		sql.addJoin("JOIN users u on u.id = ru.userID");

		sql.addWhere("r.ownerID = " + ownerId);

		try {
			return Database.select(sql.toString(), new ReportInfoMapper());
		} catch (SQLException e) {
			logger.error("Error while finding owned by reports for ownerID = " + ownerId);
		}
		return Collections.EMPTY_LIST;
	}

	public List<ReportInfo> findReportForSharedWith(Permissions permissions) {
		SelectSQL sql = new SelectSQL("report r");

		sql.addField("r.id AS id");
		sql.addField("r.name AS name");
		sql.addField("r.description AS description");
		sql.addField("r.creationDate AS creationDate");
		sql.addField("ru.favorite AS favorite");
		sql.addField("MAX(rp.editable) AS editable");
		sql.addField("ru.lastViewedDate AS lastViewedDate");
		sql.addField("u.id as 'users.id'");
		sql.addField("u.name as 'users.name'");

		sql.addJoin("LEFT JOIN report_user ru ON r.id = ru.reportID AND ru.userID = " + permissions.getUserId());
		sql.addJoin("JOIN users u ON u.id = ru.userID");
		sql.addJoin("JOIN (SELECT reportID, editable FROM report_permission_user WHERE userID = " + permissions.getUserId() +
					" UNION SELECT reportID, editable FROM report_permission_user WHERE userID IN (" + Strings.implode(permissions.getAllInheritedGroupIds()) + ") " +
					" UNION SELECT reportID, 0 FROM report_permission_account WHERE accountID = " + permissions.getAccountId() + ") rp ON rp.reportID = r.id");

		sql.addWhere("r.ownerID != u.id");
		sql.addGroupBy("r.id");

		try {
			return Database.select(sql.toString(), new ReportInfoMapper());
		} catch (SQLException e) {
			logger.error("Error while finding shared with reports for userID = " + permissions.getUserId());
		}
		return Collections.EMPTY_LIST;
	}
}