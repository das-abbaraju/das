package com.picsauditing.actions.cron;

import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.FlagDataOverrideDAO;
import com.picsauditing.jpa.entities.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClearForcedFlagsTask implements CronTask {
    @Autowired
    private FlagDataOverrideDAO flagDataOverrideDAO;
    @Autowired
    private ContractorOperatorDAO contractorOperatorDAO;
    private User system = new User(User.SYSTEM);

    public String getDescription() {
        return "Clear expired forced flags";
    }

    public List<String> getSteps() {
        List<String> steps = new ArrayList<>();
        List<FlagDataOverride> fdos = flagDataOverrideDAO.findExpiredForceFlags();
        for (FlagDataOverride fdo : fdos) {
            steps.add("Clear Forced " + fdo.getCriteria().getCategory() + " Flag for the relationship of " + fdo.getContractor().getName() + " and " + fdo.getOperator().getName());
        }
        List<ContractorOperator> overrides = contractorOperatorDAO.findExpiredForceFlags();
        for (ContractorOperator override : overrides) {
            steps.add("Clear Forced Overall Flag for the relationship of " + override.getContractorAccount().getName() + " and " + override.getOperatorAccount().getName());
        }
        return steps;
    }

    public CronTaskResult run() {
        CronTaskResult results = new CronTaskResult(true, "");
        List<FlagDataOverride> fdos = flagDataOverrideDAO.findExpiredForceFlags();

        Iterator<FlagDataOverride> fdoIter = fdos.iterator();
        while (fdoIter.hasNext()) {
            FlagDataOverride fdo = fdoIter.next();

            // save history
            FlagOverrideHistory foh = new FlagOverrideHistory();
            foh.setOverride(fdo);
            foh.setAuditColumns(system);
            foh.setDeleted(false);
            foh.setDeleteReason("Flag Data Override Expired");
            contractorOperatorDAO.save(foh);

            // Create note & Delete override
            Note note = new Note(fdo.getContractor(), system, "Forced " + fdo.getCriteria().getLabel() + " Flag to "
                    + fdo.getForceflag() + " Expired for " + fdo.getContractor().getName());
            note.setCanContractorView(true);
            note.setPriority(LowMedHigh.Med);
            note.setNoteCategory(NoteCategory.Flags);
            note.setAuditColumns(system);
            note.setViewableBy(fdo.getOperator());
            contractorOperatorDAO.save(note);

            flagDataOverrideDAO.remove(fdo);
            fdoIter.remove();
        }

        List<ContractorOperator> overrides = contractorOperatorDAO.findExpiredForceFlags();

        Iterator<ContractorOperator> overrideIter = overrides.iterator();
        while (overrideIter.hasNext()) {
            ContractorOperator override = overrideIter.next();

            // save history
            FlagOverrideHistory foh = new FlagOverrideHistory();
            foh.setOverride(override);
            foh.setAuditColumns(system);
            foh.setDeleted(false);
            foh.setDeleteReason("Overall Flag Override Expired");
            flagDataOverrideDAO.save(foh);

            // Create note & Remove override
            Note note = new Note(override.getContractorAccount(), system, "Overall Forced Flag to "
                    + override.getFlagColor() + " Expired for " + override.getContractorAccount().getName());
            note.setCanContractorView(true);
            note.setPriority(LowMedHigh.Med);
            note.setNoteCategory(NoteCategory.Flags);
            note.setAuditColumns(system);
            note.setViewableBy(override.getOperatorAccount());
            flagDataOverrideDAO.save(note);

            override.setForceEnd(null);
            override.setForceFlag(null);
            override.setForceBegin(null);
            override.setForcedBy(null);

            contractorOperatorDAO.save(override);
        }
        return results;
    }
}
