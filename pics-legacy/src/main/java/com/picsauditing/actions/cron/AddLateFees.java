package com.picsauditing.actions.cron;

import com.picsauditing.PICS.BillingService;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.billing.AccountingSystemSynchronization;
import com.picsauditing.util.IndexerEngine;
import com.picsauditing.util.PicsDateFormat;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddLateFees implements CronTask {
    public static final int MINIMUM_LATE_FEE = 20;
    public static final double LATE_FEE_PERCENTAGE = 0.05;
    @Autowired
    IndexerEngine indexer;
    @Autowired
    InvoiceDAO invoiceDAO;
    @Autowired
    InvoiceItemDAO invoiceItemDAO;
    @Autowired
    InvoiceFeeDAO invoiceFeeDAO;
    @Autowired
    BillingService billingService;

    public String getDescription() {
        return "Add late fee invoices to accounts with delinquent invoices";
    }

    public List<String> getSteps() {
        List<String> steps = new ArrayList<>();
        List<Invoice> invoicesMissingLateFees = invoiceDAO.findDelinquentInvoicesMissingLateFees();
        for (Invoice i : invoicesMissingLateFees) {
            if (invoiceHasReactivation(i)) {
                continue;
            }
            steps.add("Will Create late fee invoice for delinquent invoice #" + i.getId());
        }
        return steps;
    }

    public CronTaskResult run() {
        CronTaskResult results = new CronTaskResult(true, "");
        List<Invoice> invoicesMissingLateFees = invoiceDAO.findDelinquentInvoicesMissingLateFees();
        if (invoicesMissingLateFees.size() == 0) {
            return results;
        }
        results.getLogger().append("Found " +
                invoicesMissingLateFees.size() + " invoices that require a late fee");
        for (Invoice i : invoicesMissingLateFees) {
            if (invoiceHasReactivation(i)) {
                results.getLogger().append("Skipping this invoice because it's a reactivation invoice: " + i.getId() + "\n\n");
                continue;
            }
            results.getLogger().append("Create late fee invoice for delinquent invoice " + i.getId() + "\n\n");
            addLateFeeToDelinquentInvoice(i);
        }
        return results;
    }

    private boolean invoiceHasReactivation(Invoice i) {
        for (InvoiceItem ii : i.getItems()) {
            if (ii.getInvoiceFee().isReactivation()) {
                return true;
            }
        }
        return false;
    }

    private Invoice addLateFeeToDelinquentInvoice(Invoice invoiceWhichIsLate) {
        // Calculate Late Fee
        BigDecimal lateFee = calculateLateFeeFor(invoiceWhichIsLate);

        InvoiceItem lateFeeItem = createLateFeeInvoiceItem(invoiceWhichIsLate, lateFee);

        Invoice lateFeeInvoice = generateLateFeeInvoice(invoiceWhichIsLate, lateFeeItem);

        lateFeeItem.setInvoice(lateFeeInvoice);

        AccountingSystemSynchronization.setToSynchronize(lateFeeInvoice);

        updateContractorAccountForLateInvoice(lateFeeInvoice);

        lateFeeInvoice = saveLateFeeInvoiceAndRelated(invoiceWhichIsLate, lateFeeItem, lateFeeInvoice);
        return lateFeeInvoice;
    }

    public BigDecimal calculateLateFeeFor(Invoice invoiceWhichIsLate) {
        BigDecimal lateFee = invoiceWhichIsLate.getTotalAmount().multiply(BigDecimal.valueOf(LATE_FEE_PERCENTAGE))
                .setScale(0, BigDecimal.ROUND_HALF_UP);
        if (lateFee.compareTo(BigDecimal.valueOf(MINIMUM_LATE_FEE)) < 1) {
            lateFee = BigDecimal.valueOf(MINIMUM_LATE_FEE);
        }
        return lateFee;
    }

    public InvoiceItem createLateFeeInvoiceItem(Invoice invoiceWhichIsLate, BigDecimal lateFee) {
        InvoiceFee fee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.LateFee,
                ((ContractorAccount) invoiceWhichIsLate.getAccount()).getPayingFacilities());
        InvoiceItem lateFeeItem = new InvoiceItem(fee);
        lateFeeItem.setAmount(lateFee);
        lateFeeItem.setOriginalAmount(lateFee);
        lateFeeItem.setAuditColumns(new User(User.SYSTEM));
        lateFeeItem.setDescription("Assessed "
                + new SimpleDateFormat(PicsDateFormat.American).format(new Date())
                + " due to delinquent payment on invoice #" + invoiceWhichIsLate.getId() + " which was due on " + invoiceWhichIsLate.getDueDate() + ".");
        return lateFeeItem;
    }

    private void updateContractorAccountForLateInvoice(Invoice lateFeeInvoice) {
        if (lateFeeInvoice.getAccount() instanceof ContractorAccount) {
            billingService.syncBalance(((ContractorAccount) lateFeeInvoice.getAccount()));
            invoiceItemDAO.save(lateFeeInvoice.getAccount());
        }
    }

    private Invoice saveLateFeeInvoiceAndRelated(Invoice invoiceWhichIsLate, InvoiceItem lateFeeItem, Invoice lateFeeInvoice) {
        try {
            lateFeeInvoice = (Invoice) billingService.saveInvoice(lateFeeInvoice);
        } catch (Exception e) {

        }

        lateFeeItem.setInvoice(lateFeeInvoice);
        lateFeeItem = (InvoiceItem) invoiceItemDAO.save(lateFeeItem);
        invoiceWhichIsLate.setLateFeeInvoice(lateFeeInvoice);
        Invoice savedInvoice = invoiceDAO.save(invoiceWhichIsLate);
        return savedInvoice;
    }

    public Invoice generateLateFeeInvoice(Invoice invoiceWhichIsLate, InvoiceItem lateFeeItem) {
        Invoice lateFeeInvoice = new Invoice();
        ContractorAccount contractorAccount = (ContractorAccount)invoiceWhichIsLate.getAccount();
        lateFeeInvoice.setAccount(contractorAccount);
        lateFeeInvoice.getItems().add(lateFeeItem);
        lateFeeInvoice.setCurrency(contractorAccount.getCurrency());
        lateFeeInvoice.updateTotalAmount();
        lateFeeInvoice.updateAmountApplied();
        lateFeeInvoice.setInvoiceType(InvoiceType.LateFee);
        lateFeeInvoice.setPayingFacilities(contractorAccount.getPayingFacilities());
        lateFeeInvoice.setAuditColumns(new User(User.SYSTEM));
        lateFeeInvoice.setDueDate(lateFeeInvoice.getCreationDate());
        return lateFeeInvoice;
    }


}
