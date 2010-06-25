package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.ibm.icu.text.DateFormat;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.util.Strings;

/**
 * Widgets for a single contractor
 * 
 * @author Trevor
 */
@SuppressWarnings("serial")
public class ContractorWidget extends ContractorActionSupport {
	private AppPropertyDAO appPropDAO;

	protected boolean reminderTask = false;
	
	protected boolean showAgreement = false;

	protected boolean openReq = false;

	public ContractorWidget(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AppPropertyDAO appPropDAO) {
		super(accountDao, auditDao);
		this.appPropDAO = appPropDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN_AJAX;

		findContractor();
		return SUCCESS;
	}
	
	public void setShowAgreement(boolean showAgreement) {
		this.showAgreement = showAgreement;
	}
	
	public boolean getShowAgreement(){
		return showAgreement;
	}

	private List<String> openTasks = null;

	private CreditCard creditCard;

	public List<String> getOpenTasks() {
		if (openTasks == null) {
			openTasks = new ArrayList<String>();

			// Adding a note to agree to terms of updated Contractor Agreement
			if (contractor != null
					&& !contractor.isAgreementInEffect()
					&& (permissions.hasPermission(OpPerms.ContractorBilling)
							|| permissions.hasPermission(OpPerms.ContractorAdmin) || permissions
							.hasPermission(OpPerms.ContractorSafety))){
				showAgreement = true;
				openTasks
						.add("Please <a title=\"Click here to view the PICS Contractor Agreement\" href=\"ContractorAgreementAjax.action?id="+contractor.getId()+"\" rel=\"facebox\" >"
								+ "review the terms of our <strong>updated</strong> Contractor User Agreement and click 'I Agree'</a> to accept the terms of the Agreement.");
			}

			if (permissions.hasPermission(OpPerms.ContractorAdmin) || permissions.isAdmin()) {
				if (contractor.getUsers().size() == 1
						&& DateBean.getDateDifference(contractor.getCreationDate()) > -180) {
					openTasks
							.add("PICS now requires contractors to have two or more users to help maintain their account."
									+ "Please click here <a href=\"UsersManage.action\"> to add a new user</a>");
				}

				if (contractor.isAcceptsBids()) {
					String due = null;
					try {
						due = DateBean.toShowFormat(contractor.getPaymentExpires());
					} catch (Exception ignoreFormattingErrors) {
					}
					openTasks.add("Your Account is a BID-ONLY Account and will expire on " + due
							+ ". To continue with the audit process" + " please <a href=\"ContractorView.action?id="
							+ contractor.getId()
							+ "&button=Upgrade to Full Membership\"> upgrade your account to a full membership</a>");
				}
			}

			if (permissions.hasPermission(OpPerms.ContractorBilling) || permissions.isAdmin()) {
				String billingStatus = contractor.getBillingStatus();
				if ("Upgrade".equals(billingStatus)
						|| ("Renewal".equals(billingStatus) && contractor.getMembershipLevel().getId() == InvoiceFee.BIDONLY)) {
					openTasks.add("Your Account is upgraded to " + contractor.getNewMembershipLevel().getFee()
							+ ". To continue working at your selected facilities"
							+ " please <a href=\"BillingDetail.action?id=" + contractor.getId()
							+ "&button=Create\"> generate and pay the invoice </a>");
				}

				if (contractor.getBalance().compareTo(BigDecimal.ZERO) > 0) {
					for (Invoice invoice : contractor.getInvoices()) {
						if (invoice.getStatus().isUnpaid()) {
							String due = null;
							try {
								due = DateBean.toShowFormat(invoice.getDueDate());
							} catch (Exception ignoreFormattingErrors) {
							}
							openTasks.add("You have an <a href=\"InvoiceDetail.action?invoice.id=" + invoice.getId()
									+ "\">invoice of <b>$" + invoice.getBalance() + "</b></a> due " + due);
						}
					}
				}

				if (!contractor.isPaymentMethodStatusValid() && contractor.isMustPayB()) {
					openTasks.add("Please <a href=\"ContractorPaymentOptions.action?id=" + contractor.getId()
							+ "\">update your payment method</a>");
				}
			}
			String auditName;
			Set<AuditType> auditTypeList = new HashSet<AuditType>();
			for (ContractorOperator co : contractor.getNonCorporateOperators()) {
				if (co.getOperatorAccount().getStatus().isActiveDemo()) {
					for (AuditOperator ao : co.getOperatorAccount().getVisibleAudits()) {
						if (ao.isRequiredFor(contractor) && ao.getAuditType().getId() != AuditType.PQF
								&& ao.getAuditType().getId() != AuditType.WELCOME) {
							auditTypeList.add(ao.getAuditType());
						}
					}
				}
			}

			for (ContractorAudit conAudit : getActiveAudits()) {
				if (conAudit.getAuditType().isCanContractorView()
						&& (conAudit.getAuditType().isPqf() || conAudit.isManuallyAdded() || auditTypeList
								.contains(conAudit.getAuditType()))) {
					if (permissions.hasPermission(OpPerms.ContractorSafety) || permissions.isAdmin()) {
						if (conAudit.getAuditType().getClassType().isPqf()) {
							if (conAudit.getAuditType().isPqf())
								auditName = "Pre-Qualification Form";
							else
								auditName = conAudit.getAuditType().getAuditName();
							if (conAudit.getAuditStatus().isPending() || conAudit.getAuditStatus().isIncomplete()) {
								String auditFor = "";
								if(conAudit.getAuditFor()!=null){
									auditFor = " for "+ conAudit.getAuditFor();
								}
								openTasks.add("Please <a href=\"Audit.action?auditID=" + conAudit.getId()
										+ "\">complete and submit your " + auditName + auditFor + "</a>");
							} else if (conAudit.getAuditStatus().isActiveSubmitted() && conAudit.isAboutToExpire()) {
								String auditFor = "";
								if(conAudit.getAuditFor()!=null){
									auditFor = " for "+ conAudit.getAuditFor();
								}
								openTasks.add("Please <a href=\"Audit.action?auditID=" + conAudit.getId()
										+ "\">review and re-submit your " + auditName + auditFor  + "</a>");
							}
						}
						if (conAudit.getAuditType().isAnnualAddendum()
								&& (conAudit.getAuditStatus().isPending() || conAudit.getAuditStatus().isIncomplete())) {
							String text = "Please <a href=\"Audit.action?auditID=" + conAudit.getId()
									+ "\">upload and submit your EMR and/or OSHA forms for " + conAudit.getAuditFor()
									+ " </a>";
							if (conAudit.getAuditFor().equals(Integer.toString(DateBean.getCurrentYear() - 1))) {
								text += "<br/><b>NOTE: <a href=\"http://help.picsauditing.com/wiki/Annual_Updates\">Click here to watch the Annual Update tutorial</a></b>";
							}
							openTasks.add(text);

						}

						if (conAudit.getAuditType().isHasRequirements() && conAudit.getAuditStatus().isSubmitted()
								&& conAudit.getPercentVerified() < 100) {
							String auditFor = "";
							if(conAudit.getAuditFor()!=null){
								auditFor = " for "+ conAudit.getAuditFor();
							}
							String text = "You have <a href=\"ContractorAuditFileUpload.action?auditID="
									+ conAudit.getId() + "\">open requirements from your recent "
									+ conAudit.getAuditType().getAuditName() + auditFor + "</a>";
							if (!openReq) {
								text += "<br/>NOTE: Open requirements can be uploaded online.";
								openReq = true;
							}
							openTasks.add(text);
						}

						if (conAudit.getAuditStatus().equals(AuditStatus.Pending)
								&& conAudit.getAuditType().isCanContractorView()
								&& !conAudit.getAuditType().isCanContractorEdit()
								&& conAudit.getAuditType().isHasAuditor()) {
							String auditFor = "";
							if(conAudit.getAuditFor()!=null){
								auditFor = " for "+ conAudit.getAuditFor();
							}
							String text;
							if (conAudit.getAuditType().getId() == AuditType.OFFICE
									&& conAudit.getScheduledDate() == null) {
								text = "Please <a href='ScheduleAudit.action?auditID=" + conAudit.getId()
										+ "'>click here to schedule your Implementation Audit"+ auditFor +"</a>";
							} else {
								text = "Prepare for an <a href=\"Audit.action?auditID=" + conAudit.getId()
										+ "\">upcoming " + conAudit.getAuditType().getAuditName() + auditFor +"</a>";
								if (conAudit.getScheduledDate() != null) {
									try {
										text += " on " + DateBean.toShowFormat(conAudit.getScheduledDate());
									} catch (Exception e) {
									}
								}
								if (conAudit.getAuditor() != null)
									text += " with " + conAudit.getAuditor().getName();
							}

							openTasks.add(text);
						}
					}

					if (permissions.hasPermission(OpPerms.ContractorInsurance) || permissions.isAdmin()) {
						if (conAudit.getAuditType().getClassType() == AuditTypeClass.Policy) {
							Set<String> pendingOperators = new TreeSet<String>();
							for (ContractorAuditOperator cAuditOperator : conAudit.getCurrentOperators()) {
								if (CaoStatus.Pending.equals(cAuditOperator.getStatus())) {
									AuditOperator ao = cAuditOperator.getOperator().getAuditMap().get(
											cAuditOperator.getAudit().getAuditType().getId());
									if (ao.isCanSee() && ao.getMinRiskLevel() > 0) {
										pendingOperators.add(cAuditOperator.getOperator().getName());
									}
								}
							}

							if (pendingOperators.size() > 0) {
								openTasks.add("Please <a href=\"Audit.action?auditID=" + conAudit.getId()
										+ "\">upload and submit your " + conAudit.getAuditType().getAuditName()
										+ " Policy for </a>" + Strings.implode(pendingOperators, ","));
							}

							for (ContractorAuditOperator cAuditOperator : conAudit.getCurrentOperators()) {
								if (CaoStatus.Rejected.equals(cAuditOperator.getStatus())) {
									AuditOperator ao = cAuditOperator.getOperator().getAuditMap().get(
											cAuditOperator.getAudit().getAuditType().getId());
									if (ao.isCanSee() && ao.getMinRiskLevel() > 0
											&& ao.getMinRiskLevel() <= contractor.getRiskLevel().ordinal()) {
										String Text = "<a href=\"Audit.action?auditID=" + conAudit.getId()
												+ "\">Update your " + conAudit.getAuditType().getAuditName()
												+ " Policy rejected by </a> " + cAuditOperator.getOperator().getName();
										if (!Strings.isEmpty(cAuditOperator.getNotes()))
											Text += " for reason " + cAuditOperator.getNotes();
										openTasks.add(Text);
									}
								}
							}
						}
					}
				}
			}

			if (permissions.hasPermission(OpPerms.ContractorSafety) || permissions.isAdmin()) {
				if (!contractor.isNaicsValid() && contractor.getCountries().contains("US")) {
					AuditCatData auditCatData = getAuditCatData(contractor);
					if (auditCatData != null)
						openTasks.add("Please <a href=\"AuditCat.action?auditID=" + auditCatData.getAudit().getId()
								+ "&catDataID=" + auditCatData.getId()
								+ "&mode=Edit#node_57\"> update your 2008 NAICS code</a>");
				}

				if (contractor.getWebcam().getTrackingNumber().trim().length() > 0) {
					openTasks.add("Your webcam has been shipped. "
							+ "<a href=\"http://www.fedex.com/Tracking?tracknumber_list="
							+ contractor.getWebcam().getTrackingNumber() + "\" target=\"_blank\">"
							+ "Click here to track your webcam." + "</a>");
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
				if (!sdf.format(contractor.getViewedFacilities()).equals(sdf.format(new Date()))) {
					return true;
				}
			}

		if (contractor.getViewedFacilities() == null) {
			return true;
		} else {
			Calendar ninetyDaysAgo = Calendar.getInstance();
			ninetyDaysAgo.setTime(new Date());
			ninetyDaysAgo.add(Calendar.DATE, -90);

			if (contractor.getViewedFacilities().compareTo(ninetyDaysAgo.getTime()) == -1) {
				return true;
			}

		}

		return false;
	}

	private AuditCatData getAuditCatData(ContractorAccount contractor) {
		for (ContractorAudit contractorAudit : contractor.getAudits()) {
			if (contractorAudit.getAuditType().isPqf()) {
				for (AuditCatData auditCatData : contractorAudit.getCategories()) {
					if (auditCatData.getCategory().getId() == 2 && auditCatData.isAppliesB())
						return auditCatData;
				}
			}
		}
		return null;
	}
	
	public boolean getHasUnpaidInvoices(){
		for(Invoice invoice : contractor.getInvoices()){
			if(invoice.getStatus().isUnpaid())
				return true;
		}
		return false;
	}
	
	// Will return the earliest unpaid invoice with the assumption that 
	// is the one we want to display on con_stats.jsp
	public String getChargedOn(){
		String result = "";
		try {
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
			Date d = format.parse("12/31/4000");
			for (Invoice invoice : contractor.getInvoices()) {
				if (invoice.getStatus().isUnpaid()) {
						Date d1 = invoice.getDueDate();
						if(d1.before(d))
							d  = d1;
				}
			}
			result = format.format(d);	
		} catch (Exception ignoreFormattingErrors) {
		}
		return result;
	}

	public BrainTreeService.CreditCard getCreditCard() {
		if (creditCard == null) {
			try {
				BrainTreeService bt = new BrainTreeService();
				bt.setUserName(appPropDAO.find("brainTree.username").getValue());
				bt.setPassword(appPropDAO.find("brainTree.password").getValue());

				creditCard = bt.getCreditCard(id);
			} catch (Exception itllJustStayNull) {
			}
		}
		return creditCard;
	}
}
