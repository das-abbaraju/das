package com.picsauditing.report.service;

import com.intuit.developer.adaptors.*;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Payment;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class ReportQBService {

    @Autowired
    private ContractorAccountDAO contractorAccountDAO;
    @Autowired
    private InvoiceDAO invoiceDAO;
    @Autowired
    private PaymentDAO paymentDAO;

    public List<ContractorAccount> getContractorsToInsert(Currency currency) {
        String whereClause = InsertContractors.getWhereClause(currency);
        return contractorAccountDAO.findWhere(whereClause);
    }

    public List<Invoice> getInvoicesToInsert(Currency currency) {
        String whereClause = InsertInvoices.getWhereClause(currency);
        return invoiceDAO.findWhere(whereClause, 10);
    }

    public List<Payment> getPaymentsToInsert(Currency currency) {
        String whereClause = InsertPayments.getWhereClause(currency);
        return paymentDAO.findWhere(whereClause, 10);
    }

    public List<ContractorAccount> getContractorsForUpdate(Currency currency) {
        String whereClause = GetContractorsForUpdate.getWhereClause(currency);
        return contractorAccountDAO.findWhere(whereClause);
    }

    public List<Invoice> getInvoicesForUpdate(Currency currency) {
        String whereClause = GetInvoicesForUpdate.getWhereClause(currency);
        return invoiceDAO.findWhere(whereClause, 10);
    }

    public List<Payment> getPaymentsForUpdate(Currency currency) {
        String whereClause = GetPaymentsForUpdate.getWhereClause(currency);
        return paymentDAO.findWhere(whereClause, 10);
    }



}
