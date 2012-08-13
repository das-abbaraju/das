package com.picsauditing.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AbstractTable;
import com.picsauditing.util.Strings;

public class ReportDynamicModel {

	@Autowired
	private ReportDAO reportDao;

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

		if (reportDao.isReportPublic(reportId))
			return true;

		try {
			ReportUser userReport = reportDao.findOneUserReport(userId, reportId);
			if (userReport != null)
				return true;
		} catch (NoResultException e) {
			return false;
		}

		return false;
	}

	public boolean canUserEdit(int userId, Report report) {
		try {
			ReportUser userReport = reportDao.findOneUserReport(userId, report.getId());
			return userReport.isEditable();
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
		if (!canUserViewAndCopy(user.getId(), sourceReport))
			throw new NoRightsException("User " + user.getId() + " does not have permission to copy report " + sourceReport.getId());

		Report newReport = copyReportWithoutPermissions(sourceReport);

		// TODO the front end is passing new report data in the current report,
		// so we need to change sourceReport to it's old state.
		// Is this is the desired behavior?
		reportDao.refresh(sourceReport);

		reportDao.saveReport(newReport, user);
		reportDao.connectReportToUser(newReport, user);
		reportDao.grantEditPermission(newReport, user);

		return newReport;
	}

	public void edit(Report report, Permissions permissions) throws Exception {
		if (!canUserEdit(permissions.getUserId(), report))
			throw new NoRightsException("User " + permissions.getUserId() + " cannot edit report " + report.getId());

		reportDao.saveReport(report, new User(permissions.getUserId()));
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
		if (report == null)
			throw new ReportValidationException("Tried to validate a null report");

		if (report.getModelType() == null)
			throw new ReportValidationException("Report " + report.getId() + " is missing its base", report);

		try {
			new JSONParser().parse(report.getParameters());
		} catch (ParseException e) {
			throw new ReportValidationException(e, report);
		}
	}

	public List<BasicDynaBean> getReportsForSearch(String searchTerm, int userId) {
		List<BasicDynaBean> results = new ArrayList<BasicDynaBean>();

		if (Strings.isEmpty(searchTerm)) {
			// By default, show the top ten most favorited reports sorted by number of favorites
			results = reportDao.findTopTenFavoriteReports(userId);
		} else {
			// Otherwise, search on all public reports and all of the user's reports
			results = reportDao.findReportsForSearchFilter(userId, searchTerm);
		}

		return results;
	}

	public List<ReportUser> populateUserReports(List<BasicDynaBean> results) {
		List<ReportUser> userReports = new ArrayList<ReportUser>();

		for (BasicDynaBean result : results) {
			Report report = new Report();

			report.setId(Integer.parseInt(result.get("id").toString()));
			report.setName(result.get("name").toString());
			report.setDescription(result.get("description").toString());

			User user = new User(result.get("userName").toString());
			user.setId(Integer.parseInt(result.get("userId").toString()));
			report.setCreatedBy(user);

			report.setNumTimesFavorited(Integer.parseInt(result.get("numTimesFavorited").toString()));

			userReports.add(new ReportUser(0, report));
		}

		return userReports;
	}
}
