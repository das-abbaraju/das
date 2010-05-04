package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorWatch;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

import edu.emory.mathcs.backport.java.util.Arrays;


@SuppressWarnings("serial")
public class ReportActivityWatch extends ReportAccount {
	private UserDAO userDAO;
	
	private int conID = 0;
	private String[] activityTypes =
		new String[] { "AuditExpiration", "AuditSubmitted", "AuditActivated", "FlagColorChange", "Login", "Note" };
	private List<String> atype = new ArrayList<String>();
	private int limit = 50;
	
	private List<ContractorWatch> watched;
	
	public ReportActivityWatch(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	@Override
	protected void buildQuery() {
		super.buildQuery();
		String activity = "JOIN (";
		List<String> watchOptions = new ArrayList<String>();
		
		if (atype.size() == 0)
			atype.addAll(Arrays.asList(activityTypes));
		
		if (atype.contains("AuditExpiration")) {
			SelectSQL sql2 = buildWatch("AuditExpiration", "contractor_audit ca", "ca.conID", "ca.expiresDate", "CONCAT(aType.auditName, (CASE WHEN ca.auditFor IS NULL THEN '' ELSE CONCAT(' for ', ca.auditFor) END), ' Expired')", "CONCAT('Audit.action?auditID=', ca.id)");
			sql2.addJoin("JOIN audit_type aType ON ca.auditTypeID = aType.id");
			sql2.addWhere("expiresDate IS NOT NULL AND ca.auditStatus = 'Expired'");
			watchOptions.add("(" + sql2.toString() + ")");
		}
		if (atype.contains("AuditSubmitted")) {
			SelectSQL sql2 = buildWatch("AuditSubmitted", "contractor_audit ca", "ca.conID", "ca.completedDate", "CONCAT(aType.auditName, (CASE WHEN ca.auditFor IS NULL THEN '' ELSE CONCAT(' for ', ca.auditFor) END), ' Submitted')", "CONCAT('Audit.action?auditID=', ca.id)");
			sql2.addJoin("JOIN audit_type aType ON ca.auditTypeID = aType.id AND aType.hasRequirements = 1");
			sql2.addWhere("completedDate IS NOT NULL AND ca.auditStatus = 'Submitted'");
			watchOptions.add("(" + sql2.toString() + ")");
		}
		if (atype.contains("AuditActivated")) {
			SelectSQL sql2 = buildWatch("AuditActivated", "contractor_audit ca", "ca.conID", "ca.closedDate", "CONCAT(aType.auditName, (CASE WHEN ca.auditFor IS NULL THEN '' ELSE CONCAT(' for ', ca.auditFor) END), ' Activated')", "CONCAT('Audit.action?auditID=', ca.id)");
			sql2.addJoin("JOIN audit_type aType ON ca.auditTypeID = aType.id");
			sql2.addWhere("closedDate IS NOT NULL AND ca.auditStatus = 'Active'");
			watchOptions.add("(" + sql2.toString() + ")");
		}
		if (atype.contains("FlagColorChange")) {
			SelectSQL sql2 = buildWatch("FlagColorChange", "generalcontractors gc", "gc.subID", "gc.flagLastUpdated", "CONCAT('Flag Color changed to ', flag)", "CONCAT('ConFlag.action?auditID=', gc.subID)");
			sql2.addWhere("gc.genID = " + permissions.getAccountId());
			watchOptions.add("(" + sql2.toString() + ")");
		}
		if (atype.contains("Login")) {
			SelectSQL sql2 = buildWatch("Login", "users u", "u.accountID", "u.lastLogin", "CONCAT(u.name, ' logged in')", "''");
			sql2.addWhere("u.lastLogin IS NOT NULL AND u.isGroup = 'No'");
			watchOptions.add("(" + sql2.toString() + ")");
		}
		if (atype.contains("Note")) {
			SelectSQL sql2 = buildWatch("Note", "note n USE INDEX(creationDate)", "n.accountID", "n.creationDate", "CONCAT(u.name, ' posted a Note')", "CONCAT('ContractorNotes.action?id=', n.accountID)");
			sql2.addJoin("JOIN users u ON n.createdBy = u.id AND u.id > " + User.SYSTEM);
			watchOptions.add("(" + sql2.toString() + ")");
		}
		
		activity += Strings.implode(watchOptions, " UNION ");
		activity += ") ac ON a.id = ac.conID";

		sql.addJoin(activity);
		sql.addField("ac.*");
		orderByDefault = "ac.activityDate DESC";
		report.setLimit(limit);
	}
	
	private SelectSQL buildWatch(String activityType, String from, String accountID, String activityDate, String body, String url) {
		SelectSQL sql = new SelectSQL(from);
		sql.addField(accountID + " conID");
		sql.addField("'" + activityType + "' activityType");
		sql.addField(activityDate + " activityDate");
		sql.addField(body + " body");
		sql.addField(url + " url");
		sql.addOrderBy("activityDate DESC");
		sql.setLimit(limit);
		if (conID > 0)
			sql.addWhere(accountID + " = " + conID);
		else
			sql.addJoin("JOIN contractor_watch w ON " + accountID +
					" = w.conID AND w.userID = " + permissions.getUserId());
		
		return sql;
	}
	
	public int getConID() {
		return conID;
	}
	
	public void setConID(int conID) {
		this.conID = conID;
	}
	
	public String[] getActivityTypes() {
		return activityTypes;
	}
	
	public void setAtype(List<String> atype) {
		this.atype = atype;
	}
	
	public List<ContractorWatch> getWatched() {
		if (watched == null)
			watched = userDAO.findContractorWatch(permissions.getUserId());
		
		return watched;
	}
}
