package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.SmartFacilitySuggest;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AssessmentResultStage;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;

/**
 * Widgets for a single contractor
 * 
 * @author Trevor
 */
@SuppressWarnings("serial")
public class ContractorWidget extends ContractorActionSupport {

	private AppPropertyDAO appPropDAO;
	private AssessmentTestDAO testDAO;

	protected boolean reminderTask = false;

	protected boolean showAgreement = false;

	protected boolean openReq = false;

	public ContractorWidget(AppPropertyDAO appPropDAO, AssessmentTestDAO testDAO) {
		this.appPropDAO = appPropDAO;
		this.testDAO = testDAO;
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

	public boolean getShowAgreement() {
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
							.hasPermission(OpPerms.ContractorSafety))) {
				showAgreement = true;
				openTasks
						.add(getText("ContractorWidget.message.UpdatedAgreement", new Object[] { contractor.getId() }));
			}

			if (permissions.hasPermission(OpPerms.ContractorAdmin) || permissions.isAdmin()) {
				if (contractor.getUsers().size() == 1
						&& DateBean.getDateDifference(contractor.getCreationDate()) > -180) {
					openTasks.add(getText("ContractorWidget.message.RequiresTwoUsers"));
				}

				if (contractor.isAcceptsBids()) {
					openTasks.add(getText("ContractorWidget.message.BidOnlyUpdgrade",
							new Object[] { contractor.getPaymentExpires(), contractor.getId() }));
				}
			}

			if (permissions.hasPermission(OpPerms.ContractorBilling)) {
				String billingStatus = contractor.getBillingStatus();
				if ("Upgrade".equals(billingStatus)
						|| ("Renewal".equals(billingStatus) && contractor.getMembershipLevel().getId() == InvoiceFee.BIDONLY)) {
					openTasks.add(getText("ContractorWidget.message.GenerateInvoice", new Object[] {
							contractor.getNewMembershipLevel().getFee(), contractor.getId() }));
				}

				if (contractor.getBalance().compareTo(BigDecimal.ZERO) > 0) {
					for (Invoice invoice : contractor.getInvoices()) {
						if (invoice.getStatus().isUnpaid()) {
							openTasks.add(getText("ContractorWidget.message.OpenInvoiceReminder", new Object[] {
									invoice.getId(), invoice.getBalance(), invoice.getDueDate() }));
						}
					}
				}

				if (!contractor.isPaymentMethodStatusValid() && contractor.isMustPayB()) {
					openTasks.add(getText("ContractorWidget.message.UpdatePaymentMethod",
							new Object[] { contractor.getId() }));
				}
			}
			String auditName;

			for (ContractorAudit conAudit : contractor.getAudits()) {
				if (conAudit.getAuditType().isCanContractorView() && !conAudit.isExpired()) {
					int needed = 0;

					for (ContractorAuditOperator cao : conAudit.getOperators()) {
						if (cao.isVisible()) {
							if (permissions.hasPermission(OpPerms.ContractorSafety) || permissions.isAdmin()) {
								if (conAudit.getAuditType().isCanContractorEdit()) {
									// Maybe use conAudit.isAboutToRenew()
									// instead
									// of conAudit.getAuditType().isRenewable()
									// &&
									// conAudit.isAboutToExpire()
									if (conAudit.getAuditType().getId() == 176) {
										if (cao.getStatus().before(AuditStatus.Complete)) {
											needed++;
										}
									} else if (cao.getStatus().before(AuditStatus.Submitted)
											|| cao.getStatus() == AuditStatus.Resubmit
											|| (conAudit.getAuditType().isRenewable() && conAudit.isAboutToExpire())) {
										needed++;
									}
								} else if (conAudit.getAuditType().getWorkFlow().isHasRequirements()) {
									if (cao.getStatus().before(AuditStatus.Complete)) {
										needed++;
									}
								}
							}
							if (permissions.hasPermission(OpPerms.ContractorInsurance) || permissions.isAdmin()) {
								if (cao.getStatus().before(AuditStatus.Submitted)) {
									needed++;
								}
							}
						}
					}

					if (needed > 0) {
						// Add to tasks
						auditName = getText(conAudit.getAuditType().getI18nKey("name"));
						Object showAuditFor = (conAudit.getAuditFor() != null && !conAudit.getAuditFor().isEmpty()) ? 1
								: 0;
						String auditFor = conAudit.getAuditFor();
						if (conAudit.getAuditType().getClassType().isPolicy()) {
							if (permissions.hasPermission(OpPerms.ContractorInsurance) || permissions.isAdmin()) {
								if (conAudit.hasCaoStatus(AuditStatus.Incomplete)) {
									openTasks.add(getText("ContractorWidget.message.FixPolicyIssues", new Object[] {
											conAudit.getId(), auditName }));
								} else {
									openTasks.add(getText("ContractorWidget.message.UploadAndSubmitPolicy",
											new Object[] { conAudit.getId(), auditName }));
								}
							}
						} else if (conAudit.getAuditType().isRenewable() && conAudit.isAboutToExpire()) {
							if (permissions.hasPermission(OpPerms.ContractorSafety) || permissions.isAdmin()) {
								openTasks.add(getText("ContractorWidget.message.ResubmitPolicy", new Object[] {
										conAudit.getId(), auditName, showAuditFor, auditFor }));
							}
						} else if (conAudit.getAuditType().getWorkFlow().isHasRequirements()
								&& (conAudit.getAuditType().getId() != AuditType.WA_STATE_VERIFICATION || (conAudit
										.getAuditType().getId() == AuditType.WA_STATE_VERIFICATION && conAudit
										.hasCaoStatusAfter(AuditStatus.Pending)))) {
							if (conAudit.hasCaoStatus(AuditStatus.Submitted)) {
								// Submitted
								if (permissions.hasPermission(OpPerms.ContractorSafety) || permissions.isAdmin()) {
									Integer conAuditID = conAudit.getId();
									String text = getText("ContractorWidget.message.OpenRequirements", new Object[] {
											conAuditID, auditName, showAuditFor, auditFor });
									if (!openReq) {
										text += "<br/>" + getText("ContractorWidget.message.OpenRequirementsNote");
										openReq = true;
									}
									openTasks.add(text);
								}
							} else {
								// Pending
								if (permissions.hasPermission(OpPerms.ContractorSafety) || permissions.isAdmin()) {
									String text = "";
									if (conAudit.getAuditType().getId() == AuditType.OFFICE
											&& conAudit.getScheduledDate() == null) {
										text = getText("ContractorWidget.message.ScheduleYourImplementationAudit",
												new Object[] { conAudit.getId(), auditName, showAuditFor, auditFor });
									} else {
										Integer showScheduledDate = (conAudit.getScheduledDate() != null) ? 1 : 0;
										Integer showAuditor = (conAudit.getAuditor() != null) ? 1 : 0;
										if (conAudit.getAuditType().getId() == AuditType.DESKTOP) {
											text = getText(
													"ContractorWidget.message.UpcomingAuditConductedBy",
													new Object[] {
															conAudit.getId(),
															auditName,
															showAuditor,
															(conAudit.getAuditor() != null) ? conAudit.getAuditor()
																	.getName() : "", showScheduledDate,
															conAudit.getScheduledDate() });
										} else {
											text = getText("ContractorWidget.message.PrepareForAnUpcomingAudit",
													new Object[] {
															conAudit.getId(),
															auditName,
															showAuditFor,
															auditFor,
															showScheduledDate,
															conAudit.getScheduledDate(),
															showAuditor,
															(conAudit.getAuditor() != null) ? conAudit.getAuditor()
																	.getName() : "" });
										}
									}
									openTasks.add(text);
								}
							}
						} else if (conAudit.getAuditType().isCanContractorEdit()) {
							if (permissions.hasPermission(OpPerms.ContractorSafety) || permissions.isAdmin()) {
								openTasks.add(getText("ContractorWidget.message.CompleteAndSubmitAudit", new Object[] {
										conAudit.getId(), auditName, showAuditFor, auditFor }));
							}
						}
					}
				}
			}

