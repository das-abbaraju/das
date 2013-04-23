package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Grepper;
import com.picsauditing.PICS.OshaOrganizer;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class VerifyView extends ContractorActionSupport {
	private static final String YES = "Yes";
	@Autowired
	protected AuditDataDAO auditDataDAO;
	@Autowired
	protected NoteDAO noteDAO;
	@Autowired
	private EmailSender emailSender;

	private Map<Integer, AuditData> pqfQuestions = new LinkedHashMap<Integer, AuditData>();
	private Map<Integer, AuditData> infoSection = new LinkedHashMap<Integer, AuditData>();
	private List<OshaAudit> oshasUS = new ArrayList<OshaAudit>();
	private List<OshaAudit> oshasCA = new ArrayList<OshaAudit>();
	protected List<ContractorAudit> annualUpdates = new ArrayList<ContractorAudit>();
	protected Map<AuditQuestion, Map<Integer, AuditData>> emrs = new TreeMap<AuditQuestion, Map<Integer, AuditData>>();
	protected List<ContractorAudit> verificationAudits = null;
	protected String emailBody;
	protected String emailSubject;
	protected EmailQueue previewEmail;
	protected OshaOrganizer oshaOrganizer;

	public VerifyView() {
		noteCategory = NoteCategory.Audits;
		subHeading = getText("VerifyView.title");
	}

	@RequiredPermission(value = OpPerms.AuditVerification)
	public String execute() throws Exception {
		findContractor();
		oshaOrganizer = contractor.getOshaOrganizer();
		fillAuditData();
		
		return SUCCESS;
	}

	private void fillAuditData() {
		boolean needsOsha = false;
		boolean needsEmr = false;

		for (ContractorAudit conAudit : getVerificationAudits()) {
			if (conAudit.getAuditType().isPicsPqf() && !conAudit.hasCaoStatus(AuditStatus.Incomplete)) {
				pqfQuestions = getPQFAnswerMap(conAudit);
			}
			if (conAudit.getAuditType().isAnnualAddendum()) {
				
				AuditData us = auditDataDAO.findAnswerToQuestion(conAudit.getId(), 2064);
				if (us != null && YES.equals(us.getAnswer())) {
					OshaAudit oshaAudit = new OshaAudit(conAudit);
					if (!oshaAudit.isEmpty(OshaType.OSHA)) {
						oshasUS.add(new OshaAudit(conAudit));
					
						if (!needsOsha) {
							needsOsha = conAudit.hasCaoStatus(AuditStatus.Submitted)
							|| conAudit.hasCaoStatus(AuditStatus.Resubmitted);
						}
					}
				}

				annualUpdates.add(conAudit);

				for (AuditData d : conAudit.getData()) {
					if (d.getAudit().isCategoryApplicable(d.getQuestion().getCategory().getId())) {
						int categoryID = d.getQuestion().getCategory().getId();
						if ((categoryID != AuditCategory.CITATIONS &&
							!OshaAudit.isSafetyStatisticsCategory(categoryID))
								|| (categoryID == AuditCategory.CITATIONS && (d.getQuestion().isRequired()) || (d
										.getQuestion().getId() >= 3565
										&& d.getQuestion().getId() <= 3568 && d.isAnswered()))) {
							if (!needsEmr)
								needsEmr = conAudit.hasCaoStatus(AuditStatus.Submitted)
										|| conAudit.hasCaoStatus(AuditStatus.Resubmitted);

							Map<Integer, AuditData> inner = emrs.get(d.getQuestion());

							if (inner == null) {
								inner = new TreeMap<Integer, AuditData>();
								for (ContractorAudit ca : annualUpdates)
									inner.put(ca.getId(), null);
								emrs.put(d.getQuestion(), inner);
							}

							inner.put(conAudit.getId(), d);
						}
					}
				}
			}
		}

		Collections.sort(annualUpdates, new Comparator<ContractorAudit>() {
			public int compare(ContractorAudit o1, ContractorAudit o2) {
				if (o1.getAuditFor().equals(o2.getAuditFor()))
					return o1.getCreationDate().compareTo(o2.getCreationDate());

				return o1.getAuditFor().compareTo(o2.getAuditFor());
			}
		});

		infoSection = auditDataDAO.findAnswersByContractor(contractor.getId(), Arrays.asList(69, 71, 1616, 
				57, 103, 104, 123, 124, 125));
	}

	private Map<Integer, AuditData> getPQFAnswerMap(ContractorAudit conAudit) {
		LinkedHashMap<Integer, AuditData> answerMap = new LinkedHashMap<Integer, AuditData>();

		List<AuditData> answers = auditDataDAO.findCustomPQFVerifications(conAudit.getId());
		for (AuditData answer : answers) {
			if (answer.getAudit().isCategoryApplicable(answer.getQuestion().getCategory().getId()) && answer.getQuestion().isVisibleInAudit(answer.getAudit())) {
				answerMap.put(answer.getQuestion().getId(), answer);
			}
		}

		return answerMap;
	}

	public String addMissingItemsToEmail() {
		StringBuffer sb = new StringBuffer("");

		for (ContractorAudit conAudit : getVerificationAudits()) {
			if (conAudit.getAuditType().isAnnualAddendum()
					&& (conAudit.hasCaoStatus(AuditStatus.Submitted) || conAudit.hasCaoStatus(AuditStatus.Resubmitted) || conAudit
							.hasCaoStatus(AuditStatus.Incomplete))) {
				StringBuffer sb2 = new StringBuffer("");
				
				for (AuditData auditData : conAudit.getData()) {
					if (isCommentNeeded(auditData)) {
						sb2.append(auditData.getQuestion().getColumnHeaderOrQuestion());
						sb2.append(" : " + auditData.getComment());
						sb2.append("\n");
					}
				}

				if (sb2.length() > 0) {
					sb.append("\n\n");
					sb.append(conAudit.getAuditFor() + " Annual Update");
					sb.append("\n");
					sb.append("-------------------------------");
					sb.append("\n");
					sb.append(sb2.toString());
				}
			}
			if (conAudit.getAuditType().isPicsPqf()) {
				List<AuditData> temp = auditDataDAO.findCustomPQFVerifications(conAudit.getId());
				for (AuditData ad : temp) {
					if (!ad.isVerified() && !Strings.isEmpty(ad.getComment())) {
						sb.append(ad.getQuestion().getCategory().getNumber() + "."
								+ ad.getQuestion().getCategory().getNumber() + "." + ad.getQuestion().getNumber());
						for (AuditCategory ac : ad.getQuestion().getCategory().getSubCategories()) {
							if (ad.getQuestion().getCategory().getId() == ac.getParent().getId()) {
								sb.append(":" + ad.getQuestion().getCategory() + "/"
										+ ad.getQuestion().getColumnHeaderOrQuestion());
							}

						}
						sb.append("\n");
						sb.append("Comment : " + ad.getComment());
						sb.append("\n");
					}
				}
			}
		}
		return sb.toString();
	}

	private boolean isCommentNeeded(AuditData auditData) {
		if (auditData.getQuestion().getId() == 2447)
			return false;
		if (auditData.getQuestion().getId() == 2448)
			return false;
		
		int categoryID = auditData.getQuestion().getCategory().getId();
		
		if (categoryID == AuditCategory.CITATIONS && !auditData.getQuestion().isRequired())
			return false;
		
		if (auditData.isVerified())
			return false;
		
		if (Strings.isEmpty(auditData.getComment()))
			return false;

		return true;
	}

	public String previewEmail() throws Exception {
		findContractor();
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(11); // PQF Verification
		emailBuilder
				.setFromAddress(contractor.getCurrentCsr().getName() + " <" + contractor.getCurrentCsr().getEmail() + ">");
		emailBuilder.setPermissions(permissions);
		emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
		emailBuilder.addToken("missing_items", addMissingItemsToEmail());
		previewEmail = emailBuilder.build();

		return SUCCESS;
	}

	public String sendEmail() throws Exception {
		findContractor();

		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setPermissions(permissions);
		emailBuilder.setContractor(contractor, OpPerms.ContractorSafety);
		if (previewEmail == null || Strings.isEmpty(previewEmail.getBody())
				|| Strings.isEmpty(previewEmail.getSubject())) {
			emailBuilder.setTemplate(11);
			emailBuilder.addToken("missing_items", addMissingItemsToEmail());
			emailBuilder.setFromAddress("\"" + contractor.getCurrentCsr().getName() + "\"<"
					+ contractor.getCurrentCsr().getEmail() + ">");
		} else {
			EmailTemplate emailTemplate = new EmailTemplate();
			emailTemplate.setId(11);
			emailTemplate.setBody(previewEmail.getBody());
			emailTemplate.setSubject(previewEmail.getSubject());
			emailBuilder.setEdited(true);
			emailBuilder.setFromAddress("\"" + contractor.getCurrentCsr().getName() + "\"<"
					+ contractor.getCurrentCsr().getEmail() + ">");
			emailBuilder.setTemplate(emailTemplate);
		}
		EmailQueue email = emailBuilder.build();
		email.setViewableById(Account.EVERYONE);
		emailSender.send(email);
		String note = "PQF Verification email sent to " + emailBuilder.getSentTo();
		addNote(contractor, note, NoteCategory.Audits);

		fillAuditData();
		
		addActionMessage("The email was sent and the contractor notes were stamped");
		return SUCCESS;
	}

	public Map<Integer, AuditData> getPqfQuestions() {
		return pqfQuestions;
	}

	public void setPqfQuestions(Map<Integer, AuditData> pqfQuestions) {
		this.pqfQuestions = pqfQuestions;
	}

	public List<OshaAudit> getOshasUS() {
		return oshasUS;
	}

	public void setOshasUS(List<OshaAudit> oshasUS) {
		this.oshasUS = oshasUS;
	}

	public List<OshaAudit> getOshasCA() {
		return oshasCA;
	}

	public void setOshasCA(List<OshaAudit> oshasCA) {
		this.oshasCA = oshasCA;
	}

	public List<ContractorAudit> getAnnualUpdates() {
		return annualUpdates;
	}

	public void setAnnualUpdates(List<ContractorAudit> annualUpdates) {
		this.annualUpdates = annualUpdates;
	}

	public Map<AuditQuestion, Map<Integer, AuditData>> getEmrs() {
		return emrs;
	}

	public void setEmrs(Map<AuditQuestion, Map<Integer, AuditData>> emrs) {
		this.emrs = emrs;
	}

	public Map<Integer, AuditData> getInfoSection() {
		return infoSection;
	}

	public void setInfoSection(Map<Integer, AuditData> infoSection) {
		this.infoSection = infoSection;
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
					boolean foundSomethingToBeVerified = false;
					if (t.getAuditType().isPicsPqf()) {
						for (ContractorAuditOperator cao : t.getOperatorsVisible()) {
							if (cao.getStatus().isSubmittedResubmitted()) {
								foundSomethingToBeVerified = true;
								break;
							}
						}
					} else if (t.getAuditType().isAnnualAddendum())
						foundSomethingToBeVerified = true;
					return foundSomethingToBeVerified;
				}
			}.grep(getActiveAudits());

			if (verificationAudits.size() > 0) {
				Collections.sort(verificationAudits, new Comparator<ContractorAudit>() {
					public int compare(ContractorAudit o1, ContractorAudit o2) {
						if (o1.getAuditFor() == null)
							return -1;
						if (o2.getAuditFor() == null)
							return 1;
						return o1.getAuditFor().compareTo(o2.getAuditFor());
					}
				});
			}
		}

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

	public boolean isHasVerifiableAudits() {
		// check if PQFQuestions/OSHA/EMR data is empty
		boolean hasVerifiable = false;
		boolean hasUpload = false;
		boolean hasSubmittedStatus = false;
		for (ContractorAudit audit : getVerificationAudits()) {
			if (audit.getAuditType().isPicsPqf()) {
				if (audit.hasCaoStatus(AuditStatus.Submitted)
						|| audit.hasCaoStatus(AuditStatus.Resubmitted)) {
					hasSubmittedStatus = true;
					break;
				}
				for (AuditData data : audit.getData()) {
					if (data.getQuestion().getId() == AuditQuestion.MANUAL_PQF
							&& data.getQuestion().isVisibleInAudit(audit))
						hasUpload = true;
					break;
				}
				break;
			}
		}

		hasVerifiable = hasSubmittedStatus
				|| hasUpload
				|| !(pqfQuestions.isEmpty() && oshasUS.isEmpty() && emrs
						.isEmpty());
		return hasVerifiable;
	}
}
