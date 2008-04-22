package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;

//TODO make some logic in a cron job or something that creates the DA audits
//according to the criteria below, ie they answered yes on question 894
//sql.addPQFQuestion(894, false, "requiredAnswer"); //q318.answer
//sql.addWhere("q894.answer = 'Yes' OR c.daRequired IS NULL OR c.daRequired = 'Yes'");

public class ReportContractorAuditAssignment extends ReportContractorAudits {

	private List<User> auditors;
	
	public String execute() throws Exception {
		getPermissions();
		permissions.tryPermission(OpPerms.AssignAudits);

		sql.addWhere("auditStatus='Pending'");
		sql.addWhere("atype.isScheduled=1 OR atype.hasAuditor=1");
		orderBy = "ca.createdDate";

		return super.execute();
	}
	
	public boolean isCanEdit() {
		return permissions.hasPermission(OpPerms.AssignAudits, OpType.Edit);
	}
	
	public List<User> getAuditorList() {
		if (auditors == null) {
			UserDAO dao = (UserDAO) SpringUtils.getBean("UserDAO");
			auditors = dao.findAuditors();
		}
		return auditors;
	}

	public List<AuditType> getAuditTypeList() {
		List<AuditType> list = new ArrayList<AuditType>();
		list.add(new AuditType(AuditType.DEFAULT_AUDITTYPE));
		AuditTypeDAO dao = (AuditTypeDAO) SpringUtils.getBean("AuditTypeDAO");
		list.addAll(dao.findWhere("isScheduled = 1 OR hasAuditor = 1"));
		return list;
	}
}

