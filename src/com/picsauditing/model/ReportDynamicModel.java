package com.picsauditing.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.access.ReportAccessor;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AbstractTable;

/**
 * This is the business layer. It should not have any DAOs in it.
 */
public class ReportDynamicModel {

	@Autowired
	private ReportAccessor reportAccessor;

	private static final List<Integer> baseReports =
			Collections.unmodifiableList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));

	public boolean canUserViewAndCopy(int userId, Report report) {
		if (report == null)
			return false;

		return canUserViewAndCopy(userId, report.getId());
	}

	public boolean canUserViewAndCopy(int userId, int reportId) {
		if (baseReports.contains(reportId))
			return true;

		if (reportAccessor.isReportPublic(reportId))
			return true;

		try {
			ReportUser userReport = reportAccessor.findOneUserReport(userId, reportId);
			if (userReport != null)
				return true;
		} catch (NoResultException e) {
			return false;
		}

		return false;
	}

	public boolean canUserEdit(int userId, Report report) {
		try {
			ReportUser user = reportAccessor.findOneUserReport(userId, report.getId());
			return user.isEditable();
		} catch (NoResultException e) {
			// We don't care. The user can't edit.
		}

		return false;
	}

	// The only reason this method is static is because ManageReports calls it
	// and doesn't have a ReportDynamicModel.
	public static boolean canUserDelete(int userId, Report report) {
		if (report.getCreatedBy().getId() == userId)
			return true;

		return false;
	}

	public Report copy(Report sourceReport, User user) throws NoRightsException, ReportValidationException {
		// TODO Add i18n to this
		if (!canUserViewAndCopy(user.getId(), sourceReport))
			throw new NoRightsException("Invalid User, does not have permission.");

		Report newReport = copyReportWithoutPermissions(sourceReport);

		// TODO the front end is passing new report data in the current report,
		// so we need to change sourceReport to it's old state.
		// Is this is the desired behavior?
		reportAccessor.refresh(sourceReport);

		reportAccessor.saveReport(newReport, user);
		reportAccessor.connectReportToUser(newReport, user);
		reportAccessor.grantEditPermission(newReport, user);

		return newReport;
	}

	public void edit(Report report, Permissions permissions) throws Exception {
		// TODO Add i18n to this
		if (!canUserEdit(permissions.getUserId(), report))
			throw new NoRightsException("Invalid User, cannot edit reports that are not your own.");

		reportAccessor.saveReport(report, new User(permissions.getUserId()));
	}

	public static Map<String, Field> buildAvailableFields(AbstractTable baseTable) {
		Map<String, Field> availableFields = new HashMap<String, Field>();

		addAllAvailableFields(availableFields, baseTable);

		return availableFields;
	}

	private Report copyReportWithoutPermissions(Report sourceReport) {
		Report newReport = new Report();

		newReport.setModelType(sourceReport.getModelType());
		newReport.setName(sourceReport.getName());
		newReport.setDescription(sourceReport.getDescription());
		newReport.setParameters(sourceReport.getParameters());

		return newReport;
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

	public static void validate(Report report) throws ReportValidationException {
		// TODO Add i18n to this
		if (report == null)
			throw new ReportValidationException("Please provide a saved or ad hoc report to run");

		// TODO Add i18n to this
		if (report.getModelType() == null)
			throw new ReportValidationException("The report is missing its base", report);

		try {
			new JSONParser().parse(report.getParameters());
		} catch (ParseException e) {
			throw new ReportValidationException(e, report);
		}
	}
}
