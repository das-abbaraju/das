package com.picsauditing.PICS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.actions.contractors.ContractorBadge;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AssessmentResultStage;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.LocaleController;

@SuppressWarnings("serial")
public class OpenTasks extends TranslationActionSupport {

	@Autowired
	protected ContractorAuditDAO contractorAuditDao;

	private boolean hasImportPQF = false;
	private boolean importPQFComplete = false;
	private boolean openReq = false;
	private ArrayList<String> openTasks;
	private ContractorAccount contractor;
	private User user;
	private Permissions permissions;

	/**
	 * Gathers Open Tasks, restricting the list based on user permissions and
	 * the viewed contractor account.
	 * 
	 * @param contractor
	 * @param user
	 * @return TODO Eliminate the side-effect of having to call
	 *         gatherTasksAboutUploadingPqf before gatherTasksAboutAudits in
	 *         order to set up the hasImportPQF and importPQFComplete fields.
	 *         Perhaps combine the two methods (as
	 *         gatherTasksAboutAuditsIncludingPqfs).
	 */
	public List<String> getOpenTasks(ContractorAccount contractor, User user) {
		initializeForGatheringTasks(contractor, user);
		gatherTasksAboutDeclaringTrades();
		gatherTasksAboutRelationshipBetweenContractorAndPics();
		gatherTasksAboutUploadingPqf(); // sets hasImportPQF, importPQFComplete
		gatherTasksAboutBillingAndPayments();
		gatherTasksAboutAudits(); // uses hasImportPQF, importPQFComplete
		gatherTasksAboutWebCamShipments();
		gatherTasksAboutOperatorQualification();
		gatherTasksAboutMembershipSeal(contractor);

		return openTasks;
	}

	private void initializeForGatheringTasks(ContractorAccount contractor, User user) {
		this.contractor = contractor;
		this.user = user;
		establishPermissions(user);

		openTasks = new ArrayList<String>();
		hasImportPQF = false;
		importPQFComplete = false;
	}

	private void establishPermissions(User user) {
		permissions = new Permissions();
		try {
			permissions.login(user);
			LocaleController.setLocaleOfNearestSupported(permissions);
		} catch (Exception e) {
			// FIXME
			e.printStackTrace();
		}

	}

	private void gatherTasksAboutDeclaringTrades() {
		// check if trades need review
		if (contractor.getTrades().size() == 0) {
			openTasks.add(getTextParameterized("ContractorWidget.message.NoTradesSelected", contractor.getId()));
		} else if (contractor.isNeedsTradesUpdated()) {
			openTasks.add(getTextParameterized("ContractorWidget.message.NeedsTradesUpdated", contractor.getId()));
		}
	}

	private void gatherTasksAboutRelationshipBetweenContractorAndPics() {
		if (!contractor.isAgreementInEffect()
				&& (permissions.hasPermission(OpPerms.ContractorBilling)
						|| permissions.hasPermission(OpPerms.ContractorAdmin) || permissions
							.hasPermission(OpPerms.ContractorSafety))) {
			openTasks.add(getTextParameterized("ContractorWidget.message.UpdatedAgreement", contractor.getId()));
		}
		if (permissions.hasPermission(OpPerms.ContractorAdmin) || user.getAccount().isAdmin()) {
			if (contractor.getUsers().size() == 1 && !contractor.getSoleProprietor()
					&& DateBean.getDateDifference(contractor.getCreationDate()) > -180) {
				openTasks.add(getText("ContractorWidget.message.RequiresTwoUsers"));
			}

			if (contractor.getAccountLevel().isBidOnly()) {
				openTasks.add(getTextParameterized("ContractorWidget.message.BidOnlyUpdgrade",
						contractor.getPaymentExpires(), contractor.getId()));
			}
		}
	}

	private void gatherTasksAboutUploadingPqf() {
		for (ContractorAudit audit : contractor.getAudits()) {
			if (audit.getAuditType().getId() == AuditType.IMPORT_PQF && !audit.isExpired()) {
				if (audit.hasCaoStatusBefore(AuditStatus.Submitted))
					openTasks.add(getTextParameterized("ContractorWidget.message.ImportAndSubmitPQF", audit.getId()));

				hasImportPQF = true;
				importPQFComplete = audit.hasCaoStatus(AuditStatus.Complete);
			}
		}
	}

