package com.picsauditing.actions.customerservice;

import java.util.Calendar;
import java.util.List;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class AuditorInvoices extends PicsActionSupport {
	private ContractorAuditDAO auditDAO;

	private List list;
	private List<ContractorAudit> auditList;

	private int auditorID = 0;
	private String paidDate = "";

	public AuditorInvoices(ContractorAuditDAO auditDAO) {
		this.auditDAO = auditDAO;
	}

	@Override
	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.hasPermission(OpPerms.AuditorPayments) && !permissions.hasGroup(User.INDEPENDENT_CONTRACTOR)) {
			throw new NoRightsException(OpPerms.AuditorPayments, OpType.View);
		}

		if (button != null) {
			if ("detail".equals(button)) {
				String where = "auditor.id = " + auditorID + " AND paidDate = '" + paidDate + "'";
				auditList = auditDAO.findWhere(1000, where, "contractorAccount.name");

				return SUCCESS;
			}
		}

		fillList();

		return SUCCESS;
	}

	private void fillList() {
		if (permissions.hasPermission(OpPerms.AuditorPayments)) {
			// Jesse can see everyone's Audits and Payments
			auditorID = 0;
		} else if (permissions.hasGroup(User.INDEPENDENT_CONTRACTOR)) {
			auditorID = permissions.getUserId();
		}

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -7);
		list = auditDAO.findAuditorBatches(auditorID, cal.getTime());
	}

	public int getTotal() {
		int total = 0;
		for (ContractorAudit conAudit : auditList) {
			total += conAudit.getAuditorPayment();
		}
		return total;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public List<ContractorAudit> getAuditList() {
		return auditList;
	}

	public void setAuditList(List<ContractorAudit> auditList) {
		this.auditList = auditList;
	}

	public int getAuditorID() {
		return auditorID;
	}

	public void setAuditorID(int auditorID) {
		this.auditorID = auditorID;
	}

	public String getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(String paidDate) {
		this.paidDate = paidDate;
	}
}