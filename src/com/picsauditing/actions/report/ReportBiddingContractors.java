package com.picsauditing.actions.report;

import java.util.Iterator;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
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

	public ReportBiddingContractors(ContractorAccountDAO contractorAccountDAO, NoteDAO noteDAO,
			ContractorOperatorDAO contractorOperatorDAO, ContractorAuditDAO contractorAuditDAO) {
		this.contractorAccountDAO = contractorAccountDAO;
		this.noteDAO = noteDAO;
		this.contractorOperatorDAO = contractorOperatorDAO;
		this.contractorAuditDAO = contractorAuditDAO;
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
			if ("Approve".equals(button)) {
				cAccount.setAcceptsBids(false);
				cAccount.setRenew(true);
				for (ContractorAudit cAudit : cAccount.getAudits()) {
					if (cAudit.getAuditType().isPqf() && !cAudit.getAuditStatus().isPending()) {
						cAudit.changeStatus(AuditStatus.Pending, getUser());
						contractorAuditDAO.save(cAudit);
						break;
					}
				}
				for(ContractorOperator cOperator : cAccount.getOperators()) {
					if(cOperator.getOperatorAccount().getId() == permissions.getAccountId()) {
						cOperator.setWorkStatus("Y");
						cOperator.setAuditColumns(permissions);
						contractorOperatorDAO.save(cOperator);
						break;
					}
				}
				summary = "Upgraded and Approved the Trial Account for " + permissions.getAccountName();
				
				// Sending a Email to the contractor for upgrade
				EmailBuilder emailBuilder = new EmailBuilder();
				emailBuilder.setTemplate(73); // Trial Contractor Account Approval
				emailBuilder.setPermissions(permissions);
				emailBuilder.setContractor(cAccount);
				emailBuilder.addToken("permissions", permissions);
				EmailQueue emailQueue = emailBuilder.build();
				emailQueue.setPriority(60);
				EmailSender.send(emailQueue);
			}
			if ("Reject".equals(button)) {
				cAccount.setRenew(false);
				Iterator<ContractorOperator> cIterator = cAccount.getOperators().iterator();
				while(cIterator.hasNext()) {
					ContractorOperator co = cIterator.next();
					if (co.getOperatorAccount().getId() == permissions.getAccountId()) {
						contractorOperatorDAO.remove(co);
						cAccount.getOperators().remove(co);
						break;
					}	
				}
				summary = "Rejected the Trial Account for " + permissions.getAccountName();
			}
			cAccount.setNeedsRecalculation(true);
			cAccount.setAuditColumns(permissions);
			contractorAccountDAO.save(cAccount);

			Note note = new Note(cAccount, getUser(), summary);
			if(!Strings.isEmpty(operatorNotes)) {
				note.setBody(operatorNotes);
			}
			note.setNoteCategory(NoteCategory.OperatorChanges);
			note.setCanContractorView(true);
			note.setViewableById(permissions.getAccountId());
			noteDAO.save(note);
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
}
