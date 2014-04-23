package com.picsauditing.flagcalculator.service;

import com.picsauditing.flagcalculator.entities.*;
import com.picsauditing.flagcalculator.util.YearList;
import com.picsauditing.flagcalculator.util.comparators.ContractorAuditComparator;

import java.util.*;

public class AuditService {

    public static final Set<Integer> CANADIAN_PROVINCES = new HashSet<Integer>(Arrays.asList(new Integer[]{145, 146,
            143, 170, 261, 168, 148, 147, 169, 166, 167, 144}));

    public static boolean isWCB(int id) {
        return CANADIAN_PROVINCES.contains(id);
    }

    public static boolean isPicsPqf(int id) {
        return (id == AuditType.PQF);
    }

    public static boolean isAnnualAddendum(int id) {
        return (id == AuditType.ANNUALADDENDUM);
    }

    public static boolean isAuditExpired(ContractorAudit contractorAudit) {
        if (contractorAudit.getExpiresDate() == null)
            return false;
        return contractorAudit.getExpiresDate().before(new Date());
    }

    public static boolean isAuditScoreable(AuditType auditType) {
        return auditType.getScoreType() != null;
    }

    public static AuditType getParentAuditType(AuditCategory category) {
        if (category.getAuditType() == null) {
            return getParentAuditType(category.getParent());
        }

        return category.getAuditType();
    }

    public static boolean isHasSubmittedStep(Workflow workflow) {
		for (WorkflowStep step : workflow.getSteps()) {
			if (step.getNewStatus().isSubmitted())
				return true;
		}
		return false;
	}

    public static AuditType getAuditType(AuditQuestion question) {
        return AuditService.getParentAuditType(question.getCategory());
    }

    public static boolean hasCaoStatus(ContractorAudit audit, AuditStatus auditStatus) {
        for (ContractorAuditOperator cao : audit.getOperators()) {
            if (cao.isVisible() && cao.getStatus().equals(auditStatus))
                return true;
        }
        return false;
    }

    public static boolean hasCaoStatusAfter(ContractorAudit audit, AuditStatus auditStatus) {
        return AuditService.hasCaoStatusAfter(audit, auditStatus, false);
    }

