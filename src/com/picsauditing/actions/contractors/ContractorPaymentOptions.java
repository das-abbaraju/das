package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.util.BrainTree;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class ContractorPaymentOptions extends ContractorActionSupport {
	private String response_code = null;
	private String orderid = "";
	private String amount = "";
	private String response;
	private String responsetext;
	private String transactionid;
	private String avsresponse;
	private String cvvresponse;
	private String customer_vault_id;
	private String customer_vault;
	private String time;
	private String hash;
	private String key;
	private String key_id;
	private String company;
	private CreditCard cc;
	private InvoiceFeeDAO invoiceFeeDAO;

	private InvoiceFee activationFee;
	// Any time we do a get w/o an exception we set the communication status.
	// That way we know the information switched off of in the jsp is valid
	private boolean braintreeCommunicationError = false;

	AppPropertyDAO appPropDao;

	public ContractorPaymentOptions(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AppPropertyDAO appPropDao, InvoiceFeeDAO invoiceFeeDAO) {
		super(accountDao, auditDao);
		this.appPropDao = appPropDao;
		this.invoiceFeeDAO = invoiceFeeDAO;
		this.subHeading = "Payment Options";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		this.findContractor();

		// Only during registration - redirect if no requestedBy operator is set
		if (permissions.isContractor() && contractor.getStatus().isPending() && contractor.getRequestedBy() == null) {
			if (contractor.getNonCorporateOperators().size() == 1) {
				contractor.setRequestedBy(contractor.getNonCorporateOperators().get(0).getOperatorAccount());
			} else {
				String msg;
				if (contractor.getNonCorporateOperators().size() == 0)
					msg = "Please select the facilities that you work or will work at.";
				else
					msg = "Please select the operator that referred you to PICS before continuing.";
				this.redirect("ContractorFacilities.action?id=" + contractor.getId() + "&msg=" + msg);
				return BLANK;
			}
		}

		// The payment method has changed.
		if ("Change Payment Method to Check".equalsIgnoreCase(button)) {
			contractor.setPaymentMethod(PaymentMethod.Check);
		}
		if ("Change Payment Method to Credit Card".equalsIgnoreCase(button)) {
			contractor.setPaymentMethod(PaymentMethod.CreditCard);
		}
		if ("copyBillingEmail".equals(button)) {
			contractor.setCcEmail(contractor.getUsersByRole(OpPerms.ContractorBilling).get(0).getEmail());
		}
		if ("Mark this Credit Card Invalid".equals(button)) {
			contractor.setCcOnFile(false);
		}

		accountDao.save(contractor);
		activationFee = null;
		if (contractor.getStatus().isPendingDeactivated()) {
			if (contractor.getMembershipDate() == null) {
				int feeID = InvoiceFee.ACTIVATION;
				if (BillingCalculatorSingle.hasReducedActivation(contractor)) {
					feeID = InvoiceFee.ACTIVATION99;
				}
				activationFee = invoiceFeeDAO.find(feeID);
			} else
				activationFee = invoiceFeeDAO.find(InvoiceFee.REACTIVATION);
		}

		if (!contractor.getPaymentMethod().isCreditCard())
			return SUCCESS;

		// Setup the new variables for sending the CC to braintree
		customer_vault_id = contractor.getIdString();

		// This is a credit card method
		key = appPropDao.find("brainTree.key").getValue();
		key_id = appPropDao.find("brainTree.key_id").getValue();

		// A response was received
		if (response_code != null) {
			String newHash = BrainTree.buildHash(orderid, amount, response, transactionid, avsresponse, cvvresponse,
					customer_vault_id, time, key);

			if (response.equals("3")) {
				PicsLogger.start("CC_Hash_Errors");
				PicsLogger.log("Hash issues for Contractor id= " + contractor.getIdString());
				PicsLogger.log("CREDIT CARD HASH PROBLEM: ");
				PicsLogger.log("CONTRACTOR ID: " + contractor.getIdString());
				PicsLogger.log("PICS HASH: " + newHash);
				PicsLogger.log("BASED ON:");
				PicsLogger.log("    RESPONSE     : " + response);
				PicsLogger.log("    TRANS ID     : " + transactionid);
				PicsLogger.log("    CUST VAULT ID: " + customer_vault_id);
				PicsLogger.log("    TIME         : " + time);
				PicsLogger.log("BRAINTREE HASH: " + hash);
				PicsLogger.log("BASED ON:");
				PicsLogger.log("    CUST VAULT ID: " + customer_vault_id + "\n\n");
				PicsLogger.stop();
			}

			if (!Strings.isEmpty(responsetext) && !response.equals("1")) {
				String errorMessage = responsetext;
				try {
					int endPos = responsetext.indexOf("REFID");
					if (endPos > 1)
						responsetext.substring(0, endPos - 1);
				} catch (Exception justUseThePlainResponseText) {
				}
				addActionError(errorMessage);
			} else {
				contractor.setCcOnFile(true);
				contractor.setPaymentMethod(PaymentMethod.CreditCard);
				accountDao.save(contractor);
				addActionMessage("Successfully added Credit Card");
			}
		}

		// Response code not received, can either be transmission error or no
		// previous info entered
		BrainTreeService ccService = new BrainTreeService();
		ccService.setUserName(appPropDao.find("brainTree.username").getValue());
		ccService.setPassword(appPropDao.find("brainTree.password").getValue());

		if ("Delete".equalsIgnoreCase(button)) {
			ccService.deleteCreditCard(contractor.getId());
			contractor.setCcOnFile(false);
		}

		// Accounting for transmission errors which result in
		// exceptions being thrown.
		boolean transmissionError = true;
		int retries = 0, quit = 5;
		while (transmissionError && retries < quit) {
			try {
				cc = ccService.getCreditCard(contractor.getId());
				transmissionError = false;
				braintreeCommunicationError = false;
			} catch (Exception communicationProblem) {
				// a message or packet could have been dropped in transmission
				// wait and resume retrying
				retries++;
				Thread.sleep(150);
			}
		}

		// Should exit immediately on communication error since we do not know
		// the true status of a contractor's account on braintree, and should
		// not show cc data
		if (retries >= quit) {
			addActionError("An network error has occured while communicating"
					+ " with our credit processing gateway. Please wait for twenty"
					+ " seconds and try refreshing this page. If you continue to see this "
					+ "message, or believe there is an error please contact PICS support.");
			braintreeCommunicationError = true;
			return SUCCESS;
		}

		if (cc == null || cc.getCardNumber() == null) {
			contractor.setCcOnFile(false);
			contractor.setCcExpiration(null);
		} else {
			contractor.setCcExpiration(cc.getExpirationDate2());
			contractor.setCcOnFile(true);
			// Need to set CcOnFile to true in case an insert was performed
			// properly, but PICS never received the response message.
		}

		time = DateBean.getBrainTreeDate();
		hash = BrainTree.buildHash(orderid, amount, customer_vault_id, time, key);

		accountDao.save(contractor);
		return SUCCESS;
	}

	/** ******** BrainTree Getters/Setters ******** */

	public void setResponse_code(String response_code) {
		this.response_code = response_code;
	}

	public void setAuthcode(String authcode) {
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

	public String getResponsetext() {
		return responsetext;
	}

	public void setResponsetext(String responsetext) {
		this.responsetext = responsetext;
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

	public String getCustomer_vault() {
		return customer_vault;
	}

	public void setCustomer_vault(String customer_vault) {
		this.customer_vault = customer_vault;
	}

	public String getCompany() {
		company = contractor.getName();
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	/** ****** End BrainTree Setters ****** */

	public List<String> getCreditCardTypes() {
		List<String> types = new ArrayList<String>();
		types.add("Visa");
		types.add("Mastercard");
		types.add("Discover Card");
		types.add("American Express");
		return types;
	}

	public CreditCard getCc() {
		return cc;
	}

	public InvoiceFee getActivationFee() {
		return activationFee;
	}

	public boolean isBraintreeCommunicationError() {
		return braintreeCommunicationError;
	}

	public void setBraintreeCommunicationError(boolean braintreeCommunicationError) {
		this.braintreeCommunicationError = braintreeCommunicationError;
	}
}
