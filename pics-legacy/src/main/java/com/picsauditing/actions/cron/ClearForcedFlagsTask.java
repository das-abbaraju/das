package com.picsauditing.actions.cron;

import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.FlagDataOverrideDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.util.IndexerEngine;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ClearForcedFlagsTask extends CronTask {
    private static String NAME = "ClearForcedFlags";
    private FlagDataOverrideDAO flagDataOverrideDAO;
    private ContractorOperatorDAO contractorOperatorDAO;
    private User system = new User(User.SYSTEM);

    public ClearForcedFlagsTask(FlagDataOverrideDAO flagDataOverrideDAO, ContractorOperatorDAO contractorOperatorDAO) {
        super(NAME);
        this.flagDataOverrideDAO = flagDataOverrideDAO;
        this.contractorOperatorDAO = contractorOperatorDAO;
    }

    protected void run() {
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
    }
}
