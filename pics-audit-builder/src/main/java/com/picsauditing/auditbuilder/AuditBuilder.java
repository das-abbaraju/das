package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.AuditTypesBuilder.AuditTypeDetail;
import com.picsauditing.auditbuilder.dao.AuditDataDAO;
import com.picsauditing.auditbuilder.dao.ContractorAuditDAO;
import com.picsauditing.auditbuilder.dao.ContractorAuditFileDAO;
import com.picsauditing.auditbuilder.dao.ContractorTagDAO;
import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.service.AuditPeriodService;
import com.picsauditing.auditbuilder.service.AuditService;
import com.picsauditing.auditbuilder.util.DateBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class AuditBuilder {

//	@Autowired
//	private AuditCategoryMatrixDAO auditCatMatrixDAO;
	@Autowired
	private ContractorAuditDAO conAuditDao;
//	@Autowired
//	private ContractorAuditOperatorDAO contractorAuditOperatorDAO;
    @Autowired
    private ContractorAuditFileDAO contractorAuditFileDAO;
//	@Autowired
//	private AuditDecisionTableDAO auditDecisionTableDAO;
	@Autowired
	private ContractorTagDAO contractorTagDAO;
	@Autowired
	private AuditDataDAO auditDataDAO;
	@Autowired
	private AuditTypeRuleCache typeRuleCache;
	@Autowired
	private AuditCategoryRuleCache categoryRuleCache;
//	@Autowired
//	private AuditPercentCalculator auditPercentCalculator;
    @Autowired
    AuditPeriodService auditPeriodService;
//    @Autowired
//    private AuditTypeDAO auditTypeDao;
//
//	private static final Logger logger = LoggerFactory.getLogger(AuditBuilder.class);
//
//	private User systemUser = new User(User.SYSTEM);
    private Date today;
//
//	HashSet<ContractorAuditOperator> caosToMoveToApprove = new HashSet<ContractorAuditOperator>();
//	HashSet<ContractorAuditOperator> caosToMoveToComplete = new HashSet<ContractorAuditOperator>();
//	HashSet<ContractorAuditOperator> caosToMoveToResubmit = new HashSet<ContractorAuditOperator>();
//	HashSet<ContractorAuditOperator> caosToMoveToSubmit = new HashSet<ContractorAuditOperator>();
//
//	private Set<String> yearsForAllWCBs;
//
	public void buildAudits(ContractorAccount contractor) {
		AuditTypesBuilder typesBuilder = new AuditTypesBuilder(typeRuleCache, contractor);
        typesBuilder.setContractorTagDAO(contractorTagDAO);
        typesBuilder.setAuditDataDAO(auditDataDAO);

		Set<AuditTypeDetail> requiredAuditTypeDetails = typesBuilder.calculate();
		Set<AuditType> requiredAuditTypes = new HashSet<>();

		/* Add audits not already there */
		int year = DateBean.getCurrentYear();
		for (AuditTypeDetail detail : requiredAuditTypeDetails) {
			if (!detail.rule.isManuallyAdded()) {
				AuditType auditType = detail.rule.getAuditType();

				if (auditType.getId() == AuditType.WELCOME
						&& !conAuditDao.isNeedsWelcomeCall(contractor.getId())) {
					continue;
				}

				requiredAuditTypes.add(auditType);
				if (auditType.getPeriod().isMonthlyQuarterlyAnnual()) {
					auditType = reconnectAuditType(auditType);
                    addMonthlyQuarterlyYearly(contractor, auditType);
                } else if (AuditService.isAnnualAddendum(auditType)) {
                    auditType = reconnectAuditType(auditType);
                    // not the annual updates must be done in this order to find their previous audit
                    addAnnualUpdate(contractor, year - 3, auditType);
                    addAnnualUpdate(contractor, year - 2, auditType);
                    addAnnualUpdate(contractor, year - 1, auditType);
				} else {
					boolean found = false;
					for (ContractorAudit conAudit : contractor.getAudits()) {
						if (conAudit.getAuditType().equals(auditType)) {
							// We found a matching audit for this requirement
							// Now determine if it will be good enough
							if (auditType.isRenewable()) {
								// This audit should not be renewed but we already
								// have one
								found = true;
							} else if (AuditService.isWCB(auditType)) {
								found = foundCurrentYearWCB(conAudit);
							} else {
								if (!AuditService.isExpired(conAudit) && !AuditService.willExpireSoon(conAudit)) {
									// The audit is still valid for a number of days dependent on its type
									found = true;
								}
							}
						}
					}

					if (!found) {
						auditType = reconnectAuditType(auditType);
						if (AuditService.isWCB(auditType)) {
							createWCBAudits(contractor, auditType);
						} else {
							if (!resetRenewableAudit(contractor, auditType)) {
								ContractorAudit audit = createNewAudit(contractor, auditType);
								conAuditDao.save(audit);
							}
						}
					}
				}
			}
		}

		removeUnneededAudits(contractor, requiredAuditTypes);

		AuditCategoriesBuilder categoriesBuilder = new AuditCategoriesBuilder(categoryRuleCache, contractor);

		/** Generate Categories and CAOs **/
		for (ContractorAudit conAudit : contractor.getAudits()) {
			// We may want to consider only calculating the detail for non-expired Audits
			AuditTypeDetail auditTypeDetail = findDetailForAuditType(requiredAuditTypeDetails, conAudit.getAuditType());
			if (auditTypeDetail == null) {
				/*
				 * This audit is no longer required either because of a rule change or a data change (like removing
				 * operators)
				 */
				// TODO testing updating categories and caos for a manually added audit
			} else {
				Set<AuditCategory> categories = categoriesBuilder.calculate(conAudit, auditTypeDetail.operators);
				if (conAudit.getAuditType().getId() == AuditType.IMPORT_PQF) {
					// Import PQF does not have an audit type detail because it is manually added, and the audit is an
					// exception in that the only CAO is PICS Global. The audit_cat_data need to be generated here. We
					// need all the categories for this audit.
					categories = new HashSet<>(conAudit.getAuditType().getCategories());
				}

				// order dependent in czse of new caos
				fillAuditOperators(conAudit, categoriesBuilder.getCaos());
				fillAuditCategories(conAudit, categories);
			}
		}

		Iterator<ContractorAudit> iterator = contractor.getAudits().iterator();
		while (iterator.hasNext()) {
			ContractorAudit conAudit = iterator.next();
			// checking to see if we still need audit
			if (!conAudit.isManuallyAdded() && !requiredAuditTypes.contains(conAudit.getAuditType())) {
				if (!isValidAudit(conAudit)) {
					// Make sure that the caos' visibility is set correctly
					for (ContractorAuditOperator cao : conAudit.getOperators()) {
						if (cao.isVisible()) {
							cao.setVisible(false);
						}
					}
				}
			}
		}

		conAuditDao.save(contractor);
	}

	private void removeUnneededAudits(ContractorAccount contractor, Set<AuditType> requiredAuditTypes) {
		Iterator<ContractorAudit> iter = contractor.getAudits().iterator();
		while (iter.hasNext()) {
			ContractorAudit conAudit = iter.next();
			if (okToRemoveAudit(conAudit, requiredAuditTypes)) {
				iter.remove();
                contractorAuditFileDAO.removeAllByAuditID(conAudit.getId());
				conAuditDao.remove(conAudit);
			}
		}
	}

	private boolean okToRemoveAudit(ContractorAudit conAudit, Set<AuditType> requiredAuditTypes) {
		if (conAudit.isManuallyAdded()) {
			return false;
		}

		if (requiredAuditTypes.contains(conAudit.getAuditType())) {
			return false;
		}

		// Never delete the PQF or WCB
		if (conAudit.getAuditType().getId() == AuditType.PQF || AuditService.isWCB(conAudit.getAuditType())) {
			return false;
		}

		if (conAudit.getScheduledDate() != null) {
			return false;
		}

		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.getStatus().after(AuditStatus.Pending)) {
				return false;
			}
			else if (cao.getPercentComplete() > 0) {
				return false;
			}
		}

		if (conAudit.getData().size() == 0) {
			return false;
		}

		if (isAPreviousAudit(conAudit)) {
			return false;
		}

		return true;
	}

	private boolean isAPreviousAudit(ContractorAudit conAudit) {
		List<ContractorAudit> subsequentAudits = conAuditDao.findSubsequentAudits(conAudit);
		return !subsequentAudits.isEmpty();
	}

	private void addMonthlyQuarterlyYearly(ContractorAccount contractor, AuditType auditType) {
        List<String> auditFors = auditPeriodService.getAuditForByDate(auditType, getToday());
        List<AuditType> children = auditTypeDao.findWhere("t.parent.id = " + auditType.getId());
        AuditType childAuditType = null;
        if (children.size() > 0) {
            childAuditType = children.get(0);
        }
        for (String auditFor:auditFors) {
            if (auditPeriodService.shouldCreateAudit(contractor.getAudits(), auditType, auditFor, childAuditType)) {
                ContractorAudit audit = new ContractorAudit();
                audit.setContractorAccount(contractor);
                audit.setAuditType(auditType);
                audit.setAuditColumns(systemUser);
                audit.setAuditFor(auditFor);
                audit.setCreationDate(auditPeriodService.getEffectiveDateForMonthlyQuarterlyYearly(auditType, auditFor));
                audit.setEffectiveDate(auditPeriodService.getEffectiveDateForMonthlyQuarterlyYearly(auditType, auditFor));
                audit.setExpiresDate(auditPeriodService.getExpirationDateForMonthlyQuarterlyYearly(auditType, auditFor));
                audit.setPreviousAudit(conAuditDao.findPreviousAudit(audit));
                conAuditDao.save(audit);
                contractor.getAudits().add(audit);
            }
        }
    }

    private boolean resetRenewableAudit(ContractorAccount contractor, AuditType auditType) {
		if (!auditType.isRenewable())
			return false;

		ContractorAudit renewableAudit = conAuditDao.findMostRecentAuditByContractorAuditType(contractor.getId(), auditType.getId());
		if (renewableAudit != null) {
			renewableAudit.setExpiresDate(null);
			for (ContractorAuditOperator cao:renewableAudit.getOperators()) {
				ContractorAuditOperatorWorkflow caow = cao.changeStatus(AuditStatus.Pending, null);
				if (caow != null) {
					caow.setNotes(String.format("Resetting renewable audit to %s", AuditStatus.Pending));
					caow.setCreatedBy(systemUser);
					contractorAuditOperatorDAO.save(caow);
				}
			}
			conAuditDao.save(renewableAudit);
			return true;
		}
		return false;
	}

	private boolean foundCurrentYearWCB(ContractorAudit audit) {
		validateWCBAudit(audit);

		buildSetOfAllWCBYears(audit.getContractorAccount(), audit.getAuditType());
		if (DateBean.isGracePeriodForWCB()) {
			return hasAllWCBsForGracePeriod(audit);
		}

		return yearsForAllWCBs.contains(DateBean.getWCBYear());
	}

