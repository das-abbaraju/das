package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.util.BrainTree;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class ContractorPaymentOptions extends ContractorActionSupport {
	@Autowired
	private InvoiceFeeDAO invoiceFeeDAO;
	@Autowired
	private BrainTreeService paymentService;

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
	private boolean newRegistration = false;

	private InvoiceFee activationFee;
	private InvoiceFee gstFee;
	private InvoiceFee vatFee;
	private InvoiceFee importFee;

	// Any time we do a get w/o an exception we set the communication status.
	// That way we know the information switched off of in the jsp is valid
	private boolean braintreeCommunicationError = false;
	@Autowired
	private AppPropertyDAO appPropDao;

	public ContractorPaymentOptions() {
		this.subHeading = getText(String.format("%s.title", getScope()));
	}

	public String execute() throws Exception {
		this.findContractor();

		// Only during registration - redirect if no requestedBy operator is set
		if (permissions.isContractor() && contractor.getStatus().isPending() && contractor.getRequestedBy() == null) {
			if (contractor.getNonCorporateOperators().size() == 1) {
				contractor.setRequestedBy(contractor.getNonCorporateOperators().get(0).getOperatorAccount());
			} else {
				String msg;
				if (contractor.getNonCorporateOperators().size() == 0)
					msg = getText("ContractorPaymentOptions.PleaseSelectFacilities");
				else
					msg = getText("ContractorPaymentOptions.SelectRequestedByOperator");
				this.redirect("ContractorFacilities.action?id=" + contractor.getId() + "&msg=" + msg);
				return BLANK;
			}
		}
		if (newRegistration) {
			addActionMessage(getText(
					"ContractorPaymentOptions.ImportPQFCreated",
					new Object[] {getText("PicsTollFreePhone")}));
		}

		if ("copyBillingEmail".equals(button)) {
			contractor.setCcEmail(contractor.getUsersByRole(OpPerms.ContractorBilling).get(0).getEmail());
		}

		contractorAccountDao.save(contractor);

		if (!contractor.getPaymentMethod().isCreditCard())
			return SUCCESS;

		// Setup the new variables for sending the CC to braintree
		loadCC();

		return SUCCESS;
	}

	private void loadCC() throws Exception {
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
				try {
					int endPos = responsetext.indexOf("REFID");
					if (endPos > 1)
						responsetext.substring(0, endPos - 1);
				} catch (Exception justUseThePlainResponseText) {
				}
				// TODO: test
				addActionError(getText(
						"ContractorPaymentOptions.GatewayCommunicationError",
						new Object[] { getText("PicsTollFreePhone") }));
			} else {
				contractor.setCcOnFile(true);
				contractor.setPaymentMethod(PaymentMethod.CreditCard);
				contractorAccountDao.save(contractor);
				addActionMessage(getText("ContractorPaymentOptions.SuccessfullyAddedCC"));
			}
		}

		// Response code not received, can either be transmission error or no
		// previous info entered

		if ("Delete".equalsIgnoreCase(button)) {
			try {
				paymentService.deleteCreditCard(contractor.getId());
				contractor.setCcOnFile(false);
			} catch (Exception x) {
				// TODO: Test
				addActionError(getText(
						"ContractorPaymentOptions.GatewayCommunicationError",
						new Object[] { getText("PicsTollFreePhone") }));
				braintreeCommunicationError = true;
				return;
			}
		}

		// Accounting for transmission errors which result in
		// exceptions being thrown.
		boolean transmissionError = true;
		int retries = 0, quit = 5;
		while (transmissionError && retries < quit) {
			try {
				cc = paymentService.getCreditCard(contractor.getId());
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
			// TODO: Test
			addActionError(getText(
					"ContractorPaymentOptions.GatewayCommunicationError",
					new Object[] { getText("PicsTollFreePhone") }));
			braintreeCommunicationError = true;
			return;
		}

		if (cc == null || cc.getCardNumber() == null) {
			contractor.setCcOnFile(false);
			contractor.setCcExpiration(null);
		} else if ((!contractor.isCcOnFile() && contractor.getCcExpiration() == null)
				|| (response_code != null && (Strings.isEmpty(responsetext) || response.equals("1")))) {
			contractor.setCcExpiration(cc.getExpirationDate2());
			contractor.setCcOnFile(true);
			// Need to set CcOnFile to true only in no-credit card case
			// (ccOnFile == False && expDate == null)
			// in case an insert was performed
			// properly, but PICS never received the response message.
			// Note: should not insert credit card info in invalid cc case:
			// (ccOnFile == False && expDate != null)
		}

		time = DateBean.getBrainTreeDate();
		hash = BrainTree.buildHash(orderid, amount, customer_vault_id, time, key);

		contractorAccountDao.save(contractor);
		return;
	}

	/** ******** DMI ******** */
	public String importPQF() throws Exception {
		findContractor();

		if (!isHasPQFImportAudit()) {
			this.redirect("CreateImportPQFAudit.action?id=" + contractor.getId()
					+ "&url=ContractorPaymentOptions.action");
		}

		return SUCCESS;
	}

	public String changePaymentToCheck() throws Exception {
		findContractor();
		contractor.setPaymentMethod(PaymentMethod.Check);
		contractorAccountDao.save(contractor);
		if (contractor.isCcOnFile())
			loadCC();

		return SUCCESS;
	}

	public String changePaymentToCC() throws Exception {
		findContractor();
		contractor.setPaymentMethod(PaymentMethod.CreditCard);
		contractorAccountDao.save(contractor);
		loadCC();

		return SUCCESS;
	}

	public String markCCInvalid() throws Exception {
		findContractor();
		contractor.setCcOnFile(false);
		contractorAccountDao.save(contractor);
		loadCC();

		return SUCCESS;
	}

	public String markCCValid() throws Exception {
		findContractor();
		contractor.setCcOnFile(true);
		contractorAccountDao.save(contractor);
		loadCC();

		return SUCCESS;
	}

	public String acceptContractorAgreement() throws Exception {
		findContractor();
		if (!permissions.isAdmin()
				&& (permissions.hasPermission(OpPerms.ContractorAdmin)
						|| permissions.hasPermission(OpPerms.ContractorBilling) || permissions
						.hasPermission(OpPerms.ContractorSafety))) {
			contractor.setAgreementDate(new Date());
			contractor.setAgreedBy(getUser());
			contractorAccountDao.save(contractor);
		} else {
			addActionError(getText("ContractorPaymentOptions.ContractorAgreementError"));
		}

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

	public void setNewRegistration(boolean newRegistration) {
		this.newRegistration = newRegistration;
	}

	public boolean isNewRegistration() {
		return newRegistration;
	}

	public InvoiceFee getActivationFee() {
		if (activationFee == null) {
			activationFee = new InvoiceFee();

			if (contractor.getStatus().isPendingDeactivated() && contractor.getAccountLevel().isFull()) {
				if (contractor.getMembershipDate() == null) {
					activationFee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.Activation, 1);
					activationFee.setAmount(FeeClass.Activation.getAdjustedFeeAmountIfNecessary(contractor,
							activationFee));
				} else {
					activationFee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.Reactivation, contractor
							.getPayingFacilities());
					activationFee.setAmount(FeeClass.Activation.getAdjustedFeeAmountIfNecessary(contractor,
							activationFee));
				}
			}
		}

		return activationFee;
	}

	public boolean isBraintreeCommunicationError() {
		return braintreeCommunicationError;
	}

	public void setBraintreeCommunicationError(boolean braintreeCommunicationError) {
		this.braintreeCommunicationError = braintreeCommunicationError;
	}

	public boolean isHasPQFImportAudit() {
		for (ContractorAudit ca : contractor.getAudits()) {
			if (ca.getAuditType().getId() == AuditType.IMPORT_PQF)
				return true;
		}

		return false;
	}

	public InvoiceFee getGstFee() {
		if (gstFee == null) {
			gstFee = new InvoiceFee();

			if (contractor.getCountry().getCurrency().isCAD()) {
				gstFee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.GST, contractor.getPayingFacilities());
				BigDecimal total = BigDecimal.ZERO.setScale(2);
				for (FeeClass feeClass : contractor.getFees().keySet()) {
					if (!contractor.getFees().get(feeClass).getNewLevel().isFree())
						total = total.add(contractor.getFees().get(feeClass).getNewAmount());
				}

				total = total.add(getActivationFee().getAmount());
				gstFee.setAmount(gstFee.getTax(total));
			}
		}

		return gstFee;
	}

	public void setGstFee(InvoiceFee gstFee) {
		this.gstFee = gstFee;
	}

	public InvoiceFee getVatFee() {
		if (vatFee == null) {
			vatFee = new InvoiceFee();

			if (contractor.getCountry().getCurrency().isEUR() || contractor.getCountry().getCurrency().isGBP()) {
				vatFee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.VAT, contractor.getPayingFacilities());
				BigDecimal total = BigDecimal.ZERO.setScale(2);
				for (FeeClass feeClass : contractor.getFees().keySet()) {
					if (!contractor.getFees().get(feeClass).getNewLevel().isFree())
						total = total.add(contractor.getFees().get(feeClass).getNewAmount());
				}

				total = total.add(getActivationFee().getAmount());
				vatFee.setAmount(vatFee.getTax(total));
			}
		}

		return vatFee;
	}

	public void setVatFee(InvoiceFee vatFee) {
		this.vatFee = vatFee;
	}

	public InvoiceFee getImportFee() {
		if (importFee == null) {
			importFee = new InvoiceFee();

			if (contractor.getFees().containsKey(FeeClass.ImportFee)
					&& contractor.getFees().get(FeeClass.ImportFee).isUpgrade()) {
				importFee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.ImportFee, 1);
			}
		}

		return importFee;
	}

	public InvoiceFee getImportFeeForTranslation() {
		return invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.ImportFee, 1);
	}
}
