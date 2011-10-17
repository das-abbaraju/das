package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.search.Database;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportAuditAnalysis extends PicsActionSupport {
	private List<BasicDynaBean> data;
	//private String where = "AND auditorID = 940 ";
	//private String where = "AND auditTypeID = 2 ";
	private String where = "";
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		permissions.tryPermission(OpPerms.ManageAudits);
		
		String sql = "select label, sortBy, sum(completeCount) completeCount, " +
			"sum(closedCount) closedCount " +
			"FROM (select DATE_FORMAT(creationDate, '%b %Y') label, " +
			"DATE_FORMAT(creationDate, '%Y%m') sortBy, " +
			"count(*) creationDate, 0 completeCount, 0 closedCount " +
			"FROM contractor_audit WHERE creationDate > 0 AND creationDate > '2001-01-01 00:00:00'" + where + 
			"GROUP BY sortBy) t group by sortBy " +
			"ORDER BY sortBy desc";

		Database db = new Database();
		data = db.select(sql, true);
		
		return SUCCESS;
	}

	
	/**
	 * 
	 * @return List of AuditTypes the current user can see
	 */
	public List<AuditType> getAuditTypeList() {
		AuditTypeDAO dao = (AuditTypeDAO) SpringUtils.getBean("AuditTypeDAO");
		List<AuditType> list = new ArrayList<AuditType>();
		for(AuditType aType : dao.findAll()) {
			if (permissions.canSeeAudit(aType))
				list.add(aType);
		}
		return list;
	}
	
	public List<BasicDynaBean> getData() {
		return data;
	}
	
	public void setAuditorId(int[] auditorId) {
		String auditorIdList = Strings.implode(auditorId, ",");
		if (!auditorIdList.equals(""))
			where += " AND auditorID IN (" + auditorIdList + ") ";
	}

	public void setAuditTypeID(int[] auditTypeID) {
		String auditList = Strings.implode(auditTypeID, ",");
		if (auditList.equals("0") || auditList.equals(""))
			return;

		where += " AND auditTypeID IN (" + auditList + ") ";
		
	}
}
