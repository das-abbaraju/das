package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.YesNo;

public class VerifyView extends ContractorActionSupport {
	private int followUp = 0;
	private Map<Integer, AuditData> pqfQuestions = new LinkedHashMap<Integer, AuditData>();
	private List<OshaAudit> oshas = new ArrayList<OshaAudit>(); 
	protected AuditDataDAO auditDataDAO;
	protected List<AuditData> emrQuestions = new ArrayList<AuditData>(); 

	public VerifyView(ContractorAccountDAO accountDao, ContractorAuditDAO contractorAuditDAO, AuditDataDAO auditDataDAO) {
		super(accountDao, contractorAuditDAO);
		this.auditDataDAO = auditDataDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.AuditVerification);
		this.findContractor();
		this.subHeading = "Verify PQF/OSHA/EMR";

		for (ContractorAudit conAudit : contractor.getAudits()) {
			if (conAudit.getAuditStatus().equals(AuditStatus.Pending)
					|| conAudit.getAuditStatus().equals(AuditStatus.Submitted)) {
				if (conAudit.getAuditType().isPqf()) {
					List<AuditData> temp = auditDataDAO.findCustomPQFVerifications(conAudit.getId());
					pqfQuestions = new LinkedHashMap<Integer, AuditData>();
					for (AuditData ad : temp) {
						pqfQuestions.put(ad.getQuestion().getQuestionID(), ad);
					}
				}
				if (conAudit.getAuditType().isAnnualAddendum()) {
					for (OshaAudit oshaAudit : conAudit.getOshas()) {
						if (oshaAudit.isCorporate()) {
							oshas.add(oshaAudit);
						}
					}
					for (AuditData auditData : conAudit.getData()) {
						emrQuestions.add(auditData);
					}
				}
			}
		}	

		// for (AuditCatData auditCatData : getCategories()) {
		// if (auditCatData.getCategory().getId() == 29)
		// oshaCatDataId = auditCatData.getId();
		// if (auditCatData.getCategory().getId() == 10)
		// emrCatDataId = auditCatData.getId();
		// }
		//
		// if (emr.size() > 0) {
		// ArrayList<Integer> emrQuestions = new ArrayList<Integer>();
		// emrQuestions.add(AuditQuestion.EMR07);
		// emrQuestions.add(AuditQuestion.EMR06);
		// emrQuestions.add(AuditQuestion.EMR05);
		//
		// Map<Integer, AuditData> emrDB =
		// auditDataDao.findAnswers(this.auditID, emrQuestions);
		// saveAuditData(emrDB, AuditQuestion.EMR07);
		// saveAuditData(emrDB, AuditQuestion.EMR06);
		// saveAuditData(emrDB, AuditQuestion.EMR05);
		// }
		//
		// if (customVerification != null) {
		// for (Integer i : customVerification.keySet()) {
		// AuditData aq = (AuditData) customVerification.get(i);
		//
		// AuditData toMerge = auditDataDao.findAnswerToQuestion(this.auditID,
		// i);
		//
		// toMerge.setVerifiedAnswer(aq.getVerifiedAnswer());
		// toMerge.setComment(aq.getComment());
		//
		// if (toMerge.isVerified() != aq.isVerified()) {
		// if (aq.isVerified()) {
		// toMerge.setDateVerified(new Date());
		// toMerge.setAuditor(new User(permissions.getUserId()));
		// } else {
		// toMerge.setDateVerified(null);
		// toMerge.setAuditor(null);
		// }
		// toMerge.setVerified(aq.isVerified());
		// }
		//
		// auditDataDao.save(toMerge);
		// }
		// }

