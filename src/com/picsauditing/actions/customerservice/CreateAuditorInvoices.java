package com.picsauditing.actions.customerservice;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class CreateAuditorInvoices extends PicsActionSupport {
	private List<ContractorAudit> list;
	private ContractorAuditDAO auditDAO;
	private int auditorID = 0;
	private int listCount = 0;

	public CreateAuditorInvoices(ContractorAuditDAO auditDAO) {
		this.auditDAO = auditDAO;
	}

	@Override
	public String execute() throws Exception {
		loadPermissions();

		if (permissions.hasPermission(OpPerms.AuditorPayments)) {
			// Jesse can see everyone's Audits and Payments
			auditorID = 0;
		} else if (permissions.hasGroup(User.INDEPENDENT_CONTRACTOR)) {
			auditorID = permissions.getUserId();
		}

		fillList();

		if (button != null) {
			permissions.tryPermission(OpPerms.AuditorPayments, OpType.Edit);
			if (listCount == list.size()) {
				Date paidDate = new Date();
				for (ContractorAudit conAudit : list) {
					conAudit.setPaidDate(paidDate);
					auditDAO.save(conAudit);
				}
				return redirect("AuditorInvoices.action");
			} else {
				addActionError("Audit count does not match. The list must have changed. "
						+ "Please review the list again and resubmit.");
			}
		}

		listCount = list.size();
		return SUCCESS;
	}

	private void fillList() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -3);
		String startDate = DateBean.format(cal.getTime(), "yyyy-MM-dd");
		String where = "paidDate IS NULL AND auditType.classType = 'Audit' AND id IN (SELECT cao.audit.id FROM ContractorAuditOperator cao WHERE cao.visible = 1 AND statusChangedDate > '" + startDate + "' )" ;
		if (auditorID > 0) {
			where += " AND auditor.id = " + auditorID;
		} else {
			where += " AND auditor IN (SELECT user FROM UserGroup WHERE group.id = " + User.INDEPENDENT_CONTRACTOR
					+ ")";
		}
		list = auditDAO.findWhere(100, where, "auditor.name, creationDate");
		if (list.size() == 50)
			addActionMessage("Showing first 100 audits. Invoice these audits to see the remaining ones.");
		if (list.size() == 0)
			addActionMessage("No more audits remaining to invoice");
	}

	public List<ContractorAudit> getList() {
		return list;
	}

	public void setList(List<ContractorAudit> list) {
		this.list = list;
	}

	public int getAuditorID() {
		return auditorID;
	}

	public void setAuditorID(int auditorID) {
		this.auditorID = auditorID;
	}

	public int getListCount() {
		return listCount;
	}

	public void setListCount(int listCount) {
		this.listCount = listCount;
	}

}
