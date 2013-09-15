package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.report.models.ModelType;
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

    public List<Report> findByModel(ModelType modelType) {
        return findWhere(Report.class, "t.modelType = '" + modelType.toString() + "'");
    }

    public void detach(Report newReport) {
		em.detach(newReport);
	}

	public List<ReportInfo> findByOwnerID(ReportSearch reportSearch) {
		SelectSQL sql = buildQueryForFindByOwnerId(reportSearch);

		try {
			return new Database().select(sql.toString(), new ReportInfoMapper());
		} catch (SQLException e) {
			logger.error("Error while finding owned by reports for ownerID = "
					+ reportSearch.getPermissions().getUserId());
		}

		return Collections.EMPTY_LIST;
	}

	private SelectSQL buildQueryForFindByOwnerId(ReportSearch reportSearch) {
		SelectSQL sql = new SelectSQL("report r");

        addFields(sql);
        sql.addField("0 AS " + ReportInfoMapper.NUMBER_OF_TIMES_FAVORITED);
        sql.addField("1 AS " + ReportInfoMapper.EDITABLE_FIELD); // because if you own it, you can edit it

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
			return new Database().select(sql.toString(), new ReportInfoMapper());
		} catch (SQLException e) {
			logger.error("Error while finding shared with reports for userID = " + permissions.getUserId());
		}
		return Collections.EMPTY_LIST;
	}

	private SelectSQL buildQueryForFindSharedWith(ReportSearch reportSearch, Permissions permissions) {
		SelectSQL sql = new SelectSQL("report r");

        addFields(sql);
        sql.addField("0 AS " + ReportInfoMapper.NUMBER_OF_TIMES_FAVORITED);
		sql.addField("MAX(rp.editable) AS " + ReportInfoMapper.EDITABLE_FIELD);

		sql.addJoin("LEFT JOIN report_user ru ON r.id = ru.reportID AND ru.userID = " + permissions.getUserId());
		sql.addJoin("LEFT JOIN users u ON u.id = ru.userID");
		sql.addJoin("JOIN (" + getPermissionsJoin(permissions) + ") rp ON rp.reportID = r.id");

		// do not return reports this user owns
		String notOwnerClause = "(r.ownerID != " + permissions.getUserId() + ")";
		String notHiddenClause = "(ru.hidden = 0 OR ru.hidden IS NULL)";

		sql.addWhere(notOwnerClause + "AND " + notHiddenClause);

		sql.addGroupBy("r.id");
		addOrderBy(sql, reportSearch);

		return sql;
	}

    public static String getPermissionsJoin(Permissions permissions) {
        Set<Integer> users = new HashSet<Integer>();
        users.addAll(permissions.getAllInheritedGroupIds());
        users.add(permissions.getUserId());

        Set<Integer> accounts = new HashSet<Integer>();
        accounts.addAll(permissions.getCorporateParent());
        accounts.add(permissions.getAccountId());

        return "SELECT reportID, editable FROM report_permission_user WHERE userID IN (" + Strings.implode(users) + ") UNION "
                + " SELECT reportID, editable FROM report_permission_account WHERE accountID IN (" + Strings.implode(accounts) + ")";
    }

    private void addFields(SelectSQL sql) {
        sql.addField("r.id AS " + ReportInfoMapper.ID_FIELD);
        sql.addField("r.name AS " + ReportInfoMapper.NAME_FIELD);
        sql.addField("r.description AS " + ReportInfoMapper.DESCRIPTION_FIELD);
        sql.addField("r.creationDate AS " + ReportInfoMapper.CREATION_DATE_FIELD);
        sql.addField("ru.favorite AS " + ReportInfoMapper.FAVORITE_FIELD);
        sql.addField("r.public AS " + ReportInfoMapper.PUBLIC_FIELD);
        sql.addField("ru.lastViewedDate AS " + ReportInfoMapper.LAST_VIEWED_DATE_FIELD);
        sql.addField("u.id AS '" + UserMapper.USER_ID_FIELD + "'");
        sql.addField("u.name AS '" + UserMapper.USER_NAME_FIELD + "'");
    }

    private void addOrderBy(SelectSQL sql, ReportSearch reportSearch) {
		if (Strings.isEmpty(reportSearch.getSortType()) || Strings.isEmpty(reportSearch.getSortDirection())) {
			return;
		}

		sql.addOrderBy(getOrderBySort(reportSearch.getSortType()) + Strings.SINGLE_SPACE
				+ reportSearch.getSortDirection());
	}

    public void updateReportSuggestions() throws SQLException {
        Database database = new Database();
        database.executeUpdate("CALL dw_calc_inherited_user_groups();");
        database.executeUpdate("CALL dw_calc_report_suggestions();");
    }

    public List<ReportInfo> findReportSuggestions(Permissions permissions) {
        SelectSQL sql = new SelectSQL("calc_inherited_user_group c");

        addFields(sql);
        sql.addField("0 AS " + ReportInfoMapper.EDITABLE_FIELD);
        sql.addField("f.total AS " + ReportInfoMapper.NUMBER_OF_TIMES_FAVORITED);
        sql.addField("MAX(rgs.score) AS myScore");

        sql.addJoin("JOIN report_group_suggestion rgs ON rgs.groupID = c.groupID");
        sql.addJoin("JOIN report r ON r.id = rgs.reportID AND r.deleted = 0");
        sql.addJoin("LEFT JOIN report_user ru ON rgs.reportID = ru.reportID AND ru.userID = c.userID");
        sql.addJoin("LEFT JOIN (SELECT reportID, SUM(favorite) total, SUM(viewCount) viewCount FROM report_user GROUP BY reportID) AS f ON r.id = f.reportID");
        sql.addJoin("LEFT JOIN users u ON ru.userID = u.id");

        sql.addWhere("c.userID = " + permissions.getUserId());
        sql.addWhere("ru.favorite IS NULL OR (ru.favorite = 0)");

        String permissionsUnion = getPermissionsUnion(permissions);

        String ownerClause = "r.ownerID = " + permissions.getUserId();
        String publicClause = "r.public = 1";
        String permissionsClause = "r.id IN (" + permissionsUnion + ")";
        String canViewClause = "(" + ownerClause + " OR " + publicClause + " OR " + permissionsClause + ")";

        sql.addWhere(canViewClause);

        sql.addGroupBy("rgs.reportID");
        sql.addOrderBy("myScore DESC");
        sql.setLimit(10);

        try {
            return new Database().select(sql.toString(), new ReportInfoMapper());
        } catch (SQLException e) {

        }

        return Collections.EMPTY_LIST;
    }

    private static String getPermissionsUnion(Permissions permissions) {
        Set<Integer> users = new HashSet<Integer>();
        users.addAll(permissions.getAllInheritedGroupIds());
        users.add(permissions.getUserId());

        Set<Integer> accounts = new HashSet<Integer>();
        accounts.addAll(permissions.getCorporateParent());
        accounts.add(permissions.getAccountId());

        return "SELECT reportID FROM report_permission_user WHERE userID IN (" + Strings.implode(users) + ") UNION "
                + " SELECT reportID FROM report_permission_account WHERE accountID IN (" + Strings.implode(accounts) + ")";
    }

}