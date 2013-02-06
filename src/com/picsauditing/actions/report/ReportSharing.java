package com.picsauditing.actions.report;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.ReportService;
import com.picsauditing.service.PermissionService;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportSharing extends PicsActionSupport {

	@Autowired
	private ReportService reportService;
	@Autowired
	private PermissionService permissionService;

	private Report report;

	private static final Logger logger = LoggerFactory.getLogger(ReportSharing.class);

	public String share() {
		boolean editable = false;
		int id = -1;
		String type = "";
		String dirtyParameter = "";

		try {
			dirtyParameter = ServletActionContext.getRequest().getParameter("id");
			id = Integer.parseInt(dirtyParameter);

			type = ServletActionContext.getRequest().getParameter("type");

			editable = Boolean.parseBoolean(ServletActionContext.getRequest().getParameter("editable"));

			if (type != null && permissionService.canUserEditReport(permissions, report)
					&& id != permissions.getUserId()) {

				json.put("title", "Report Shared");

				if ("user".equalsIgnoreCase(type)) {
					reportService.connectReportPermissionUser(permissions, id, report.getId(), editable);
					json.put("html", "Your report has been added to the user's My Reports.");
				} else if ("group".equalsIgnoreCase(type)) {
					reportService.connectReportPermissionUser(permissions, id, report.getId(), editable);
					json.put("html", "Your report has been added to the users' My Reports.");
				} else if ("account".equalsIgnoreCase(type)) {
					reportService.connectReportPermissionAccount(id, report.getId(), permissions);
					json.put("html", "Your report has been added to the users' My Reports.");
				} else {
					throw new IllegalArgumentException("Invalid type for sharing.");
				}

				json.put("success", true);
			} else {
				json.put("success", false);
			}
		} catch (NumberFormatException nfe) {
			logger.error("Bad url parameter(" + dirtyParameter + ")", nfe);
			writeJsonError(nfe);
		} catch (Exception e) {
			logger.error("Unexpected exception", e);
			writeJsonError(e);
		}

		return JSON;
	}

	public String unshare() {
		int id = -1;
		String type = "";
		String dirtyParameter = "";

		try {
			dirtyParameter = ServletActionContext.getRequest().getParameter("id");
			id = Integer.parseInt(dirtyParameter);

			if (permissionService.canUserEditReport(permissions, report) && id != permissions.getUserId()) {
				if ("user".equalsIgnoreCase(type) || "group".equalsIgnoreCase(type)) {
					reportService.disconnectReportPermissionUser(id, report.getId());
				} else if ("account".equalsIgnoreCase(type)) {
					reportService.disconnectReportPermissionAccount(id, report.getId());
				} else {
					throw new IllegalArgumentException("Invalid type for unsharing.");
				}

				json.put("success", true);
			} else {
				json.put("success", false);
			}
		} catch (NumberFormatException nfe) {
			logger.error("Bad url parameter(" + dirtyParameter + ")", nfe);
			writeJsonError(nfe);
		} catch (Exception e) {
			logger.error("Unexpected exception", e);
			writeJsonError(e);
		}

		return JSON;
	}

	public String editPermissions() {
		int id = -1;
		boolean editable = false;
		String dirtyParameter = "";

		try {
			dirtyParameter = ServletActionContext.getRequest().getParameter("id");
			id = Integer.parseInt(dirtyParameter);

			dirtyParameter = ServletActionContext.getRequest().getParameter("editable");
			editable = Boolean.parseBoolean(dirtyParameter);

			if (permissionService.canUserEditReport(permissions, report)) {
				reportService.setEditPermissions(permissions, id, report.getId(), editable);
				json.put("success", true);
			} else {
				json.put("success", false);
			}
		} catch (NumberFormatException nfe) {
			logger.error("Bad url parameter(" + dirtyParameter + ")", nfe);
			writeJsonError(nfe);
		} catch (Exception e) {
			logger.error("Unexpected exception", e);
			writeJsonError(e);
		}

		return JSON;
	}

	private void writeJsonError(Exception e) {
		json.put("success", false);
		json.put("message", e.toString());
	}

}
