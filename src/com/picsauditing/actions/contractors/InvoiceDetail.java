package com.picsauditing.actions.contractors;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	private boolean edit = false;

	private InvoiceDAO invoiceDAO;
	private InvoiceFeeDAO invoiceFeeDAO;
	private PaymentDAO paymentDAO;

	private int newFeeId;

	private Invoice invoice;

	private List<InvoiceFee> feeList = null;

	AppPropertyDAO appPropDao;

	private BrainTreeService paymentService = new BrainTreeService();

	public InvoiceDetail(InvoiceDAO invoiceDAO, AppPropertyDAO appPropDao, InvoiceFeeDAO invoiceFeeDAO,
			PaymentDAO paymentDAO) {
		this.invoiceDAO = invoiceDAO;
		this.appPropDao = appPropDao;
		this.paymentDAO = paymentDAO;
		this.invoiceFeeDAO = invoiceFeeDAO;
	}

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
		if (!forceLogin())
			return LOGIN;

		if (invoice == null) {
			addActionError("We could not find the invoice you were looking for");
			return BLANK;
		}

		if (!permissions.hasPermission(OpPerms.AllContractors)
				&& permissions.getAccountId() != invoice.getAccount().getId()) {
			throw new NoRightsException("You can't view this invoice");
		}

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
				List<String> changedItems = new ArrayList<String>();
				List<InvoiceItem> removalList = new ArrayList<InvoiceItem>();

				for (FeeClass feeClass : contractor.getFees().keySet()) {
					boolean found = false;
					for (InvoiceItem item : invoice.getItems()) {
						if (item.getInvoiceFee().equals(contractor.getFees().get(feeClass).getCurrentLevel())
								&& !contractor.getFees().get(feeClass).getNewLevel().equals(item.getInvoiceFee())) {
							found = true;
							item.setInvoiceFee(contractor.getFees().get(feeClass).getNewLevel());
							item.setAmount(contractor.getFees().get(feeClass).getNewLevel().getAmount());
							item.setAuditColumns(permissions);

							changedItems.add(contractor.getFees().get(feeClass).getNewLevel().getFee());
							if (contractor.getFees().get(feeClass).getNewLevel().isFree())
								removalList.add(item);
						}
					}

					// found a new fee
					if (!found && !contractor.getFees().get(feeClass).getNewLevel().isFree()) {
						InvoiceItem newInvoiceItem = new InvoiceItem();
						newInvoiceItem.setInvoiceFee(contractor.getFees().get(feeClass).getNewLevel());
						newInvoiceItem.setAmount(contractor.getFees().get(feeClass).getNewLevel().getAmount());
						newInvoiceItem.setAuditColumns(new User(User.SYSTEM));

						newInvoiceItem.setInvoice(invoice);
						invoice.getItems().add(newInvoiceItem);

						changedItems.add(contractor.getFees().get(feeClass).getNewLevel().getFee());
					}
				}

				for (Iterator<InvoiceItem> iterator = removalList.iterator(); iterator.hasNext();) {
					InvoiceItem item = iterator.next();
					invoice.getItems().remove(item);
					invoiceDAO.remove(item);
					iterator.remove();
				}

				contractor.syncBalance();
				addNote("Changed invoice " + invoice.getId() + " to " + Strings.implode(changedItems), getUser());
				message = "Changed Membership Level";
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
				invoice.setAuditColumns(permissions);
				invoice.setQbSync(true);
				invoice.setNotes("Cancelled Invoice");

				// Automatically deactivating account based on expired
				// membership
				String status = contractor.getBillingStatus();
				if (!contractor.getStatus().equals(AccountStatus.Deactivated)
						&& ("Renewal Overdue".equals(status) || "Reactivation".equals(status))) {
					contractor.setStatus(AccountStatus.Deactivated);
					if ("Renewal Overdue".equals(status))
						contractor.setRenew(false);
					if (contractor.isAcceptsBids())
						contractor.setReason("List Only Account");
					Note note = new Note(contractor, new User(User.SYSTEM),
							"Automatically inactivating account based on expired membership");
					note.setNoteCategory(NoteCategory.Billing);
					note.setCanContractorView(true);
					note.setViewableById(Account.PicsID);
					getNoteDao().save(note);
				}

				BillingCalculatorSingle.calculateAnnualFees(contractor);
				contractor.syncBalance();
				contractor.incrementRecalculation(10);
				accountDao.save(contractor);

				message = "Cancelled Invoice";

				String noteText = "Cancelled Invoice " + invoice.getId() + " for $"
						+ invoice.getTotalAmount().toString();
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
							invoice.updateAmountApplied();
							contractor.syncBalance();

							// Activate the contractor
							BillingCalculatorSingle.activateContractor(contractor, invoice, accountDao);
							accountDao.save(contractor);

							addNote("Credit Card transaction completed and emailed the receipt for $"
									+ invoice.getTotalAmount(), getUser());
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

							addActionError("There has been a connection error while processing your payment. Our Billing department has been notified and will contact you after confirming the status of your payment. Please contact the PICS Billing Department at 1-(800)506-PICS x708.");

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

		BillingCalculatorSingle.calculateAnnualFees(contractor);
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
		getNoteDao().save(note);
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

	public List<InvoiceFee> getFeeList() {
		if (feeList == null)
			feeList = invoiceFeeDAO.findAll();

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
}