//	/**
//	 * Performs validation to make sure that a WCB Audit's auditFor field only contains
//	 * a numeric year value.
//	 *
//	 * @param audit A WCB Audit
//	 */
//	private void validateWCBAudit(ContractorAudit audit) {
//		String auditFor = audit.getAuditFor();
//		if (Strings.isEmpty(auditFor) || !NumberUtils.isDigits(auditFor) || auditFor.length() != 4) {
//			throw new RuntimeException("WCBs must always have an AuditFor that is a 4-digit year.");
//		}
//	}
//
//	private void buildSetOfAllWCBYears(ContractorAccount contractor, AuditType wcbAuditType) {
//		List<ContractorAudit> audits = contractor.getAudits();
//		if (CollectionUtils.isEmpty(audits)) {
//			yearsForAllWCBs = Collections.unmodifiableSet(new HashSet<String>());
//		}
//
//		Set<String> years = new HashSet<String>();
//		for (ContractorAudit audit : audits) {
//			if (isMatchingWCBAudit(audit, wcbAuditType)) {
//				years.add(audit.getAuditFor());
//			}
//		}
//
//		yearsForAllWCBs = Collections.unmodifiableSet(years);
//	}
//
//	private boolean hasAllWCBsForGracePeriod(ContractorAudit audit) {
//		String previousYear = Integer.toString(DateBean.getPreviousWCBYear());
//		String currentWCBYear = DateBean.getWCBYear();
//		return yearsForAllWCBs.contains(previousYear) && yearsForAllWCBs.contains(currentWCBYear);
//	}
//
    public Date getToday() {
        if (today == null)
            return new Date();
        else
            return today;
    }

