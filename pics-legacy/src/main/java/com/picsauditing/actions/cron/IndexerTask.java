package com.picsauditing.actions.cron;

import com.picsauditing.jpa.entities.Indexable;
import com.picsauditing.util.IndexerEngine;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

public class IndexerTask implements CronTask {
    @Autowired
    private IndexerEngine indexer;

    public String getDescription() {
        return "Re Index all the contractor names";
    }

    public List<String> getSteps() {
        return null;
    }

    public CronTaskResult run() {
        CronTaskResult results = new CronTaskResult(true, "");
        Set<Class<? extends Indexable>> entries = indexer.getEntries();
        results.getLogger().append("Found " +
                entries.size() + " entries");
        indexer.runAll(entries);
        return results;
    }
}
