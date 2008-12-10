package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.picsauditing.PICS.ContractorBean;
import com.picsauditing.PICS.Grepper;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;

public class VerifyView extends ContractorActionSupport {
	private Map<Integer, AuditData> pqfQuestions = new LinkedHashMap<Integer, AuditData>();
	private Map<Integer, AuditData> infoSection = new LinkedHashMap<Integer, AuditData>();
	private List<OshaAudit> oshas = new ArrayList<OshaAudit>();
	protected AuditDataDAO auditDataDAO;
	protected SortedSet<String> years = new TreeSet<String>();
	protected Map<AuditQuestion, Map<String, AuditData>> emrs = new TreeMap<AuditQuestion, Map<String, AuditData>>();
	protected List<ContractorAudit> verificationAudits = null;
	protected String emailBody;
	protected String emailSubject;
	protected EmailQueue previewEmail;

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

		for (ContractorAudit conAudit : getVerificationAudits()) {
			if (conAudit.getAuditType().isPqf()) {
				List<AuditData> temp = auditDataDAO.findCustomPQFVerifications(conAudit.getId());
				pqfQuestions = new LinkedHashMap<Integer, AuditData>();
				for (AuditData ad : temp) {
					pqfQuestions.put(ad.getQuestion().getId(), ad);
				}
			}
			if (conAudit.getAuditType().isAnnualAddendum()) {
				for (OshaAudit oshaAudit : conAudit.getOshas()) {
					if (oshaAudit.isCorporate()) {
						oshas.add(oshaAudit);
					}
				}
				years.add(conAudit.getAuditFor());
			}
		}
		for (ContractorAudit conAudit : getVerificationAudits()) {
			if (conAudit.getAuditType().isAnnualAddendum()) {
				for (AuditData auditData : conAudit.getData()) {
					Map<String, AuditData> inner = emrs.get(auditData.getQuestion());

					if (inner == null) {
						inner = new TreeMap<String, AuditData>();
						for (String year : years)
							inner.put(year, null);
						emrs.put(auditData.getQuestion(), inner);
					}
					inner.put(conAudit.getAuditFor(), auditData);
				}
			}
		}

		Map<Integer, Map<String, AuditData>> tempAnswers = auditDataDAO.findAnswersByContractor(contractor.getId(),
				Arrays.<Integer> asList(69, 1616, 55, 57, 103, 104, 123, 124, 125));
		infoSection = new HashMap<Integer, AuditData>();
		for (Integer questionID : tempAnswers.keySet())
			// Get the PQF data out
			infoSection.put(questionID, tempAnswers.get(questionID).get(""));
		return SUCCESS;
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

	public String previewEmail() throws Exception {
		this.findContractor();

		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(11); // PQF Verification
		emailBuilder.setPermissions(permissions);
		emailBuilder.setContractor(contractor);

		StringBuffer sb = new StringBuffer("");

		for (ContractorAudit conAudit : getVerificationAudits()) {
			if (conAudit.getAuditType().isAnnualAddendum()) {
				for (OshaAudit oshaAudit : conAudit.getOshas()) {
					if (oshaAudit.isCorporate() && !oshaAudit.isVerified()) {
						sb.append(oshaAudit.getConAudit().getAuditFor());
						sb.append(" OSHA - ");
						sb.append(oshaAudit.getComment());
						sb.append("\n");
					}
				}
				for (AuditData auditData : conAudit.getData()) {
					if (!auditData.isVerified()) {
						sb.append(conAudit.getAuditFor());
						sb.append(auditData.getQuestion().getColumnHeaderOrQuestion());
						sb.append(" " + auditData.getComment());
						sb.append("\n");
					}
				}
			}
			if (conAudit.getAuditType().isPqf()) {
				List<AuditData> temp = auditDataDAO.findCustomPQFVerifications(conAudit.getId());
				for (AuditData ad : temp) {
					if (!ad.isVerified()) {
						sb.append(ad.getQuestion().getSubCategory().getSubCategory() + "/"
								+ ad.getQuestion().getQuestion());
						sb.append(" " + ad.getComment());
						sb.append("\n");
					}
				}
			}
		}

		String items = sb.toString();

		emailBuilder.addToken("missing_items", items);
		previewEmail = emailBuilder.build();

		return SUCCESS;
	}

	public String sendEmail() throws Exception {
		this.findContractor();

		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setPermissions(permissions);
		emailBuilder.setContractor(contractor);
		EmailTemplate emailTemplate = new EmailTemplate();
		emailTemplate.setId(11);
		emailTemplate.setBody(emailBody);
		emailTemplate.setSubject(emailSubject);
		emailBuilder.setTemplate(emailTemplate);
		EmailSender.send(emailBuilder.build());
		String note = "PQF Verification email sent to " + emailBuilder.getSentTo();
		ContractorBean.addNote(contractor.getId(), permissions, note);

		output = "The email was sent at and the contractor notes were stamped";
		return SUCCESS;
	}

	public String getContractorNotes() {
		String notes = contractor.getNotes();
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

	public Map<AuditQuestion, Map<String, AuditData>> getEmrs() {
		return emrs;
	}

	public void setEmrs(Map<AuditQuestion, Map<String, AuditData>> emrs) {
		this.emrs = emrs;
	}

	public Map<Integer, AuditData> getInfoSection() {
		return infoSection;
	}

	public void setInfoSection(Map<Integer, AuditData> infoSection) {
		this.infoSection = infoSection;
	}

	public SortedSet<String> getYears() {
		return years;
	}

	public void setYears(SortedSet<String> years) {
		this.years = years;
	}

	/**
	 * Returns a list of pending and submitted AnnualAddendum and Pqf audits.
	 * 
	 * @return
	 */
	public List<ContractorAudit> getVerificationAudits() {
		if (verificationAudits == null) {
			verificationAudits = new Grepper<ContractorAudit>() {
				@Override
				public boolean check(ContractorAudit t) {
					return t.getAuditStatus().isPendingSubmitted()
							&& (t.getAuditType().isAnnualAddendum() || t.getAuditType().isPqf());
				}
			}.grep(getActiveAudits());
		}
		Collections.sort(verificationAudits, new Comparator<ContractorAudit>() {
			@Override
			public int compare(ContractorAudit o1, ContractorAudit o2) {
				if (o1.getAuditFor() == null)
					return -1;
				if (o2.getAuditFor() == null)
					return 1;
				return o1.getAuditFor().compareTo(o2.getAuditFor());
			}
		});
		return verificationAudits;
	}

	public String getEmailBody() {
		return emailBody;
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public EmailQueue getPreviewEmail() {
		return previewEmail;
	}

	public void setPreviewEmail(EmailQueue previewEmail) {
		this.previewEmail = previewEmail;
	}

}
