package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.ReportModel;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

public class ReportDAO extends PicsDAO {

	@Transactional(propagation = Propagation.NESTED)
	public void save(Report report, User user) throws ReportValidationException {
		ReportModel.validate(report);
		report.setAuditColumns(user);
		save(report);
	}

	public List<Report> findAllPublic() {
		String query = "private = 0";
		return findWhere(Report.class, query);
	}

	@SuppressWarnings("unchecked")
	public List<BasicDynaBean> runQuery(SelectSQL sql, JSONObject json) throws SQLException {
		Database database = new Database();
		List<BasicDynaBean> rows = database.select(sql.toString(), true);
		json.put("total", database.getAllRows());
		return rows;
	}

}
