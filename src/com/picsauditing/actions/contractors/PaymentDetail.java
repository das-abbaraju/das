package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.Transaction;

@SuppressWarnings("serial")
public class PaymentDetail extends ContractorActionSupport implements Preparable {
	private boolean edit = false;

	private InvoiceDAO invoiceDAO;
	private InvoiceFeeDAO invoiceFeeDAO;
	private InvoiceItemDAO invoiceItemDAO;
	private PaymentDAO paymentDAO;
	private NoteDAO noteDAO;

	private String checkNumber;
	private int newFeeId;
	private int refundFeeId;
	private BrainTreeService paymentService = new BrainTreeService();

	private Payment payment;
	private int invoiceID = 0;

	private List<InvoiceFee> feeList = null;

	private BigDecimal amountApply = BigDecimal.ZERO;

	AppPropertyDAO appPropDao;

	public PaymentDetail(InvoiceDAO invoiceDAO, AppPropertyDAO appPropDao, NoteDAO noteDAO,
			ContractorAccountDAO conAccountDAO, InvoiceFeeDAO invoiceFeeDAO, InvoiceItemDAO invoiceItemDAO,
			ContractorAuditDAO auditDao, PaymentDAO paymentDAO) {
		super(conAccountDAO, auditDao);
		this.invoiceDAO = invoiceDAO;
		this.appPropDao = appPropDao;
		this.noteDAO = noteDAO;
		this.invoiceFeeDAO = invoiceFeeDAO;
		this.invoiceItemDAO = invoiceItemDAO;
		this.paymentDAO = paymentDAO;
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

		if (button != null) {
			if ("Apply".equals(button) && payment != null && invoiceID > 0) {
				StringBuilder sb = new StringBuilder();

				sb.append("Payment applied for invoice #").append(invoiceID).append(" with the amomunt $").append(
						amountApply);
				addActionMessage(sb.toString());
			}
		}

		return SUCCESS;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public BigDecimal getAmountApply() {
		return amountApply;
	}

	public void setAmountApply(BigDecimal amountApply) {
		this.amountApply = amountApply;
	}

	public int getInvoiceID() {
		return invoiceID;
	}

	public void setInvoiceID(int invoiceID) {
		this.invoiceID = invoiceID;
	}
}
