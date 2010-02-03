package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.PICS.PaymentProcessor;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.jpa.entities.TransactionStatus;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EventSubscriptionBuilder;

@SuppressWarnings("serial")
public class ContractorRegistrationFinish extends ContractorActionSupport {

	private InvoiceDAO invoiceDAO;
	private InvoiceFeeDAO invoiceFeeDAO;
	private PaymentDAO paymentDAO;
	private AppPropertyDAO appPropDAO;
	private NoteDAO noteDAO;
	private InvoiceItemDAO invoiceItemDAO;
	private AuditBuilder auditBuilder;

	private BrainTreeService paymentService = new BrainTreeService();

	private Invoice invoice;
	private boolean complete = false;

	public ContractorRegistrationFinish(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			InvoiceDAO invoiceDAO, InvoiceFeeDAO invoiceFeeDAO, PaymentDAO paymentDAO, AppPropertyDAO appPropDAO,
			NoteDAO noteDAO, InvoiceItemDAO invoiceItemDAO, AuditBuilder auditBuilder) {
		super(accountDao, auditDao);
		this.invoiceDAO = invoiceDAO;
		this.invoiceFeeDAO = invoiceFeeDAO;
		this.paymentDAO = paymentDAO;
		this.appPropDAO = appPropDAO;
		this.noteDAO = noteDAO;
		this.invoiceItemDAO = invoiceItemDAO;
		this.auditBuilder = auditBuilder;
		subHeading = "Finish Registration";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findContractor();

		findUnpaidInvoice();

		auditBuilder.buildAudits(contractor);
		this.resetActiveAudits();

		if ("Complete My Registration".equals(button)) {

			if (contractor.getNewMembershipLevel().isFree()) {
				// Free accounts should just be activated
				contractor.setStatus(AccountStatus.Active);
				contractor.setAuditColumns(permissions);
			} else {
				if (invoice != null && invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
					if (contractor.isCcValid()) {
						paymentService.setUserName(appPropDAO.find("brainTree.username").getValue());
						paymentService.setPassword(appPropDAO.find("brainTree.password").getValue());

						try {
							Payment payment = PaymentProcessor.PayOffInvoice(invoice, getUser(),
									PaymentMethod.CreditCard);

							paymentService.processPayment(payment);

							CreditCard creditCard = paymentService.getCreditCard(id);
							payment.setCcNumber(creditCard.getCardNumber());

							// Only if the transaction succeeds
							PaymentProcessor.ApplyPaymentToInvoice(payment, invoice, getUser(), payment
									.getTotalAmount());
							payment.setQbSync(true);

							paymentDAO.save(payment);
							invoice.updateAmountApplied();

							// Activate the contractor
							BillingCalculatorSingle.activateContractor(contractor, invoice);
							contractor.syncBalance();
							accountDao.save(contractor);

							addNote("Credit Card transaction completed and emailed the receipt for $"
									+ invoice.getTotalAmount());
						} catch (Exception e) {
							addNote("Credit Card transaction failed: " + e.getMessage());
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

			complete = true;

		} else if (contractor.getStatus().isPendingDeactivated()) {
			InvoiceFee newFee = BillingCalculatorSingle.calculateAnnualFee(contractor);
			newFee = invoiceFeeDAO.find(newFee.getId());
			contractor.setNewMembershipLevel(newFee);

			if (!contractor.getNewMembershipLevel().isFree()) {
				// There are no unpaid invoices - we should create a new one
				// (could be a re-activation)
				if (invoice == null) {
					List<InvoiceItem> items = BillingCalculatorSingle.createInvoiceItems(contractor, invoiceFeeDAO);
					if (items.size() > 0) {
						invoice = new Invoice();
						invoice.setStatus(TransactionStatus.Unpaid);
						invoice.setItems(items);
						invoice.setAccount(contractor);
						invoice.setCurrency(contractor.getCurrency());
						invoice.setAuditColumns(new User(User.SYSTEM));
						invoice.setDueDate(new Date());

						for (InvoiceItem item : items) {
							item.setInvoice(invoice);
							item.setAuditColumns(new User(User.SYSTEM));
						}

						updateTotals();
						this.addNote(contractor, "Created invoice for $" + invoice.getTotalAmount(),
								NoteCategory.Billing, LowMedHigh.Med, false, Account.PicsID, new User(User.SYSTEM));
					}

				} else {

					if (!contractor.getMembershipLevel().equals(contractor.getNewMembershipLevel())) {
						changeInvoiceItem(contractor.getMembershipLevel(), contractor.getNewMembershipLevel());
						updateTotals();
						this.addNote(contractor, "Modified current invoice, changed to $" + invoice.getTotalAmount(),
								NoteCategory.Billing, LowMedHigh.Med, false, Account.PicsID, new User(User.SYSTEM));

					}
				}

				if (invoice != null && invoice.getStatus().isUnpaid()) {

					if (invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0)
						invoice.setQbSync(true);

					String notes = "Thank you for your business.";
					AppProperty prop = appPropDAO.find("invoice_comment");
					if (prop != null) {
						notes = prop.getValue();
					}
					invoice.setNotes(notes);

					invoice = invoiceDAO.save(invoice);

					if (!contractor.getInvoices().contains(invoice))
						contractor.getInvoices().add(invoice);

					contractor.syncBalance();
					accountDao.save(contractor);
				}
			}
		}

		if (!contractor.isAcceptsBids() && !contractor.isRenew()) {
			contractor.setRenew(true);
			accountDao.save(contractor);
		}

		return SUCCESS;
	}

	private void findUnpaidInvoice() {
		// Get the first unpaid Invoice - this is mainly for re-activations.
		for (Invoice inv : contractor.getInvoices()) {
			if (inv.getStatus().isUnpaid()) {
				invoice = inv;
				break;
			}
		}
	}

	private void changeInvoiceItem(InvoiceFee currentFee, InvoiceFee newFee) {
		for (Iterator<InvoiceItem> iterator = invoice.getItems().iterator(); iterator.hasNext();) {
			InvoiceItem item = iterator.next();
			if (item.getInvoiceFee().getId() == currentFee.getId()) {
				iterator.remove();
				invoiceItemDAO.remove(item);
			}
		}

		InvoiceItem newInvoiceItem = new InvoiceItem();
		newInvoiceItem.setInvoiceFee(newFee);
		newInvoiceItem.setAmount(newFee.getAmount());
		newInvoiceItem.setAuditColumns(new User(User.SYSTEM));

		newInvoiceItem.setInvoice(invoice);
		invoice.getItems().add(newInvoiceItem);

		contractor.setMembershipLevel(newFee);
	}

	private void updateTotals() {
		if (!invoice.getStatus().isPaid()) {
			invoice.setTotalAmount(BigDecimal.ZERO);
			for (InvoiceItem item : invoice.getItems())
				invoice.setTotalAmount(invoice.getTotalAmount().add(item.getAmount()));
		}
	}

	private void addNote(String subject) {
		Note note = new Note(contractor, new User(User.SYSTEM), subject);
		note.setNoteCategory(NoteCategory.Billing);
		note.setCanContractorView(true);
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public boolean isComplete() {
		return complete;
	}
	
	public Set<String> getRequiredAudits() {
		Set<String> auditTypeList = new HashSet<String>();
		for(ContractorOperator cOperator : contractor.getOperators()) {
			for(AuditOperator aOperator : cOperator.getOperatorAccount().getVisibleAudits()) {
				if(aOperator.isRequiredFor(contractor) 
						&& !aOperator.getAuditType().isAnnualAddendum()) {
					auditTypeList.add(aOperator.getAuditType().getAuditName());
				}
			}
		}
		return auditTypeList;
	}
}
