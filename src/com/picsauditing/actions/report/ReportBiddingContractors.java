package com.picsauditing.actions.report;

import java.util.Date;
import java.util.Iterator;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
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
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportBiddingContractors extends ReportAccount {
	protected int conID;
	protected String operatorNotes;

	protected ContractorAccountDAO contractorAccountDAO;
	protected NoteDAO noteDAO;
	protected ContractorOperatorDAO contractorOperatorDAO;
	protected InvoiceItemDAO invoiceItemDAO;
	protected OperatorAccountDAO operatorAccountDAO;

	public ReportBiddingContractors(ContractorAccountDAO contractorAccountDAO,
			NoteDAO noteDAO, ContractorOperatorDAO contractorOperatorDAO,
			InvoiceItemDAO invoiceItemDAO, OperatorAccountDAO operatorAccountDAO) {
		this.contractorAccountDAO = contractorAccountDAO;
		this.noteDAO = noteDAO;
		this.contractorOperatorDAO = contractorOperatorDAO;
		this.invoiceItemDAO = invoiceItemDAO;
		this.operatorAccountDAO = operatorAccountDAO;
	}

	@Override
	protected void buildQuery() {
		skipPermissions = true;
		super.buildQuery();

		filteredDefault = true;
		getFilter().setShowConWithPendingAudits(false);
		
		// Anytime we query contractor accounts as an operator,
		// get the flag color/status at the same time
		if(permissions.isOperatorCorporate()) {
			sql.addJoin("JOIN generalcontractors gc ON gc.subID = a.id AND gc.genID = "
						+ permissions.getAccountId());
			sql.addField("gc.workStatus");
			sql.addField("gc.waitingOn");
			sql.addWhere("gc.genID = " + permissions.getAccountId());
		}
		sql.addWhere("a.acceptsBids = 1");

		PermissionQueryBuilder qb = new PermissionQueryBuilder(permissions, PermissionQueryBuilder.SQL);
		qb.setWorkingFacilities(false);
		sql.addWhere("1 " + qb.toString());
	}

	@Override
	public String execute() throws Exception {
		loadPermissions();
		if (button != null) {
			ContractorAccount cAccount = contractorAccountDAO.find(conID);
			String summary = "";
			int templateId = 0;
			if ("Upgrade".equals(button)) {
				// See also ContractorDashboard Upgrade to Full Membership
				cAccount.setAcceptsBids(false);
				cAccount.setRenew(true);
				
				for (ContractorAudit cAudit : cAccount.getAudits()) {
					if (cAudit.getAuditType().isPqf()) {
						for (ContractorAuditOperator cao : cAudit.getOperators()) {
							if (cao.getStatus().after(AuditStatus.Pending)) {
								cao.changeStatus(AuditStatus.Pending, permissions);
								contractorAccountDAO.save(cao);
							}
						}
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

				if (permissions.isOperator()
						&& permissions.isApprovesRelationships()) {
					approveContractor(cAccount, permissions.getAccountId());
				}

				if (permissions.isCorporate()) {
					OperatorAccount corporate = operatorAccountDAO
							.find(permissions.getAccountId());
					for (Facility facility : corporate.getOperatorFacilities()) {
						if (YesNo.Yes.equals(facility.getOperator()
								.getApprovesRelationships())) {
							approveContractor(cAccount, facility.getOperator()
									.getId());
						}
					}
				}

				templateId = 73; // Trial Contractor Account Approval
				summary = "Upgraded and Approved the Bid Only Account for "
						+ permissions.getAccountName()
						+ " and notified contractor via email.";
			}
			if ("Reject".equals(button)) {
				cAccount.setRenew(false);
				Iterator<ContractorOperator> cIterator = cAccount
						.getNonCorporateOperators().iterator();
				while (cIterator.hasNext()) {
					ContractorOperator co = cIterator.next();
					if (co.getOperatorAccount().getId() == permissions
							.getAccountId()) {
						contractorOperatorDAO.remove(co);
						cAccount.getOperators().remove(co);
						break;
					}
				}
				templateId = 75;// Trial Contractor Account Rejection
				summary = "Rejected Contractor for the Bid Only Account for "
						+ permissions.getAccountName();
			}

			cAccount.incrementRecalculation();
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
					emailBuilder.setContractor(cAccount,
							OpPerms.ContractorAdmin);
					emailBuilder.addToken("permissions", permissions);
					EmailQueue emailQueue = emailBuilder.build();
					emailQueue.setPriority(100);
					emailQueue
							.setFromAddress((templateId == 73) ? "PICS Billing <billing@picsauditing.com>"
									: "PICS Info <info@picsauditing.com>");
					emailQueue.setViewableById(permissions.getTopAccountID());
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
		for (ContractorOperator cOperator : cAccount.getNonCorporateOperators()) {
			if (cOperator.getOperatorAccount().getId() == operatorID) {
				cOperator.setWorkStatus("Y");
				cOperator.setAuditColumns(permissions);
				contractorOperatorDAO.save(cOperator);
				break;
			}
		}
	}
}
