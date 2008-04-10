package com.picsauditing.actions.audits;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.AuditActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.OshaLog;
import com.picsauditing.jpa.entities.YesNo;

public class VerifyView extends AuditActionSupport {
	private int oshaID = 0;
	@Autowired
	private OshaLog osha;
	private AuditDataDAO pqfDao;
	private HashMap<Integer, AuditData> emr;
	private int followUp = 0;

	public VerifyView(ContractorAuditDAO contractorAuditDAO, AuditDataDAO pqfDao) {
		this.contractorAuditDAO = contractorAuditDAO;
		this.pqfDao = pqfDao;
	}

	public String execute() throws Exception {
		this.findConAudit();

		if (osha != null)
		{
			for(OshaLog osha2 : conAudit.getContractorAccount().getOshas()) {
				if (osha2.getId() == osha.getId()) {
					osha2.setManHours1(osha.getManHours1());
					osha2.setManHours2(osha.getManHours2());
					osha2.setManHours3(osha.getManHours3());
					osha2.setFatalities1(osha.getFatalities1());
					osha2.setFatalities2(osha.getFatalities2());
					osha2.setFatalities3(osha.getFatalities3());
				
				
				
				}
			}
			contractorAuditDAO.save(conAudit);
		}
		// Retreive the osha record we selected
		// or pick the only child if only one exists
		if (oshaID > 0) {
			for (OshaLog row : conAudit.getContractorAccount().getOshas())
				if (row.getId() == oshaID)
					osha = row;
		
		} else if (conAudit.getContractorAccount().getOshas().size() == 1) {
			osha = conAudit.getContractorAccount().getOshas().get(0);
			oshaID = osha.getId();
		}

		// Now get the EMR data
		ArrayList<Integer> emrQuestions = new ArrayList<Integer>();
		emrQuestions.add(AuditQuestion.EMR07);
		emrQuestions.add(AuditQuestion.EMR06);
		emrQuestions.add(AuditQuestion.EMR05);
		emrQuestions.add(AuditQuestion.EMR04);
		emr = pqfDao.findAnswers(this.auditID, emrQuestions);

		return SUCCESS;
	}
	
	public String saveFollowUp() throws Exception {
		this.findConAudit();

		if (followUp > 0) {
			Calendar followUpCal = Calendar.getInstance();
			followUpCal.add(Calendar.DAY_OF_MONTH, followUp);
			conAudit.setScheduledDate(followUpCal.getTime());
			contractorAuditDAO.save(conAudit);
		}
		message = new SimpleDateFormat("MM/dd").format(conAudit.getScheduledDate());
		return SUCCESS;
	}

	public OshaLog getOsha() {
		return osha;
	}

	public void setOsha(OshaLog oshalog){
		this.osha = oshalog;
	}
	
	public int getOshaID() {
		return oshaID;
	}

	public void setOshaID(int oshaID) {
		this.oshaID = oshaID;
	}

	public HashMap<Integer, AuditData> getEmr() {
		return emr;
	}

	public void setEmr(HashMap<Integer, AuditData> emr) {
		this.emr = emr;
	}

	public int getYear1() {
		return 2007;
	}

	public int getYear2() {
		return this.getYear1() - 1;
	}

	public int getYear3() {
		return this.getYear1() - 2;
	}

	public void setEmr1(AuditData emr) {
		this.emr.put(AuditQuestion.EMR07, emr);
	}

	public void setEmr2(AuditData emr) {
		this.emr.put(AuditQuestion.EMR06, emr);
	}

	public void setEmr3(AuditData emr) {
		this.emr.put(AuditQuestion.EMR05, emr);
	}

	public AuditData getEmr1() {
		return emr.get(AuditQuestion.EMR07);
	}

	public AuditData getEmr2() {
		return emr.get(AuditQuestion.EMR06);
	}

	public AuditData getEmr3() {
		return emr.get(AuditQuestion.EMR05);
	}

	public YesNo[] getYesNos() {
		return YesNo.values();
	}

	public ArrayList<String> getOshaProblems() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("");
		list.add("Contradicting Data");
		list.add("Missing 300");
		list.add("Missing 300a");
		list.add("Incomplete");
		list.add("Incorrect Form");
		list.add("Incorrect Year");
		return list;
	}

	public ArrayList<String> getEmrProblems() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("");
		list.add("Need EMR");
		list.add("Need Loss Run");
		list.add("Not Insurance Issued");
		list.add("Incorrect Upload");
		list.add("Incorrect Year");
		return list;
	}

	public String getContractorNotes() {
		String notes = this.conAudit.getContractorAccount().getNotes();
		notes = notes.replace("\n", "<br>");

		int position = 0;
		for (int i = 0; i < 5; i++) {
			position = notes.indexOf("<br>", position + 1);
			if (position == -1)
				return notes;
		}

		notes = notes.substring(0, position);
		return notes;
	}

	public int getFollowUp() {
		return followUp;
	}

	public void setFollowUp(int followUp) {
		this.followUp = followUp;

	}

}
