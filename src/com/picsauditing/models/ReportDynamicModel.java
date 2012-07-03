package com.picsauditing.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

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

		try {
			ReportUser user = reportAccessor.queryReportUser(userId, reportId);
			if (user == null)
				return false;
			return true;

		} catch (NoResultException e) {
			return false;
		}
	}

	public boolean canUserEdit(int userId, Report report) {
		try {
			ReportUser user = reportAccessor.queryReportUser(userId, report.getId());
			return user.isEditable();
		} catch (NoResultException e) {
			return false;
		}
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

	public void removeReportFrom(User user, Report report) {
		if (canUserDelete(user.getId(), report)) {
			reportAccessor.deleteReport(report);
		} else {
			reportAccessor.removeReportAssociation(user, report);
		}
	}
}
