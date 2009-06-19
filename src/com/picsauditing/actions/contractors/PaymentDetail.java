package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.PaymentApplied;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentMethod;

@SuppressWarnings("serial")
public class PaymentDetail extends ContractorActionSupport implements Preparable {

	private Payment payment;
	private PaymentMethod method = null;
	private CreditCard creditCard;
	private Map<Integer, BigDecimal> amountApplyMap = new HashMap<Integer, BigDecimal>();

	private PaymentDAO paymentDAO;
	private NoteDAO noteDAO;
	private AppPropertyDAO appPropDao;
	private BrainTreeService paymentService = new BrainTreeService();

	public PaymentDetail(AppPropertyDAO appPropDao, NoteDAO noteDAO, ContractorAccountDAO conAccountDAO,
			ContractorAuditDAO auditDao, PaymentDAO paymentDAO) {
		super(conAccountDAO, auditDao);
		this.appPropDao = appPropDao;
		this.noteDAO = noteDAO;
		this.paymentDAO = paymentDAO;
		this.subHeading = "Payment Detail";
	}

	@Override
	public void prepare() {
		int paymendId = getParameter("payment.id");
		if (paymendId > 0) {
			payment = paymentDAO.find(paymendId);
			if (payment != null) {
				id = payment.getAccount().getId();
				account = payment.getAccount();
				contractor = (ContractorAccount) account;
			}
		}
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (contractor == null)
			findContractor();

		if (method == null)
			method = contractor.getPaymentMethod();

		if (payment != null) {
			payment.updateAmountApplied();
			for (PaymentAppliedToInvoice ip : payment.getInvoices())
				ip.getInvoice().updateAmountApplied();
		} else {
			if (method.isCreditCard()) {
				creditCard = paymentService.getCreditCard(id);
			}
			// Useful during development, we can remove this later
			for (Invoice invoice : contractor.getInvoices())
				invoice.updateAmountApplied();
		}
		
		for (Invoice invoice : contractor.getInvoices()) {
			if (!amountApplyMap.containsKey(invoice.getId()))
				amountApplyMap.put(invoice.getId(), BigDecimal.ZERO.setScale(2));
		}

		if (button != null) {
			if (payment == null || payment.getId() == 0) {
				// If we have a button but no payment, then we're creating a new
				// payment
				payment.setAccount(contractor);
				payment.setAuditColumns(getUser());
				payment.setPaymentMethod(method);

				if (payment.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
					addActionError("Payments must be greater than zero");
					return SUCCESS;
				}
				if (method.isCreditCard()) {
					paymentService.setUserName(appPropDao.find("brainTree.username").getValue());
					paymentService.setPassword(appPropDao.find("brainTree.password").getValue());

					try {
						if (creditCard == null || creditCard.getCardNumber() == null) {
							creditCard = paymentService.getCreditCard(id);
						}
						paymentService.processPayment(payment);

						addNote("Credit Card transaction completed and emailed the receipt for $"
								+ payment.getTotalAmount());
					} catch (Exception e) {
						addNote("Credit Card transaction failed: " + e.getMessage());
						this.addActionError("Failed to charge credit card. " + e.getMessage());
					}

				} else {
					// Check

				}

				paymentDAO.save(payment);

				// if (invoice.getStatus().isPaid()) {
				// if (!contractor.isActiveB()) {
				// for (InvoiceItem item : invoice.getItems()) {
				// if (item.getInvoiceFee().getFeeClass().equals("Membership"))
				// {
				// contractor.setActive('Y');
				// contractor.setAuditColumns(getUser());
				// }
				// }
				// }
				// }

			}
			if (button.equalsIgnoreCase("Delete")) {
				paymentDAO.remove(payment);
				redirect("PaymentDetail.action?id=" + id);
				return BLANK;
			}

			// Sync up the amountApplyMap and the payment.invoices()
			// First delete the applications that aren't there

			// TODO Maybe we should just unapply or apply once invoice/refund at
			// a time
			// this would be a LOT easier, than try to figure out what has
			// changed
			// especially when we have to consider both invoice and refund lists

			if (amountApplyMap.size() > 0) {

			}

			redirect("PaymentDetail.action?payment.id=" + payment.getId());
			return BLANK;
		}

		return SUCCESS;
	}

	private void addNote(String subject) {
		Note note = new Note(payment.getAccount(), getUser(), subject);
		note.setNoteCategory(NoteCategory.Billing);
		note.setCanContractorView(true);
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}

	public boolean isHasUnpaidInvoices() {
		if (payment != null && !payment.getStatus().isUnpaid())
			return false;

		for (Invoice invoice : contractor.getInvoices()) {
			if (invoice.getStatus().isUnpaid())
				return true;
		}

		return false;
	}

	// Getters and Setters

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public PaymentMethod getMethod() {
		return method;
	}

	public void setMethod(PaymentMethod method) {
		this.method = method;
	}

	public Map<Integer, BigDecimal> getAmountApplyMap() {
		return amountApplyMap;
	}

	public void setAmountApplyMap(Map<Integer, BigDecimal> amountApplyMap) {
		this.amountApplyMap = amountApplyMap;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

}
