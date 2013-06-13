package com.picsauditing.actions.contractors;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.*;
import com.picsauditing.PICS.data.DataEvent;
import com.picsauditing.PICS.data.DataObservable;
import com.picsauditing.PICS.data.InvoiceDataEvent;
import com.picsauditing.PICS.data.InvoiceDataEvent.InvoiceEventType;
import com.picsauditing.PICS.data.PaymentDataEvent;
import com.picsauditing.PICS.data.PaymentDataEvent.PaymentEventType;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.billing.BrainTree;
import com.picsauditing.braintree.exception.NoBrainTreeServiceResponseException;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.model.account.AccountStatusChanges;
import com.picsauditing.model.billing.AccountingSystemSynchronization;
import com.picsauditing.model.billing.BillingNoteModel;
import com.picsauditing.model.billing.CommissionDetail;
import com.picsauditing.model.billing.InvoiceModel;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.braintree.CreditCard;
import com.picsauditing.util.log.PicsLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("serial")
public class InvoiceDetail extends ContractorActionSupport implements Preparable {

	private static final String PAY_BUTTON = "pay";
	private static final String CANCEL_BUTTON = "cancel";
	private static final String SAVE_BUTTON = "save";
	private static final String CHANGE_TO_BUTTON = "changeto";
	private static final String EMAIL_BUTTON = "email";

	@Autowired
	private InvoiceService invoiceService;
	@Autowired
	private InvoiceDAO invoiceDAO;
	@Autowired
	private InvoiceItemDAO invoiceItemDAO;
	@Autowired
	private InvoiceFeeDAO invoiceFeeDAO;
	@Autowired
	private PaymentDAO paymentDAO;
	@Autowired
	private NoteDAO noteDAO;
	@Autowired
	private BillingCalculatorSingle billingService;
	@Autowired
	private BrainTree paymentService;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private DataObservable salesCommissionDataObservable;
	@Autowired
	private InvoiceModel invoiceModel;
	@Autowired
	private BillingNoteModel billingNoteModel;

	private boolean edit = false;
	private int newFeeId;
	private Invoice invoice;
	private List<InvoiceFee> feeList = null;
	private String country;

	private static final Logger logger = LoggerFactory.getLogger(ContractorActionSupport.class);

	@Override
	public void prepare() {
		int invoiceId = getParameter("invoice.id");
		if (invoiceId > 0) {
			invoice = invoiceDAO.find(invoiceId);
			if (invoice != null) {
				id = invoice.getAccount().getId();
				account = invoice.getAccount();
				contractor = (ContractorAccount) account;
			}
		}
	}

