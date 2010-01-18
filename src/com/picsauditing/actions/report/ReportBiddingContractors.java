package com.picsauditing.actions.report;

import java.util.Date;
import java.util.Iterator;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportBiddingContractors extends ReportAccount {
	protected int conID;
	protected String operatorNotes;

	protected ContractorAccountDAO contractorAccountDAO;
	protected NoteDAO noteDAO;
	protected ContractorOperatorDAO contractorOperatorDAO;
	protected ContractorAuditDAO contractorAuditDAO;
	protected InvoiceItemDAO invoiceItemDAO;
	protected OperatorAccountDAO operatorAccountDAO;

	public ReportBiddingContractors(ContractorAccountDAO contractorAccountDAO, NoteDAO noteDAO,
			ContractorOperatorDAO contractorOperatorDAO, ContractorAuditDAO contractorAuditDAO,
			InvoiceItemDAO invoiceItemDAO, OperatorAccountDAO operatorAccountDAO) {
		this.contractorAccountDAO = contractorAccountDAO;
		this.noteDAO = noteDAO;
		this.contractorOperatorDAO = contractorOperatorDAO;
		this.contractorAuditDAO = contractorAuditDAO;
		this.invoiceItemDAO = invoiceItemDAO;
		this.operatorAccountDAO = operatorAccountDAO;
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		if (permissions.isOperator()) {
			sql.addField("flags.waitingOn");
		}
		sql.addWhere("a.acceptsBids = 1");

		filteredDefault = true;
		getFilter().setShowConWithPendingAudits(false);
	}

	@Override
	public String execute() throws Exception {
		loadPermissions();
		if (button != null) {
			ContractorAccount cAccount = contractorAccountDAO.find(conID);
			String summary = "";
			int templateId = 0;
			if ("Upgrade".equals(button)) {
				cAccount.setAcceptsBids(false);
				cAccount.setRenew(true);
				for (ContractorAudit cAudit : cAccount.getAudits()) {
					if (cAudit.getAuditType().isPqf() && !cAudit.getAuditStatus().isPending()) {
						cAudit.changeStatus(AuditStatus.Pending, getUser());
						contractorAuditDAO.save(cAudit);
						break;
					}
				}

				// Setting the payment Expires date to today
				for (Invoice invoice : cAccount.getInvoices()) {
					for (InvoiceItem invoiceItem : invoice.getItems()) {
						if (invoiceItem.getInvoiceFee().getId() == InvoiceFee.BIDONLY) {
							invoiceItem.setPaymentExpires(new Date());
							invoiceItemDAO.save(invoiceItem);
						}
					}
				}

				if (permissions.isOperator() && permissions.isApprovesRelationships()) {
					approveContractor(cAccount, permissions.getAccountId());
				}

				if (permissions.isCorporate()) {
					OperatorAccount corporate = operatorAccountDAO.find(permissions.getAccountId());
					for (Facility facility : corporate.getOperatorFacilities()) {
						if (YesNo.Yes.equals(facility.getOperator().getApprovesRelationships())) {
							approveContractor(cAccount, facility.getOperator().getId());
						}
					}
				}

				templateId = 73; // Trial Contractor Account Approval
				summary = "Upgraded and Approved the Bid Only Account for " + permissions.getAccountName() + " and notified contractor via email.";
			}
			if ("Reject".equals(button)) {
				cAccount.setRenew(false);
				Iterator<ContractorOperator> cIterator = cAccount.getOperators().iterator();
				while (cIterator.hasNext()) {
					ContractorOperator co = cIterator.next();
					if (co.getOperatorAccount().getId() == permissions.getAccountId()) {
						contractorOperatorDAO.remove(co);
						cAccount.getOperators().remove(co);
						break;
					}
				}
				templateId = 75;// Trial Contractor Account Rejection
				summary = "Rejected Contractor for the Bid Only Account for " + permissions.getAccountName();
			}

			cAccount.setNeedsRecalculation(true);
			cAccount.setAuditColumns(permissions);
			contractorAccountDAO.save(cAccount);

			Note note = new Note(cAccount, getUser(), summary);
			if (!Strings.isEmpty(operatorNotes)) {
				note.setBody(operatorNotes);
			}
			note.setNoteCategory(NoteCategory.OperatorChanges);
			note.setCanContractorView(true);
			note.setViewableById(permissions.getAccountId());
			noteDAO.save(note);

			if (templateId > 0) {
				try {
					// Sending a Email to the contractor for upgrade/rejection
					EmailBuilder emailBuilder = new EmailBuilder();
					emailBuilder.setTemplate(templateId);
					emailBuilder.setPermissions(permissions);
					emailBuilder.setContractor(cAccount);
					emailBuilder.addToken("permissions", permissions);
					EmailQueue emailQueue = emailBuilder.build();
					emailQueue.setPriority(100);
					EmailSender.send(emailQueue);
					addActionMessage(summary);
				} catch (Exception e) {
					addActionError(e.getLocalizedMessage());
				}
			}

			operatorNotes = "";
		}
		return super.execute();
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	public String getOperatorNotes() {
		return operatorNotes;
	}

	public void setOperatorNotes(String operatorNotes) {
		this.operatorNotes = operatorNotes;
	}

	public void approveContractor(ContractorAccount cAccount, int operatorID) {
		for (ContractorOperator cOperator : cAccount.getOperators()) {
			if (cOperator.getOperatorAccount().getId() == operatorID) {
				cOperator.setWorkStatus("Y");
				cOperator.setAuditColumns(permissions);
				contractorOperatorDAO.save(cOperator);
				break;
			}
		}
	}
}
