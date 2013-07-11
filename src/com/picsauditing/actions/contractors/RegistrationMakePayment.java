package com.picsauditing.actions.contractors;

import com.picsauditing.PICS.*;
import com.picsauditing.PICS.data.DataEvent;
import com.picsauditing.PICS.data.DataObservable;
import com.picsauditing.PICS.data.InvoiceDataEvent;
import com.picsauditing.PICS.data.PaymentDataEvent;
import com.picsauditing.access.OpPerms;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.billing.BrainTree;
import com.picsauditing.braintree.BrainTreeHash;
import com.picsauditing.braintree.exception.NoBrainTreeServiceResponseException;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.model.billing.AccountingSystemSynchronization;
import com.picsauditing.model.billing.BillingNoteModel;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.braintree.exception.BrainTreeServiceErrorResponseException;
import com.picsauditing.braintree.CreditCard;
import com.picsauditing.validator.ContractorValidator;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class RegistrationMakePayment extends RegistrationAction {
	private static final String DELETE_BUTTON = "Delete";
	private static final String EMAIL_BUTTON = "email";

	private static final Logger logger = LoggerFactory.getLogger(RegistrationMakePayment.class);

	@Autowired
	private InvoiceService invoiceService;
	@Autowired
	private InvoiceFeeDAO invoiceFeeDAO;
	@Autowired
	private NoteDAO noteDAO;
	@Autowired
	private PaymentDAO paymentDAO;
	@Autowired
	private AppPropertyDAO appPropDao;
	@Autowired
	private BillingCalculatorSingle billingService;
	@Autowired
	private BrainTree paymentService;
	@Autowired
	private AuditBuilder auditBuilder;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private ContractorValidator contractorValidator;
	@Autowired
	private FeatureToggle featureToggleChecker;
	@Autowired
	private DataObservable saleCommissionDataObservable;
	@Autowired
	private BillingNoteModel billingNoteModel;

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
	private String ccName;

	private Invoice invoice;
	private boolean processPayment = false;
	private InvoiceFee importFee;

	// Any time we do a get w/o an exception we set the communication status.
	// That way we know the information switched off of in the jsp is valid
	private boolean braintreeCommunicationError = false;

	public RegistrationMakePayment() {
		this.currentStep = ContractorRegistrationStep.Payment;
	}

	private boolean complete = false;

	public String execute() throws Exception {
		findContractor();
		this.subHeading = getText(String.format("%s.title", getScope()));

		if (redirectIfNotReadyForThisStep()) {
			return BLANK;
		}

		if (!processPayment && generateOrUpdateInvoiceIfNecessary()) {
			return BLANK;
		}

		// Email proforma invoice
		if (EMAIL_BUTTON.equals(button)) {
			contractor.setPaymentMethod(PaymentMethod.EFT);
			contractorAccountDao.save(contractor);
			try {
				EventSubscriptionBuilder.contractorInvoiceEvent(contractor, invoice);
				addActionMessage(getText("RegistrationMakePayment.message.SentProFormaEmail"));
			} catch (Exception e) {
				addActionError(getText("RegistrationMakePayment.message.ProFormaEmailError"));
			}

			url = "Login.action";
			return REDIRECT;
		}

		loadCC();
		if (hasActionErrors()) {
			return SUCCESS;
		}

		if (processPayment) {
			completeRegistration();
			return BLANK;
		}

		return SUCCESS;
	}

	public String completeRegistration() throws Exception {
		findContractor();
		if (redirectIfNotReadyForThisStep()) {
			return BLANK;
		}

		Invoice invoice = getInvoice();

		contractorValidator.setOfficeLocationInPqfBasedOffOfAddress(contractor);

		auditBuilder.buildAudits(contractor);
		this.resetActiveAudits();

		// enforcing workflow steps before completing registration
		String url = contractorRiskUrl();

		if (!url.isEmpty()) {
			ServletActionContext.getResponse().sendRedirect(url);
			return BLANK;
		}

		if (contractor.isHasFreeMembership()) {
			// Free accounts should just be activated
			contractor.setStatus(AccountStatus.Active);
			contractor.setAuditColumns(permissions);
			contractor.setMembershipDate(new Date());
			if (contractor.getBalance() == null) {
				contractor.setBalance(BigDecimal.ZERO);
			}
			contractorAccountDao.save(contractor);
		} else {
			if (invoice != null && invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (contractor.isCcValid()) {

					Payment payment = null;
					try {
						payment = PaymentProcessor.PayOffInvoice(invoice, getUser(), PaymentMethod.CreditCard);
						paymentService.processPayment(payment, invoice);

						CreditCard creditCard = paymentService.getCreditCard(contractor);
						payment.setCcNumber(creditCard.getCardNumber());

						// Only if the transaction succeeds
						PaymentProcessor.ApplyPaymentToInvoice(payment, invoice, getUser(), payment.getTotalAmount());
						AccountingSystemSynchronization.setToSynchronize(payment);

						paymentDAO.save(payment);
						invoice.updateAmountApplied();
						billingService.performInvoiceStatusChangeActions(invoice, TransactionStatus.Paid);
						contractor.syncBalance();

						// Activate the contractor
						billingService.activateContractor(contractor, invoice);
						contractorAccountDao.save(contractor);

                        notifyDataChange(new PaymentDataEvent(payment, PaymentDataEvent.PaymentEventType.SAVE));

						addNote(contractor, "Credit Card transaction completed and emailed the receipt for "
								+ invoice.getCurrency().getSymbol() + invoice.getTotalAmount(), NoteCategory.Billing,
								LowMedHigh.High, true, Account.EVERYONE,
								billingNoteModel.findUserForPaymentNote(permissions), null);
					} catch (NoBrainTreeServiceResponseException re) {
						addNote("Credit Card service connection error: " + re.getMessage());

						EmailBuilder emailBuilder = new EmailBuilder();
						emailBuilder.setTemplate(106);
						emailBuilder.setFromAddress(EmailAddressUtils.PICS_IT_TEAM_EMAIL);
						emailBuilder.setToAddresses(EmailAddressUtils.getBillingEmail(contractor.getCurrency()));
						emailBuilder.setPermissions(permissions);
						emailBuilder.addToken("permissions", permissions);
						emailBuilder.addToken("contractor", contractor);
						emailBuilder.addToken("billingusers", contractor.getUsersByRole(OpPerms.ContractorBilling));
						emailBuilder.addToken("invoice", invoice);

						EmailQueue emailQueue;
						try {
							emailQueue = emailBuilder.build();
							emailQueue.setVeryHighPriority();
							emailQueue.setSubjectViewableById(Account.PicsID);
							emailQueue.setBodyViewableById(Account.PicsID);

							emailSender.send(emailQueue);
						} catch (Exception e) {
							logger.error("Cannot send email error message or "
									+ "determine credit processing status for contractor {} ({}) for invoice {}",
									new Object[]{contractor.getName(), contractor.getId(), invoice.getId()});
						}

						addActionError(getText("ContractorRegistrationFinish.error.ConnectionFailure",
								getText("PicsBillingPhone")));

						// Assuming paid status per Aaron so that he can
						// refund or void manually.
						payment.setStatus(TransactionStatus.Unpaid);
						paymentDAO.save(payment);

						return SUCCESS;
					} catch (BrainTreeServiceErrorResponseException e) {
						addNote("Credit Card transaction failed: " + e.getMessage());
						addActionError(getText("ContractorRegistrationFinish.error.CreditCardFailure") + " "
								+ e.getMessage());
						return SUCCESS;
					}
				}

				// Send a receipt to the contractor
				try {
					EventSubscriptionBuilder.contractorInvoiceEvent(contractor, invoice);
				} catch (Exception theyJustDontGetAnEmail) {
				}
			}
		}

		featureToggleChecker.addToggleVariable("contractor", contractor);
		if (contractor.getCountry().getIsoCode().equals("CA")
				&& featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_LCCOR)) {
			contractor.setLcCorPhase(LcCorPhase.RemindMeLater);
			contractor.setLcCorNotification(new Date());
			contractorAccountDao.save(contractor);
		}

		complete = true;

		if (!contractor.getAccountLevel().isBidOnly() && !contractor.isRenew()) {
			contractor.setRenew(true);
			contractorAccountDao.save(contractor);
		}

		closeRelatedRegistrationRequests();
		if (contractor.getStatus().equals(AccountStatus.Requested)) {
			contractor.setStatus(AccountStatus.Active);
			contractorAccountDao.save(contractor);
		}

		// Reload permissions for this user so they view just their country
		// specific questions.
		if (complete) {
			permissions.setAccountPerms(getUser());
		}

		if ("Check".equals(ccName) && contractor.getNewMembershipAmount().intValue() > 500) {
			contractor.setPaymentMethod(PaymentMethod.Check);
			contractorAccountDao.save(contractor);
			return SUCCESS;
		}

		if (contractor.getLcCorPhase() != null) {
			ServletActionContext.getResponse().sendRedirect("GetLcCorQuote.action?id=" + contractor.getId());
		} else {
			ServletActionContext.getResponse().sendRedirect(getRegistrationStep().getUrl());
		}
		return BLANK;
	}

	private String contractorRiskUrl() {
		String url = "";
		if ((LowMedHigh.None.equals(contractor.getSafetyRisk()) && !(contractor.isMaterialSupplierOnly() || contractor
				.isTransportationServices()))
				|| (LowMedHigh.None.equals(contractor.getProductRisk()) && contractor.isMaterialSupplier())) {
			url = "RegistrationServiceEvaluation.action?id=" + contractor.getId();

			addActionMessage(getText("ContractorRegistrationFinish.message.SelectService"));
		} else if (contractor.getNonCorporateOperators().size() == 0) {
			url = "AddClientSite.action?id=" + contractor.getId();

			addActionMessage(getText("ContractorRegistrationFinish.message.AddFacility"));
		}
		return url;
	}

	private void addNote(String subject) {
		Note note = new Note(contractor, billingNoteModel.findUserForPaymentNote(permissions), subject);
		note.setNoteCategory(NoteCategory.Billing);
		note.setCanContractorView(true);
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}

	public boolean isComplete() {
		return complete;
	}

	private void loadCC() throws Exception {
		// Setup the new variables for sending the CC to BrainTree
		customer_vault_id = contractor.getIdString();

		// This is a credit card method
		key = appPropDao.find("brainTree.key").getValue();
		key_id = appPropDao.find("brainTree.key_id").getValue();

		// A response was received

        if (response_code != null) {
			String newHash = BrainTreeHash.buildHash(orderid, amount, response, transactionid, avsresponse, cvvresponse,
                    customer_vault_id, time, key);

			if (response.equals("3")) {
				Marker ccHashErrors = MarkerFactory.getMarker("CC Hash Errors");

				logger.error(ccHashErrors, "CC_Hash_Errors");
				logger.error(ccHashErrors, "Hash issues for Contractor id = {}", contractor.getId());
				logger.error(ccHashErrors, "CREDIT CARD HASH PROBLEM: ");
				logger.error(ccHashErrors, "CONTRACTOR ID: {}", contractor.getIdString());
				logger.error(ccHashErrors, "PICS HASH: {}", newHash);
				logger.error(ccHashErrors, "BASED ON:");
				logger.error(ccHashErrors, "    RESPONSE     : {}", response);
				logger.error(ccHashErrors, "    TRANS ID     : {}", transactionid);
				logger.error(ccHashErrors, "    CUST VAULT ID: {}", customer_vault_id);
				logger.error(ccHashErrors, "    TIME         : ()", time);
				logger.error(ccHashErrors, "BRAINTREE HASH: {}", hash);
				logger.error(ccHashErrors, "BASED ON:");
				logger.error(ccHashErrors, "    CUST VAULT ID: {}", customer_vault_id);
			}

			if (!Strings.isEmpty(responsetext) && !response.equals("1")) {
				try {
					int endPos = responsetext.indexOf("REFID");
					if (endPos > 1) {
						responsetext = responsetext.substring(0, endPos - 1);
					}
				} catch (Exception justUseThePlainResponseText) {
				}

				addActionError(getText("ContractorPaymentOptions.SessionExpired"));
			} else {
				contractor.setCcOnFile(true);
				contractor.setPaymentMethod(PaymentMethod.CreditCard);
				contractorAccountDao.save(contractor);
				addActionMessage(getText("ContractorPaymentOptions.SuccessfullyAddedCC"));
			}
		}

		// Response code not received, can either be transmission error or no
		// previous info entered

		if (DELETE_BUTTON.equalsIgnoreCase(button)) {
			try {
				paymentService.deleteCreditCard(contractor);
				contractor.setCcOnFile(false);
			} catch (Exception x) {
				// TODO: Test
				addActionError(getText("ContractorPaymentOptions.GatewayCommunicationError",
						new Object[]{getPicsPhoneNumber()}));
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
				cc = paymentService.getCreditCard(contractor);
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
		// the true status of a contractor's account on BrainTree, and should
		// not show cc data
		if (retries >= quit) {
			// TODO: Test
			addActionError(getText("ContractorPaymentOptions.GatewayCommunicationError",
					new Object[]{getPicsPhoneNumber()}));
			braintreeCommunicationError = true;
			return;
		}

		if (cc == null || cc.getCardNumber() == null) {
			contractor.setCcOnFile(false);
			contractor.setCcExpiration(null);
		} else if ((!contractor.isCcOnFile() && contractor.getCcExpiration() == null)
				|| (response_code != null && (Strings.isEmpty(responsetext) || response.equals("1")))) {
			contractor.setCcExpiration(cc.getExpirationDate());
			contractor.setCcOnFile(true);
			// Need to set CcOnFile to true only in no-credit card case
			// (ccOnFile == False && expDate == null)
			// in case an insert was performed
			// properly, but PICS never received the response message.
			// Note: should not insert credit card info in invalid CC case:
			// (ccOnFile == False && expDate != null)
		}

		time = DateBean.getBrainTreeDate();
		hash = BrainTreeHash.buildHash(orderid, amount, customer_vault_id, time, key);

		contractorAccountDao.save(contractor);
	}

	@Override
	public ContractorRegistrationStep getNextRegistrationStep() {
		if (permissions.isContractor() && contractor.getStatus().isPendingRequestedOrDeactivated()
				&& (contractor.isPaymentMethodStatusValid() || !contractor.isMustPayB())) {
			return ContractorRegistrationStep.Done;
		}

		return null;
	}

	public String changePaymentToCC() throws Exception {
		findContractor();
		contractor.setPaymentMethod(PaymentMethod.CreditCard);
		contractorAccountDao.save(contractor);
		loadCC();

		return this.setUrlForRedirect("RegistrationMakePayment.action");
	}

	/**
	 * ******* BrainTree Getters/Setters ********
	 */

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

	/**
	 * ****** End BrainTree Setters ******
	 *
	 * @throws Exception
	 */

	public List<String> getCreditCardTypes() throws Exception {
		findContractor();
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

	public boolean isBraintreeCommunicationError() {
		return braintreeCommunicationError;
	}

	public void setBraintreeCommunicationError(boolean braintreeCommunicationError) {
		this.braintreeCommunicationError = braintreeCommunicationError;
	}

	public boolean isProcessPayment() {
		return processPayment;
	}

	public void setProcessPayment(boolean processPayment) {
		this.processPayment = processPayment;
	}

	public InvoiceFee getImportFee() {
		if (importFee == null) {
			importFee = invoiceFeeDAO.find(InvoiceFee.IMPORTFEE);
		}
		return importFee;
	}

	public String removeImportFee() throws Exception {
		findContractor();
		billingService.removeImportPQF(contractor);
		generateOrUpdateInvoiceIfNecessary();

		if (!Strings.isEmpty(url)) {
			return REDIRECT;
		}

		return BLANK;
	}

	public String addImportFee() throws Exception {
		findContractor();
		billingService.addImportPQF(contractor, permissions);
		generateOrUpdateInvoiceIfNecessary();

		if (!Strings.isEmpty(url)) {
			return REDIRECT;
		}

		return BLANK;
	}

	public String printInvoice() throws Exception {
		findContractor();

		return "print";
	}

	public void setCcName(String ccName) {
		this.ccName = ccName;
	}

	public String getCcName() {
		return ccName;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Invoice getInvoice() throws Exception {
		if (invoice == null) {
			findContractor();
			invoice = contractor.findLastUnpaidInvoice();
		}

		return invoice;
	}

	private boolean generateOrUpdateInvoiceIfNecessary() throws Exception {
		billingService.calculateContractorInvoiceFees(contractor, false);
		invoice = contractor.findLastUnpaidInvoice();

		if (invoice == null) {
			invoice = billingService.createInvoice(contractor, getUser());
			contractor.getInvoices().add(invoice);
			invoiceService.saveInvoice(invoice);
			contractor.syncBalance();
			contractorAccountDao.save(contractor);
			notifyDataChange(new InvoiceDataEvent(invoice, InvoiceDataEvent.InvoiceEventType.NEW));
			ServletActionContext.getResponse().sendRedirect("RegistrationMakePayment.action");
			return true;
		}

		Invoice newInvoice = billingService.createInvoice(contractor, BillingStatus.Activation, getUser());
		if (contractor.isHasMembershipChanged()
				|| (newInvoice != null && !invoice.getTotalAmount().equals(newInvoice.getTotalAmount()))) {
			billingService.updateInvoice(invoice, newInvoice, getUser());
			contractor.syncBalance();
			contractorAccountDao.save(contractor);
			notifyDataChange(new InvoiceDataEvent(invoice, InvoiceDataEvent.InvoiceEventType.NEW));
			ServletActionContext.getResponse().sendRedirect("RegistrationMakePayment.action");
			return true;
		}

		return false;
	}

	private <T> void notifyDataChange(DataEvent<T> dataEvent) {
		saleCommissionDataObservable.setChanged();
		saleCommissionDataObservable.notifyObservers(dataEvent);
	}

	private void closeRelatedRegistrationRequests() {
		List<ContractorRegistrationRequest> linkedRequests = dao.findWhere(ContractorRegistrationRequest.class,
				"t.contractor.id = " + contractor.getId());
		for (ContractorRegistrationRequest request : linkedRequests) {
			ContractorRegistrationRequestStatus status = ContractorRegistrationRequestStatus.ClosedSuccessful;

			if (request.getContactCountByPhone() > 0) {
				status = ContractorRegistrationRequestStatus.ClosedContactedSuccessful;
			}

			request.setStatus(status);
			dao.save(request);
		}
	}
}
