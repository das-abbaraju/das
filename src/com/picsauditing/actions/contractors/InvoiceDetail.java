package com.picsauditing.actions.contractors;

import java.util.Date;

import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class InvoiceDetail extends PicsActionSupport {
	private int id;
	private InvoiceDAO invoiceDAO;
	private String response;	
	private String responsetext;
	private String transactionId;	
	private NoteDAO noteDAO;
	private Invoice invoice;
	
	AppPropertyDAO appPropDao;
	
	public InvoiceDetail(InvoiceDAO invoiceDAO, AppPropertyDAO appPropDao) {
		this.invoiceDAO = invoiceDAO;
		this.appPropDao = appPropDao;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		invoice = invoiceDAO.find(id);
		
		if (!permissions.hasPermission(OpPerms.AllContractors)
				&& permissions.getAccountId() != invoice.getAccount().getId()) {
			throw new NoRightsException("You can't view this invoice");
		}
		
		BrainTreeService processPayment = new BrainTreeService();
		processPayment.setUserName(appPropDao.find("brainTree.username").getValue());
		processPayment.setPassword(appPropDao.find("brainTree.password").getValue());		
		
		if ("Charge".equals(button)) {
			processPayment.procesPayment(invoice.getAccount().getId(), invoice.getTotalAmount());

			if (!Strings.isEmpty(responsetext) && !response.equals("1")) {
				String errorMessage = responsetext;
				try {
					int endPos = responsetext.indexOf("REFID");
					if (endPos > 1)
						responsetext.substring(0, endPos - 1);
				} catch (Exception justUseThePlainResponseText) {
				}
				addActionError(errorMessage);
			} else {
				invoice.setPaid(true);
				invoice.setPaidDate(new Date());
				invoice.setTransactionID(transactionId);
				invoice.setAuditColumns(getUser());
				invoiceDAO.save(invoice);
				
				noteDAO.addPicsAdminNote(invoice.getAccount(), getUser(), "Paid the invoice");
				addActionMessage("Transaction Successfully Processed. Invoice " + invoice.getId() + " has been Paid.");
			}
		}
		
		return SUCCESS;
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

	public String getResponse() {
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

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

}
