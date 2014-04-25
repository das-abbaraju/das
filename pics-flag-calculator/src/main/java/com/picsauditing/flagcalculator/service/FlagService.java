package com.picsauditing.flagcalculator.service;

import com.picsauditing.flagcalculator.entities.FlagCriteriaOperator;
import com.picsauditing.flagcalculator.entities.FlagDataOverride;
import com.picsauditing.flagcalculator.entities.OperatorAccount;
import com.picsauditing.flagcalculator.util.DateBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FlagService {
    public static boolean isInForce(FlagDataOverride flagDataOverride) {
        if (flagDataOverride.getForceEnd() == null)
            return false;
        return flagDataOverride.getForceEnd().after(new Date());
    }

    public static List<FlagCriteriaOperator> getFlagCriteriaInherited(OperatorAccount operatorAccount) {
        List<FlagCriteriaOperator> criteriaList = new ArrayList<>();

        criteriaList.addAll(getFlagAuditCriteriaInherited(operatorAccount));
        criteriaList.addAll(getFlagQuestionCriteriaInherited(operatorAccount));

        return criteriaList;
    }

    public static List<FlagCriteriaOperator> getFlagAuditCriteriaInherited(OperatorAccount operatorAccount) {
        List<FlagCriteriaOperator> criteriaList = new ArrayList<>();

        if (operatorAccount.getInheritFlagCriteria() != null) {
            for (FlagCriteriaOperator c : operatorAccount.getInheritFlagCriteria().getFlagCriteria()) {
                if (c.getCriteria().getAuditType() != null) {
                    if (!c.getCriteria().getAuditType().getClassType().isPolicy() || "Yes".equals(operatorAccount.getCanSeeInsurance())) {
                        criteriaList.add(c);
                    }
                }
            }
        }

        return criteriaList;
    }

    public static List<FlagCriteriaOperator> getFlagQuestionCriteriaInherited(OperatorAccount operatorAccount) {
        List<FlagCriteriaOperator> criteriaList = new ArrayList<FlagCriteriaOperator>();

        if (operatorAccount.getInheritFlagCriteria() != null) {
            for (FlagCriteriaOperator c : operatorAccount.getInheritFlagCriteria().getFlagCriteria()) {
                if (c.getCriteria().getQuestion() != null) {
                    if (DateBean.isCurrent(c.getCriteria().getQuestion())) {
                        if (!AuditService.getAuditType(c.getCriteria().getQuestion()).getClassType().isPolicy()
                                || "Yes".equals(operatorAccount.getCanSeeInsurance())) {
                            criteriaList.add(c);
                        }
                    }
                }
                if (c.getCriteria().getOshaType() != null) {
                    if (c.getCriteria().getOshaType().equals(operatorAccount.getOshaType())) {
                        criteriaList.add(c);
                    }
                }
            }
        }

        return criteriaList;
    }

}
