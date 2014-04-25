package com.picsauditing.flagcalculator.service;

import com.picsauditing.flagcalculator.entities.*;
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

}
