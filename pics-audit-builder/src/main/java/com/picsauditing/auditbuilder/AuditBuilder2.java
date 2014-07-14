package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.AuditTypesBuilder.AuditTypeDetail;
import com.picsauditing.auditbuilder.dao.*;
import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.service.AccountService;
import com.picsauditing.auditbuilder.service.AuditPeriodService2;
import com.picsauditing.auditbuilder.service.AuditService;
import com.picsauditing.auditbuilder.util.DateBean;
import com.picsauditing.auditbuilder.util.Strings;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class AuditBuilder2 {

	@Autowired
	private AuditCategoryMatrixDAO2 auditCatMatrixDAO;
	@Autowired
	private AppTranslationDAO2 appTranslationDAO;
	@Autowired
	private ContractorAuditDAO2 conAuditDao;
	@Autowired
	private ContractorAuditOperatorDAO2 contractorAuditOperatorDAO;
    @Autowired
    private ContractorAuditFileDAO2 contractorAuditFileDAO;
	@Autowired
	private ContractorTagDAO2 contractorTagDAO;
	@Autowired
	private AuditBuilderDAO auditDataDAO;
	@Autowired
	private AuditTypeRuleCache2 typeRuleCache;
	@Autowired
	private AuditCategoryRuleCache2 categoryRuleCache;
	@Autowired
	private AuditPercentCalculator2 auditPercentCalculator;
    @Autowired
    AuditPeriodService2 auditPeriodService;
    @Autowired
    private AuditTypeDAO2 auditTypeDao;

	private static final Logger logger = LoggerFactory.getLogger(AuditBuilder2.class);

	private User systemUser = new User(User.SYSTEM);
    private Date today;

	HashSet<ContractorAuditOperator> caosToMoveToApprove = new HashSet<>();
	HashSet<ContractorAuditOperator> caosToMoveToComplete = new HashSet<>();
	HashSet<ContractorAuditOperator> caosToMoveToResubmit = new HashSet<>();
	HashSet<ContractorAuditOperator> caosToMoveToSubmit = new HashSet<>();

	private Set<String> yearsForAllWCBs;

    public void buildAudits(int conID) {
        ContractorAccount contractor = contractorAuditOperatorDAO.find(ContractorAccount.class, conID);
        buildAudits(contractor);
    }

	public void buildAudits(ContractorAccount contractor) {
		AuditTypesBuilder typesBuilder = new AuditTypesBuilder(typeRuleCache, contractor);
        typesBuilder.setContractorTagDAO(contractorTagDAO);
        typesBuilder.setAuditDataDAO(auditDataDAO);

		Set<AuditTypeDetail> requiredAuditTypeDetails = typesBuilder.calculate();
		Set<AuditType> requiredAuditTypes = new HashSet<>();

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
                    addAnnualUpdate(contractor, year - 3, auditType);
                    addAnnualUpdate(contractor, year - 2, auditType);
                    addAnnualUpdate(contractor, year - 1, auditType);
				} else {
					boolean found = false;
					for (ContractorAudit conAudit : contractor.getAudits()) {
						if (conAudit.getAuditType().equals(auditType)) {
							if (auditType.isRenewable()) {
								found = true;
							} else if (AuditService.isWCB(auditType)) {
								found = foundCurrentYearWCB(conAudit);
							} else {
								if (!AuditService.isExpired(conAudit) && !AuditService.willExpireSoon(conAudit)) {
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

		for (ContractorAudit conAudit : contractor.getAudits()) {
			AuditTypeDetail auditTypeDetail = findDetailForAuditType(requiredAuditTypeDetails, conAudit.getAuditType());
			if (auditTypeDetail == null) {
			} else {
				Set<AuditCategory> categories = categoriesBuilder.calculate(conAudit, auditTypeDetail.operators);
				if (conAudit.getAuditType().getId() == AuditType.IMPORT_PQF) {
					categories = new HashSet<>(conAudit.getAuditType().getCategories());
				}

				fillAuditOperators(conAudit, categoriesBuilder.getCaos());
				fillAuditCategories(conAudit, categories);
			}
		}

		Iterator<ContractorAudit> iterator = contractor.getAudits().iterator();
		while (iterator.hasNext()) {
			ContractorAudit conAudit = iterator.next();
			if (!conAudit.isManuallyAdded() && !requiredAuditTypes.contains(conAudit.getAuditType())) {
				if (!isValidAudit(conAudit)) {
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
				ContractorAuditOperatorWorkflow caow = AuditService.changeStatus(cao, AuditStatus.Pending);
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
			return hasAllWCBsForGracePeriod();
		}

		return yearsForAllWCBs.contains(DateBean.getWCBYear());
	}

	private void validateWCBAudit(ContractorAudit audit) {
		String auditFor = audit.getAuditFor();
		if (Strings.isEmpty(auditFor) || !NumberUtils.isDigits(auditFor) || auditFor.length() != 4) {
			throw new RuntimeException("WCBs must always have an AuditFor that is a 4-digit year.");
		}
	}

	private void buildSetOfAllWCBYears(ContractorAccount contractor, AuditType wcbAuditType) {
		List<ContractorAudit> audits = contractor.getAudits();
		if (CollectionUtils.isEmpty(audits)) {
			yearsForAllWCBs = Collections.unmodifiableSet(new HashSet<String>());
		}

		Set<String> years = new HashSet<>();
		for (ContractorAudit audit : audits) {
			if (isMatchingWCBAudit(audit, wcbAuditType)) {
				years.add(audit.getAuditFor());
			}
		}

		yearsForAllWCBs = Collections.unmodifiableSet(years);
	}

	private boolean hasAllWCBsForGracePeriod() {
		String previousYear = Integer.toString(DateBean.getPreviousWCBYear());
		String currentWCBYear = DateBean.getWCBYear();
		return yearsForAllWCBs.contains(previousYear) && yearsForAllWCBs.contains(currentWCBYear);
	}

    public Date getToday() {
        if (today == null)
            return new Date();
        else
            return today;
    }

    public void setToday(Date today) {
        this.today = today;
    }

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

	private Set<String> getAllYearsForNewWCB() {
		Set<String> years = new HashSet<>();

		String previousYear = Integer.toString(DateBean.getPreviousWCBYear());
		if (!yearsForAllWCBs.contains(previousYear) && DateBean.isGracePeriodForWCB()) {
			years.add(previousYear);
		}

		String currentWCBYear = DateBean.getWCBYear();
		if (!yearsForAllWCBs.contains(currentWCBYear)) {
			years.add(currentWCBYear);
		}

		return years;
	}

	private ContractorAudit createNewAudit(ContractorAccount contractor, AuditType auditType) {
		ContractorAudit audit = new ContractorAudit();
		audit.setContractorAccount(contractor);
		audit.setAuditType(auditType);
		audit.setAuditColumns(systemUser);
		audit.setPreviousAudit(conAuditDao.findPreviousAudit(audit));
		contractor.getAudits().add(audit);

		return audit;
	}

	private boolean isMatchingWCBAudit(ContractorAudit audit, AuditType wcbAuditType) {
		return audit != null
				&& audit.getAuditType() != null
				&& AuditType.CANADIAN_PROVINCES.contains(audit.getAuditType().getId())
				&& (audit.getAuditType().getId() == wcbAuditType.getId());
	}

	private boolean isValidAudit(ContractorAudit conAudit) {
		if (conAudit.getAuditType().getId() != AuditType.COR
				&& conAudit.getAuditType().getId() != AuditType.IEC_AUDIT
				&& conAudit.getAuditType().getId() != AuditType.WELCOME) {
			return false;
		}

		if (AuditService.isExpired(conAudit)) {
			return false;
		}

		if (conAudit.getAuditType().getId() == AuditType.WELCOME) {
			Calendar date = Calendar.getInstance();
			date.add(Calendar.DATE, -6 * 7);
			if (date.getTime().getTime() > conAudit.getCreationDate().getTime()) {
				conAudit.setExpiresDate(new Date());
			}
			return true;
		}

		int auditQuestionID = (conAudit.getAuditType().getId() == AuditType.COR) ? AuditQuestion.COR
				: AuditQuestion.IEC;

		ContractorAudit pqfAudit = null;
		for (ContractorAudit ca : conAudit.getContractorAccount().getAudits()) {
			if (ca.getAuditType().getId() == AuditType.PQF) {
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
				&& (!StringUtils.equals(data.getAnswer(), "Yes") || !AuditService.isVisibleInAudit(data.getQuestion(), pqfAudit))) {
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
		caosToMoveToApprove = new HashSet<>();
		caosToMoveToComplete = new HashSet<>();
		caosToMoveToResubmit = new HashSet<>();
		caosToMoveToSubmit = new HashSet<>();
		HashSet<Integer> operatorsGoingVisibleIds = new HashSet<>();

		Set<OperatorAccount> caosToEnsureExist = caoMap.keySet();
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			boolean caoShouldBeVisible = contains(caosToEnsureExist, cao.getOperator());

			if (!cao.isVisible() && caoShouldBeVisible) {
				operatorsGoingVisibleIds.add(cao.getOperator().getId());
			}

			cao.setVisible(caoShouldBeVisible);

			for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
				previousCaoMap.put(caop.getOperator(), cao);
			}

			if (!caoShouldBeVisible && cao.getCaoPermissions().size() > 0) {
				fillAuditOperatorPermissions(cao, new HashSet<OperatorAccount>());
				cao.getCaoPermissions().clear();
			}
		}

		for (OperatorAccount governingBody : caosToEnsureExist) {
			ContractorAuditOperator cao = null;
			for (ContractorAuditOperator cao2 : conAudit.getOperators()) {
				if (cao2.getOperator().getId() == governingBody.getId()) {
					cao = cao2;
					break;
				}
			}
			if (cao == null) {
				cao = new ContractorAuditOperator();
				cao.setAudit(conAudit);
				cao.setOperator(governingBody);
				cao.setAuditColumns(systemUser);
				AuditStatus firstStatus = AuditService.getFirstStep(conAudit.getAuditType().getWorkFlow()).getNewStatus();
				AuditService.changeStatus(cao, firstStatus);
				conAudit.setLastRecalculation(null);

                cao = contractorAuditOperatorDAO.save(cao);
                conAudit.getOperators().add(cao);
            }

			fillAuditOperatorPermissions(cao, caoMap.get(governingBody));
		}

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

	private void removeDuplicateCaosFromLists() {
		for (ContractorAuditOperator cao : caosToMoveToApprove){
			caosToMoveToSubmit.remove(cao);
			caosToMoveToResubmit.remove(cao);
			caosToMoveToComplete.remove(cao);
		}

		for (ContractorAuditOperator cao : caosToMoveToComplete){
			caosToMoveToSubmit.remove(cao);
			caosToMoveToResubmit.remove(cao);
		}

		for (ContractorAuditOperator cao : caosToMoveToResubmit){
			caosToMoveToSubmit.remove(cao);
		}
	}

	private void adjustCaoStatus(ContractorAudit conAudit) {
		if (isAuditThatCanAdjustStatus(conAudit)) {
			if (!caosToMoveToApprove.isEmpty()) {
				adjustCaosToStatus(conAudit, caosToMoveToApprove, AuditStatus.Approved);
			}
			if (!caosToMoveToComplete.isEmpty()) {
				adjustCaosToStatus(conAudit, caosToMoveToComplete, AuditStatus.Complete);
			}
			if (!caosToMoveToResubmit.isEmpty()) {
				adjustCaosToStatus(conAudit, caosToMoveToResubmit, AuditStatus.Resubmit);
			}
			if (!caosToMoveToSubmit.isEmpty()) {
				adjustCaosToStatus(conAudit, caosToMoveToSubmit, AuditStatus.Submitted);
			}
		}
	}

	private boolean isAuditThatCanAdjustStatus(ContractorAudit conAudit) {
		return conAudit.getAuditType().getId() == AuditType.PQF
				|| conAudit.getAuditType().getId() == AuditType.INTEGRITYMANAGEMENT
				|| conAudit.getAuditType().getId() == AuditType.ANNUALADDENDUM;
	}

	private void adjustCaosToStatus(ContractorAudit conAudit, HashSet<ContractorAuditOperator> caosToChange, AuditStatus status) {
		Iterator<ContractorAuditOperator> list = caosToChange.iterator();
		while (list.hasNext()) {
			ContractorAuditOperator cao = list.next();
			if (conAudit.getAuditType().getId() == AuditType.PQF && status.isComplete() &&
					!AuditService.pqfIsOkayToChangeCaoStatus(conAudit, cao)) {
				continue;
			}
			ContractorAuditOperatorWorkflow caow = AuditService.changeStatus(cao, status);
			if (caow != null) {
                List<AppTranslation> translations = appTranslationDAO.findWhere("t.locale = 'en' AND t.msgKey = concat('AuditType.'," + conAudit.getAuditType().getId() + ",'.name'");

                String auditTypeName = "";
                if (!translations.isEmpty()) {
                    AppTranslation appTranslation = translations.get(0);
                    auditTypeName = appTranslation.getValue();
                }

                caow.setNotes(String.format("Changing Status for %s(%d) from %s to %s", auditTypeName, conAudit.getId(), caow.getPreviousStatus(), caow.getStatus()));
				caow.setCreatedBy(systemUser);
				contractorAuditOperatorDAO.save(caow);
			}
		}
	}

	private void fillAuditCategories(ContractorAudit conAudit, Set<AuditCategory> categoriesNeeded) {
		if (conAudit.getAuditType().getId() == AuditType.SHELL_COMPETENCY_REVIEW) {
			List<AuditCategory> requiredCompetencies = auditCatMatrixDAO.findCategoriesForCompetencies(conAudit
					.getContractorAccount().getId());
			categoriesNeeded = new HashSet<>();
			if (AuditService.hasCaoStatus(conAudit, AuditStatus.Pending)) {
				for (AuditCategory ac : conAudit.getAuditType().getCategories()) {
					if (requiredCompetencies.contains(AuditService.getTopParent(ac))) {
						categoriesNeeded.add(ac);
					}
				}
			} else {
				return;
			}
		}

		boolean hasPendingCaos = auditHasPendingCaos(conAudit);

		for (AuditCatData auditCatData : conAudit.getCategories()) {
			if (auditCatData.getCategory().getParent() == null) {
				if (conAudit.getAuditType().getId() == AuditType.MANUAL_AUDIT || conAudit.getAuditType().getId() == AuditType.IMPLEMENTATION_AUDIT) {
					if (hasAnyCaoStatusAfterIncomplete(conAudit) || auditCatData.isOverride()) {
						if (auditCatData.isApplies()) {
							categoriesNeeded.add(auditCatData.getCategory());
						} else {
							categoriesNeeded.remove(auditCatData.getCategory());
						}
					}
				} else {
					if (!hasPendingCaos || auditCatData.isOverride()) {
						if (auditCatData.isApplies()) {
							categoriesNeeded.add(auditCatData.getCategory());
						} else {
							categoriesNeeded.remove(auditCatData.getCategory());
						}
					}
				}
			}
		}

		for (AuditCategory category : conAudit.getAuditType().getCategories()) {

			AuditCatData catData = getCatData(conAudit, category);
			if (catData.isOverride()) {
			} else {
				boolean categoryApplies = categoriesNeeded.contains(catData.getCategory());
				if (categoryApplies) {
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
		}

		if (conAudit.getCreationDate() == null || conAudit.getCreationDate().getTime() > new Date().getTime() - (60 * 1000L)) {
			auditPercentCalculator.percentCalculateComplete(conAudit, true);
		}
	}

	protected boolean areAllParentsApplicable(Set<AuditCategory> categoriesNeeded, AuditCategory category) {
		if (category.getParent() != null) {
			return areAllParentsApplicable(categoriesNeeded, category.getParent())
					&& categoriesNeeded.contains(category);
		} else {
			return categoriesNeeded.contains(category);
		}
	}

	private boolean auditHasPendingCaos(ContractorAudit conAudit) {
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.isVisible()) {
				if (cao.getStatus().isPending()) {
					return true;
				}
				if (cao.getStatus().isIncomplete()) {
					return true;
				}
				if (cao.getStatus().isResubmit()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasAnyCaoStatusAfterIncomplete(ContractorAudit conAudit) {
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.getStatus().after(AuditStatus.Incomplete) && cao.isVisible()) {
				return true;
			}
		}

		return false;
	}

	private void fillAuditOperatorPermissions(ContractorAuditOperator cao, Set<OperatorAccount> caopOperators) {
		if (cao.getAudit().getRequestingOpAccount() != null
				&& cao.isVisible()
				&& AccountService.getOperatorHeirarchy(cao.getAudit().getRequestingOpAccount())
						.contains(cao.getOperator().getId())) {
			caopOperators.add(cao.getAudit().getRequestingOpAccount());
		} else if (cao.getAudit().getAuditType().getId() == AuditType.MANUAL_AUDIT && AuditService.hasCaoStatus(cao.getAudit(), AuditStatus.Complete)) {
			for (ContractorOperator co : cao.getAudit().getContractorAccount().getOperators()) {
				if (cao.isVisible()
						&& AccountService.getOperatorHeirarchy(co.getOperatorAccount()).contains(cao.getOperator().getId())) {
					caopOperators.add(co.getOperatorAccount());
				}
			}
		}

		if (!cao.getAudit().getAuditType().isCanOperatorView()) {
			caopOperators.clear();
		}

		Iterator<ContractorAuditOperatorPermission> caopIter = cao.getCaoPermissions().iterator();
		while (caopIter.hasNext()) {
			ContractorAuditOperatorPermission caop = caopIter.next();
			if (caopOperators.contains(caop.getOperator())) {
				caopOperators.remove(caop.getOperator());
			} else {
				caopIter.remove();
				contractorAuditOperatorDAO.remove(caop);
			}
		}
		for (OperatorAccount operator : caopOperators) {
			ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
			caop.setCao(cao);
			caop.setOperator(operator);
			cao.getCaoPermissions().add(caop);
		}
	}

	private AuditType reconnectAuditType(AuditType auditType) {
		auditType = conAuditDao.find(AuditType.class, auditType.getId());
		return auditType;
	}

	private void addAnnualUpdate(ContractorAccount contractor, int year, AuditType auditType) {
		for (ContractorAudit cAudit : contractor.getAudits()) {
			if (cAudit.getAuditType().getId() == AuditType.ANNUALADDENDUM && year == Integer.parseInt(cAudit.getAuditFor())) {
				return;
			}
		}

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

	private boolean contains(Collection<? extends BaseTable> haystack, BaseTable needle) {
		for (BaseTable entity : haystack) {
			if (entity.getId() == needle.getId()) {
				return true;
			}
		}
		return false;
	}

	private AuditCatData getCatData(ContractorAudit conAudit, AuditCategory category) {
		for (AuditCatData catData : conAudit.getCategories()) {
			if (catData.getCategory().equals(category)) {
				return catData;
			}
		}

		AuditCatData catData = new AuditCatData();
		catData.setCategory(category);
		catData.setAudit(conAudit);
		catData.setApplies(true);
		catData.setOverride(false);
		catData.setAuditColumns(systemUser);
		catData.setNumRequired(category.getNumRequired());
		conAudit.getCategories().add(catData);
		return catData;
	}
}
