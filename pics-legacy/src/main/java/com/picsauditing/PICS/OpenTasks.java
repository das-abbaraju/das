package com.picsauditing.PICS;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings("serial")
public class OpenTasks extends TranslationActionSupport {

	@Autowired
	protected ContractorAuditDAO contractorAuditDao;
	@Autowired
	protected OperatorTagDAO operatorTagDao;
	@Autowired
	private FeatureToggle featureToggleChecker;

	private static final int VOPAK_OPERATOR_QUALIFICATION = 640;

	private boolean hasImportPQF = false;
	private boolean importPQFComplete = false;
	private boolean openReq = false;
	private ArrayList<String> openTasks;
	private ContractorAccount contractor;
	private User user;
	// TODO We should convert this and build all the tasks off a user instead of a permission
	private Permissions permissions;
	private final Logger logger = LoggerFactory.getLogger(OpenTasks.class);

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
		// user.getAccount().isOperatorCorporate()
		if (!permissions.isOperatorCorporate()) {
			gatherTasksAboutRelationshipBetweenContractorAndPics();
		}
		gatherTasksAboutUploadingPqf(); // sets hasImportPQF, importPQFComplete
		if (!permissions.isOperatorCorporate()) {
			gatherTasksAboutBillingAndPayments();
		}
		gatherTasksAboutAudits(); // uses hasImportPQF, importPQFComplete

		if (!permissions.isOperatorCorporate()) {
			gatherTasksAboutWebCamShipments();
			gatherTasksAboutOperatorQualification();
		}

		gatherTasksAboutMarketing();

		vopakSpecificOperatorQualificationTag();

