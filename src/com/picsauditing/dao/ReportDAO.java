package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.report.ReportPaginationParameters;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;
import com.picsauditing.util.pagination.Paginatable;
import com.picsauditing.util.pagination.PaginationParameters;

@SuppressWarnings("unchecked")
public class ReportDAO extends PicsDAO implements Paginatable<Report> {

	private static final Logger logger = LoggerFactory.getLogger(ReportPermissionUserDAO.class);

	public List<BasicDynaBean> runQuery(SelectSQL sql, JSONObject json) throws SQLException {
		Database database = new Database();
		List<BasicDynaBean> rows = database.select(sql.toString(), true);
		json.put("total", database.getAllRows());
		return rows;
	}

	@Override
	public List<Report> getPaginationResults(PaginationParameters parameters) {
		ReportPaginationParameters reportParams = (ReportPaginationParameters)parameters;
		List<Report> reports = new ArrayList<Report>();

		// TODO escape properly
		String terms = "\"%" + Strings.escapeQuotes(reportParams.getQuery()) + "%\"";

		try {
			SelectSQL sql = ReportUserDAO.setupSqlForSearchFilterQuery(reportParams.getUserId(), reportParams.getAccountId());

			sql.addWhere("r.name LIKE " + terms +
					" OR r.description LIKE " + terms +
					" OR u.name LIKE " + terms);

			sql.setPageNumber(reportParams.getPageSize(), reportParams.getPage());

			Query query = em.createNativeQuery(sql.toString(), ReportUser.class);

			List<ReportUser> reportUsers = query.getResultList();

			for (ReportUser reportUser : reportUsers) {
				reports.add(reportUser.getReport());
			}
		} catch (Exception e) {
			logger.error("Unexpected exception in getPaginationResults()");
		}

		return reports;
	}

	@Override
	public int getPaginationOverallCount(PaginationParameters parameters) {
		ReportPaginationParameters reportParams = (ReportPaginationParameters)parameters;

		// TODO escape properly
		String query = "\"%" + Strings.escapeQuotes(reportParams.getQuery()) + "%\"";

		try {
			SelectSQL sql = new SelectSQL("report r");

			sql.addField("count(r.id) AS count");

			sql.addJoin("JOIN users as u ON r.createdBy = u.id");

			sql.addWhere("r.private = 0 OR r.createdBy = " + reportParams.getUserId());
			sql.addWhere("r.name LIKE " + query +
					" OR r.description LIKE " + query +
					" OR u.name LIKE " + query);

			Database db = new Database();
			List<BasicDynaBean> results = db.select(sql.toString(), false);
			String countStr = results.get(0).get("count").toString();

			return Integer.parseInt(countStr);
		} catch (SQLException se) {
			logger.error("SQL Exception in getPaginationOverallCount()", se);
		} catch (NumberFormatException nfe) {
			logger.error("Number Format Exception in getPaginationOverallCount()", nfe);
		} catch (Exception e) {
			logger.error("Unexpected exception in getPaginationOverallCount()", e);
		}

		return -1;
	}
}