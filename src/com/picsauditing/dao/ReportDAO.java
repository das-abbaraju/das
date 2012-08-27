package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.NoResultException;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.ReportModel;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

public class ReportDAO extends PicsDAO {

	@Autowired
	private ReportUserDAO reportUserDao;

	private static final Logger logger = LoggerFactory.getLogger(ReportDAO.class);

	public void save(Report report, User user) throws ReportValidationException {
		ReportModel.validate(report);
		report.setAuditColumns(user);

		saveInternal(report);
	}

	@Transactional(propagation = Propagation.NESTED)
	private Report saveInternal(Report report) {
		if (report.getId() == 0) {
			em.persist(report);
		} else {
			report = em.merge(report);
		}

		return report;
	}

	public void remove(int id) {
		Report report = findOne(id);
		remove(report);
	}

	public void remove(Report report) {
		List<ReportUser> userReports = reportUserDao.findAllByReportId(report.getId());
		for (ReportUser userReport : userReports) {
			reportUserDao.remove(userReport);
		}

		removeInternal(report);
	}

	@Transactional(propagation = Propagation.NESTED)
	private void removeInternal(Report report) {
		if (report != null) {
			em.remove(report);
		}
	}

	public Report findOne(int id) throws NoResultException {
		return findOne(Report.class, "t.id = " + id);
	}

	public List<Report> findAll() {
		return findAll(Report.class);
	}

	public List<Report> findAllPublic() {
		String query = "private = 0";
		List<Report> publicReports = findWhere(Report.class, query);

		return publicReports;
	}

	public void refresh(Report report) {
		super.refresh(report);
	}

	public boolean isPublic(int reportId) {
		try {
			Report report = findOne(reportId);
			if (report != null && report.isPublic()) {
				return true;
			}
		} catch (NoResultException nre) {
			// If the report doesn't exist, it's not public
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public List<BasicDynaBean> runQuery(SelectSQL sql, JSONObject json) throws SQLException {
		Database database = new Database();
		List<BasicDynaBean> rows = database.select(sql.toString(), true);
		json.put("total", database.getAllRows());

		return rows;
	}
}
