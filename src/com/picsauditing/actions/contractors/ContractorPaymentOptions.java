package com.picsauditing.actions.contractors;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

@SuppressWarnings("serial")
public class ContractorPaymentOptions extends ContractorActionSupport {
	
	private String paymentMethod = "Credit Card";
	private String responseCode = null;
	
	public ContractorPaymentOptions(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}
	
	public String execute() throws Exception {
		// TODO allow contractors to edit this page during registration
		if (!forceLogin())
			return LOGIN;

		this.findContractor();
		
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
	
	public void setAmount(String amount) {
	}
	
	public void setAuthcode(String authcode) {
	}
	
	public void setAvsresponse(String avsresponse) {
	}
	
	public void setCvvresponse(String cvvresponse) {
	}
	
	public void setHash(String hash) {
	}
	
	public void setOrderid(String orderid) {
	}
	
	public void setResponse(String response) {
	}
	
	public void setResponsetext(String responsetext) {
	}
	
	public void setTime(String time) {
	}
	
	public void setTransactionid(String transactionid) {
	}
	
	public void setType(String type) {
	}
	
	public void setUsername(String username) {
	}
	/******** End BrainTree Setters *******/
}