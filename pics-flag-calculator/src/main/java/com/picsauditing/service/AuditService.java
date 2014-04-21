package com.picsauditing.service;

import com.picsauditing.model.entities.*;
import com.picsauditing.util.YearList;
import com.picsauditing.util.comparators.ContractorAuditComparator;

import java.util.*;

public class AuditService {
    public static boolean isAuditExpired(ContractorAudit contractorAudit) {
        if (contractorAudit.getExpiresDate() == null)
            return false;
        return contractorAudit.getExpiresDate().before(new Date());
    }

    public static boolean isAuditScoreable(AuditType auditType) {
        return auditType.getScoreType() != null;
    }

    public static boolean isHasSubmittedStep(Workflow workflow) {
		for (WorkflowStep step : workflow.getSteps()) {
			if (step.getNewStatus().isSubmitted())
				return true;
		}
		return false;
	}

    public static final Set<Integer> CANADIAN_PROVINCES = new HashSet<Integer>(Arrays.asList(new Integer[]{145, 146,
            143, 170, 261, 168, 148, 147, 169, 166, 167, 144}));

    public static boolean isWCB(int id) {
        return CANADIAN_PROVINCES.contains(id);
    }

    public static List<ContractorAudit> getSortedAnnualUpdates(ContractorAccount contractorAccount) {
        List<ContractorAudit> annualAList = new ArrayList<ContractorAudit>();
        for (ContractorAudit contractorAudit : contractorAccount.getAudits()) {
            if (contractorAudit.getAuditType().isAnnualAddendum() && contractorAudit.getExpiresDate() != null
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
            if (annualUpdate.hasCaoStatusAfter(AuditStatus.Pending)) {
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