//    public void setToday(Date today) {
//        this.today = today;
//    }
//
    private void createWCBAudits(ContractorAccount contractor, AuditType auditType) {
		buildSetOfAllWCBYears(contractor, auditType);
		Set<String> yearsForWCBs = getAllYearsForNewWCB();
		if (CollectionUtils.isEmpty(yearsForWCBs)) {
			return;
		}

		for (String year : yearsForWCBs) {
			ContractorAudit audit = createNewAudit(contractor, auditType);
			audit.setAuditFor(year);
			audit.setExpiresDate(DateBean.getWCBExpirationDate(year));
			audit.setPreviousAudit(conAuditDao.findPreviousAudit(audit));
			conAuditDao.save(audit);
		}
	}

//	private Set<String> getAllYearsForNewWCB() {
//		Set<String> years = new HashSet<String>();
//
//		String previousYear = Integer.toString(DateBean.getPreviousWCBYear());
//		if (!yearsForAllWCBs.contains(previousYear) && DateBean.isGracePeriodForWCB()) {
//			years.add(previousYear);
//		}
//
//		String currentWCBYear = DateBean.getWCBYear();
//		if (!yearsForAllWCBs.contains(currentWCBYear)) {
//			years.add(currentWCBYear);
//		}
//
//		return years;
//	}
//
	private ContractorAudit createNewAudit(ContractorAccount contractor, AuditType auditType) {
		ContractorAudit audit = new ContractorAudit();
		audit.setContractorAccount(contractor);
		audit.setAuditType(auditType);
		audit.setAuditColumns(systemUser);
		audit.setPreviousAudit(conAuditDao.findPreviousAudit(audit));
		contractor.getAudits().add(audit);

		return audit;
	}

