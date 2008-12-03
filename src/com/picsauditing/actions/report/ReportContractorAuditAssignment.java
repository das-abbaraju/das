package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.util.SpringUtils;

//TODO make some logic in a cron job or something that creates the DA audits
//according to the criteria below, ie they answered yes on question 894
//sql.addPQFQuestion(894, false, "requiredAnswer"); //q318.answer
//sql.addWhere("q894.answer = 'Yes' OR c.daRequired IS NULL OR c.daRequired = 'Yes'");

@SuppressWarnings("serial")
public class ReportContractorAuditAssignment extends ReportContractorAudits {

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.AssignAudits);
		sql.addField("ca.contractorConfirm");
		sql.addField("ca.auditorConfirm");
		sql.addField("ca2.expiresDate AS current_expiresDate");
		sql.addWhere("ca.auditStatus='Pending'");
		sql.addJoin("LEFT JOIN contractor_audit ca2 ON " + "ca2.conID = a.id "
				+ "AND ca2.auditTypeID = ca.auditTypeID " + "AND ca2.auditStatus = 'Active' "
				+ "AND atype.hasMultiple = 0");
		if (getFilter().isUnScheduledAudits()) {
			sql.addWhere("(ca.contractorConfirm IS NULL OR ca.auditorConfirm IS NULL) AND atype.isScheduled = 1");
		} else {
			sql.addWhere("atype.isScheduled=1 OR atype.hasAuditor=1");
		}

		if (orderBy == null) {
			orderBy = "ca.createdDate";
		}

		getFilter().setShowAuditStatus(false);
		getFilter().setShowUnConfirmedAudits(true);
		if (filtered == null)
			filtered = false;

		return super.execute();
	}

	public boolean isCanEdit() {
		return permissions.hasPermission(OpPerms.AssignAudits, OpType.Edit);
	}

	public List<AuditType> getAuditTypeList() {
		List<AuditType> list = new ArrayList<AuditType>();
		list.add(new AuditType(AuditType.DEFAULT_AUDITTYPE));
		AuditTypeDAO dao = (AuditTypeDAO) SpringUtils.getBean("AuditTypeDAO");
		list.addAll(dao.findWhere("isScheduled = 1 OR hasAuditor = 1"));
		return list;
	}

	public String getYesterday() {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DAY_OF_YEAR, -1);
		return DateBean.format(date.getTime(), "M/d/yyyy");
	}
}
