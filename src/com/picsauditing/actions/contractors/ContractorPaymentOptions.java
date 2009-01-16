package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.util.BrainTree;

@SuppressWarnings("serial")
public class ContractorPaymentOptions extends ContractorActionSupport {
	static public String creditCard = "Credit Card";
	private String paymentMethod = creditCard;
	private String response_code = null;
	private String orderid = "";
	private String amount = "";
	private String response;
	private String responsetext;
	private String transactionid;
	private String avsresponse;
	private String cvvresponse;
	private String customer_vault_id;
	private String time;
	private String hash;
	private String key;
	private String key_id;
	private CreditCard cc;
	
	AppPropertyDAO appPropDao;
	
	public ContractorPaymentOptions(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AppPropertyDAO appPropDao, CreditCard cc) {
		super(accountDao, auditDao);
		this.appPropDao = appPropDao;
		this.cc = cc;
	}
	
	public String execute() throws Exception {
		// TODO allow contractors to edit this page during registration
		if (!forceLogin())
			return LOGIN;
		
		this.findContractor();
		
		if (paymentMethod.equals(creditCard)) {
			key = appPropDao.find("brainTree.key").getValue();
			key_id = appPropDao.find("brainTree.key_id").getValue();
		}
		
		if (response_code != null) {
			// Hey we're receiving some sort of response
			String newHash = BrainTree.buildHash(orderid, amount, 
					response, transactionid, avsresponse, cvvresponse, 
					customer_vault_id, time, key);
			if (!newHash.equals(hash))
				throw new Exception("Invalid hash from BrainTree");
			contractor.setPaymentMethod(creditCard);
			accountDao.save(contractor);
		}
		
		if (paymentMethod == null)
			paymentMethod = creditCard;
		if (!paymentMethod.equals(contractor.getPaymentMethod())) {
			// We have a new payment method, reset the status
			contractor.setPaymentMethod(paymentMethod);
			if (paymentMethod.equals(creditCard))
				contractor.setPaymentMethodStatus("Missing");
			else
				contractor.setPaymentMethodStatus("Pending");
			accountDao.save(contractor);
		}
		
		if (paymentMethod.equals(creditCard)) {
			AppProperty prop = appPropDao.find("brainTree.key");
			key = prop.getValue();
			customer_vault_id = contractor.getIdString();
			time = DateBean.getBrainTreeDate();
			hash = BrainTree.buildHash(orderid, amount, customer_vault_id, time, key);
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
	
	public void setResponse_code(String response_code) {
		this.response_code = response_code;
	}
	
	public void setAuthcode(String authcode) {
	}
	
	public void setResponsetext(String responsetext) {
		this.responsetext = responsetext;
	}
	
	public String getResponsetext() {
		return responsetext;
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

	public String getResponse1() {
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
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey_id() {
		return key_id;
	}

	public void setKey_id(String key_id) {
		this.key_id = key_id;
	}

	/******** End BrainTree Setters *******/
	
	
	
	public List<String> getCreditCardTypes() {
		List<String> types = new ArrayList<String>();
		types.add("Visa");
		types.add("Mastercard");
		return types;
	}


}