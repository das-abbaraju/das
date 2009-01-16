package com.picsauditing.actions.contractors;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

@SuppressWarnings("serial")
public class ContractorPaymentOptions extends ContractorActionSupport {
	protected String responseCode = null;
	
	public ContractorPaymentOptions(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		this.findContractor();
		this.subHeading = "Payment Options";

		return SUCCESS;
	}

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
}