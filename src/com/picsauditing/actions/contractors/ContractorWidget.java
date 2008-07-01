package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;

/**
 * Widgets for a single contractor
 * 
 * @author Trevor
 */
public class ContractorWidget extends ContractorActionSupport {

	public ContractorWidget(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN_AJAX;

		findContractor();
		return SUCCESS;
	}

	private List<String> openTasks = null;

	public List<String> getOpenTasks() {
		if (openTasks == null) {
			openTasks = new ArrayList<String>();
			// get the balance due
			// TODO - do some more testing with this
			if (contractor.getUpgradeAmountOwed() > 0) {
				openTasks.add("You have an invoice of <b>$" + contractor.getBillingAmount() + "</b> due "
						+ contractor.getLastInvoiceDate() + ", please call 949-387-1940 x708 to make a payment");
			}
			if (contractor.getAnnualAmountOwed() > 0) {
				openTasks.add("You have an invoice of <b>$" + (contractor.getNewBillingAmount())
						+ "</b>, please call 949-387-1940 x708 to make a payment");
			}

			for (ContractorAudit conAudit : getActiveAudits()) {
				if (conAudit.getAuditType().isPqf() && conAudit.getAuditStatus().equals(AuditStatus.Pending)) {
					openTasks.add("Please <a href=\"Audit.action?auditID=" + conAudit.getId()
							+ "\">complete your Pre-Qualification Form</a>");
				}

				if (conAudit.getAuditType().isHasRequirements()
						&& conAudit.getAuditStatus().equals(AuditStatus.Submitted)
						&& conAudit.getPercentVerified() < 100 && conAudit.getId() == 1) {
					openTasks.add("You have <a href=\"Audit.action?auditID=" + conAudit.getId()
							+ "\">open requirements from your recent " + conAudit.getAuditType().getAuditName()
							+ "</a>");
				}
				if (conAudit.getAuditStatus().equals(AuditStatus.Pending)
						&& conAudit.getAuditType().isCanContractorView()
						&& !conAudit.getAuditType().isCanContractorEdit()) {
					String text = "Prepare for an <a href=\"Audit.action?auditID=" + conAudit.getId() + "\">upcoming "
							+ conAudit.getAuditType().getAuditName() + "</a>";
					if (conAudit.getScheduledDate() != null) {
						try {
							text += " on " + DateBean.toShowFormat(conAudit.getScheduledDate());
						} catch (Exception e) {
						}
					}
					if (conAudit.getAuditor() != null)
						text += " with " + conAudit.getAuditor().getName();
					openTasks.add(text);
				}
			}

			if (isHasInsurance()) {
				openTasks.add("Please <a href=\"contractor_upload_certificates.jsp?id=" + id
						+ "\">upload your insurance certificates</a>");
			}

			if (Calendar.getInstance().get(Calendar.MONTH) == 0
					|| DateBean.getDateDifference(contractor.getAccountDate()) < 30) {
				// During January and the first month of registration, we
				// encourage Contractors to update their facility list
				openTasks
						.add("Please <a href=\"con_selectFacilities.jsp?id=" + id + "\">update your facility list</a>");
			}
		}
		return openTasks;
	}
}