		// setVerifiedPercent();
		return SUCCESS;
	}

	private void saveOSHA(OshaAudit oldOsha, OshaAudit newOsha) {
		oldOsha.setManHours(newOsha.getManHours());
		oldOsha.setFatalities(newOsha.getFatalities());
		oldOsha.setLostWorkCases(newOsha.getLostWorkCases());
		oldOsha.setLostWorkDays(newOsha.getLostWorkDays());
		oldOsha.setInjuryIllnessCases(newOsha.getInjuryIllnessCases());
		oldOsha.setRestrictedWorkCases(newOsha.getRestrictedWorkCases());
		oldOsha.setRecordableTotal(newOsha.getRecordableTotal());
		oldOsha.setApplicable(newOsha.isApplicable());
		oldOsha.setComment(newOsha.getComment());
		oldOsha.setVerifiedDate(newOsha.getVerifiedDate());
	}

	// private void setVerifiedPercent() {
	// int verified = 0;
	//
	// for (OshaAudit oshaAudit : conAudit.getOshas()) {
	// if (oshaAudit != null && (oshaAudit.isVerified() ||
	// !oshaAudit.isApplicable()))
	// verified++;
	// }
	//
	// if (getEmr1() != null && YesNo.Yes.equals(getEmr1().getIsCorrect()))
	// verified++;
	// if (getEmr2() != null && YesNo.Yes.equals(getEmr2().getIsCorrect()))
	// verified++;
	// if (getEmr3() != null && YesNo.Yes.equals(getEmr3().getIsCorrect()))
	// verified++;
	//
	// int verifyTotal = 6;
	//
	// if (customVerification != null) {
	// for (AuditData ad : customVerification.values()) {
	// // Training and Safety Policy questions are only necessary to
	// // validate
	// // before a desktop audit. They are NOT required to be validated
	// // before
	// // Activating a PQF
	// int catID = ad.getQuestion().getSubCategory().getCategory().getId();
	// if (catID != AuditCategory.SAFETY_POLICIES && catID !=
	// AuditCategory.TRAINING) {
	// verifyTotal++;
	// if (ad.isVerified())
	// verified++;
	// }
	// }
	// }
	//
	// conAudit.setPercentVerified(Math.round((float) (100 * verified) /
	// verifyTotal));
	//
	// if (conAudit.getPercentVerified() == 100 &&
	// conAudit.getAuditStatus().equals(AuditStatus.Submitted)) {
	// conAudit.setAuditStatus(AuditStatus.Active);
	// emailContractorOnAudit();
	// }
	// // Don't un-Activate it anymore, per conversation with Trevor, Jared,
	// // John 5/16/08
	// // This can cause more problems with PQFs that are already active during
	// // the year
	// // After we convert and get all our data reverified, we may be able to
	// // turn this back on
	// // else
	// // conAudit.setAuditStatus(AuditStatus.Submitted);
	//
	// auditDao.save(conAudit);
	// }

	// private void saveAuditData(Map<Integer, AuditData> emrDB, int year) {
	// if (emrDB.get(year) == null) {
	// AuditData emrNewDB = new AuditData();
	// emrNewDB.setAudit(conAudit);
	// AuditQuestion auQuestion = new AuditQuestion();
	// auQuestion.setQuestionID(year);
	// emrNewDB.setQuestion(auQuestion);
	// emrNewDB.setNum(emr.get(year).getNum());
	// emrNewDB.setAnswer("");
	// emrDB.put(year, emrNewDB);
	// }
	// emrDB.get(year).setVerifiedAnswer(emr.get(year).getVerifiedAnswer());
	// emrDB.get(year).setComment(emr.get(year).getComment());
	// if (emrDB.get(year).getIsCorrect() != emr.get(year).getIsCorrect()) {
	// emrDB.get(year).setIsCorrect(emr.get(year).getIsCorrect());
	// if (YesNo.Yes.equals(emr.get(year).getIsCorrect()))
	// emrDB.get(year).setDateVerified(new Date());
	// else
	// emrDB.get(year).setDateVerified(null);
	// }
	// auditDataDao.save(emrDB.get(year));
	// }
	//
	// public String saveFollowUp() throws Exception {
	// this.findConAudit();
	//
	// if (followUp > 0) {
	// Calendar followUpCal = Calendar.getInstance();
	// followUpCal.add(Calendar.DAY_OF_MONTH, followUp);
	// conAudit.setScheduledDate(followUpCal.getTime());
	// auditDao.save(conAudit);
	// }
	// output = new
	// SimpleDateFormat("MM/dd").format(conAudit.getScheduledDate());
	// return SUCCESS;
	// }
	//
	// private void appendOsha(StringBuffer sb, OshaAudit osha) {
	// if (!osha.isVerified()) {
	// sb.append(osha.getConAudit().getAuditFor());
	// sb.append(" OSHA - ");
	// sb.append(osha.getComment());
	// sb.append("\n");
	// }
	// }
	//
	// public String sendEmail() throws Exception {
	// this.findConAudit();
	// loadData();
	//
	// EmailBuilder emailBuilder = new EmailBuilder();
	// emailBuilder.setTemplate(11); // PQF Verification
	// emailBuilder.setPermissions(permissions);
	// emailBuilder.setConAudit(conAudit);
	//
	// StringBuffer sb = new StringBuffer("");
	//
	// for (OshaAudit osha : conAudit.getOshas()) {
	// appendOsha(sb, osha);
	// }
	//
	// AuditData temp = getEmr1();
	// if (temp == null || !temp.isVerified()) {
	// sb.append(getYear1());
	// sb.append(" EMR - ");
	// if (temp == null)
	// sb.append("Missing EMR");
	// else
	// sb.append(temp.getComment());
	// sb.append("\n");
	// }
	//
	// temp = getEmr2();
	// if (temp == null || !temp.isVerified()) {
	// sb.append(getYear2());
	// sb.append(" EMR - ");
	// if (temp == null)
	// sb.append("Missing EMR");
	// else
	// sb.append(temp.getComment());
	// sb.append("\n");
	// }
	//
	// temp = getEmr3();
	// if (temp == null || !temp.isVerified()) {
	// sb.append(getYear3());
	// sb.append(" EMR - ");
	// if (temp == null)
	// sb.append("Missing EMR");
	// else
	// sb.append(temp.getComment());
	// sb.append("\n");
	// }
	//
	// String items = sb.toString();
	//
	// emailBuilder.addToken("missing_items", items);
	// emailBuilder.addToken("safetyManual", getSafetyManualAnswer());
	// EmailSender.send(emailBuilder.build());
	//
	// String note = "PQF Verification email sent to " +
	// emailBuilder.getSentTo();
	// ContractorBean.addNote(conAudit.getContractorAccount().getId(),
	// permissions, note);
	//
	// // message = conAudit.getContractorAccount().getNotes();
	// output = "The email was sent at and the contractor notes were stamped";
	// return SUCCESS;
	// }

	public YesNo[] getYesNos() {
		return YesNo.values();
	}

	// public String getContractorNotes() {
	// String notes = this.conAudit.getContractorAccount().getNotes();
	// notes = notes.replace("\n", "<br>");
	//
	// int position = 0;
	// for (int i = 0; i < 5; i++) {
	// position = notes.indexOf("<br>", position + 1);
	// if (position == -1)
	// return notes;
	// }
	//
	// notes = notes.substring(0, position);
	// return notes;
	// }

	public int getFollowUp() {
		return followUp;
	}

	public void setFollowUp(int followUp) {
		this.followUp = followUp;

	}

	public Map<Integer, AuditData> getPqfQuestions() {
		return pqfQuestions;
	}

	public void setPqfQuestions(Map<Integer, AuditData> pqfQuestions) {
		this.pqfQuestions = pqfQuestions;
	}

	public List<OshaAudit> getOshas() {
		return oshas;
	}

	public void setOshas(List<OshaAudit> oshas) {
		this.oshas = oshas;
	}

	public List<AuditData> getEmrQuestions() {
		return emrQuestions;
	}

	public void setEmrQuestions(List<AuditData> emrQuestions) {
		this.emrQuestions = emrQuestions;
	}
}
