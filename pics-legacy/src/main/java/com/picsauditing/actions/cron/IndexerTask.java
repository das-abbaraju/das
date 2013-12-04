package com.picsauditing.actions.cron;

import com.picsauditing.jpa.entities.Indexable;
import com.picsauditing.util.IndexerEngine;
import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IndexerTask implements CronTask {
    @Autowired
    private IndexerEngine indexer;

    public String getDescription() {
        return "Reindex all indexable values in PICS ORG";
    }

    public List<String> getSteps() {
        List<String> steps = new ArrayList<>();
        Set<Class<? extends Indexable>> entries = indexer.getEntries();
        for (Class clazz : entries) {
            steps.add("reindexing " + clazz.toString() + "s");
        }
        return steps;
    }

    public CronTaskResult run() {
        CronTaskResult results = new CronTaskResult(true, "");
        Set<Class<? extends Indexable>> entries = indexer.getEntries();
        indexer.runAll(entries);
        return results;
    }
}
