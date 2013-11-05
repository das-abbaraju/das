package com.picsauditing.actions.cron;

import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.Indexable;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.IndexerEngine;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class IndexerTask extends CronTask {
    private static String NAME = "Indexer";
    private IndexerEngine indexer;

    public IndexerTask(IndexerEngine indexer) {
        super(NAME);
        this.indexer = indexer;
    }

    protected void run() {
        Set<Class<? extends Indexable>> entries = indexer.getEntries();
        logger.debug("Found {1} entries", entries.size());
        indexer.runAll(entries);
    }
}
