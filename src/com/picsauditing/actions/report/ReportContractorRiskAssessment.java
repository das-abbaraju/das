package com.picsauditing.actions.report;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

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

	@Autowired
	protected ContractorAccountDAO contractorAccountDAO;
	@Autowired
	protected AuditDataDAO auditDataDAO;
	@Autowired
	protected NoteDAO noteDAO;

	public ReportContractorRiskAssessment() {
		this.orderByDefault = "a.creationDate DESC, a.name";
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.RiskRank);
	}

	public void buildQuery() {
		skipPermissions = true;
		super.buildQuery();
		sql.addJoin("JOIN contractor_audit ca ON ca.conid = c.id");
		sql.addJoin("JOIN pqfdata pd ON pd.auditid = ca.id");
		sql.addWhere("ca.audittypeID = 1");
		sql.addWhere("pd.questionid = 2444");
		sql.addWhere("((pd.answer = 'Low' and c.safetyRisk > 1) or (pd.answer like 'Med%' and c.safetyRisk > 2))");
		// TODO Add Product Risk question and modify this report to reject/accept either SafetyRisk or ProductRisk
		sql.addWhere("pd.dateVerified is null");
		sql.addField("pd.answer");
		sql.addField("pd.id AS answerID");
	}

	public String accept() throws Exception {
		ContractorAccount cAccount = contractorAccountDAO.find(conID);
		AuditData aData = auditDataDAO.find(answerID);

		String answer = aData.getAnswer();
		if (answer.equals("Medium"))
			answer = "Med";

		cAccount.setSafetyRisk(LowMedHigh.valueOf(answer));
		cAccount.setLastUpgradeDate(new Date());
		cAccount.setAuditColumns(permissions);
		contractorAccountDAO.save(cAccount);

		Note note = new Note(cAccount, getUser(), "Safety Risk adjusted from " + cAccount.getSafetyRisk().toString() + " to "
				+ aData.getAnswer() + " for " + auditorNotes);
		addNote(note, cAccount);

		aData.setDateVerified(new Date());
		if (!Strings.isEmpty(auditorNotes))
			aData.setComment(auditorNotes);
		aData.setAuditColumns(permissions);

		auditDataDAO.save(aData);
		auditorNotes = "";

		return super.execute();
	}

	public String reject() throws Exception {
		ContractorAccount cAccount = contractorAccountDAO.find(conID);
		AuditData aData = auditDataDAO.find(answerID);

		aData.setDateVerified(new Date());
		if (!Strings.isEmpty(auditorNotes))
			aData.setComment(auditorNotes);
		aData.setAuditColumns(permissions);

		Note note = new Note(cAccount, getUser(), "Rejected Safety Risk adjustment from "
				+ cAccount.getSafetyRisk().toString() + " to " + aData.getAnswer() + " for " + auditorNotes);
		addNote(note, cAccount);

		auditDataDAO.save(aData);
		auditorNotes = "";

		return super.execute();
	}

	private void addNote(Note note, ContractorAccount account) {
		note.setNoteCategory(NoteCategory.RiskRanking);
		note.setCanContractorView(false);
		note.setViewableById(Account.EVERYONE);
		note.setAccount(account);
		note.setAuditColumns(permissions);
		noteDAO.save(note);
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