			if (permissions.hasPermission(OpPerms.ContractorSafety) || permissions.isAdmin()) {
				if (!contractor.isNaicsValid() && contractor.getCountries().contains("US")) {
					AuditCatData auditCatData = getAuditCatData(contractor);
					if (auditCatData != null)
						openTasks.add(getText("ContractorWidget.message.UpdateNAICSCode", new Object[] {
								auditCatData.getAudit().getId(), auditCatData.getId() }));
				}

				if (contractor.getWebcam() != null && contractor.getWebcam().getTrackingNumber().trim().length() > 0) {
					openTasks.add(getText("ContractorWidget.message.WebcamHasShipped", new Object[] { contractor
							.getWebcam().getTrackingNumber() }));
				}
			}

			// OQ: Add unmapped employees
			List<AssessmentResultStage> staged = testDAO.findStagedByAccount(contractor.getId());
			if (staged.size() > 0) {
				boolean unmapped = false;

				for (AssessmentResultStage stage : staged) {
					if (stage.getPicsEmployee() == null) {
						unmapped = true;
						break;
					}
				}

				if (unmapped)
					if (permissions.hasPermission(OpPerms.ContractorSafety) || permissions.isAdmin()) {
						openTasks.add(getText("ContractorWidget.message.AssessmentResultsNeedMatching"));
					}
			}
			
			// check if trades need review
			if (contractor.isNeedsTradesUpdated()) {
				openTasks.add(getText("ContractorWidget.message.NeedsTradesUpdated", new Object[] {contractor.getId()}));
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
					if (auditCatData.getCategory().getId() == 2 && auditCatData.isApplies())
						return auditCatData;
				}
			}
		}
		return null;
	}

	public boolean getHasUnpaidInvoices() {
		for (Invoice invoice : contractor.getInvoices()) {
			if (invoice.getStatus().isUnpaid())
				return true;
		}
		return false;
	}

	// Will return the earliest unpaid invoice with the assumption that
	// is the one we want to display on con_stats.jsp
	public Date getChargedOn() {
		try {
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
			Date d = format.parse("12/31/4000");
			for (Invoice invoice : contractor.getInvoices()) {
				if (invoice.getStatus().isUnpaid()) {
					Date d1 = invoice.getDueDate();
					if (d1.before(d))
						d = d1;
				}
			}
			return d;
		} catch (Exception ignoreFormattingErrors) {
		}
		return new Date();
	}

	public BrainTreeService.CreditCard getCreditCard() {
		if (creditCard == null) {
			try {
				BrainTreeService bt = new BrainTreeService();
				bt.setCanadaProcessorID(appPropDAO.find("brainTree.processor_id.canada").getValue());
				bt.setUsProcessorID(appPropDAO.find("brainTree.processor_id.us").getValue());
				bt.setUserName(appPropDAO.find("brainTree.username").getValue());
				bt.setPassword(appPropDAO.find("brainTree.password").getValue());

				creditCard = bt.getCreditCard(id);
			} catch (Exception itllJustStayNull) {
			}
		}
		return creditCard;
	}

	public List<BasicDynaBean> getSuggestedOperators() {
		try {
			return SmartFacilitySuggest.getSimilarOperators(contractor, 5);
		} catch (SQLException e) {
			return null;
		}
	}
}
