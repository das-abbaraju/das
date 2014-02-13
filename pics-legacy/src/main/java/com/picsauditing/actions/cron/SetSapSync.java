package com.picsauditing.actions.cron;

import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.jpa.entities.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class SetSapSync implements CronTask {
    @Autowired
    InvoiceDAO invoiceDAO;

    public String getDescription() {
        return "Set all Valid SAP Transaction to Sync after 24 hrs";
    }

    public List<String> getSteps() {
        List<String> steps = new ArrayList<>();
        List<Transaction> transactions = invoiceDAO.findTransactionsToSapSync();
        for (Transaction t : transactions) {
            steps.add("Will Sync for invoice #" + t.getId());
        }
        return steps;
    }

    public CronTaskResult run() throws Exception {
        CronTaskResult results = new CronTaskResult(true, "");

        List<String> steps = getSteps();
        for (String step : steps) {
            results.getLogger().append(step);
        }

        invoiceDAO.updateTransactionsToSapSync();

        return results;
    }
}
