package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class InvoiceDetail extends PicsActionSupport implements Preparable {
	private int id; //accountID
	private boolean edit = false;
	
	private InvoiceDAO invoiceDAO;
	private InvoiceFeeDAO invoiceFeeDAO;
	private InvoiceItemDAO invoiceItemDAO;
	private NoteDAO noteDAO;
	private ContractorAccountDAO conAccountDAO;
	
	private int newFeeId;
	
	private String response;	
	private String responsetext;
	private String transactionid;	
	
	private Invoice invoice;
	private ContractorAccount contractor;
	
	private List<InvoiceFee> feeList = new ArrayList<InvoiceFee>();
	
	AppPropertyDAO appPropDao;
	
	public InvoiceDetail(InvoiceDAO invoiceDAO, AppPropertyDAO appPropDao,
			NoteDAO noteDAO,
			ContractorAccountDAO conAccountDAO, InvoiceFeeDAO invoiceFeeDAO,
			InvoiceItemDAO invoiceItemDAO) {
		this.invoiceDAO = invoiceDAO;
		this.appPropDao = appPropDao;
		this.noteDAO = noteDAO;
		this.conAccountDAO = conAccountDAO;
		this.invoiceFeeDAO = invoiceFeeDAO;
		this.invoiceItemDAO = invoiceItemDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		feeList = invoiceFeeDAO.findAll();
		
		if (!permissions.hasPermission(OpPerms.AllContractors)
				&& permissions.getAccountId() != invoice.getAccount().getId()) {
			throw new NoRightsException("You can't view this invoice");
		}
		
		contractor = (ContractorAccount) invoice.getAccount();
		
		if (edit) {
			if ("Save".equals(button)) {
				if (newFeeId > 0) {
					
					InvoiceItem newItem = new InvoiceItem();
					InvoiceFee newFee = invoiceFeeDAO.find(newFeeId);
					newItem.setInvoiceFee(newFee);
					newItem.setAmount(newFee.getAmount());
					newItem.setInvoice(invoice);
					newItem.setAuditColumns(getUser());
					
					invoiceItemDAO.save(newItem);
					
					invoice.getItems().add(newItem);
					newFeeId = 0;
				}
				
				int invoiceTotalAmt = invoice.getTotalAmount();
				int conBalance = contractor.getBalance();
				for(InvoiceItem item : invoice.getItems()) {
					invoiceTotalAmt += item.getAmount();
				}
				
				conBalance += invoiceTotalAmt;
				
				invoice.setTotalAmount(invoiceTotalAmt);
				contractor.setBalance(conBalance);
				
				invoiceDAO.save(invoice);
				conAccountDAO.save(contractor);
			}
		} else {
			if ("Charge".equals(button) && contractor.isCcOnFile()) {
				BrainTreeService paymentService = new BrainTreeService();
				paymentService.setUserName(appPropDao.find("brainTree.username").getValue());
				paymentService.setPassword(appPropDao.find("brainTree.password").getValue());

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
					contractor
							.setBalance(conBalance - invoice.getTotalAmount());
					conAccountDAO.save(contractor);

					noteDAO.addPicsAdminNote(invoice.getAccount(), getUser(),
							"Paid the invoice");
					addActionMessage("Transaction Successfully Processed. Invoice "
							+ invoice.getId() + " has been Paid.");
				}
			}
		}
		
		return SUCCESS;
	}
	
	@Override
	public void prepare() throws Exception {
		int invoiceId = getParameter("invoice.id");
		invoice = invoiceDAO.find(invoiceId);
		id = invoice.getAccount().getId();
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

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public List<InvoiceFee> getFeeList() {
		return feeList;
	}

	public int getNewFeeId() {
		return newFeeId;
	}

	public void setNewFeeId(int newFeeId) {
		this.newFeeId = newFeeId;
	}

	
}
