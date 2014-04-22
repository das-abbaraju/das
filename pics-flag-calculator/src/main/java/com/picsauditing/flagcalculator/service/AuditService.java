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

    public static List<ContractorAudit> getAuditByAuditType(ContractorAccount contractorAccount, AuditType auditType) {
        List<ContractorAudit> auditList = new ArrayList<ContractorAudit>();

        for (ContractorAudit ca : contractorAccount.getAudits()) {
            if (ca.getAuditType().equals(auditType)) {
                auditList.add(ca);
            }
        }

        return auditList;
    }
}