	private void gatherTasksAboutBillingAndPayments() {
		if (permissions.hasPermission(OpPerms.ContractorBilling) || user.getAccount().isAdmin()) {
			String billingStatus = contractor.getBillingStatus();
			if ("Upgrade".equals(billingStatus)
					|| ("Renewal".equals(billingStatus) && contractor.getAccountLevel().isBidOnly())) {
				openTasks.add(getTextParameterized(
						"ContractorWidget.message.GenerateInvoice" + ((user.getAccount().isAdmin()) ? ".IsAdmin" : ""),
						contractor.getId()));
			}

			if (contractor.getBalance().compareTo(BigDecimal.ZERO) > 0) {
				for (Invoice invoice : contractor.getInvoices()) {
					if (invoice.getStatus().isUnpaid()) {
						openTasks.add(getTextParameterized("ContractorWidget.message.OpenInvoiceReminder"
								+ ((user.getAccount().isAdmin() ? ".IsAdmin" : "")), invoice.getId(),
								invoice.getBalance(), invoice.getDueDate(), invoice.getCurrency().getSymbol()));
					}
				}
			}

			if (!contractor.isPaymentMethodStatusValid() && contractor.isMustPayB()) {
				openTasks.add(getTextParameterized("ContractorWidget.message.UpdatePaymentMethod"
						+ ((user.getAccount().isAdmin() ? ".IsAdmin" : "")), contractor.getId()));
			}
		}
	}

	private void gatherTasksAboutAudits() {
		for (ContractorAudit conAudit : contractor.getAudits()) {
			if (conAudit.getAuditType().isCanContractorView() && !conAudit.isExpired()) {
				if (isOpenTaskNeeded(conAudit, user, permissions)) {
					addAuditOpenTasks(conAudit);
				}
			}
		}
	}

