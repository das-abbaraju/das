package com.picsauditing.actions.report;

import java.util.Date;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportContractorRiskAssessment extends ReportAccount {
	protected int conID;
	protected int answerID;
	protected String auditorNotes;
	protected ContractorAccountDAO contractorAccountDAO;
	protected AuditDataDAO auditDataDAO;
	protected NoteDAO noteDAO;

	public ReportContractorRiskAssessment(ContractorAccountDAO contractorAccountDAO, AuditDataDAO auditDataDAO,
			NoteDAO noteDAO) {
		this.contractorAccountDAO = contractorAccountDAO;
		this.auditDataDAO = auditDataDAO;
		this.noteDAO = noteDAO;
		this.orderByDefault = "a.creationDate DESC, a.name";
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.ManageAudits);
	}

	public void buildQuery() {
		skipPermissions = true;
		super.buildQuery();
		sql.addJoin("JOIN contractor_audit ca ON ca.conid = c.id");
		sql.addJoin("JOIN pqfdata pd ON pd.auditid = ca.id");
		sql.addWhere("ca.audittypeID = 1");
		sql.addWhere("pd.questionid = 2444");
		sql.addWhere("((pd.answer = 'Low' and c.riskLevel > 1) or (pd.answer like 'Med%' and c.riskLevel > 2))");
		sql.addWhere("pd.dateVerified is null");
		sql.addField("pd.answer");
		sql.addField("pd.id AS answerID");
	}

	@Override
	public String execute() throws Exception {
		loadPermissions();
		if (button != null) {
			ContractorAccount cAccount = contractorAccountDAO.find(conID);
			AuditData aData = auditDataDAO.find(answerID);
			Note note = null;
			if ("Accept".equals(button)) {
				String answer = aData.getAnswer();
				if (answer.equals("Medium")) {
					answer = "Med";
				}
				note = new Note(cAccount, getUser(), "RiskLevel adjusted from " + cAccount.getRiskLevel().toString()
						+ " to " + aData.getAnswer() + " for " + auditorNotes);
				cAccount.setRiskLevel(LowMedHigh.valueOf(answer));
				cAccount.setAuditColumns(permissions);
				contractorAccountDAO.save(cAccount);
			} else
				note = new Note(cAccount, getUser(), "Rejected RiskLevel adjustment from " + cAccount.getRiskLevel().toString()
						+ " to " + aData.getAnswer() + " for " + auditorNotes);

			aData.setDateVerified(new Date());
			if (!Strings.isEmpty(auditorNotes))
				aData.setComment(auditorNotes);
			aData.setAuditColumns(permissions);

			// Update the note and save it in the database.
			note.setNoteCategory(NoteCategory.General);
			note.setCanContractorView(false);
			note.setViewableById(Account.EVERYONE);
			note.setAccount(cAccount);
			note.setAuditColumns(permissions);
			noteDAO.save(note);

			auditDataDAO.save(aData);
			auditorNotes = "";
		}
		return super.execute();
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	public int getAnswerID() {
		return answerID;
	}

	public void setAnswerID(int answerID) {
		this.answerID = answerID;
	}

	public String getAuditorNotes() {
		return auditorNotes;
	}

	public void setAuditorNotes(String auditorNotes) {
		this.auditorNotes = auditorNotes;
	}
}
