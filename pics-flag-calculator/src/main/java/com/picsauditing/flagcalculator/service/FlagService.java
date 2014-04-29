package com.picsauditing.flagcalculator.service;

import com.picsauditing.flagcalculator.entities.*;
import com.picsauditing.flagcalculator.util.DateBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FlagService {
    public static boolean isInForce(FlagDataOverride flagDataOverride) {
        if (flagDataOverride.getForceEnd() == null)
            return false;
        return flagDataOverride.getForceEnd().after(new Date());
    }

    public static List<FlagCriteriaOperator> getFlagCriteriaInherited(OperatorAccount operatorAccount, boolean insurance) {
        if (insurance) {
            return operatorAccount.getInheritFlagCriteria().getFlagCriteria();
        } else {
            if (operatorAccount.getInheritFlagCriteria() == null)
                return new ArrayList<>();
            else
                return operatorAccount.getInheritFlagCriteria().getFlagCriteria();
        }
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
                    if (!c.getCriteria().getAuditType().getClassType().isPolicy() || YesNo.Yes.equals(operatorAccount.getCanSeeInsurance())) {
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
                                || YesNo.Yes.equals(operatorAccount.getCanSeeInsurance())) {
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

    public static Integer includeExcess(FlagCriteria flagCriteria) {
        if (!flagCriteria.isInsurance() || flagCriteria.getOptionCode() == null) {
            return null;
        }

        // We should consider putting this into the DB eventually
        if (flagCriteria.getOptionCode() == FlagCriteriaOptionCode.ExcessAggregate) {
            return AuditQuestion.EXCESS_AGGREGATE;
        }

        if (flagCriteria.getOptionCode() == FlagCriteriaOptionCode.ExcessEachOccurrence) {
            return AuditQuestion.EXCESS_EACH;
        }

        return null;
    }

    public static void updateFlagCriteriaContractor(FlagCriteriaContractor toUpdate, FlagCriteriaContractor fromUpdate) {
        if (!StringUtils.equals(fromUpdate.getAnswer(), toUpdate.getAnswer())) {
            toUpdate.setAnswer(fromUpdate.getAnswer());
            toUpdate.setAuditColumns(new User(User.SYSTEM));
        }

        if (!StringUtils.isEmpty(fromUpdate.getAnswer2()) && !fromUpdate.getAnswer2().equals(toUpdate.getAnswer2())) {
            toUpdate.setAnswer2(fromUpdate.getAnswer2());
            toUpdate.setAuditColumns(new User(User.SYSTEM));
        }
        if (toUpdate.isVerified() != fromUpdate.isVerified()) {
            toUpdate.setVerified(fromUpdate.isVerified());
            toUpdate.setAuditColumns(new User(User.SYSTEM));
        }
    }

    public static void updateFlagData(FlagData toUpdate, FlagData fromUpdate) {
        if (!toUpdate.equals(fromUpdate))
            // Don't update flag data for the wrong contractor/operator/criteria
            return;

        if (!toUpdate.getFlag().equals(fromUpdate.getFlag())) {
            toUpdate.setFlag(fromUpdate.getFlag());
            toUpdate.setAuditColumns(new User(User.SYSTEM));
        }
    }

    /**
     * Uses the OshaVisitor to gather all the data
     *
     * @return
     */
    public static OshaOrganizer getOshaOrganizer(ContractorAccount contractorAccount) {
        OshaOrganizer oshaOrganizer = new OshaOrganizer();
        for (OshaAudit audit : getOshaAudits(contractorAccount)) {
            if (audit.isVerified()) {
                audit.accept(oshaOrganizer);
            }
        }

        return oshaOrganizer;
    }

    public static List<OshaAudit> getOshaAudits(ContractorAccount contractorAccount) {
        List<OshaAudit> oshaAudits = new ArrayList<OshaAudit>();

        for (ContractorAudit audit : contractorAccount.getAudits()) {
            if (AuditService.isAnnualAddendum(audit.getAuditType().getId())) {
                oshaAudits.add(new OshaAudit(audit));
            }
        }

        return oshaAudits;
    }

    public static ContractorOperator getForceOverallFlag(ContractorOperator contractorOperator) {
        if (isForcedFlag(contractorOperator))
            return contractorOperator;
        if (contractorOperator.getOperatorAccount().getCorporateFacilities().size() > 0) {
            for (Facility facility : contractorOperator.getOperatorAccount().getCorporateFacilities()) {
                for (ContractorOperator conOper : contractorOperator.getContractorAccount().getOperators()) {
                    if (facility.getCorporate().equals(conOper.getOperatorAccount()) && isForcedFlag(conOper))
                        return conOper;
                }
            }
        }
        return null;
    }

    public static boolean isForcedFlag(ContractorOperator contractorOperator) {
        if (contractorOperator.getForceFlag() == null || contractorOperator.getForceEnd() == null) {
            return false;
        }

        // We have a forced flag, but make sure it's still in effect
        if (contractorOperator.getForceEnd().before(new Date())) {
            return false;
        }
        return true;
    }
}