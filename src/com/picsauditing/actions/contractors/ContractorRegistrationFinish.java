package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.PICS.BrainTreeServiceErrorResponseException;
import com.picsauditing.PICS.NoBrainTreeServiceResponseException;
import com.picsauditing.PICS.PaymentProcessor;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.access.OpPerms;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorFee;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.jpa.entities.TransactionStatus;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class ContractorRegistrationFinish extends ContractorActionSupport {

	// we probably can reuse BaseTabe save methods and eliminate some of these DAOs
	@Autowired
	private InvoiceDAO invoiceDAO;
	@Autowired
	private InvoiceFeeDAO invoiceFeeDAO;
	@Autowired
	private PaymentDAO paymentDAO;
	@Autowired
	private AppPropertyDAO appPropDAO;
	@Autowired
	private NoteDAO noteDAO;
	@Autowired
	private InvoiceItemDAO invoiceItemDAO;
	@Autowired
	private AuditBuilder auditBuilder;
	@Autowired
	private BillingCalculatorSingle billingService;

	private BrainTreeService paymentService = new BrainTreeService();

	private Invoice invoice;
	private boolean complete = false;

	public String execute() throws Exception {
		subHeading = "Finish Registration";

		findContractor();

		findUnpaidInvoice();

		auditBuilder.buildAudits(contractor);
		this.resetActiveAudits();

		if ("Complete My Registration".equals(button)) {
			// enforcing workflow steps before completing registration
			String url = "";
			if ((LowMedHigh.None.equals(contractor.getSafetyRisk()) && !contractor.isMaterialSupplierOnly())
					|| (LowMedHigh.None.equals(contractor.getProductRisk()) && contractor.isMaterialSupplier())) {
				url = "ContractorRegistrationServices.action?id=" + contractor.getId()
						+ "&msg=Please select the services you perform.";
			} else if (contractor.getNonCorporateOperators().size() == 0) {
				url = "ContractorFacilities.action?id=" + contractor.getId() + "&msg=Please add at least one facility.";
			} else if (!contractor.isPaymentMethodStatusValid() && contractor.isMustPayB()) {
				url = "ContractorPaymentOptions.action?id=" + contractor.getId()
						+ "&msg=Please add a valid payment method.";
			}

			if (!url.isEmpty()) {
				ServletActionContext.getResponse().sendRedirect(url);
				return SUCCESS;
			}

			if (contractor.isHasFreeMembership()) {
				// Free accounts should just be activated
				contractor.setStatus(AccountStatus.Active);
				contractor.setAuditColumns(permissions);
				contractor.setMembershipDate(new Date());
				if (contractor.getBalance() == null)
					contractor.setBalance(BigDecimal.ZERO);
				accountDao.save(contractor);
			} else {
				if (invoice != null && invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
					if (contractor.isCcValid()) {
						String canadaProcessorID = appPropDAO.find("brainTree.processor_id.canada").getValue();
						paymentService.setUsProcessorID(appPropDAO.find("brainTree.processor_id.us").getValue());
						paymentService.setUserName(appPropDAO.find("brainTree.username").getValue());
						paymentService.setPassword(appPropDAO.find("brainTree.password").getValue());

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
							PaymentProcessor.ApplyPaymentToInvoice(payment, invoice, getUser(),
									payment.getTotalAmount());
							payment.setQbSync(true);

							paymentDAO.save(payment);
							invoice.updateAmountApplied();
							billingService.performInvoiceStatusChangeActions(invoice, TransactionStatus.Paid);
							contractor.syncBalance();

							// Activate the contractor
							billingService.activateContractor(contractor, invoice);
							accountDao.save(contractor);

							addNote("Credit Card transaction completed and emailed the receipt for "
									+ contractor.getCurrencyCode().getSymbol() + invoice.getTotalAmount());
						} catch (NoBrainTreeServiceResponseException re) {
							addNote("Credit Card service connection error: " + re.getMessage());

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

							// Assuming paid status per Aaron so that he can
							// refund or void manually.
							payment.setStatus(TransactionStatus.Unpaid);
							paymentDAO.save(payment);

							return SUCCESS;
						} catch (BrainTreeServiceErrorResponseException e) {
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
			billingService.calculateAnnualFees(contractor);

			if (!contractor.isHasFreeMembership()) {
				String notes = "";
				// There are no unpaid invoices - we should create a new one
				// (could be a re-activation)
				if (invoice == null) {
					List<InvoiceItem> items = billingService.createInvoiceItems(contractor);
					if (items.size() > 0) {
						invoice = new Invoice();
						invoice.setStatus(TransactionStatus.Unpaid);
						invoice.setItems(items);
						invoice.setAccount(contractor);
						invoice.setCurrency(contractor.getCurrency());
						invoice.setAuditColumns(new User(User.SYSTEM));
						invoice.setDueDate(new Date());

						InvoiceFee activation = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.Activation, 1);
						// Just wanting to add note, reduced activation is saved
						// in invoice item list already
						if (contractor.hasReducedActivation(activation)) {
							OperatorAccount reducedOperator = contractor.getReducedActivationFeeOperator(activation);
							notes += "(" + reducedOperator.getName() + " Promotion) Activation reduced from "
									+ contractor.getCurrencyCode().getSymbol() + activation.getAmount() + " to "
									+ contractor.getCurrencyCode().getSymbol() + reducedOperator.getActivationFee()
									+ ". ";
							invoice.setNotes(notes);
						}

						for (InvoiceItem item : items) {
							item.setInvoice(invoice);
							item.setAuditColumns(new User(User.SYSTEM));
						}

						if (contractor.getAccountLevel().isBidOnly())
							contractor.setRenew(true);

						updateTotals();
						this.addNote(
								"Created invoice for " + contractor.getCurrencyCode().getSymbol()
										+ invoice.getTotalAmount(), NoteCategory.Billing);
					}

				} else {

					if (contractor.isHasMembershipChanged()) {
						for (FeeClass feeClass : contractor.getFees().keySet()) {
							ContractorFee cf = contractor.getFees().get(feeClass);
							if (cf.isHasChanged()) {
								for (Iterator<InvoiceItem> iterator = invoice.getItems().iterator(); iterator.hasNext();) {
									InvoiceItem item = iterator.next();
									if (cf.getFeeClass().equals(item.getInvoiceFee().getFeeClass())) {
										iterator.remove();
										invoiceItemDAO.remove(item);
									}
								}

								if (!cf.getNewLevel().isFree()) {
									InvoiceItem newInvoiceItem = new InvoiceItem();
									newInvoiceItem.setInvoiceFee(cf.getNewLevel());
									newInvoiceItem.setAmount(cf.getNewAmount());
									newInvoiceItem.setAuditColumns(new User(User.SYSTEM));

									newInvoiceItem.setInvoice(invoice);
									invoice.getItems().add(newInvoiceItem);
								}

								updateTotals();
							}
						}
						contractor.syncBalance();

						this.addNote("Modified current invoice, changed to " + contractor.getCurrencyCode().getSymbol()
								+ invoice.getTotalAmount(), NoteCategory.Billing);
					}
				}

				if (invoice != null && invoice.getStatus().isUnpaid()) {

					if (invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0)
						invoice.setQbSync(true);

					notes += "Thank you for doing business with PICS!";
					notes += billingService.getOperatorsString(contractor);
					// AppProperty prop = appPropDAO.find("invoice_comment");
					// if (prop != null) {
					// notes = prop.getValue();
					// }
					invoice.setNotes(notes);

					invoice = invoiceDAO.save(invoice);

					if (!contractor.getInvoices().contains(invoice))
						contractor.getInvoices().add(invoice);

					contractor.syncBalance();
					accountDao.save(contractor);
				}
			}
		}

		if (!contractor.getAccountLevel().isBidOnly() && !contractor.isRenew()) {
			contractor.setRenew(true);
			accountDao.save(contractor);
		}

		// Reload permissions for this user so they view just their country
		// specific questions.
		if (complete) {
			permissions.setAccountPerms(getUser());
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

	private void addNote(String subject, NoteCategory category) {
		Note note = new Note();
		note.setAccount(contractor);
		note.setAuditColumns(new User(User.SYSTEM));
		note.setSummary(subject);
		note.setPriority(LowMedHigh.Med);
		note.setNoteCategory(category);
		note.setViewableById(Account.PicsID);
		note.setCanContractorView(false);
		note.setStatus(NoteStatus.Closed);
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

	@Deprecated
	public Set<String> getRequiredAudits() {
		Set<String> auditTypeList = new HashSet<String>();
		auditTypeList.add("Don't use this!! Call the AuditBuilder first and then show the contractor audits");
		return auditTypeList;
	}
}
