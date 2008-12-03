package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAudit;

/**
 * Widgets for a single contractor
 * 
 * @author Trevor
 */
@SuppressWarnings("serial")
public class ContractorWidget extends ContractorActionSupport {
	protected boolean reminderTask = false;
	protected boolean openReq = false;

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
				openTasks.add("You have an invoice of <b>$" + contractor.getUpgradeAmountOwed() + "</b> due "
						+ contractor.getLastInvoiceDate() + ", please call 949-387-1940 x708 to make a payment");
			}
			if (contractor.getAnnualAmountOwed() > 0) {
				openTasks.add("You have an invoice of <b>$" + (contractor.getAnnualAmountOwed())
						+ "</b>, please call 949-387-1940 x708 to make a payment");
			}

			for (ContractorAudit conAudit : getActiveAudits()) {
				if (conAudit.getAuditType().isPqf() && conAudit.getAuditStatus().equals(AuditStatus.Pending)) {
					openTasks.add("Please <a href=\"Audit.action?auditID=" + conAudit.getId()
							+ "\">complete your Pre-Qualification Form</a>");
				}

				if (conAudit.getAuditType().isHasRequirements()
						&& conAudit.getAuditStatus().equals(AuditStatus.Submitted)
						&& conAudit.getPercentVerified() < 100 && !conAudit.getAuditType().isPqf()) {
					String text = "You have <a href=\"Audit.action?auditID=" + conAudit.getId()
							+ "\">open requirements from your recent " + conAudit.getAuditType().getAuditName()
							+ "</a>";
					if (!openReq) {
						text += "<br/>NOTE: Open requirements cannot be closed online. You must submit these items to audits@picsauditing.com or fax to 949-269-9110 for further review. Please attach a cover sheet to all submitted information.";
						openReq = true;
					}
					openTasks.add(text);
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

			if (isRequiresInsurance() && getInsuranceCount() == 0) {
				openTasks.add("Please <a href=\"contractor_upload_certificates.jsp?id=" + id
						+ "\">upload your insurance certificates</a>");
			}
			for (Certificate certificate : contractor.getCertificates()) {
				if (certificate.getStatus().equals("Expired"))
					openTasks.add("You have an <a href=\"contractor_upload_certificates.jsp?id=" + id
							+ "\">Expired " + certificate.getType() + " Certificate</a>");
				if (certificate.getStatus().equals("Rejected"))
					openTasks.add("You have a <a href=\"contractor_upload_certificates.jsp?id=" + id
							+ "\">Rejected " + certificate.getType() + " Certificate</a>");
			}

		}
		return openTasks;
	}

	public boolean isReminderTask() {
		if (Calendar.getInstance().get(Calendar.MONTH) == 0
				|| DateBean.getDateDifference(contractor.getAccountDate()) > -30) {
			reminderTask = true;
		}
		return reminderTask;
	}

}
