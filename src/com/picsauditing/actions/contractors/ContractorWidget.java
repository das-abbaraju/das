package com.picsauditing.actions.contractors;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.util.Strings;

/**
 * Widgets for a single contractor
 * 
 * @author Trevor
 */
@SuppressWarnings("serial")
public class ContractorWidget extends ContractorActionSupport {
	protected boolean reminderTask = false;
	protected boolean openReq = false;

	public ContractorWidget(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao) {
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
			/*
			if (contractor.getBalance() > 0) {
				for(Invoice invoice : contractor.getInvoices()) {
					if(!invoice.isPaid() && invoice.getTotalAmount() > 0) {
						try {
							openTasks.add("You have an <a href=\"Invoice.action?invoice.id=" + invoice.getId() +
									"\">invoice of <b>$" + invoice.getTotalAmount() + "</b></a> due " + 
									DateBean.toShowFormat(invoice.getDueDate())
									+ ", please call 949-387-1940 x708 to make a payment");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}		
			}
			*/
			if (!contractor.isPaymentMethodStatusValid()) {
				try {
					openTasks
					.add("Please <a href=\"ContractorEdit.action?id=" +  contractor.getId() + "\">update your payment method</a><br/>NOTE: You will not be billed until your renewal date "+ DateBean.toShowFormat(contractor.getPaymentExpires()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			

			for (ContractorAudit conAudit : getActiveAudits()) {
				// TODO get the Tasks to show up right for OSHA/EMR
				if (conAudit.getAuditType().isPqf()) {
					if (conAudit.getAuditStatus().equals(AuditStatus.Pending)) {
						openTasks.add("Please <a href=\"Audit.action?auditID="
								+ conAudit.getId()
								+ "\">complete and submit your Pre-Qualification Form</a>");
					} else if (conAudit.getAuditStatus().isActiveSubmitted()
							&& conAudit.isAboutToExpire()) {
						openTasks.add("Please <a href=\"Audit.action?auditID="
								+ conAudit.getId()
								+ "\">review and re-submit your Pre-Qualification Form</a>");
					}
				}
				if (conAudit.getAuditType().isAnnualAddendum()
						&& conAudit.getAuditStatus()
								.equals(AuditStatus.Pending)) {
					openTasks
							.add("Please <a href=\"Audit.action?auditID="
									+ conAudit.getId()
									+ "\">upload and submit your EMR and/or OSHA forms for "
									+ conAudit.getAuditFor() + " </a>");
				}

				if (conAudit.getAuditType().getClassType() == AuditTypeClass.Policy) { 
					Set<String> pendingOperators = new TreeSet<String>();
					for (ContractorAuditOperator cAuditOperator : conAudit.getOperators()) {
						if (CaoStatus.Pending.equals(cAuditOperator.getStatus())) {
							AuditOperator ao = cAuditOperator.getOperator().getAuditMap().get(cAuditOperator.getAudit().getAuditType().getId());
							if (ao.isCanSee() && ao.getMinRiskLevel() > 0) {
								pendingOperators.add(cAuditOperator.getOperator().getName());
							}
						}
					}
					
					if (pendingOperators.size() > 0) {
						openTasks.add("Please <a href=\"Audit.action?auditID=" + conAudit.getId() + "\">upload and submit your "
								+ conAudit.getAuditType().getAuditName() + " Policy for </a>"
								+ Strings.implode(pendingOperators, ","));
					}

					Set<String> rejectedOperators = new TreeSet<String>();
					for(ContractorAuditOperator cAuditOperator : conAudit.getOperators()) {
						if(CaoStatus.Rejected.equals(cAuditOperator.getStatus())) {
							AuditOperator ao = cAuditOperator.getOperator().getAuditMap().get(cAuditOperator.getAudit().getAuditType().getId());
							if(ao.isCanSee() 
									&& ao.getMinRiskLevel() > 0 
									&& ao.getMinRiskLevel() <= contractor.getRiskLevel().ordinal()) {
								rejectedOperators.add(cAuditOperator.getOperator().getName());
							}
						}	
					}
					if(rejectedOperators.size() > 0) {
						openTasks
						.add("<a href=\"Audit.action?auditID="
							+ conAudit.getId()
							+ "\">Update your "
							+ conAudit.getAuditType().getAuditName() + " Policy rejected for </a>"+ Strings.implode(rejectedOperators, ","));
					}
				}		

				if (conAudit.getAuditType().isHasRequirements()
						&& conAudit.getAuditStatus().equals(
								AuditStatus.Submitted)
						&& conAudit.getPercentVerified() < 100 ) {
					String text = "You have <a href=\"Audit.action?auditID="
							+ conAudit.getId()
							+ "\">open requirements from your recent "
							+ conAudit.getAuditType().getAuditName() + "</a>";
					if (!openReq) {
						text += "<br/>NOTE: Open requirements cannot be closed online. You must submit these items to audits@picsauditing.com or fax to 949-269-9165 for further review. Please attach a cover sheet to all submitted information.";
						openReq = true;
					}
					openTasks.add(text);
				}

				if (conAudit.getAuditStatus().equals(AuditStatus.Pending)
						&& conAudit.getAuditType().isCanContractorView()
						&& !conAudit.getAuditType().isCanContractorEdit() 
						&& conAudit.getAuditType().isHasAuditor()) {
					String text = "Prepare for an <a href=\"Audit.action?auditID="
							+ conAudit.getId()
							+ "\">upcoming "
							+ conAudit.getAuditType().getAuditName() + "</a>";
					if (conAudit.getScheduledDate() != null) {
						try {
							text += " on "
									+ DateBean.toShowFormat(conAudit
											.getScheduledDate());
						} catch (Exception e) {
						}
					}
					if (conAudit.getAuditor() != null)
						text += " with " + conAudit.getAuditor().getName();
					openTasks.add(text);
				}
			}


			
			

		}
		return openTasks;
	}

	public boolean isReminderTask() {

		if (Calendar.getInstance().get(Calendar.MONTH) == 0)
			if (contractor.getViewedFacilities() == null) {
				return true;
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
				if( ! sdf.format(contractor.getViewedFacilities()).equals(sdf.format(new Date()))) {
					return true;	
				}
			}
		
		if (contractor.getViewedFacilities() == null) {
			return true;
		} else {
			Calendar ninetyDaysAgo = Calendar.getInstance();
			ninetyDaysAgo.setTime(new Date());
			ninetyDaysAgo.add(Calendar.DATE, -90);

			if (contractor.getViewedFacilities().compareTo(
					ninetyDaysAgo.getTime()) == -1) {
				return true;
			}

		}
		
		return false;
	}

}
