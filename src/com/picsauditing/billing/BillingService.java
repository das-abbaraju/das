package com.picsauditing.billing;

import java.util.List;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.BillingStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.TransactionStatus;
import com.picsauditing.jpa.entities.User;

/**
 * Defines the operations provided by the BillingService.
 */
public interface BillingService {

	public static final String CONTRACTORS_PAY_NO = "No";
	public static final String CONTRACTORS_PAY_YES = "Yes";
	public static final String CONTRACTORS_PAY_MULTIPLE = "Multiple";
	
	/**
	 * Builds a String that is the concatenation of all the Client site this
	 * Contractor works for.
	 */
	String getOperatorsString(ContractorAccount contractor);
	
	/**
	 * 
	 */
	void performInvoiceStatusChangeActions(Invoice invoice, TransactionStatus newStatus);
	
	boolean removeImportPQF(ContractorAccount contractor);
	
	void addImportPQF(ContractorAccount contractor, Permissions permissions);
	
	void setPayingFacilities(ContractorAccount contractor);
	
	void calculateAnnualFees(ContractorAccount contractor);
	
	void updateInvoice(Invoice toUpdate, Invoice updateWith, User user) throws Exception;
	
	Invoice createInvoice(ContractorAccount contractor, User user);
	
	Invoice createInvoice(ContractorAccount contractor, BillingStatus billingStatus, User user);
	
	List<InvoiceItem> createInvoiceItems(ContractorAccount contractor, User user);
	
	List<InvoiceItem> createInvoiceItems(ContractorAccount contractor, BillingStatus billingStatus, User user);
	
	boolean activateContractor(ContractorAccount contractor, Invoice invoice);
	
}
