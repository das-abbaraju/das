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

	public ReportContractorRiskAssessment(ContractorAccountDAO contractorAccountDAO, AuditDataDAO auditDataDAO, NoteDAO noteDAO) {
		this.contractorAccountDAO = contractorAccountDAO;
		this.auditDataDAO = auditDataDAO;
		this.noteDAO = noteDAO;
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
		sql.addWhere("((pd.answer = 'Low' and c.riskLevel > 1) or (pd.answer = 'Medium' and c.riskLevel > 2))");
		sql.addWhere("pd.dateVerified is null");
		sql.addField("pd.answer");
		sql.addField("pd.id AS answerID");
		sql.addWhere("a.active = 'Y'");
	}

	@Override
	public String execute() throws Exception {
		loadPermissions();
		if (button != null) {
			ContractorAccount cAccount = contractorAccountDAO.find(conID);
			AuditData aData = auditDataDAO.find(answerID);
			if ("Accept".equals(button)) {
				cAccount.setRiskLevel(LowMedHigh.valueOf(aData.getAnswer()));
				cAccount.setAuditColumns(permissions);
				contractorAccountDAO.save(cAccount);
				Note note = new Note(cAccount, getUser(), "RiskLevel adjusted to "+aData.getAnswer() + " for " + auditorNotes);
				note.setNoteCategory(NoteCategory.General);
				note.setCanContractorView(true);
				note.setViewableById(Account.EVERYONE);
				noteDAO.save(note);
			}
			aData.setDateVerified(new Date());
			if(!Strings.isEmpty(auditorNotes))
				aData.setComment(auditorNotes);
			aData.setAuditColumns(permissions);
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