	@SuppressWarnings("deprecation")
	public String execute() throws Exception, IOException, InvoiceValidationException {
		if (invoice == null) {
			addActionError(getText("InvoiceDetail.error.CantFindInvoice"));
			return BLANK;
		}

		if (!permissions.hasPermission(OpPerms.AllContractors)
				&& permissions.getAccountId() != invoice.getAccount().getId()) {
			throw new NoRightsException(getText("InvoiceDetail.error.CantViewInvoice"));
		}

		country = invoice.getAccount().getCountry().toString();
		invoice.updateAmountApplied();

		for (PaymentApplied ip : invoice.getPayments()) {
			ip.getPayment().updateAmountApplied();
		}

		if (button != null) {
			String message = null;

			if (SAVE_BUTTON.equals(button)) {
				edit = false;
				if (newFeeId > 0) {
					addInvoiceItem(newFeeId);
					newFeeId = 0;
					edit = true;
				} else {
					message = getText("InvoiceDetail.message.SavedInvoice");
				}
				updateTotals();
				AccountingSystemSynchronization.setToSynchronize(invoice);
			}
			if (CHANGE_TO_BUTTON.equals(button)) {
				List<String> removedItemNames = new ArrayList<String>();
				List<String> createdItemNames = new ArrayList<String>();

				Date membershipExpiration = null;

				// Removing all Membership items
				Iterator<InvoiceItem> iterator = invoice.getItems().iterator();
				while (iterator.hasNext()) {
					InvoiceItem item = iterator.next();
					if (item.getInvoiceFee().isMembership() || item.getInvoiceFee().isImportFee()) {
						if (item.getPaymentExpires() != null) {
							membershipExpiration = item.getPaymentExpires();
						}
						removedItemNames.add(item.getInvoiceFee().getFee().toString());
						iterator.remove();
						invoiceItemDAO.remove(item);
					}
				}

				// Re-adding Membership Items
				for (FeeClass feeClass : contractor.getFees().keySet()) {
					if ((feeClass.isMembership() || feeClass.equals(FeeClass.ImportFee))
							&& !contractor.getFees().get(feeClass).getNewLevel().isFree()) {
						InvoiceItem newInvoiceItem = new InvoiceItem();
						newInvoiceItem.setInvoiceFee(contractor.getFees().get(feeClass).getNewLevel());
						newInvoiceItem.setAmount(contractor.getFees().get(feeClass).getNewAmount());
						newInvoiceItem.setAuditColumns(new User(User.SYSTEM));
						newInvoiceItem.setPaymentExpires(membershipExpiration);
						newInvoiceItem.setAuditColumns(permissions);

						newInvoiceItem.setInvoice(invoice);
						invoice.getItems().add(newInvoiceItem);

						createdItemNames.add(newInvoiceItem.getInvoiceFee().getFee().toString());
					}
				}

				AccountingSystemSynchronization.setToSynchronize(invoice);
				invoice.updateAmount();
				invoice.updateAmountApplied();
				invoiceService.saveInvoice(invoice);

				addNote("Changed Membership Level", "Changed invoice from " + Strings.implode(removedItemNames, ", ")
						+ " to " + Strings.implode(createdItemNames, ", "), billingNoteModel.findUserForPaymentNote(permissions));
				message = getText("InvoiceDetail.message.ChangedLevel");

				invoice.setNotes(invoiceModel.getSortedClientSiteList(contractor));
				contractor.syncBalance();

				notifyDataChange(new InvoiceDataEvent(invoice, InvoiceEventType.UPDATE));
			}

			if (EMAIL_BUTTON.equals(button)) {
				try {
					EmailQueue email = EventSubscriptionBuilder.contractorInvoiceEvent(contractor, invoice);
					String note = Strings.EMPTY_STRING;
					if (invoice.getStatus().isPaid()) {
						note += "Payment Receipt for Invoice";
					} else {
						note += "Invoice";
					}

					note += " emailed to " + email.getToAddresses();
					if (!Strings.isEmpty(email.getCcAddresses())) {
						note += " and cc'd " + email.getCcAddresses();
					}
					addNote(note, billingNoteModel.findUserForPaymentNote(permissions));
					message = getText("InvoiceDetail.message.SentEmail");

				} catch (Exception e) {
                    logger.error("Unable to send InvoiceDetail email due to exception: {}", e.getMessage());
					addActionError(getText("InvoiceDetail.message.EmailFail"));
				}
			}
			if (CANCEL_BUTTON.equals(button)) {
				Iterator<PaymentAppliedToInvoice> paIterator = invoice.getPayments().iterator();
				if (paIterator.hasNext()) {
					PaymentAppliedToInvoice paymentAppliedToInvoice = paIterator.next();
					paymentDAO.removePaymentInvoice(paymentAppliedToInvoice, getUser());
				}
				invoice.setStatus(TransactionStatus.Void);
				billingService.performInvoiceStatusChangeActions(invoice, TransactionStatus.Void);
				invoice.setAuditColumns(permissions);
				AccountingSystemSynchronization.setToSynchronize(invoice);
				invoice.setNotes("Cancelled Invoice");

				// Automatically deactivating account based on expired
				// membership
				BillingStatus status = contractor.getBillingStatus();
				if (!contractor.getStatus().equals(AccountStatus.Deactivated)
						&& (status.isRenewalOverdue() || status.isReactivation())) {
					if (contractor.getAccountLevel().isBidOnly()) {
						contractor.setReason(AccountStatusChanges.BID_ONLY_ACCOUNT_REASON);
					}
				}

				billingService.calculateContractorInvoiceFees(contractor);
				contractor.syncBalance();
				contractor.incrementRecalculation(10);
				contractor.setAuditColumns(permissions);
				contractorAccountDao.save(contractor);

				message = getText("InvoiceDetail.message.CanceledInvoice");
				notifyDataChange(new InvoiceDataEvent(invoice, InvoiceEventType.VOID));

				String noteText = "Cancelled Invoice " + invoice.getId() + " for "
						+ contractor.getCountry().getCurrency().getSymbol() + invoice.getTotalAmount().toString();
				addNote(noteText, billingNoteModel.findUserForPaymentNote(permissions));
			}
			if (PAY_BUTTON.equals(button)) {
				if (invoice != null && invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
					if (contractor.isCcValid()) {
						Payment payment = null;
						try {
							payment = PaymentProcessor.PayOffInvoice(invoice, getUser(), PaymentMethod.CreditCard);
							paymentService.processPayment(payment, invoice);

							CreditCard creditCard = paymentService.getCreditCard(contractor);
							payment.setCcNumber(creditCard.getCardNumber());

							// Only if the transaction succeeds
							PaymentProcessor.ApplyPaymentToInvoice(payment, invoice, billingNoteModel.findUserForPaymentNote(permissions),
									payment.getTotalAmount());
							AccountingSystemSynchronization.setToSynchronize(payment);

							paymentDAO.save(payment);
							billingService.performInvoiceStatusChangeActions(invoice, TransactionStatus.Paid);
							invoice.updateAmountApplied();

							// Activate the contractor
							billingService.activateContractor(contractor, invoice);
							contractorAccountDao.save(contractor);

							notifyDataChange(new PaymentDataEvent(payment, PaymentEventType.SAVE));

							addNote("Credit Card transaction completed and emailed the receipt for "
									+ invoice.getTotalAmount() + Strings.SINGLE_SPACE
									+ invoice.getCurrency().getDisplay(), billingNoteModel.findUserForPaymentNote(permissions));
						} catch (NoBrainTreeServiceResponseException re) {
							addNote("Credit Card service connection error: " + re.getMessage(), billingNoteModel.findUserForPaymentNote(permissions));

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
								emailQueue.setViewableById(Account.PicsID);
								emailSender.send(emailQueue);
							} catch (Exception e) {
								PicsLogger
										.log("Cannot send email error message or determine credit processing status for contractor "
												+ contractor.getName()
												+ " ("
												+ contractor.getId()
												+ ") for invoice "
												+ invoice.getId());
							}
							addActionError(getTextParameterized("InvoiceDetail.error.ContactBilling",
									getText("PicsBillingPhone")));

							// Assuming Unpaid status per Aaron so that he can
							// refund or void manually.
							payment.setStatus(TransactionStatus.Unpaid);
							paymentDAO.save(payment);

							notifyDataChange(new PaymentDataEvent(payment, PaymentEventType.REMOVE));

							return SUCCESS;
						} catch (Exception e) {
							addNote("Credit Card transaction failed: " + e.getMessage(), billingNoteModel.findUserForPaymentNote(permissions));
							this.addActionError(getText("InvoiceDetail.error.FailedCreditCard") + e.getMessage());
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

			invoiceService.saveInvoice(invoice);

			if (!Strings.isEmpty(message)) {
				addActionMessage(message);
			}

			if (SAVE_BUTTON.equals(button) && !invoice.getStatus().isPaid()) {
				notifyDataChange(new InvoiceDataEvent(invoice, InvoiceEventType.UPDATE));
			}

			return this.setUrlForRedirect("InvoiceDetail.action?invoice.id=" + invoice.getId() + "&edit=" + edit);
		}

		updateTotals();
		invoiceService.saveInvoice(invoice);

		billingService.calculateContractorInvoiceFees(contractor);
		contractor.syncBalance();

		contractor.setAuditColumns(permissions);
		contractorAccountDao.save(contractor);

		return SUCCESS;
	}

	private void updateTotals() {
		if (!invoice.getStatus().isPaid()) {
			invoice.setTotalAmount(BigDecimal.ZERO);
			for (InvoiceItem item : invoice.getItems()) {
				invoice.setTotalAmount(invoice.getTotalAmount().add(item.getAmount()));
			}
		}
	}

	private void addNote(String subject, User u) {
		Note note = new Note(invoice.getAccount(), u, subject);
		note.setNoteCategory(NoteCategory.Billing);
		note.setCanContractorView(true);
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}

	private void addNote(String subject, String body, User u) {
		Note note = new Note(invoice.getAccount(), u, subject);
		note.setBody(body);
		note.setNoteCategory(NoteCategory.Billing);
		note.setCanContractorView(true);
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}

	private void addInvoiceItem(int feeId) {
		InvoiceItem newItem = new InvoiceItem();
		InvoiceFee newFee = invoiceFeeDAO.find(feeId);
		newItem.setInvoiceFee(newFee);
		newItem.setAmount(invoice.getAccount().getCountry().getAmount(newFee));
		newItem.setInvoice(invoice);
		newItem.setAuditColumns(permissions);

		invoice.getItems().add(newItem);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public List<InvoiceFee> getFeeList() {
		if (feeList == null) {
			feeList = invoiceModel.getFeeList();
		}

		return feeList;
	}

	public int getNewFeeId() {
		return newFeeId;
	}

	public void setNewFeeId(int newFeeId) {
		this.newFeeId = newFeeId;
	}

	public boolean isShowHeader() {
		return true;
	}

	public String getCcNumber() {
		String ccNumber = "";
		try {
			CreditCard creditCard = paymentService.getCreditCard(contractor);
			ccNumber = creditCard.getCardNumber();
		} catch (Exception e) {
		}
		return ccNumber;
	}

	public User getBillingUser() {
		return contractor.getUsersByRole(OpPerms.ContractorBilling).get(0);
	}

	public boolean isHasInvoiceMembershipChanged() {
		for (InvoiceItem item : this.getInvoice().getItems()) {
			for (FeeClass feeClass : contractor.getFees().keySet()) {
				if (item.getInvoiceFee().isMembership()
						&& item.getInvoiceFee().getFeeClass()
						.equals(contractor.getFees().get(feeClass).getNewLevel().getFeeClass())
						&& !item.getInvoiceFee().equals(contractor.getFees().get(feeClass).getNewLevel())) {
					return true;
				}
			}
		}

		return false;
	}

	public String getCountry() {
		return country;
	}

	public List<CommissionDetail> getCommissionDetails() {
		return invoiceModel.getCommissionDetails(invoice);
	}

	private <T> void notifyDataChange(DataEvent<T> dataEvent) {
		salesCommissionDataObservable.setChanged();
		salesCommissionDataObservable.notifyObservers(dataEvent);
	}

//	private User findUserForPaymentNote() {
//		int userWhoSwitchedToCurrentUser = permissions.getAdminID();
//		User userForPaymentNote = getUser();
//		if (userForPaymentNote.getId() != userWhoSwitchedToCurrentUser && userWhoSwitchedToCurrentUser > 0) {
//			return getUser(userWhoSwitchedToCurrentUser);
//		}
//
//		return userForPaymentNote;
//	}

}