    public static boolean hasCaoStatusAfter(ContractorAudit audit, AuditStatus auditStatus, boolean ignoreNotApplicable) {
        for (ContractorAuditOperator cao : audit.getOperators()) {
            if (ignoreNotApplicable && cao.getStatus().equals(AuditStatus.NotApplicable))
                continue;
            if (cao.isVisible() && cao.getStatus().after(auditStatus)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasCaop(ContractorAuditOperator cao, int opID) {
        for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
            if (caop.getOperator().getId() == opID)
                return true;
        }
        return false;
    }

    public static List<ContractorAudit> getSortedAnnualUpdates(ContractorAccount contractorAccount) {
        List<ContractorAudit> annualAList = new ArrayList<ContractorAudit>();
        for (ContractorAudit contractorAudit : contractorAccount.getAudits()) {
            if (isAnnualAddendum(contractorAudit.getAuditType().getId()) && contractorAudit.getExpiresDate() != null
                    && !isAuditExpired(contractorAudit)) {
                annualAList.add(contractorAudit);
            }
        }
        Collections.sort(annualAList, new ContractorAuditComparator("auditFor -1"));
        return annualAList;
    }


    public static Map<MultiYearScope, ContractorAudit> getAfterPendingAnnualUpdates(ContractorAccount contractorAccount) {
        Map<MultiYearScope, ContractorAudit> annualUpdates = new LinkedHashMap<MultiYearScope, ContractorAudit>();
        Map<Integer, ContractorAudit> annuals = new LinkedHashMap<Integer, ContractorAudit>();
        YearList years = new YearList();

        for (ContractorAudit annualUpdate : getSortedAnnualUpdates(contractorAccount)) {
            if (hasCaoStatusAfter(annualUpdate, AuditStatus.Pending)) {
                years.add(annualUpdate.getAuditFor());
                annuals.put(Integer.parseInt(annualUpdate.getAuditFor()), annualUpdate);
            }
        }

        annualUpdates.put(MultiYearScope.LastYearOnly,
                annuals.get(years.getYearForScope(MultiYearScope.LastYearOnly)));
        annualUpdates.put(MultiYearScope.TwoYearsAgo,
                annuals.get(years.getYearForScope(MultiYearScope.TwoYearsAgo)));
        annualUpdates.put(MultiYearScope.ThreeYearsAgo,
                annuals.get(years.getYearForScope(MultiYearScope.ThreeYearsAgo)));

        return annualUpdates;

    }

    public static Map<MultiYearScope, ContractorAudit> getCompleteAnnualUpdates(ContractorAccount contractorAccount) {
        Map<MultiYearScope, ContractorAudit> completeAnnualUpdates = new LinkedHashMap<MultiYearScope, ContractorAudit>();
        Map<Integer, ContractorAudit> annuals = new LinkedHashMap<Integer, ContractorAudit>();
        YearList years = new YearList();

        for (ContractorAudit annualUpdate : getSortedAnnualUpdates(contractorAccount)) {
            if (AuditService.hasCaoStatus(annualUpdate, AuditStatus.Complete)) {
                years.add(annualUpdate.getAuditFor());
                annuals.put(Integer.parseInt(annualUpdate.getAuditFor()), annualUpdate);
            }
        }

        completeAnnualUpdates.put(MultiYearScope.LastYearOnly,
                annuals.get(years.getYearForScope(MultiYearScope.LastYearOnly)));
        completeAnnualUpdates.put(MultiYearScope.TwoYearsAgo,
                annuals.get(years.getYearForScope(MultiYearScope.TwoYearsAgo)));
        completeAnnualUpdates.put(MultiYearScope.ThreeYearsAgo,
                annuals.get(years.getYearForScope(MultiYearScope.ThreeYearsAgo)));

        return completeAnnualUpdates;
    }

    public static List<ContractorAudit> getAuditByAuditType(ContractorAccount contractorAccount, AuditType auditType) {
        List<ContractorAudit> auditList = new ArrayList<ContractorAudit>();

        for (ContractorAudit ca : contractorAccount.getAudits()) {
            if (ca.getAuditType().equals(auditType)) {
                auditList.add(ca);
            }
        }

        return auditList;
    }

    public static Set<AuditCategory> getVisibleCategories(ContractorAudit contractorAudit) {
        Set<AuditCategory> visibleCategories = new HashSet<AuditCategory>();
        for (AuditCatData categoryData : contractorAudit.getCategories()) {
            // Find all applicable categories
            if (categoryData.isApplies()) {
                visibleCategories.add(categoryData.getCategory());
            }
        }

        // If the ancestors aren't applicable, then remove category
        Iterator<AuditCategory> iterator = visibleCategories.iterator();
        while (iterator.hasNext()) {
            AuditCategory category = iterator.next();
            AuditCategory parent = category.getParent();

            // Breadcrumbs in case we have a cyclical relationship somewhere
            Set<Integer> alreadyProcessed = new HashSet<Integer>();
            alreadyProcessed.add(category.getId());

            while (parent != null) {
                if (alreadyProcessed.contains(parent.getId())) {
                    break;
                }

                if (!visibleCategories.contains(parent)) {
                    iterator.remove();
                    break;
                }

                alreadyProcessed.add(parent.getId());
                parent = parent.getParent();
            }
        }

        return visibleCategories;
    }

	public static boolean isMultipleChoice(AuditData auditData) {
		return auditData.getQuestion() != null && auditData.getQuestion().getQuestionType().equals("MultipleChoice") && auditData.getQuestion().getOption() != null;
	}

    public static boolean isCategoryApplicable(ContractorAudit contractorAudit, int catID) {
        for (AuditCatData acd : contractorAudit.getCategories()) {
            if (acd.getCategory().getId() == catID && acd.isApplies())
                return true;
        }
        return false;
    }

	public static boolean isVerified(AuditData auditData) {
		return auditData.getDateVerified() != null;
	}

	public static void setVerified(AuditData auditData, boolean inValue) {
        auditData.setDateVerified(inValue ? new Date() : null);
	}

    public static boolean isVisibleInAudit(AuditQuestion question, ContractorAudit audit) {
        for (AuditCatData category : audit.getCategories()) {
            if (category.getCategory().getId() == question.getCategory().getId()) {
                return category.isApplies();
            }
        }

        return false;
    }

}