//	private boolean isMatchingWCBAudit(ContractorAudit audit, AuditType wcbAuditType) {
//		return audit != null
//				&& audit.getAuditType() != null
//				&& AuditType.CANADIAN_PROVINCES.contains(audit.getAuditType().getId())
//				&& (audit.getAuditType().getId() == wcbAuditType.getId());
//	}
//
	private boolean isValidAudit(ContractorAudit conAudit) {
		if (conAudit.getAuditType().getId() != AuditType.COR
				&& conAudit.getAuditType().getId() != AuditType.IEC_AUDIT
				&& conAudit.getAuditType().getId() != AuditType.WELCOME) {
			return false;
		}

		if (conAudit.isExpired()) {
			return false;
		}

		if (conAudit.getAuditType().getId() == AuditType.WELCOME) {
			Calendar date = Calendar.getInstance();
			date.add(Calendar.DATE, -6 * 7); //6 weeks
			if (date.getTime().getTime() > conAudit.getCreationDate().getTime()) {
				conAudit.setExpiresDate(new Date());
			}
			return true;
		}

		int auditQuestionID = (conAudit.getAuditType().getId() == AuditType.COR) ? AuditQuestion.COR
				: AuditQuestion.IEC;

		ContractorAudit pqfAudit = null;
		for (ContractorAudit ca : conAudit.getContractorAccount().getAudits()) {
			if (ca.getAuditType().isPicsPqf()) {
				pqfAudit = ca;
				break;
			}
		}

		if (pqfAudit == null) {
			return false;
		}

		AuditData data = null;
		for (AuditData auditData:pqfAudit.getData()) {
			if (auditData.getQuestion().getId() == auditQuestionID) {
				data = auditData;
				break;
			}
		}

		if (data != null
				&& (!Strings.isEqualNullSafe(data.getAnswer(), "Yes") || !data
						.getQuestion().isVisibleInAudit(pqfAudit))) {
			return false;
		}

		return true;
	}

	private AuditTypeDetail findDetailForAuditType(Set<AuditTypeDetail> requiredAuditTypeDetails, AuditType auditType) {
		for (AuditTypeDetail detail : requiredAuditTypeDetails) {
			if (detail.rule.getAuditType().equals(auditType)) {
				return detail;
			}
		}
		return null;
	}

	private void fillAuditOperators(ContractorAudit conAudit, Map<OperatorAccount, Set<OperatorAccount>> caoMap) {
		HashMap<OperatorAccount, ContractorAuditOperator> previousCaoMap = new HashMap<>();
		caosToMoveToApprove = new HashSet<ContractorAuditOperator>();
		caosToMoveToComplete = new HashSet<ContractorAuditOperator>();
		caosToMoveToResubmit = new HashSet<ContractorAuditOperator>();
		caosToMoveToSubmit = new HashSet<ContractorAuditOperator>();
		HashSet<Integer> operatorsGoingVisibleIds = new HashSet<Integer>();


		// Make sure that the caos' visibility is set correctly
		Set<OperatorAccount> caosToEnsureExist = caoMap.keySet();
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			boolean caoShouldBeVisible = contains(caosToEnsureExist, cao.getOperator());

			if (!cao.isVisible() && caoShouldBeVisible) {
				operatorsGoingVisibleIds.add(cao.getOperator().getId());
			}

			cao.setVisible(caoShouldBeVisible);

			// add to map for comparison later
			for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
				previousCaoMap.put(caop.getOperator(), cao);
			}

			if (!caoShouldBeVisible && cao.getCaoPermissions().size() > 0) {
				// need to remove invisible caos because fillAuditOperatorPermissions only works on visible caos
				fillAuditOperatorPermissions(cao, new HashSet<OperatorAccount>());
				cao.getCaoPermissions().clear();
			}
		}

		// Add CAOs that don't yet exist
		for (OperatorAccount governingBody : caosToEnsureExist) {
			// Now find the existing cao record for this operator (if one exists)
			ContractorAuditOperator cao = null;
			for (ContractorAuditOperator cao2 : conAudit.getOperators()) {
				if (cao2.getOperator().getId() == governingBody.getId()) {
					cao = cao2;
					break;
				}
			}
			if (cao == null) {
				// If we don't have one, then add it
				cao = new ContractorAuditOperator();
				cao.setAudit(conAudit);
				cao.setOperator(governingBody);
				cao.setAuditColumns(systemUser);
				// This is almost always Pending
				AuditStatus firstStatus = conAudit.getAuditType().getWorkFlow().getFirstStep().getNewStatus();
				cao.changeStatus(firstStatus, null);
				conAudit.setLastRecalculation(null);

                cao = contractorAuditOperatorDAO.save(cao);
                conAudit.getOperators().add(cao);
            }

			fillAuditOperatorPermissions(cao, caoMap.get(governingBody));
		}

		// set previous cao on caop
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
				ContractorAuditOperator prevCao = previousCaoMap.get(caop.getOperator());
				if (prevCao != null && cao.getId() != prevCao.getId()) {
					caop.setPreviousCao(prevCao);
					if (prevCao.getStatus().isApproved()
							&& (cao.getStatus().isPending() || operatorsGoingVisibleIds
									.contains(cao.getOperator().getId()))) {
						caosToMoveToApprove.add(cao);
					} else if (prevCao.getStatus().isComplete()
							&& (cao.getStatus().isPending() || operatorsGoingVisibleIds
									.contains(cao.getOperator().getId()))) {
						caosToMoveToComplete.add(cao);
					} else if (prevCao.getStatus().isResubmit()
							&& (cao.getStatus().isPending() || operatorsGoingVisibleIds
									.contains(cao.getOperator().getId()))) {
						caosToMoveToResubmit.add(cao);

					} else if (prevCao.getStatus().isSubmitted()
							&& (cao.getStatus().isPending() || operatorsGoingVisibleIds.contains(cao.getOperator()
									.getId()))) {
						caosToMoveToSubmit.add(cao);
					}

				}
			}
		}

		removeDuplicateCaosFromLists();
		adjustCaoStatus(conAudit);
	}

