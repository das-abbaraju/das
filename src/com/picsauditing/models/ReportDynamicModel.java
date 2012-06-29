package com.picsauditing.models;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.access.DynamicReportUtil;
import com.picsauditing.report.access.ReportAdministration;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AbstractTable;

public class ReportDynamicModel {

	@Autowired private BasicDAO basicDao;
	@Autowired private ReportAdministration reportAccessor;

	public Report copy(Report sourceReport, User user) throws NoRightsException, ReportValidationException {

		// TODO Add i18n to this
		if (!reportAccessor.canUserViewAndCopy(user.getId(), sourceReport))
			throw new NoRightsException("Invalid User, does not have permission.");

		Report newReport = copyReportWithoutPermissions(sourceReport);

		// TODO we're passing new report data in the current report, change sourceReport to it's old state, FIX THIS
		basicDao.refresh(sourceReport);

		reportAccessor.saveReport(newReport, user);
		reportAccessor.connectReportToUser(newReport, user);
		reportAccessor.grantPermissionToEdit(newReport, user);

		return newReport;
	}

	public void edit(Report report, Permissions permissions) throws Exception {
		// TODO Add i18n to this
		if (!reportAccessor.canUserEdit(permissions.getUserId(), report))
			throw new NoRightsException("Invalid User, cannot edit reports that are not your own.");

		reportAccessor.saveReport(report, new User(permissions.getUserId()));
	}

	private Report copyReportWithoutPermissions(Report sourceReport) {
		Report newReport = new Report();
		newReport.setModelType(sourceReport.getModelType());
		newReport.setName(sourceReport.getName());
		newReport.setDescription(sourceReport.getDescription());
		newReport.setParameters(sourceReport.getParameters());

		return newReport;
	}

	public static Map<String, Field> buildAvailableFields(AbstractTable baseTable) {
		Map<String, Field> availableFields = new HashMap<String, Field>();

		addAllAvailableFields(availableFields, baseTable);

		return availableFields;
	}

	/**
	 * This method is recursively building the available fields. It works like this
	 * because the set of tables that comprise available fields for a model is a tree,
	 * which we've decided to walk recursively.
	 */
	private static void addAllAvailableFields(Map<String, Field> availableFields, AbstractTable table) {
		availableFields.putAll(table.getAvailableFields());
		for (AbstractTable joinTable : table.getJoins()) {
			addAllAvailableFields(availableFields, joinTable);
		}
	}
}
