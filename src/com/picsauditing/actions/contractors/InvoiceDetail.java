package com.picsauditing.actions.contractors;

import java.util.Date;

import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class InvoiceDetail extends PicsActionSupport {
	private int id;
	private InvoiceDAO invoiceDAO;
	private String response;	
	private String responsetext;
	private String transactionid;	
	private NoteDAO noteDAO;
	private Invoice invoice;
	private ContractorAccount contractor = new ContractorAccount();
	private AccountDAO acctDAO;
	private ContractorAccountDAO conAccountDAO;
	
	AppPropertyDAO appPropDao;
	
	public InvoiceDetail(InvoiceDAO invoiceDAO, AppPropertyDAO appPropDao, AccountDAO acctDAO, NoteDAO noteDAO, ContractorAccountDAO conAccountDAO) {
		this.invoiceDAO = invoiceDAO;
		this.appPropDao = appPropDao;
		this.acctDAO = acctDAO;
		this.noteDAO = noteDAO;
		this.conAccountDAO = conAccountDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		invoice = invoiceDAO.find(id);
		
		if (!permissions.hasPermission(OpPerms.AllContractors)
				&& permissions.getAccountId() != invoice.getAccount().getId()) {
			throw new NoRightsException("You can't view this invoice");
		}
		
		contractor = (ContractorAccount) acctDAO.find(invoice.getAccount().getId()); // do i have to do this or can i access it some other way?
		
		BrainTreeService paymentService = new BrainTreeService();
		paymentService.setUserName(appPropDao.find("brainTree.username").getValue());
		paymentService.setPassword(appPropDao.find("brainTree.password").getValue());		
		
		if ("Charge".equals(button) && !contractor.getPaymentMethodStatus().equals("Missing")) {
			paymentService.procesPayment(contractor.getId(), invoice.getTotalAmount());

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
				invoice.setTransactionID(transactionid);
				invoice.setAuditColumns(getUser());
				invoiceDAO.save(invoice);
				
				int conBalance = contractor.getBalance();
				contractor.setBalance(conBalance - invoice.getTotalAmount());
				conAccountDAO.save(contractor);
				
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

	public String getTransactionid() {
		return transactionid;
	}

	public void setTransactionid(String transactionid) {
		this.transactionid = transactionid;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

}
