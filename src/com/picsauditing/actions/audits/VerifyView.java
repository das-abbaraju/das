package com.picsauditing.actions.audits;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.OshaLog;
import com.picsauditing.jpa.entities.OshaLogYear;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.mail.EmailContractorBean;
import com.picsauditing.mail.EmailTemplates;

public class VerifyView extends AuditActionSupport {
	private int oshaID = 0;
	@Autowired
	private OshaLog osha;
	private AuditDataDAO pqfDao;
	private Map<Integer, AuditData> emr = new HashMap<Integer, AuditData>();
	private int followUp = 0;

	public VerifyView(ContractorAuditDAO contractorAuditDAO, AuditDataDAO pqfDao) {
		this.contractorAuditDAO = contractorAuditDAO;
		this.pqfDao = pqfDao;
	}

	public String execute() throws Exception {
		this.findConAudit();

		if (osha != null) {
			permissions.tryPermission(OpPerms.AuditVerification, OpType.Edit);
			for (OshaLog osha2 : conAudit.getContractorAccount().getOshas()) {
				if (osha2.getId() == osha.getId()) {
					saveOSHA(osha2.getYear1(), osha.getYear1());
					saveOSHA(osha2.getYear2(), osha.getYear2());
					saveOSHA(osha2.getYear3(), osha.getYear3());
				}
			}
			contractorAuditDAO.save(conAudit);
		}

		if (emr.size() > 0) {
			ArrayList<Integer> emrQuestions = new ArrayList<Integer>();
			emrQuestions.add(AuditQuestion.EMR07);
			emrQuestions.add(AuditQuestion.EMR06);
			emrQuestions.add(AuditQuestion.EMR05);
			Map<Integer, AuditData> emrDB = pqfDao.findAnswers(this.auditID, emrQuestions);
			saveAuditData(emrDB, AuditQuestion.EMR07);
			saveAuditData(emrDB, AuditQuestion.EMR06);
			saveAuditData(emrDB, AuditQuestion.EMR05);
		}

		loadData();
		
		setVerifiedPercent();
		return SUCCESS;
	}
	
	private void saveOSHA(OshaLogYear oldOsha, OshaLogYear newOsha) {
		oldOsha.setManHours(newOsha.getManHours());
		oldOsha.setFatalities(newOsha.getFatalities());
		oldOsha.setLostWorkCases(newOsha.getLostWorkCases());
		oldOsha.setLostWorkDays(newOsha.getLostWorkDays());
		oldOsha.setInjuryIllnessCases(newOsha.getInjuryIllnessCases());
		oldOsha.setRestrictedWorkCases(newOsha.getRestrictedWorkCases());
		oldOsha.setRecordableTotal(newOsha.getRecordableTotal());
		oldOsha.setNa(newOsha.getNa());
		oldOsha.setComment(newOsha.getComment());
		oldOsha.setVerified(newOsha.getVerified());
	}

	private void setVerifiedPercent() {
		int verified = 0;
		
		if (osha.getYear1().getVerified()) verified++;
		if (osha.getYear2().getVerified()) verified++;
		if (osha.getYear3().getVerified()) verified++;
		
		if (getEmr1() != null && YesNo.Yes.equals(getEmr1().getIsCorrect())) verified++;
		if (getEmr2() != null && YesNo.Yes.equals(getEmr2().getIsCorrect())) verified++;
		if (getEmr3() != null && YesNo.Yes.equals(getEmr3().getIsCorrect())) verified++;
		
		int verifyTotal = 6;

		conAudit.setPercentVerified(Math.round((float)(100 * verified) / verifyTotal));
		
		if (conAudit.getPercentVerified() == 100) {
			conAudit.setAuditStatus(AuditStatus.Active);
		} else
			conAudit.setAuditStatus(AuditStatus.Submitted);
		
		contractorAuditDAO.save(conAudit);
	}

	private void saveAuditData(Map<Integer, AuditData> emrDB, int year) {
		emrDB.get(year).setVerifiedAnswer(emr.get(year).getVerifiedAnswer());
		emrDB.get(year).setComment(emr.get(year).getComment());
		emrDB.get(year).setIsCorrect(emr.get(year).getIsCorrect());
		pqfDao.save(emrDB.get(year));
	}

	private void loadData() {
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
		emr = pqfDao.findAnswers(this.auditID, emrQuestions);
		
	}
	
	public String saveFollowUp() throws Exception {
		this.findConAudit();

		if (followUp > 0) {
			Calendar followUpCal = Calendar.getInstance();
			followUpCal.add(Calendar.DAY_OF_MONTH, followUp);
			conAudit.setScheduledDate(followUpCal.getTime());
			contractorAuditDAO.save(conAudit);
		}
		message = new SimpleDateFormat("MM/dd").format(conAudit
				.getScheduledDate());
		return SUCCESS;
	}

	private void appendOsha(StringBuffer sb, OshaLogYear osha, int year) {
		if( ! osha.getVerified() )
		{
			sb.append(year);
			sb.append( " OSHA - ");
			sb.append(osha.getComment());
			sb.append("\n");
		}
	}
	public String sendEmail() throws Exception {
		this.findConAudit();
		loadData();

		EmailContractorBean emailer = new EmailContractorBean();
		
		StringBuffer sb = new StringBuffer("");
		
		appendOsha(sb, osha.getYear1(), getYear1());
		appendOsha(sb, osha.getYear2(), getYear2());
		appendOsha(sb, osha.getYear3(), getYear3());
		
		AuditData temp = getEmr1();
		if( ! temp.getIsCorrectBoolean() )
		{
			sb.append(getYear1());
			sb.append( " EMR - ");
			sb.append(temp.getComment());
			sb.append("\n");
		}
		
		temp = getEmr2();
		if( ! temp.getIsCorrectBoolean() )
		{
			sb.append(getYear2());
			sb.append( " EMR - ");
			sb.append(temp.getComment());
			sb.append("\n");
		}
		
		temp = getEmr3();
		if( ! temp.getIsCorrectBoolean() )
		{
			sb.append(getYear3());
			sb.append( " EMR - ");
			sb.append(temp.getComment());
			sb.append("\n");
		}
		
		String items = sb.toString();
		
		emailer.addTokens("missing_items", items);
		emailer.sendMessage(EmailTemplates.verifyPqf, this.conAudit
				.getContractorAccount().getIdString(), permissions);

		String note = "PQF Verification email sent to " + emailer.getSentTo();
		this.conAudit.getContractorAccount().addNote(permissions, note);
		this.contractorAuditDAO.save(conAudit);

		// message = conAudit.getContractorAccount().getNotes();
		message = "The email was sent at and the contractor notes were stamped";
		return SUCCESS;
	}

	public OshaLog getOsha() {
		return osha;
	}

	public void setOsha(OshaLog oshalog) {
		this.osha = oshalog;
	}

	public int getOshaID() {
		return oshaID;
	}

	public void setOshaID(int oshaID) {
		this.oshaID = oshaID;
	}

	public Map<Integer, AuditData> getEmr() {
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
