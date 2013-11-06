package com.picsauditing.actions.cron;

import com.picsauditing.jpa.entities.Indexable;
import com.picsauditing.util.IndexerEngine;

import java.util.Set;

public class RunConCronForOldAccountsWithBalances extends CronTaskOld {
    private static String NAME = "Indexer";
    private IndexerEngine indexer;

    public RunConCronForOldAccountsWithBalances(IndexerEngine indexer) {
        super(NAME);
        this.indexer = indexer;
    }

    protected void run() {
        Set<Class<? extends Indexable>> entries = indexer.getEntries();
        logger.debug("Found {1} entries", entries.size());
        indexer.runAll(entries);
    }
}