		return openTasks;
	}

	public List<String> getOpenTasksEmail(ContractorAccount contractor, User user) {
		initializeForGatheringTasks(contractor, user);
		gatherTasksAboutDeclaringTradesEmail();
		if (!permissions.isOperatorCorporate()) {
			gatherTasksAboutRelationshipBetweenContractorAndPicsEmail();
		}

		gatherTasksAboutUploadingPqfEmail(); // sets hasImportPQF,
		// importPQFComplete
		if (!permissions.isOperatorCorporate()) {
			gatherTasksAboutBillingAndPaymentsEmail();
		}

		gatherTasksAboutAuditsEmail(); // uses hasImportPQF, importPQFComplete

		if (!permissions.isOperatorCorporate()) {
			gatherTasksAboutWebCamShipmentsEmail();
			gatherTasksAboutOperatorQualificationEmail();
		}

		vopakSpecificOperatorQualificationTagEmail();

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
		ActionContext context = ActionContext.getContext();

		if (context != null && context.getSession() != null) {
			permissions = (Permissions) context.getSession().get("permissions");
		}

		if (permissions == null) {
			permissions = new Permissions();

			if (user != null) {
				permissions.login(user);
			}
		}

		permissions.setLocale(supportedLanguages.getClosestVisibleLocale(permissions.getLocale()));
	}

	private void gatherTasksAboutDeclaringTrades() {
		gatherTasksAboutDeclaringTrades(getLocaleStatic());
	}

	private void gatherTasksAboutDeclaringTradesEmail() {
		gatherTasksAboutDeclaringTrades(contractor.getLocale());
	}

	private void gatherTasksAboutDeclaringTrades(Locale locale) {
		// check if trades need review
		if (contractor.getTrades().size() == 0) {
			openTasks
					.add(getTextParameterized(locale, "ContractorWidget.message.NoTradesSelected", contractor.getId()));
		} else if (contractor.isNeedsTradesUpdated()) {
			openTasks.add(getTextParameterized(locale, "ContractorWidget.message.NeedsTradesUpdated",
					contractor.getId()));
		}
	}

	private void gatherTasksAboutRelationshipBetweenContractorAndPics() {
		gatherTasksAboutRelationshipBetweenContractorAndPics(getLocaleStatic());
	}

	private void gatherTasksAboutRelationshipBetweenContractorAndPicsEmail() {
		gatherTasksAboutRelationshipBetweenContractorAndPics(contractor.getLocale());
	}

	private void gatherTasksAboutRelationshipBetweenContractorAndPics(Locale locale) {
		if (mustApproveUpdatedAgreement()) {
			openTasks
					.add(getTextParameterized(locale, "ContractorWidget.message.UpdatedAgreement", contractor.getId()));
		}
		if (permissions.hasPermission(OpPerms.ContractorAdmin) || user.getAccount().isAdmin()) {
			if (contractor.getUsers().size() == 1 && !contractor.getSoleProprietor()
					&& DateBean.getDateDifference(contractor.getCreationDate()) > -180) {
				openTasks.add(getText(locale, "ContractorWidget.message.RequiresTwoUsers"));
			}

			if (contractor.getAccountLevel().isBidOnly()) {
				openTasks.add(getTextParameterized(locale, "ContractorWidget.message.BidOnlyUpdgrade",
						contractor.getPaymentExpires(), contractor.getId()));
			}
		}
	}

	private boolean mustApproveUpdatedAgreement() {
		return !contractor.isAgreementInEffect()
				&& (permissions.hasPermission(OpPerms.ContractorBilling)
				|| permissions.hasPermission(OpPerms.ContractorAdmin) || permissions
				.hasPermission(OpPerms.ContractorSafety));
	}

	private void gatherTasksAboutUploadingPqf() {
		gatherTasksAboutUploadingPqfEmail(getLocaleStatic());
	}

	private void gatherTasksAboutUploadingPqfEmail() {
		gatherTasksAboutUploadingPqfEmail(contractor.getLocale());
	}

	private void gatherTasksAboutUploadingPqfEmail(Locale locale) {
		for (ContractorAudit audit : contractor.getAudits()) {
			if (auditIsVisibleUnexpiredForImportPqf(audit)) {
				// there were no braces for this next if. I am adding them to
				// preserve behavior, but make it explicit
				if (audit.hasCaoStatusBefore(AuditStatus.Submitted)) {
					openTasks.add(getTextParameterized(locale, "ContractorWidget.message.ImportAndSubmitPQF",
							audit.getId()));
				}
				hasImportPQF = true;
				importPQFComplete = audit.hasCaoStatus(AuditStatus.Complete);
			}
		}
	}

	private boolean auditIsVisibleUnexpiredForImportPqf(ContractorAudit audit) {
		return audit.isVisibleTo(permissions)
				&& (audit.getAuditType().getId() == AuditType.IMPORT_PQF && !audit.isExpired());
	}

	private void gatherTasksAboutBillingAndPayments() {
		gatherTasksAboutBillingAndPaymentsEmail(getLocaleStatic());
	}

	private void gatherTasksAboutBillingAndPaymentsEmail() {
		gatherTasksAboutBillingAndPaymentsEmail(contractor.getLocale());
	}

	private void gatherTasksAboutBillingAndPaymentsEmail(Locale locale) {
		if (permissions.hasPermission(OpPerms.ContractorBilling) || user.getAccount().isAdmin()) {
			BillingStatus billingStatus = contractor.getBillingStatus();
			if (billingStatus.isUpgrade() || (billingStatus.isRenewal() && contractor.getAccountLevel().isBidOnly())) {
				openTasks.add(getTextParameterized(locale,
						"ContractorWidget.message.GenerateInvoice" + ((user.getAccount().isAdmin()) ? ".IsAdmin" : ""),
						contractor.getId()));
			}

			if (contractor.getBalance().compareTo(BigDecimal.ZERO) > 0) {
				for (Invoice invoice : contractor.getInvoices()) {
					if (invoice.getStatus().isUnpaid()) {
						openTasks.add(getTextParameterized(locale, "ContractorWidget.message.OpenInvoiceReminder"
								+ ((user.getAccount().isAdmin() ? ".IsAdmin" : "")), invoice.getId(),
								invoice.getBalance(), invoice.getDueDate(), invoice.getCurrency().getSymbol()));
					}
				}
			}

			if (!contractor.isPaymentMethodStatusValid() && contractor.isMustPayB()) {
				openTasks.add(getTextParameterized(locale, "ContractorWidget.message.UpdatePaymentMethod"
						+ ((user.getAccount().isAdmin() ? ".IsAdmin" : "")), contractor.getId()));
			}
		}
	}

	private void gatherTasksAboutAudits() {
		gatherTasksAboutAuditsEmail(getLocaleStatic());
	}

	private void gatherTasksAboutAuditsEmail() {
		gatherTasksAboutAuditsEmail(contractor.getLocale());
	}

	private void gatherTasksAboutAuditsEmail(Locale locale) {
		List<String> auditsWithOpenTasks = new ArrayList<String>();

		for (ContractorAudit conAudit : contractor.getAudits()) {

			String auditKey = getAuditKey(conAudit);
			boolean isAuditRepeated = auditsWithOpenTasks.contains(auditKey);

			if (!isAuditRepeated) {
				if (conAudit.isVisibleTo(permissions)) {
					if (conAudit.getAuditType().isCanContractorView() && !conAudit.isExpired()) {
						if (isOpenTaskNeeded(conAudit, user, permissions)) {
							boolean addedOpenTask = addAuditOpenTasks(conAudit, locale);
							if (addedOpenTask) {
								auditsWithOpenTasks.add(auditKey);
							}
						}
					}
				}
			}
		}
	}

	private String getAuditKey(ContractorAudit conAudit) {
		String key = conAudit.getAuditType().getId()
				+ StringUtils.defaultIfEmpty(conAudit.getAuditFor(), "")
				+ ((conAudit.getAuditType().getClassType().isPolicy()) ? conAudit.getId() : "")
				+ ((conAudit.getEmployee() != null) ? conAudit.getEmployee().getId() : "");
		return key;
	}

	private void gatherTasksAboutMarketing() {
		if (isLcCorTaskNeeded()) {
			openTasks.add(getTextParameterized("ContractorWidget.message.LcCor."
					+ contractor.getLcCorPhase().toString(), contractor.getId()));
		}
	}

	private boolean isLcCorTaskNeeded() {
		featureToggleChecker.addToggleVariable("contractor", contractor);
		if (!featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_LCCOR))
			return false;
		if (contractor.getLcCorPhase() != null && !contractor.getLcCorPhase().equals(LcCorPhase.Done)) {
			if (contractor.getLcCorNotification() != null && (new Date()).after(contractor.getLcCorNotification())) {
				return true;
			}
		}

		return false;
	}

	@Deprecated
	private void gatherTasksAboutWebCamShipments() {
		gatherTasksAboutWebCamShipments(getLocaleStatic());
	}

	@Deprecated
	private void gatherTasksAboutWebCamShipmentsEmail() {
		gatherTasksAboutWebCamShipments(contractor.getLocale());
	}

	@Deprecated
	private void gatherTasksAboutWebCamShipments(Locale locale) {
		if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()) {
			if (contractor.getWebcam() != null && contractor.getWebcam().getTrackingNumber() != null
					&& contractor.getWebcam().getTrackingNumber().trim().length() > 0) {

				String carrier = contractor.getWebcam().getCarrier();

				if (carrier != null) {
					if (carrier.equals("FedEx")) {
						openTasks.add(getTextParameterized(locale, "ContractorWidget.message.WebcamHasShippedFedEx",
								contractor.getWebcam().getTrackingNumber()));
					} else if (carrier.equals("Purolator")) {
						openTasks.add(getTextParameterized(locale,
								"ContractorWidget.message.WebcamHasShippedPurolator", contractor.getWebcam()
								.getTrackingNumber()));
					} else {
						openTasks.add(getText(locale, "ContractorWidget.message.WebcamHasShippedGeneric"));
					}
				}
			}
		}
	}

	private void gatherTasksAboutOperatorQualification() {
		gatherTasksAboutOperatorQualificationEmail(getLocaleStatic());
	}

	private void gatherTasksAboutOperatorQualificationEmail() {
		gatherTasksAboutOperatorQualificationEmail(contractor.getLocale());
	}

	private void gatherTasksAboutOperatorQualificationEmail(Locale locale) {
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

				if (unmapped) {
					if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()) {
						openTasks.add(getText(locale, "ContractorWidget.message.AssessmentResultsNeedMatching"));
					}
				}
			}
		}
	}

	private void vopakSpecificOperatorQualificationTag() {
		vopakSpecificOperatorQualificationTagEmail(getLocaleStatic());
	}

	private void vopakSpecificOperatorQualificationTagEmail() {
		vopakSpecificOperatorQualificationTagEmail(contractor.getLocale());
	}

	private void vopakSpecificOperatorQualificationTagEmail(Locale locale) {
		OperatorTag vopakCorporateOperatorQualification = operatorTagDao.find(VOPAK_OPERATOR_QUALIFICATION);

		for (ContractorTag contractorTag : contractor.getOperatorTags()) {
			if (contractorTag.getTag().equals(vopakCorporateOperatorQualification)
					&& contractor.getEmployees().isEmpty()) {
				openTasks.add(getTextParameterized(locale,
						"ContractorWidget.message.OpenRequirementsEmployeeGuard.Vopak", contractor.getId()));
				break;
			}
		}
	}

	private boolean addAuditOpenTasks(ContractorAudit conAudit, Locale locale) {
		boolean addedOpenTask = false;
		openReq = false;
		String auditName = getText(conAudit.getAuditType().getI18nKey("name"));
		String auditFor = conAudit.getAuditFor();
		Object showAuditFor = (!Strings.isEmpty(auditFor)) ? 1 : 0;

		if (conAudit.getAuditType().getClassType().isPolicy()) {
			if (permissions.hasPermission(OpPerms.ContractorInsurance) || user.getAccount().isAdmin()
					|| (conAudit.isVisibleTo(permissions) && permissions.isOperatorCorporate())) {
				if (conAudit.hasCaoStatus(AuditStatus.Incomplete)) {
					if (conAudit.getAuditType().isWCB()) {
						openTasks.add(getTextParameterized(locale, "ContractorWidget.message.FixWCBIssues",
								conAudit.getId(), auditName, showAuditFor, auditFor));
					} else {
						openTasks.add(getTextParameterized(locale, "ContractorWidget.message.FixPolicyIssues",
								conAudit.getId(), auditName, 1, getPolicyAuditFor(conAudit)));
					}
					addedOpenTask = true;
				} else {
					if (conAudit.getAuditType().isWCB()) {
						openTasks.add(getTextParameterized(locale, "ContractorWidget.message.UploadAndSubmitWCB",
								conAudit.getId(), auditName, showAuditFor, auditFor));
					} else {
						openTasks.add(getTextParameterized(locale, "ContractorWidget.message.UploadAndSubmitPolicy",
								conAudit.getId(), auditName, 1, getPolicyAuditFor(conAudit)));
					}
					addedOpenTask = true;
				}
			}
		} else if (conAudit.getAuditType().isRenewable() && conAudit.isAboutToExpire()) {
			if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()
					|| (conAudit.isVisibleTo(permissions) && permissions.isOperatorCorporate())) {
				openTasks.add(getTextParameterized(locale, "ContractorWidget.message.ResubmitPolicy", conAudit.getId(),
						auditName, showAuditFor, auditFor));
				addedOpenTask = true;
			}
		} else if (conAudit.getAuditType().getWorkFlow().isHasRequirements()
				&& (conAudit.getAuditType().getId() != AuditType.WA_STATE_VERIFICATION || (conAudit.getAuditType()
				.getId() == AuditType.WA_STATE_VERIFICATION && conAudit.hasCaoStatusAfter(AuditStatus.Pending)))
				&& (conAudit.getAuditType().getId() != AuditType.SHELL_COMPETENCY_REVIEW)) {
			if (conAudit.hasCaoStatus(AuditStatus.Submitted)) {
				if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()
						|| (conAudit.isVisibleTo(permissions) && permissions.isOperatorCorporate())
						&& !conAudit.getAuditType().isCorIecWaState()) {
					Integer conAuditID = conAudit.getId();
					String text = "";
					if (conAudit.getAuditType().getClassType().isEmployee()) {
						Calendar effectiveLabel = Calendar.getInstance();
						effectiveLabel.setTime(conAudit.getEffectiveDateLabel());
						text = getTextParameterized(locale, "ContractorWidget.message.OpenRequirementsEmployeeGuard2",
								contractor.getId(), conAuditID, auditName, showAuditFor, auditFor, conAudit
								.getAuditType().getId(), effectiveLabel.get(Calendar.YEAR));
					} else {
						text = getTextParameterized(locale, "ContractorWidget.message.OpenRequirements", conAuditID,
								auditName, showAuditFor, auditFor);
					}

					if (!openReq) {
						text += "<br/>" + getText(locale, "ContractorWidget.message.OpenRequirementsNote");
						openReq = true;
						if (conAudit.getAuditType().isDesktop()) {
							text += "<br/>" + getText(locale, "ContractorWidget.message.ManualAuditNote");
						}
					}
					openTasks.add(text);
					addedOpenTask = true;
				}
			} else if (conAudit.hasCaoStatus(AuditStatus.Pending)) {
				if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()
						|| (conAudit.isVisibleTo(permissions) && permissions.isOperatorCorporate())) {
					String text = "";
					if (conAudit.getAuditType().getId() == AuditType.IMPLEMENTATION_AUDIT && conAudit.getScheduledDate() == null) {
						text = getTextParameterized(locale, "ContractorWidget.message.ScheduleYourImplementationAudit",
								conAudit.getId(), auditName, showAuditFor, auditFor);
					} else {
						Integer showScheduledDate = (conAudit.getScheduledDate() != null) ? 1 : 0;
						Integer showAuditor = (conAudit.getAuditor() != null) ? 1 : 0;
						if (conAudit.getAuditType().getId() == AuditType.MANUAL_AUDIT) {
							text = getTextParameterized(locale, "ContractorWidget.message.UpcomingAuditConductedBy",

									conAudit.getId(), auditName, showAuditor, (conAudit.getAuditor() != null) ? conAudit
									.getAuditor().getName() : "", showScheduledDate, conAudit.getScheduledDate());
						} else if (conAudit.getAuditType().getId() == AuditType.COR) {
							text = getTextParameterized(locale, "ContractorWidget.message.CompleteAndSubmitAudit",
									conAudit.getId(), auditName, showAuditFor, auditFor);
							if (!isPreviousValidCorAuditExists(conAudit)) {
								text += "<br/>"
										+ getTextParameterized(locale, "ContractorWidget.message.ReviewCORNote",
										conAudit.getCreationDate());
							}
						} else {
							String i18nKey = "ContractorWidget.message." + AuditStatus.Pending + "." + conAudit.getAuditType().getId();
							if (hasKey(i18nKey)) {
								text = getTextParameterized(locale, i18nKey,
										conAudit.getId(), auditName, showAuditFor, auditFor, showScheduledDate,
										conAudit.getScheduledDate(), showAuditor,
										(conAudit.getAuditor() != null) ? conAudit.getAuditor().getName() : "");

							} else {
								text = getTextParameterized(locale, "ContractorWidget.message.PrepareForAnUpcomingAudit",

										conAudit.getId(), auditName, showAuditFor, auditFor, showScheduledDate,
										conAudit.getScheduledDate(), showAuditor,
										(conAudit.getAuditor() != null) ? conAudit.getAuditor().getName() : "");
							}
							if (conAudit.getAuditType().isImplementation()) {
								text += "<br/>" + getText(locale, "ContractorWidget.message.ImplementationAuditNote");
							}
						}
					}
					openTasks.add(text);
					addedOpenTask = true;
				}
			} else if (conAudit.hasCaoStatus(AuditStatus.Resubmitted) && conAudit.getAuditType().isCorIecWaState()) {
				if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()
						|| (conAudit.isVisibleTo(permissions) && permissions.isOperatorCorporate())) {
					Integer conAuditID = conAudit.getId();
					String text = "";

					if (conAudit.getAuditType().getClassType().isEmployee()) {
						Calendar effectiveLabel = Calendar.getInstance();
						effectiveLabel.setTime(conAudit.getEffectiveDateLabel());
						text = getTextParameterized(locale, "ContractorWidget.message.OpenRequirementsEmployeeGuard2",
								contractor.getId(), conAuditID, auditName, showAuditFor, auditFor, conAudit
								.getAuditType().getId(), effectiveLabel.get(Calendar.YEAR));
					} else {
						text = getTextParameterized(locale, "ContractorWidget.message.OpenRequirements", conAuditID,
								auditName, showAuditFor, auditFor);
					}
					if (conAudit.getAuditType().getId() == AuditType.COR) {
						text = getTextParameterized(locale, "ContractorWidget.message.OpenRequirementsCOR",
								conAudit.getId(), auditName, showAuditFor, auditFor);
					}
					if (!openReq) {
						text += "<br/>" + getText(locale, "ContractorWidget.message.OpenRequirementsNote");
						openReq = true;
						if (conAudit.getAuditType().isDesktop()) {
							text += "<br/>" + getText(locale, "ContractorWidget.message.ManualAuditNote");
						}
					}
					openTasks.add(text);
					addedOpenTask = true;
				}
			} else if (conAudit.getAuditType().getId() == AuditType.SSIP && conAudit.hasCaoStatus(AuditStatus.Resubmitted)) {
				if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()
						|| (conAudit.isVisibleTo(permissions) && permissions.isOperatorCorporate())) {
					String i18nKey = "ContractorWidget.message." + AuditStatus.Resubmitted + "." + conAudit.getAuditType().getId();
					openTasks.add(getTextParameterized(locale, i18nKey,
							conAudit.getId(), auditName, showAuditFor, auditFor));
					addedOpenTask = true;
				}
			}
		} else if (conAudit.getAuditType().isCanContractorEdit()
				&& conAudit.getAuditType().getId() != AuditType.IMPORT_PQF) {
			if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()
					|| (conAudit.isVisibleTo(permissions) && permissions.isOperatorCorporate())) {
				if (conAudit.getAuditType().isPicsPqf() && hasImportPQF) {
					// Show a message for filling out the rest of the PQF if the
					// Import PQF is COMPLETE
					if (importPQFComplete) {
						openTasks.add(getTextParameterized(locale, "ContractorWidget.message.PQFOtherRegistry",
								conAudit.getId()));
						addedOpenTask = true;
					}
				} else if (conAudit.getAuditType().getId() == AuditType.COR
						&& conAudit.hasCaoStatus(AuditStatus.Submitted)) {
					String text = getTextParameterized(locale, "ContractorWidget.message.ReviewCOR", conAudit.getId(),
							auditName, showAuditFor, auditFor);
					text += "<br/>"
							+ getTextParameterized(locale, "ContractorWidget.message.ReviewCORNote",
							conAudit.getCreationDate());
					openTasks.add(text);
					addedOpenTask = true;
				} else if (conAudit.getAuditType().isAnnualAddendum() && conAudit.hasCaoStatus(AuditStatus.Incomplete)) {
					openTasks.add(getTextParameterized(locale, "ContractorWidget.message.FixAnnualUpdateIssues",
							conAudit.getId(), conAudit.getAuditFor()));
					addedOpenTask = true;
				} else {
					openTasks.add(getTextParameterized(locale, "ContractorWidget.message.CompleteAndSubmitAudit",
							conAudit.getId(), auditName, showAuditFor, auditFor));
					addedOpenTask = true;
				}
			}
		} else if ((conAudit.getAuditType().getId() == AuditType.HSE_COMPETENCY || conAudit.getAuditType().getId() == AuditType.HSE_COMPETENCY_REVIEW)
				&& conAudit.hasCaoStatus(AuditStatus.Resubmit)) {
			Integer conAuditID = conAudit.getId();
			String text = "";
			if (conAudit.getAuditType().getClassType().isEmployee()) {
				text = getTextParameterized(locale, "ContractorWidget.message.OpenRequirementsEmployeeGuard",
						contractor.getId(), conAuditID, auditName, showAuditFor, auditFor);
			} else {
				text = getTextParameterized(locale, "ContractorWidget.message.OpenRequirements", conAuditID, auditName,
						showAuditFor, auditFor);
			}

			if (!openReq) {
				text += "<br/>" + getText(locale, "ContractorWidget.message.OpenRequirementsNote");
				openReq = true;
			}

			openTasks.add(text);
			addedOpenTask = true;
		}

		return addedOpenTask;
	}

	private String getPolicyAuditFor(ContractorAudit conAudit) {
		String policyAuditFor = "";
		if (conAudit.getEffectiveDate() != null || conAudit.getAuditType().isWCB()) {
			String year = DateBean.format(conAudit.getEffectiveDateLabel(), "yy");
			policyAuditFor = " '" + year;
		} else {
			policyAuditFor = " " + getText("ContractorAudit.New");
		}

		return policyAuditFor;
	}

	private boolean isOpenTaskNeeded(ContractorAudit conAudit, User user, Permissions permissions) {
		int needed = 0;

		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.isVisible()) {
				if (permissions.hasPermission(OpPerms.ContractorSafety) || user.getAccount().isAdmin()
						|| cao.isVisibleTo(permissions)) {
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
					}
					if (conAudit.getAuditType().getId() == AuditType.COR || conAudit.getAuditType().getId() == AuditType.IEC_AUDIT) {
						if (conAudit.hasCaoStatus(AuditStatus.Resubmitted)
								|| conAudit.hasCaoStatus(AuditStatus.Incomplete) || conAudit
								.hasCaoStatus(AuditStatus.Pending))
							needed++;
					} else if (conAudit.getAuditType().getWorkFlow().isHasRequirements()) {
						if (conAudit.getAuditType().getId() == AuditType.INTEGRITYMANAGEMENT
								&& cao.getStatus().isPending()) {
							needed++;
						}
						if (conAudit.getAuditType().getId() == AuditType.SSIP) {
							if (conAudit.hasCaoStatus(AuditStatus.Pending) || conAudit.hasCaoStatus(AuditStatus.Resubmitted)) {
								needed++;
							}
						} else if (cao.getStatus().before(AuditStatus.Complete)) {
							needed++;
						}
					}

				}
				if (permissions.hasPermission(OpPerms.ContractorInsurance) || user.getAccount().isAdmin()
						|| cao.isVisibleTo(permissions)) {
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
			} catch (Exception e) {
				logger.error("Error occurred while checking if the date is less than three"
						+ " years ago for audit id = {}", conAudit.getId(), e);
			}
		}

		return false;
	}
}