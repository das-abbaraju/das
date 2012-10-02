package com.picsauditing.actions.report;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.ReportModel;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportSharing extends PicsActionSupport {

	@Autowired
	private ReportModel reportModel;

	private Report report;

	private static final Logger logger = LoggerFactory.getLogger(ReportSharing.class);

	public String share() {
		boolean editable = false;
		int userId = -1;
		int accountId = -1;
		String dirtyParameter = "";

		try {
			dirtyParameter = ServletActionContext.getRequest().getParameter("userId");
			if (dirtyParameter != null)
				userId = Integer.parseInt(dirtyParameter);
			else {
				dirtyParameter = ServletActionContext.getRequest().getParameter("accountId");
				accountId = Integer.parseInt(dirtyParameter);
			}
			
			if (reportModel.canUserEdit(permissions.getUserId(), report) && userId != permissions.getUserId()) {
				if (userId != -1)
					reportModel.connectReportPermissionUser(userId, report.getId(), editable);
				else
				{
					reportModel.connectReportPermissionAccount(accountId, report.getId(), permissions);
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
		int userId = -1;
		int accountId = -1;
		String dirtyParameter = "";

		try {
			dirtyParameter = ServletActionContext.getRequest().getParameter("userId");
			if (dirtyParameter != null)
				userId = Integer.parseInt(dirtyParameter);
			else {
				dirtyParameter = ServletActionContext.getRequest().getParameter("accountId");
				accountId = Integer.parseInt(dirtyParameter);
			}
			
			if (reportModel.canUserEdit(permissions.getUserId(), report) && userId != permissions.getUserId()) {
				if (userId != -1)
					reportModel.disconnectReportPermissionUser(userId, report.getId());
				else
				{
					reportModel.disconnectReportPermissionAccount(accountId, report.getId());
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
		int userId = -1;
		boolean editable = false;
		String dirtyParameter = "";

		try {
			dirtyParameter = ServletActionContext.getRequest().getParameter("userId");
			userId = Integer.parseInt(dirtyParameter);
			
			dirtyParameter = ServletActionContext.getRequest().getParameter("editable");
			editable = Boolean.parseBoolean(dirtyParameter);
		
			if (reportModel.canUserEdit(permissions.getUserId(), report)) {
				reportModel.setEditPermissions(userId, report.getId(), editable);
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
