package com.picsauditing.actions.report;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.report.ReportModel;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportSharing extends PicsActionSupport {

	@Autowired
	private ReportModel reportModel;

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

			if (type != null && reportModel.canUserEdit(permissions.getUserId(), report)
					&& id != permissions.getUserId()) {
				if (type.equalsIgnoreCase("user"))
					reportModel.connectReportPermissionUser(id, report.getId(), editable);
				else
					reportModel.connectReportPermissionAccount(id, report.getId(), permissions);
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

			if (reportModel.canUserEdit(permissions.getUserId(), report) && id != permissions.getUserId()) {
				if (type.equalsIgnoreCase("user"))
					reportModel.disconnectReportPermissionUser(id, report.getId());
				else {
					reportModel.disconnectReportPermissionAccount(id, report.getId());
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

			if (reportModel.canUserEdit(permissions.getUserId(), report)) {
				reportModel.setEditPermissions(id, report.getId(), editable);
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

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}
}
