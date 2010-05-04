package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.SelectSQL;


@SuppressWarnings("serial")
public class ReportActivityWatch extends ReportAccount {
	
	private int conID = 0;
	private List<String> atype = new ArrayList<String>();
	private int limit = 50;
	
	@Override
	protected void buildQuery() {
		super.buildQuery();
		String activity = "(";
		
/*		
(select conID, 'AuditExpiration' activityType, ca.expiresDate activityDate, concat(aType.auditName, (case when ca.auditFor is null then '' else concat(' for ',ca.auditFor) end), ' Expired') body, concat('Audit.action?auditID=', ca.id) url
from contractor_audit ca 
join audit_type aType on ca.auditTypeID = aType.id
where expiresDate is not null and ca.auditStatus = 'Expired'
order by activityDate desc
limit 50)
union
(select conID, 'AuditSubmitted' activityType, ca.completedDate activityDate, concat(aType.auditName, (case when ca.auditFor is null then '' else concat(' for ',ca.auditFor) end), ' Submitted') body, concat('Audit.action?auditID=', ca.id) url
from contractor_audit ca 
join audit_type aType on ca.auditTypeID = aType.id and aType.hasRequirements = 1
where completedDate is not null and ca.auditStatus = 'Submitted'
order by activityDate desc
limit 50)
union
(select conID, 'AuditActivated' activityType, ca.closedDate activityDate, concat(aType.auditName, (case when ca.auditFor is null then '' else concat(' for ',ca.auditFor) end), ' Submitted') body, concat('Audit.action?auditID=', ca.id) url
from contractor_audit ca 
join audit_type aType on ca.auditTypeID = aType.id
where closedDate is not null and ca.auditStatus = 'Active'
order by activityDate desc
limit 50)
union
(select gc.subID, 'FlagColorChange' activityType, gc.flagLastUpdated activityDate, concat('Flag Color changed to ', flag) body, concat('ConFlag.action?auditID=', gc.subID) url
from generalcontractors gc 
where gc.genID = 16
order by activityDate desc
limit 50)
*/
		if (atype.contains("Note")) {
			SelectSQL sql2 = buildNote("Note", "note n USE INDEX(creationDate)", "n.accountID", "n.creationDate", "CONCAT(u.name, ' posted a Note')", "CONCAT('Note.action?conID=', n.accountID)");
			sql2.addJoin("JOIN users u ON n.createdBy = u.id AND u.id > " + User.SYSTEM);
			activity += sql2.toString();
		}
		activity += ") ac ON a.id = ac.conID";
		sql.addJoin(activity);
		sql.addField("ac.*");
		orderByDefault = "ac.activityDate DESC";
		report.setLimit(limit);
	}
	
	private SelectSQL buildNote(String activityType, String from, String accountID, String activityDate, String body, String url) {
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
			sql.addWhere("JOIN contractor_watch w ON " + accountID +
					" = w.conID AND w.userID = " + permissions.getUserId() +	")");
		
		return sql;
	}
}