//	private void removeDuplicateCaosFromLists() {
//		for (ContractorAuditOperator cao : caosToMoveToApprove){
//			caosToMoveToSubmit.remove(cao);
//			caosToMoveToResubmit.remove(cao);
//			caosToMoveToComplete.remove(cao);
//		}
//
//		for (ContractorAuditOperator cao : caosToMoveToComplete){
//			caosToMoveToSubmit.remove(cao);
//			caosToMoveToResubmit.remove(cao);
//		}
//
//		for (ContractorAuditOperator cao : caosToMoveToResubmit){
//			caosToMoveToSubmit.remove(cao);
//		}
//	}
//
//
//	private void adjustCaoStatus(ContractorAudit conAudit) {
//		if (isAuditThatCanAdjustStatus(conAudit)) {
//			if (!caosToMoveToApprove.isEmpty()) {
//				adjustCaosToStatus(conAudit, caosToMoveToApprove, AuditStatus.Approved);
//			}
//			if (!caosToMoveToComplete.isEmpty()) {
//				adjustCaosToStatus(conAudit, caosToMoveToComplete, AuditStatus.Complete);
//			}
//			if (!caosToMoveToResubmit.isEmpty()) {
//				adjustCaosToStatus(conAudit, caosToMoveToResubmit, AuditStatus.Resubmit);
//			}
//			if (!caosToMoveToSubmit.isEmpty()) {
//				adjustCaosToStatus(conAudit, caosToMoveToSubmit, AuditStatus.Submitted);
//			}
//		}
//	}
//
//	private boolean isAuditThatCanAdjustStatus(ContractorAudit conAudit) {
//		return conAudit.getAuditType().isPicsPqf()
//				|| conAudit.getAuditType().getId() == AuditType.INTEGRITYMANAGEMENT
//				|| conAudit.getAuditType().getId() == AuditType.ANNUALADDENDUM;
//	}
//
//	private void adjustCaosToStatus(ContractorAudit conAudit, HashSet<ContractorAuditOperator> caosToChange, AuditStatus status) {
//		Iterator<ContractorAuditOperator> list = caosToChange.iterator();
//		while (list.hasNext()) {
//			ContractorAuditOperator cao = list.next();
//			if (conAudit.getAuditType().isPicsPqf() && status.isComplete() &&
//					!conAudit.pqfIsOkayToChangeCaoStatus(cao)) {
//				continue;
//			}
//			ContractorAuditOperatorWorkflow caow = cao.changeStatus(status, null);
//			if (caow != null) {
//				caow.setNotes(String.format("Changing Status for %s(%d) from %s to %s", conAudit.getAuditType()
//						.getName(), conAudit.getId(), caow.getPreviousStatus(), caow.getStatus()));
//				caow.setCreatedBy(systemUser);
//				contractorAuditOperatorDAO.save(caow);
//			}
//		}
//	}
//
	private void fillAuditCategories(ContractorAudit conAudit, Set<AuditCategory> categoriesNeeded) {
		// We're doing this step first so categories that get added or removed
		// manually can be caught in the next block
		if (conAudit.getAuditType().getId() == AuditType.SHELL_COMPETENCY_REVIEW) {
			List<AuditCategory> requiredCompetencies = auditCatMatrixDAO.findCategoriesForCompetencies(conAudit
					.getContractorAccount().getId());
			categoriesNeeded = new HashSet<AuditCategory>();
			if (conAudit.hasCaoStatus(AuditStatus.Pending)) {
				for (AuditCategory ac : conAudit.getAuditType().getCategories()) {
					if (requiredCompetencies.contains(ac.getTopParent())) {
						categoriesNeeded.add(ac);
					}
				}
			} else {
				// We don't want this audit to be updated
				return;
			}
		}

		boolean hasPendingCaos = auditHasPendingCaos(conAudit);

		for (AuditCatData auditCatData : conAudit.getCategories()) {
			if (auditCatData.getCategory().getParent() == null) {
				/*
				 * per Mina (PICS-2902) only change this to Manual and Implementation Audits changes logic from 'does
				 * not have any pending CAOs' to to 'has at least one submitted or greater cao'
				 */
				if (conAudit.getAuditType().isDesktop() || conAudit.getAuditType().isImplementation()) {
					if (hasAnyCaoStatusAfterIncomplete(conAudit) || auditCatData.isOverride()) {
						if (auditCatData.isApplies()) {
							categoriesNeeded.add(auditCatData.getCategory());
						} else {
							categoriesNeeded.remove(auditCatData.getCategory());
						}
					}
				} else {
					// TODO combine or change logic for all other audits to match Manual/Implementation
					if (!hasPendingCaos || auditCatData.isOverride()) {
						/*
						 * Lock the audit category down...keeping it as it was this is to ensure that we don't add new
						 * categories or remove the existing ones except the override categories for an audit after is
						 * it being submitted
						 */
						if (auditCatData.isApplies()) {
							categoriesNeeded.add(auditCatData.getCategory());
						} else {
							categoriesNeeded.remove(auditCatData.getCategory());
						}
					}
				}
			}
		}

		// Now we have an updated list categoriesNeeded, update the catData
		for (AuditCategory category : conAudit.getAuditType().getCategories()) {

			AuditCatData catData = getCatData(conAudit, category);
			if (catData.isOverride()) {
				// (show/hide) a category with more than one CAO.
			} else {
				boolean categoryApplies = categoriesNeeded.contains(catData.getCategory());
				if (categoryApplies) {
					// Making sure the top level parent applies for
					// subcategories when adding it to the AuditCatData
					categoryApplies = areAllParentsApplicable(categoriesNeeded, catData.getCategory());
				}
				if (categoryApplies != catData.isApplies()) {
					catData.setAuditColumns(systemUser);
					if (catData.getCategory().getId() == 443 && !categoryApplies) {
						logger.warn("Safety Manual category no longer applicable for audit (" + conAudit.getId() + ")");
					}
				}
				catData.setApplies(categoryApplies);
			}
			// Where are we saving the catData??
		}

		// do for audits updated with last minute for "new" audits
		if (conAudit.getCreationDate() == null || conAudit.getCreationDate().getTime() > new Date().getTime() - (60 * 1000L)) {
			auditPercentCalculator.percentCalculateComplete(conAudit, true);
		}
	}

