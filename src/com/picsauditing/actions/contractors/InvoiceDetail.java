package com.picsauditing.actions.contractors;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.NoBrainTreeServiceResponseException;
import com.picsauditing.PICS.PaymentProcessor;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentApplied;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.jpa.entities.TransactionStatus;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSenderSpring;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.braintree.BrainTreeService;
import com.picsauditing.util.braintree.CreditCard;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class InvoiceDetail extends ContractorActionSupport implements Preparable {
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
	private BrainTreeService paymentService;
	@Autowired
	private EmailSenderSpring emailSender;

	private boolean edit = false;
	private int newFeeId;
	private Invoice invoice;
	private List<InvoiceFee> feeList = null;
	private String country;

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

	public String execute() throws NoRightsException, IOException {
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
		// http://localhost:8080/picsWeb2/InvoiceDetail.action?invoice.id=85858&edit=true#
		for (PaymentApplied ip : invoice.getPayments())
			ip.getPayment().updateAmountApplied();

		if (button != null) {
			String message = null;

			if ("save".equals(button)) {
				edit = false;
				if (newFeeId > 0) {
					addInvoiceItem(newFeeId);
					newFeeId = 0;
					edit = true;
				} else {
					message = getText("InvoiceDetail.message.SavedInvoice");
				}
				updateTotals();
				invoice.setQbSync(true);
			}
			if ("changeto".equals(button)) {
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

				invoice.setQbSync(true);
				invoice.updateAmount();
				invoice.updateAmountApplied();
				invoiceDAO.save(invoice);

				addNote("Changed Membership Level", "Changed invoice from " + Strings.implode(removedItemNames, ", ")
						+ " to " + Strings.implode(createdItemNames, ", "), getUser());
				message = getText("InvoiceDetail.message.ChangedLevel");

				invoice.setNotes(billingService.getOperatorsString(contractor));
				contractor.syncBalance();
			}

			if ("email".equals(button)) {
				try {
					EmailQueue email = EventSubscriptionBuilder.contractorInvoiceEvent(contractor, invoice, getUser());
					String note = "";
					if (invoice.getStatus().isPaid())
						note += "Payment Receipt for Invoice";
					else
						note += "Invoice";

					note += " emailed to " + email.getToAddresses();
					if (!Strings.isEmpty(email.getCcAddresses()))
						note += " and cc'd " + email.getCcAddresses();
					addNote(note, getUser());
					message = getText("InvoiceDetail.message.SentEmail");

				} catch (Exception e) {
					addActionError(getText("InvoiceDetail.message.EmailFail"));
				}
			}
			if ("cancel".equals(button)) {
				Iterator<PaymentAppliedToInvoice> paIterator = invoice.getPayments().iterator();
				if (paIterator.hasNext()) {
					PaymentAppliedToInvoice paymentAppliedToInvoice = paIterator.next();
					paymentDAO.removePaymentInvoice(paymentAppliedToInvoice, getUser());
				}
				invoice.setStatus(TransactionStatus.Void);
				billingService.performInvoiceStatusChangeActions(invoice, TransactionStatus.Void);
				invoice.setAuditColumns(permissions);
				invoice.setQbSync(true);
				invoice.setNotes("Cancelled Invoice");

				// Automatically deactivating account based on expired
				// membership
				String status = contractor.getBillingStatus();
				if (!contractor.getStatus().equals(AccountStatus.Deactivated)
						&& ("Renewal Overdue".equals(status) || "Reactivation".equals(status))) {
					contractor.setStatus(AccountStatus.Deactivated);
					contractor.setRenew(false);
					if (contractor.getAccountLevel().isBidOnly())
						contractor.setReason("Bid Only Account");
					Note note = new Note(contractor, new User(User.SYSTEM),
							"Automatically inactivating account based on expired membership");
					note.setNoteCategory(NoteCategory.Billing);
					note.setCanContractorView(true);
					note.setViewableById(Account.PicsID);
					noteDAO.save(note);
				}

				billingService.calculateAnnualFees(contractor);
				contractor.syncBalance();
				contractor.incrementRecalculation(10);
				contractorAccountDao.save(contractor);

				message = getText("InvoiceDetail.message.CanceledInvoice");

				String noteText = "Cancelled Invoice " + invoice.getId() + " for "
						+ contractor.getCountry().getCurrency().getSymbol() + invoice.getTotalAmount().toString();
				addNote(noteText, getUser());
			}
			if (button.equals("pay")) {
				if (invoice != null && invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
					if (contractor.isCcValid()) {
						Payment payment = null;
						try {
							payment = PaymentProcessor.PayOffInvoice(invoice, getUser(), PaymentMethod.CreditCard);
							paymentService.processPayment(payment, invoice);

							CreditCard creditCard = paymentService.getCreditCard(id);
							payment.setCcNumber(creditCard.getCardNumber());

							// Only if the transaction succeeds
							PaymentProcessor.ApplyPaymentToInvoice(payment, invoice, getUser(),
									payment.getTotalAmount());
							payment.setQbSync(true);

							paymentDAO.save(payment);
							billingService.performInvoiceStatusChangeActions(invoice, TransactionStatus.Paid);
							invoice.updateAmountApplied();

							// Activate the contractor
							billingService.activateContractor(contractor, invoice);
							contractorAccountDao.save(contractor);

							addNote("Credit Card transaction completed and emailed the receipt for "
									+ invoice.getCurrency().getSymbol() + invoice.getTotalAmount(), getUser());

						} catch (NoBrainTreeServiceResponseException re) {
							addNote("Credit Card service connection error: " + re.getMessage(), getUser());

							EmailBuilder emailBuilder = new EmailBuilder();
							emailBuilder.setTemplate(106);
							emailBuilder.setFromAddress("\"PICS IT Team\"<it@picsauditing.com>");
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

							return SUCCESS;
						} catch (Exception e) {
							addNote("Credit Card transaction failed: " + e.getMessage(), getUser());
							this.addActionError(getText("InvoiceDetail.error.FailedCreditCard") + e.getMessage());
							return SUCCESS;
						}
					}

					// Send a receipt to the contractor
					try {
						EventSubscriptionBuilder.contractorInvoiceEvent(contractor, invoice, getUser());
					} catch (Exception theyJustDontGetAnEmail) {
					}
				}
			}

			invoiceDAO.save(invoice);

			if (!Strings.isEmpty(message)) {
				addActionMessage(message);
			}

			return this.setUrlForRedirect("InvoiceDetail.action?invoice.id=" + invoice.getId() + "&edit=" + edit);
		}

		updateTotals();
		invoiceDAO.save(invoice);

		billingService.calculateAnnualFees(contractor);
		contractor.syncBalance();

		contractor.setAuditColumns(permissions);
		contractorAccountDao.save(contractor);

		return SUCCESS;
	}

	private void updateTotals() {
		if (!invoice.getStatus().isPaid()) {
			invoice.setTotalAmount(BigDecimal.ZERO);
			for (InvoiceItem item : invoice.getItems())
				invoice.setTotalAmount(invoice.getTotalAmount().add(item.getAmount()));
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
		if (feeList == null)
			feeList = invoiceFeeDAO.findWhere(InvoiceFee.class, "t.visible = true", 100);

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
			CreditCard creditCard = paymentService.getCreditCard(id);
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
						&& !item.getInvoiceFee().equals(contractor.getFees().get(feeClass).getNewLevel()))
					return true;
			}
		}

		return false;
	}

	public String getCountry() {
		return country;
	}

}
