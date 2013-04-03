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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.actions.report.ManageReports;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportElement;
import com.picsauditing.report.ReportJson;
import com.picsauditing.report.ReportPaginationParameters;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
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
		selectSQL.addWhere("r.private = 0");

		return selectSQL;
	}

	public List<Report> findAllOrdered(Permissions permissions, String sort, String direction, boolean includeHidden) {
		String orderBy = getOrderBySort(sort);

		String groupIds = permissions.getAllInheritedGroupIds().toString();
		groupIds = groupIds.substring(1, groupIds.length() - 1);

		String queryString = "SELECT r FROM ReportUser ru \n" +
				"JOIN ru.report r \n" +
				"WHERE ru.user.id = " + permissions.getUserId() + "\n" +
				"AND ru.hidden = " + includeHidden + "\n" +
				"AND r.id IN \n" +
				"(\n" +
				"SELECT rpu.report.id \n" +
				"FROM ReportPermissionUser rpu \n" +
				"WHERE (rpu.user.id = " + permissions.getUserId() + "\n" +
				" OR rpu.user.id IN ( " + groupIds + " ))\n" +
				")\n" +
				"ORDER BY " + orderBy + " " + direction;

		Query query = em.createQuery(queryString);

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
}