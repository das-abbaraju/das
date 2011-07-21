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
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.PICS.NoBrainTreeServiceResponseException;
import com.picsauditing.PICS.PaymentProcessor;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AppPropertyDAO;
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
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.util.Strings;
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
	private AppPropertyDAO appPropDao;
	@Autowired
	private NoteDAO noteDAO;
	@Autowired
	private BillingCalculatorSingle billingService;

	private boolean edit = false;
	private int newFeeId;
	private Invoice invoice;
	private List<InvoiceFee> feeList = null;
	private String country;

	private BrainTreeService paymentService = new BrainTreeService();

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
			addActionError("We could not find the invoice you were looking for");
			return BLANK;
		}

		if (!permissions.hasPermission(OpPerms.AllContractors)
				&& permissions.getAccountId() != invoice.getAccount().getId()) {
			throw new NoRightsException("You can't view this invoice");
		}

		country = invoice.getAccount().getCountry().toString();
		invoice.updateAmountApplied();
		for (PaymentApplied ip : invoice.getPayments())
			ip.getPayment().updateAmountApplied();

		if (button != null) {
			String message = null;

			if ("Save".equals(button)) {
				edit = false;
				if (newFeeId > 0) {
					addInvoiceItem(newFeeId);
					newFeeId = 0;
					edit = true;
				} else {
					message = "Saved Invoice";
				}
				updateTotals();
				invoice.setQbSync(true);
			}
			if (button.startsWith("Change to")) {
				List<String> removedItemNames = new ArrayList<String>();
				List<String> createdItemNames = new ArrayList<String>();

				Date membershipExpiration = null;

				// Removing all Membership items
				Iterator<InvoiceItem> iterator = invoice.getItems().iterator();
				while (iterator.hasNext()) {
					InvoiceItem item = iterator.next();
					if (item.getInvoiceFee().isMembership()) {
						if (item.getPaymentExpires() != null) {
							membershipExpiration = item.getPaymentExpires();
						}
						removedItemNames.add(item.getInvoiceFee().getFee());
						iterator.remove();
						invoiceItemDAO.remove(item);
					}
				}

				// Re-adding Membership Items
				for (FeeClass feeClass : contractor.getFees().keySet()) {
					if (feeClass.isMembership() && !contractor.getFees().get(feeClass).getNewLevel().isFree()) {
						InvoiceItem newInvoiceItem = new InvoiceItem();
						newInvoiceItem.setInvoiceFee(contractor.getFees().get(feeClass).getNewLevel());
						newInvoiceItem.setAmount(contractor.getFees().get(feeClass).getNewAmount());
						newInvoiceItem.setAuditColumns(new User(User.SYSTEM));
						newInvoiceItem.setPaymentExpires(membershipExpiration);
						newInvoiceItem.setAuditColumns(permissions);

						newInvoiceItem.setInvoice(invoice);
						invoice.getItems().add(newInvoiceItem);

						createdItemNames.add(newInvoiceItem.getInvoiceFee().getFee());
					}
				}

				invoiceDAO.save(invoice);

				addNote("Changed Membership Level", "Changed invoice from " + Strings.implode(removedItemNames, ", ")
						+ " to " + Strings.implode(createdItemNames, ", "), getUser());
				message = "Changed Membership Level";

				String notes = "Thank you for doing business with PICS!";
				notes += billingService.getOperatorsString(contractor);

				invoice.setNotes(notes);
				contractor.syncBalance();
			}

			if (button.startsWith("Email")) {
				try {
					EmailQueue email = EventSubscriptionBuilder
							.contractorInvoiceEvent(contractor, invoice, permissions);
					String note = "";
					if (invoice.getStatus().isPaid())
						note += "Payment Receipt for Invoice";
					else
						note += "Invoice";

					note += " emailed to " + email.getToAddresses();
					if (!Strings.isEmpty(email.getCcAddresses()))
						note += " and cc'd " + email.getCcAddresses();
					addNote(note, getUser());
					message = "Sent Email";

				} catch (Exception e) {
					message = "Sorry!! Failed to send email.";
				}
			}
			if (button.equalsIgnoreCase("Cancel")) {
				Iterator<PaymentAppliedToInvoice> paIterator = invoice.getPayments().iterator();
				if (paIterator.hasNext()) {
					PaymentAppliedToInvoice paymentAppliedToInvoice = paIterator.next();
					paymentDAO.removePaymentInvoice(paymentAppliedToInvoice, this.getUser());
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
				accountDao.save(contractor);

				message = "Cancelled Invoice";

				String noteText = "Cancelled Invoice " + invoice.getId() + " for "
						+ contractor.getCurrencyCode().getSymbol() + invoice.getTotalAmount().toString();
				addNote(noteText, getUser());
			}
			if (button.equals("pay")) {
				if (invoice != null && invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
					if (contractor.isCcValid()) {
						String canadaProcessorID = appPropDao.find("brainTree.processor_id.canada").getValue();
						paymentService.setUsProcessorID(appPropDao.find("brainTree.processor_id.us").getValue());
						paymentService.setUserName(appPropDao.find("brainTree.username").getValue());
						paymentService.setPassword(appPropDao.find("brainTree.password").getValue());

						Payment payment = null;
						try {
							payment = PaymentProcessor.PayOffInvoice(invoice, getUser(), PaymentMethod.CreditCard);

							if (Strings.isEmpty(canadaProcessorID) && payment.getCurrency().isCanada())
								throw new RuntimeException("Canadian ProcessorID Mismatch");
							paymentService.setCanadaProcessorID(canadaProcessorID);

							paymentService.processPayment(payment, invoice);

							CreditCard creditCard = paymentService.getCreditCard(id);
							payment.setCcNumber(creditCard.getCardNumber());

							// Only if the transaction succeeds
							PaymentProcessor.ApplyPaymentToInvoice(payment, invoice, getUser(), payment
									.getTotalAmount());
							payment.setQbSync(true);

							paymentDAO.save(payment);
							billingService.performInvoiceStatusChangeActions(invoice, TransactionStatus.Paid);
							invoice.updateAmountApplied();
							contractor.syncBalance();

							// Activate the contractor
							billingService.activateContractor(contractor, invoice);
							accountDao.save(contractor);

							addNote("Credit Card transaction completed and emailed the receipt for "
									+ contractor.getCurrencyCode().getSymbol() + invoice.getTotalAmount(), getUser());

						} catch (NoBrainTreeServiceResponseException re) {
							addNote("Credit Card service connection error: " + re.getMessage(), getUser());

							EmailBuilder emailBuilder = new EmailBuilder();
							emailBuilder.setTemplate(106);
							emailBuilder.setFromAddress("\"PICS IT Team\"<it@picsauditing.com>");
							emailBuilder.setToAddresses("billing@picsauditing.com");
							emailBuilder.setPermissions(permissions);
							emailBuilder.addToken("permissions", permissions);
							emailBuilder.addToken("contractor", contractor);
							emailBuilder.addToken("billingusers", contractor.getUsersByRole(OpPerms.ContractorBilling));
							emailBuilder.addToken("invoice", invoice);

							EmailQueue emailQueue;
							try {
								emailQueue = emailBuilder.build();
								emailQueue.setPriority(90);
								emailQueue.setViewableById(Account.PicsID);
								EmailSender.send(emailQueue);
							} catch (Exception e) {
								PicsLogger
										.log("Cannot send email error message or determine credit processing status for contractor "
												+ contractor.getName()
												+ " ("
												+ contractor.getId()
												+ ") for invoice "
												+ invoice.getId());
							}

							addActionError("There has been a connection error while processing your payment. Our Billing department has been notified and will contact you after confirming the status of your payment. Please contact the PICS Billing Department at "
									+ permissions.getPicsBillingPhone() + ".");

							// Assuming Unpaid status per Aaron so that he can
							// refund or void manually.
							payment.setStatus(TransactionStatus.Unpaid);
							paymentDAO.save(payment);

							return SUCCESS;
						} catch (Exception e) {
							addNote("Credit Card transaction failed: " + e.getMessage(), getUser());
							this.addActionError("Failed to charge credit card. " + e.getMessage());
							return SUCCESS;
						}
					}

					// Send a receipt to the contractor
					try {
						EventSubscriptionBuilder.contractorInvoiceEvent(contractor, invoice, permissions);
					} catch (Exception theyJustDontGetAnEmail) {
					}
				}
			}

			invoiceDAO.save(invoice);
			this.redirect("InvoiceDetail.action?invoice.id=" + invoice.getId() + "&edit=" + edit
					+ (message == null ? "" : "&msg=" + message));

			return BLANK;
		}

		updateTotals();
		invoiceDAO.save(invoice);

		billingService.calculateAnnualFees(contractor);
		contractor.syncBalance();

		contractor.setAuditColumns(permissions);
		accountDao.save(contractor);

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
		newItem.setAmount(newFee.getAmount());
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

	@SuppressWarnings("unchecked")
	public List<InvoiceFee> getFeeList() {
		if (feeList == null)
			feeList = (List<InvoiceFee>) invoiceFeeDAO.findWhere(InvoiceFee.class, "t.visible = true", 100);

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
		paymentService.setCanadaProcessorID(appPropDao.find("brainTree.processor_id.canada").getValue());
		paymentService.setUsProcessorID(appPropDao.find("brainTree.processor_id.us").getValue());
		paymentService.setUserName(appPropDao.find("brainTree.username").getValue());
		paymentService.setPassword(appPropDao.find("brainTree.password").getValue());
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
						&& item.getInvoiceFee().getFeeClass().equals(
								contractor.getFees().get(feeClass).getNewLevel().getFeeClass())
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
