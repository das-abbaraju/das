package com.picsauditing.report.business;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.BaseTable;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.business.DynamicReportUtil;

public class ReportController {

	@Autowired
	private BasicDAO basicDao;

	// was create() in ReportDynamic
	public Report copy(Report sourceReport, Permissions permissions) throws Exception {
		if (!DynamicReportUtil.canUserViewAndCopy(permissions.getUserId(), sourceReport))
			throw new NoRightsException("Invalid User, does not have permission.");

		Report newReport = copyReportWithoutPermissions(sourceReport);

		// TODO we're passing new report data in the current report, change sourceReport to it's old state, FIX THIS
		basicDao.refresh(sourceReport);

		saveReport(newReport, permissions);
		connectReportToUser(newReport, new User(permissions.getUserId()), true);

		return newReport;
	}

	public void edit(Report report, Permissions permissions) throws Exception {
		if (!DynamicReportUtil.canUserEdit(permissions.getUserId(), report))
			throw new NoRightsException("Invalid User, cannot edit reports that are not your own.");

		validate(report);

		report.setAuditColumns(permissions);
		basicDao.save(report);
	}

	private Report copyReportWithoutPermissions(Report sourceReport) {
		Report newReport = new Report();
		newReport.setModelType(sourceReport.getModelType());
		newReport.setName(sourceReport.getName());
		newReport.setDescription(sourceReport.getDescription());
		newReport.setParameters(sourceReport.getParameters());

		return newReport;
	}

	private void connectReportToUser(Report report, User user) {
		connectReportToUser(report, user, false);
	}

	private void connectReportToUser(Report report, User user, boolean canEdit) {
		ReportUser userReport = new ReportUser();
		// TODO
		userReport.setAuditColumns(user);
		userReport.setReport(report);
		userReport.setUser(report.getCreatedBy());
		userReport.setCanEdit(canEdit);
		basicDao.save(userReport);
	}

	// This was ensureValidReport in ReportDynamic
	public void validate(Report report) throws Exception {
		if (report == null) {
			// TODO Add i18n to this
			throw new RuntimeException("Please provide a saved or ad hoc report to run");
		}

		if (report.getModelType() == null) {
			// TODO Add i18n to this
			throw new RuntimeException("The report is missing its base");
		}

		new JSONParser().parse(report.getParameters());
	}

	private void saveReport(Report report, Permissions permissions) throws Exception {
		validate(report);

		// TODO this should be like report.updateDatabaseInternalFields(User)
		report.setAuditColumns(permissions);
		basicDao.save(report);
	}

	public List<BasicDynaBean> runQuery(SelectSQL sql, JSONObject json) throws SQLException {
		Database db = new Database();
		List<BasicDynaBean> rows = db.select(sql.toString(), true);
		json.put("total", db.getAllRows());

		return rows;
	}

	public static Map<String, Field> buildAvailableFields(BaseTable baseTable) {
		Map<String, Field> availableFields = new HashMap<String, Field>();

		addAllAvailableFields(availableFields, baseTable);

		return availableFields;
	}

	private static void addAllAvailableFields(Map<String, Field> availableFields, BaseTable table) {
		availableFields.putAll(table.getAvailableFields());
		for (BaseTable joinTable : table.getJoins()) {
			addAllAvailableFields(availableFields, joinTable);
		}
	}
}