//	protected boolean areAllParentsApplicable(Set<AuditCategory> categoriesNeeded, AuditCategory category) {
//		if (category.getParent() != null) {
//			return areAllParentsApplicable(categoriesNeeded, category.getParent())
//					&& categoriesNeeded.contains(category);
//		} else {
//			return categoriesNeeded.contains(category);
//		}
//	}
//
//	/**
//	 * @param conAudit
//	 * @return true if a visible cao exists that is Pending, Incomplete or Resubmit status
//	 */
//	private boolean auditHasPendingCaos(ContractorAudit conAudit) {
//		for (ContractorAuditOperator cao : conAudit.getOperators()) {
//			if (cao.isVisible()) {
//				if (cao.getStatus().isPending()) {
//					return true;
//				}
//				if (cao.getStatus().isIncomplete()) {
//					return true;
//				}
//				if (cao.getStatus().isResubmit()) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}
//
//	private boolean hasAnyCaoStatusAfterIncomplete(ContractorAudit conAudit) {
//		for (ContractorAuditOperator cao : conAudit.getOperators()) {
//			if (cao.getStatus().after(AuditStatus.Incomplete) && cao.isVisible()) {
//				return true;
//			}
//		}
//
//		return false;
//	}
//
//	private void fillAuditOperatorPermissions(ContractorAuditOperator cao, Set<OperatorAccount> caopOperators) {
//		if (cao.getAudit().getRequestingOpAccount() != null
//				&& cao.isVisible()
//				&& cao.getAudit().getRequestingOpAccount()
//						.getOperatorHeirarchy()
//						.contains(cao.getOperator().getId())) {
//			// Warning, this only works for operator sites, not corporate accounts
//			caopOperators.add(cao.getAudit().getRequestingOpAccount());
//		} else if (cao.getAudit().getAuditType().isDesktop() && cao.getAudit().hasCaoStatus(AuditStatus.Complete)) {
//			for (ContractorOperator co : cao.getAudit().getContractorAccount().getOperators()) {
//				if (cao.isVisible()
//						&& co.getOperatorAccount().getOperatorHeirarchy().contains(cao.getOperator().getId())) {
//					// Once the Manual Audit has at least one status that's Complete, then show it to all the
//					// contractor's operators
//					caopOperators.add(co.getOperatorAccount());
//				}
//			}
//		}
//
//		/*
//		 * Remove COAPs from audits where operators cannot view the audit such as the Welcome Call
//		 */
//		if (!cao.getAudit().getAuditType().isCanOperatorView()) {
//			caopOperators.clear();
//		}
//
//		Iterator<ContractorAuditOperatorPermission> caopIter = cao.getCaoPermissions().iterator();
//		while (caopIter.hasNext()) {
//			ContractorAuditOperatorPermission caop = caopIter.next();
//			if (caopOperators.contains(caop.getOperator())) {
//				// It's already there, do nothing
//				caopOperators.remove(caop.getOperator());
//			} else {
//				// Delete the caop and remove from cao.getCaoPermissions()
//				caopIter.remove();
//				this.contractorAuditOperatorDAO.remove(caop);
//			}
//		}
//		for (OperatorAccount operator : caopOperators) {
//			// Insert the remaining operators
//			ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
//			caop.setCao(cao);
//			caop.setOperator(operator);
//			cao.getCaoPermissions().add(caop);
//		}
//	}
//
	private AuditType reconnectAuditType(AuditType auditType) {
		auditType = conAuditDao.find(AuditType.class, auditType.getId());
		return auditType;
	}

	private void addAnnualUpdate(ContractorAccount contractor, int year, AuditType auditType) {
		for (ContractorAudit cAudit : contractor.getAudits()) {
			if (cAudit.getAuditType().isAnnualAddendum() && year == Integer.parseInt(cAudit.getAuditFor())) {
				// Do nothing. It's already here
				return;
			}
		}
		// The Annual Update isn't here yet. Let's add it for the given year.
		Calendar startDate = Calendar.getInstance();
		startDate.set(year, Calendar.DECEMBER, 31);
		ContractorAudit audit = new ContractorAudit();
		audit.setContractorAccount(contractor);
		audit.setAuditType(auditType);
		audit.setAuditColumns(systemUser);

		audit.setAuditFor(Integer.toString(year));
		Calendar effDate = Calendar.getInstance();
		effDate.set(year, Calendar.JANUARY, 1);
		audit.setEffectiveDate(effDate.getTime());
		audit.setCreationDate(startDate.getTime());
		Date dateToExpire = DateBean.setToEndOfDay(DateBean.addMonths(startDate.getTime(), auditType.getMonthsToExpire()));
		audit.setExpiresDate(dateToExpire);
		audit.setPreviousAudit(conAuditDao.findPreviousAudit(audit));
		conAuditDao.save(audit);
		contractor.getAudits().add(audit);
	}