	private void gatherTasksAboutWebCamShipments() {
		if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()) {
			if (contractor.getWebcam() != null && contractor.getWebcam().getTrackingNumber() != null
					&& contractor.getWebcam().getTrackingNumber().trim().length() > 0) {

				String carrier = contractor.getWebcam().getCarrier();

				if (carrier != null) {
					if (carrier.equals("FedEx")) {
						openTasks.add(getTextParameterized("ContractorWidget.message.WebcamHasShippedFedEx", contractor
								.getWebcam().getTrackingNumber()));
					} else if (carrier.equals("Purolator")) {
						openTasks.add(getTextParameterized("ContractorWidget.message.WebcamHasShippedPurolator",
								contractor.getWebcam().getTrackingNumber()));
					} else {
						openTasks.add(getText("ContractorWidget.message.WebcamHasShippedGeneric"));
					}
				}
			}
		}
	}

	private void gatherTasksAboutOperatorQualification() {
		// OQ: Add unmapped employees
		if (contractor.isRequiresOQ()) {
			if (contractor.getAssessmentResultStages().size() > 0) {
				boolean unmapped = false;

				for (AssessmentResultStage stage : contractor.getAssessmentResultStages()) {
					if (stage.getPicsEmployee() == null) {
						unmapped = true;
						break;
					}
				}

				if (unmapped)
					if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()) {
						openTasks.add(getText("ContractorWidget.message.AssessmentResultsNeedMatching"));
					}
			}
		}
	}

	private void gatherTasksAboutMembershipSeal(ContractorAccount contractor) {
		if (!hasPQFMembershipSealAnswer(contractor)) {
			openTasks.add(getTextParameterized("ContractorWidget.PicsMembershipSeal",
					String.valueOf(contractor.getId())));
		}
	}

	private void addAuditOpenTasks(ContractorAudit conAudit) {
		openReq = false;
		String auditName = getText(conAudit.getAuditType().getI18nKey("name"));
		Object showAuditFor = (conAudit.getAuditFor() != null && !conAudit.getAuditFor().isEmpty()) ? 1 : 0;
		String auditFor = conAudit.getAuditFor();

		if (conAudit.getAuditType().getClassType().isPolicy()) {
			if (permissions.hasPermission(OpPerms.ContractorInsurance) || user.getAccount().isAdmin()) {
				if (conAudit.hasCaoStatus(AuditStatus.Incomplete)) {
					openTasks.add(getTextParameterized("ContractorWidget.message.FixPolicyIssues", conAudit.getId(),
							auditName));
				} else {
					openTasks.add(getTextParameterized("ContractorWidget.message.UploadAndSubmitPolicy",
							conAudit.getId(), auditName));
				}
			}
		} else if (conAudit.getAuditType().isRenewable() && conAudit.isAboutToExpire()) {
			if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()) {
				openTasks.add(getTextParameterized("ContractorWidget.message.ResubmitPolicy", conAudit.getId(),
						auditName, showAuditFor, auditFor));
			}
		} else if (conAudit.getAuditType().getWorkFlow().isHasRequirements()
				&& (conAudit.getAuditType().getId() != AuditType.WA_STATE_VERIFICATION || (conAudit.getAuditType()
						.getId() == AuditType.WA_STATE_VERIFICATION && conAudit.hasCaoStatusAfter(AuditStatus.Pending)))
				&& (conAudit.getAuditType().getId() != AuditType.SHELL_COMPETENCY_REVIEW)) {
			if (conAudit.hasCaoStatus(AuditStatus.Submitted)) {
				if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()
						&& !conAudit.getAuditType().isCorIecWaState()) {
					Integer conAuditID = conAudit.getId();
					String text = getTextParameterized("ContractorWidget.message.OpenRequirements", contractor.getId(),
							conAuditID, auditName, showAuditFor, auditFor);

					if (!openReq) {
						text += "<br/>" + getText("ContractorWidget.message.OpenRequirementsNote");
						openReq = true;
						if (conAudit.getAuditType().isDesktop()) {
							text += "<br/>" + getText("ContractorWidget.message.ManualAuditNote");
						}
					}
					openTasks.add(text);
				}
			} else if (conAudit.hasCaoStatus(AuditStatus.Pending)) {
				if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()) {
					String text = "";
					if (conAudit.getAuditType().getId() == AuditType.OFFICE && conAudit.getScheduledDate() == null) {
						text = getTextParameterized("ContractorWidget.message.ScheduleYourImplementationAudit",
								conAudit.getId(), auditName, showAuditFor, auditFor);
					} else {
						Integer showScheduledDate = (conAudit.getScheduledDate() != null) ? 1 : 0;
						Integer showAuditor = (conAudit.getAuditor() != null) ? 1 : 0;
						if (conAudit.getAuditType().getId() == AuditType.DESKTOP) {
							text = getTextParameterized("ContractorWidget.message.UpcomingAuditConductedBy",

							conAudit.getId(), auditName, showAuditor, (conAudit.getAuditor() != null) ? conAudit
									.getAuditor().getName() : "", showScheduledDate, conAudit.getScheduledDate());
						} else if (conAudit.getAuditType().getId() == AuditType.COR) {
							text = getTextParameterized("ContractorWidget.message.CompleteAndSubmitAudit",
									conAudit.getId(), auditName, showAuditFor, auditFor);
							if (!isPreviousValidCorAuditExists(conAudit)) {
								text += "<br/>"
										+ getTextParameterized("ContractorWidget.message.ReviewCORNote",
												conAudit.getCreationDate());
							}
						} else {
							text = getTextParameterized("ContractorWidget.message.PrepareForAnUpcomingAudit",

							conAudit.getId(), auditName, showAuditFor, auditFor, showScheduledDate,
									conAudit.getScheduledDate(), showAuditor,
									(conAudit.getAuditor() != null) ? conAudit.getAuditor().getName() : "");
							if (conAudit.getAuditType().isImplementation()) {
								text += "<br/>" + getText("ContractorWidget.message.ImplementationAuditNote");
							}
						}
					}
					openTasks.add(text);
				}
			} else if (conAudit.hasCaoStatus(AuditStatus.Resubmitted) && conAudit.getAuditType().isCorIecWaState()) {
				if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()) {
					Integer conAuditID = conAudit.getId();
					String text = getTextParameterized("ContractorWidget.message.OpenRequirements", contractor.getId(), conAuditID,
							auditName, showAuditFor, auditFor);
					if (conAudit.getAuditType().getId() == AuditType.COR) {
						text = getTextParameterized("ContractorWidget.message.OpenRequirementsCOR", conAudit.getId(),
								auditName, showAuditFor, auditFor);
					}
					if (!openReq) {
						text += "<br/>" + getText("ContractorWidget.message.OpenRequirementsNote");
						openReq = true;
						if (conAudit.getAuditType().isDesktop()) {
							text += "<br/>" + getText("ContractorWidget.message.ManualAuditNote");
						}
					}
					openTasks.add(text);
				}
			}
		} else if (conAudit.getAuditType().isCanContractorEdit()
				&& conAudit.getAuditType().getId() != AuditType.IMPORT_PQF) {
			if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()) {
				if (conAudit.getAuditType().isPqf() && hasImportPQF) {
					// Show a message for filling out the rest of the PQF if the
					// Import PQF is COMPLETE
					if (importPQFComplete) {
						openTasks.add(getTextParameterized("ContractorWidget.message.PQFOtherRegistry",
								conAudit.getId()));
					}
				} else if (conAudit.getAuditType().getId() == AuditType.COR
						&& conAudit.hasCaoStatus(AuditStatus.Submitted)) {
					String text = getTextParameterized("ContractorWidget.message.ReviewCOR", conAudit.getId(),
							auditName, showAuditFor, auditFor);
					text += "<br/>"
							+ getTextParameterized("ContractorWidget.message.ReviewCORNote", conAudit.getCreationDate());
					openTasks.add(text);
				} else {
					openTasks.add(getTextParameterized("ContractorWidget.message.CompleteAndSubmitAudit",
							conAudit.getId(), auditName, showAuditFor, auditFor));
				}
			}
		} else if ((conAudit.getAuditType().getId() == AuditType.HSE_COMPETENCY || conAudit.getAuditType().getId() == AuditType.HSE_COMPETENCY_REVIEW)
				&& conAudit.hasCaoStatus(AuditStatus.Resubmit)) {
			Integer conAuditID = conAudit.getId();
			String text = getTextParameterized("ContractorWidget.message.OpenRequirements", contractor.getId(), conAuditID, auditName,
					showAuditFor, auditFor);
			if (!openReq) {
				text += "<br/>" + getText("ContractorWidget.message.OpenRequirementsNote");
				openReq = true;
			}
			openTasks.add(text);
		}
	}

	private boolean isOpenTaskNeeded(ContractorAudit conAudit, User user, Permissions permissions) {
		int needed = 0;

		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.isVisible()) {
				if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()) {
					if (conAudit.getAuditType().isCanContractorEdit()) {
						// Maybe use conAudit.isAboutToRenew() instead of
						// conAudit.getAuditType().isRenewable() &&
						// conAudit.isAboutToExpire()
						if (conAudit.getAuditType().getId() == AuditType.WA_STATE_VERIFICATION) {
							if (cao.getStatus().before(AuditStatus.Complete)) {
								needed++;
							}
						} else if (cao.getStatus().before(AuditStatus.Submitted)
								|| cao.getStatus() == AuditStatus.Resubmit) {
							needed++;
						}
					} else if (conAudit.getAuditType().getWorkFlow().isHasRequirements()) {
						if (conAudit.getAuditType().getId() == AuditType.INTEGRITYMANAGEMENT
								&& cao.getStatus().isPending()) {
							continue;
						}
						if (cao.getStatus().before(AuditStatus.Complete)) {
							needed++;
						}
					}
					if ((conAudit.getAuditType().getId() == AuditType.COR || conAudit.getAuditType().getId() == AuditType.IEC_AUDIT)
							&& (conAudit.hasCaoStatus(AuditStatus.Resubmitted) || conAudit
									.hasCaoStatus(AuditStatus.Incomplete))) {
						needed++;
					}

				}
				if (permissions.hasPermission(OpPerms.ContractorInsurance) || user.getAccount().isAdmin()) {
					if (cao.getStatus().before(AuditStatus.Submitted)) {
						needed++;
					}
				}
			}
		}
		return needed > 0;
	}

	private boolean isPreviousValidCorAuditExists(ContractorAudit conAudit) {
		for (ContractorAudit audit : conAudit.getContractorAccount().getAudits()) {
			if (audit.getAuditType().getId() == AuditType.COR && audit.getId() != conAudit.getId())
				return true;
		}

		List<ContractorAudit> expiredAudits = contractorAuditDao.findExpiredByContractor(conAudit
				.getContractorAccount().getId());
		for (ContractorAudit audit : expiredAudits) {
			try {
				if (audit.getAuditType().getId() == AuditType.COR
						&& DateBean.isLessThanTheeYearAgo(audit.getExpiresDate()))
					return true;
			} catch (Exception ignore) {
			}
		}
		return false;
	}

	private boolean hasPQFMembershipSealAnswer(ContractorAccount contractor) {
		boolean hasMembershipSealAnswer = false;
		for (ContractorAudit pqf : contractor.getAudits()) {
			if (pqf.getAuditType().isPqf()) {
				for (AuditData data : pqf.getData()) {
					if (data.getQuestion().getId() == ContractorBadge.MEMBERSHIP_TAG_QUESTION) {
						hasMembershipSealAnswer = true;
					}
				}

				break;
			}
		}

		return hasMembershipSealAnswer;
	}
}
