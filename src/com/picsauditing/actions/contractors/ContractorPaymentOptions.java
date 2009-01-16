package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

@SuppressWarnings("serial")
public class ContractorPaymentOptions extends ContractorActionSupport {
	
	private String paymentMethod = "Credit Card";
	private String responseCode = null;
	private String orderid = null;
	private String amount = null;
	private String response = null;
	private String transactionid = null;
	private String avsresponse = null;
	private String cvvresponse = null;
	private String customer_vault_id = null;
	private String time = null;
	private String hash = null;
	
	public ContractorPaymentOptions(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}
	
	public String execute() throws Exception {
		// TODO allow contractors to edit this page during registration
		if (!forceLogin())
			return LOGIN;

		this.findContractor();
		
		if (responseCode != null) {
			// Hey we're receiving some sort of response
			// TODO Verify the authenticity of the response
			
		}
		
		if (paymentMethod == null)
			paymentMethod = "Credit Card";
		if (!paymentMethod.equals(contractor.getPaymentMethod())) {
			// We have a new payment method, reset the status
			contractor.setPaymentMethod(paymentMethod);
			if (paymentMethod.equals("Credit Card"))
				contractor.setPaymentMethodStatus("Missing");
			else
				contractor.setPaymentMethodStatus("Pending");
			this.accountDao.save(contractor);
		}
		
		if (button != null) {
			if (button.equals("ApplyForCredit")) {
				
			}
			if (button.equals("EditCreditCard")) {
				
			}
		}
		
		return SUCCESS;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	/********** BrainTree Setters *********/
	
	public void setResponse_code(String responseCode) {
		this.responseCode = responseCode;
	}
	
	public void setAuthcode(String authcode) {
	}
	
	public void setResponsetext(String responsetext) {
	}
	
	public void setType(String type) {
	}
	
	public void setUsername(String username) {
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getTransactionid() {
		return transactionid;
	}

	public void setTransactionid(String transactionid) {
		this.transactionid = transactionid;
	}

	public String getAvsresponse() {
		return avsresponse;
	}

	public void setAvsresponse(String avsresponse) {
		this.avsresponse = avsresponse;
	}

	public String getCvvresponse() {
		return cvvresponse;
	}

	public void setCvvresponse(String cvvresponse) {
		this.cvvresponse = cvvresponse;
	}

	public String getCustomer_vault_id() {
		return customer_vault_id;
	}

	public void setCustomer_vault_id(String customer_vault_id) {
		this.customer_vault_id = customer_vault_id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
	/******** End BrainTree Setters *******/
	
	
	
	public List<String> getCreditCardTypes() {
		List<String> types = new ArrayList<String>();
		types.add("Visa");
		types.add("Mastercard");
		return types;
	}
}