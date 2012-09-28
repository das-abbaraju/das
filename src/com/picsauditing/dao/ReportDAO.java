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
					reports.add(this.find(Report.class, id));
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
		
		SelectSQL selectSQL = ReportUserDAO.setupSqlForSearchFilterQuery(reportParams.getUserId(), reportParams.getAccountId());
		selectSQL.addWhere("r.name LIKE " + query +
				" OR r.description LIKE " + query +
				" OR u.name LIKE " + query);
		
		return selectSQL;
	}
}
