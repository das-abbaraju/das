package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.Permissions;
import com.picsauditing.actions.report.ManageReports;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.ReportPaginationParameters;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;
import com.picsauditing.util.pagination.Paginatable;
import com.picsauditing.util.pagination.PaginationParameters;

@SuppressWarnings("unchecked")
public class ReportDAO extends PicsDAO implements Paginatable<Report> {

	private static final Logger logger = LoggerFactory.getLogger(ReportPermissionUserDAO.class);

	public List<BasicDynaBean> runQuery(String sql, JSONObject json) throws SQLException {
		Database database = new Database();
		List<BasicDynaBean> rows = database.select(sql, true);
		json.put("total", database.getAllRows());
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
			
			return results.size();
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
		selectSQL.addWhere("r.name LIKE " + query +
				" OR r.description LIKE " + query +
				" OR u.name LIKE " + query);
		
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
		System.out.println(sql);
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
}