//	private boolean contains(Collection<? extends BaseTable> haystack, BaseTable needle) {
//		for (BaseTable entity : haystack) {
//			if (entity.getId() == needle.getId()) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	private AuditCatData getCatData(ContractorAudit conAudit, AuditCategory category) {
//		for (AuditCatData catData : conAudit.getCategories()) {
//			if (catData.getCategory().equals(category)) {
//				return catData;
//			}
//		}
//
//		// We didn't find a catData record, so let's create one now
//		AuditCatData catData = new AuditCatData();
//		catData.setCategory(category);
//		catData.setAudit(conAudit);
//		catData.setApplies(true);
//		catData.setOverride(false);
//		catData.setAuditColumns(systemUser);
//		catData.setNumRequired(category.getNumRequired());
//		conAudit.getCategories().add(catData);
//		return catData;
//	}
//
//	public void recalculateCategories(ContractorAudit conAudit) {
//		AuditCategoriesBuilder categoriesBuilder = new AuditCategoriesBuilder(categoryRuleCache,
//				conAudit.getContractorAccount());
//
//		/*
//		 * I really don't like this. We should probably have the list of operators somewhere else, but I couldn't find
//		 * it. If we find ourselves doing this more often, then this method should be placed in ContractorAudit as a
//		 * transient method.
//		 */
//		Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
//		for (ContractorAuditOperator cao : conAudit.getOperatorsVisible()) {
//			for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
//				operators.add(caop.getOperator());
//			}
//		}
//
//		Set<AuditCategory> categories = categoriesBuilder.calculate(conAudit, operators);
//		fillAuditCategories(conAudit, categories);
//		fillAuditOperators(conAudit, categoriesBuilder.getCaos());
//	}
//
//	public AuditTypeRuleCache getTypeRuleCache() {
//		return typeRuleCache;
//	}
}
