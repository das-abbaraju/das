package com.picsauditing.actions.billing;

import com.opensymphony.xwork2.Action;
import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.jpa.entities.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import scala.NotImplementedError;

import java.util.Calendar;
import java.util.Date;

public class ProductServiceQuery extends PicsActionSupport {

    @Autowired
    private AccountDAO dao;

    private static final String EMPLOYEE_GUARD = "EmployeeGUARD";
    private int accountID;
    private Account account;


    public void prepare() {
        account = dao.find(accountID);
    }

    @Anonymous
    public String execute() {
        return SUCCESS;
    }

    @Anonymous
    public String employeeGuardQuery() {
        prepare();
        if (account == null) return Action.ERROR;
        if (account.isContractor()) {
            ContractorAccount contractor = (ContractorAccount) account;
            if (recentInvoiceContainingFeeExists(contractor, FeeClass.EmployeeGUARD))
                return positiveJsonFor(EMPLOYEE_GUARD);
            else
                return negativeJsonFor(EMPLOYEE_GUARD);
        } else {
            throw new NotImplementedError();
        }
    }

    private boolean recentInvoiceContainingFeeExists(ContractorAccount contractor, FeeClass feeClass) {
        for (Invoice invoice : contractor.getSortedInvoices()) {
            if (invoice.getCreationDate().after(oneYearAgo())) {
                for (InvoiceItem invoiceItem : invoice.getItems()) {
                    if (invoiceItem.getInvoiceFee().getFeeClass().equals(feeClass))
                        return true;
                }
            }
        }
        return false;
    }

    private Date oneYearAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        return calendar.getTime();
    }

    private String positiveJsonFor(String value) {
        return setJSONResponse(value, true);
    }

    private String negativeJsonFor(String value) {
        return setJSONResponse(value, false);
    }

    private String setJSONResponse(String key, Boolean value) {
        JSONObject json = new JSONObject();
        json.put(key, value);
        setJson(json);
        return JSON;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }
}
