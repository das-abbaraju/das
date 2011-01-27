package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.picsauditing.PICS.Grepper;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
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

	private Map<Integer, AuditData> pqfQuestions = new LinkedHashMap<Integer, AuditData>();
	private Map<Integer, AuditData> infoSection = new LinkedHashMap<Integer, AuditData>();
	private List<OshaAudit> oshas = new ArrayList<OshaAudit>();
	protected AuditDataDAO auditDataDAO;
	protected List<ContractorAudit> annualUpdates = new ArrayList<ContractorAudit>();
	protected Map<AuditQuestion, Map<Integer, AuditData>> emrs = new TreeMap<AuditQuestion, Map<Integer, AuditData>>();
	protected List<ContractorAudit> verificationAudits = null;
	protected String emailBody;
	protected String emailSubject;
	protected EmailQueue previewEmail;
	protected NoteDAO noteDAO;

	public VerifyView(ContractorAccountDAO accountDao, ContractorAuditDAO contractorAuditDAO,
			AuditDataDAO auditDataDAO, NoteDAO noteDAO) {
		super(accountDao, contractorAuditDAO);
		this.auditDataDAO = auditDataDAO;
		this.noteDAO = noteDAO;
		noteCategory = NoteCategory.Audits;
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
				AuditData auditData = auditDataDAO.findAnswerToQuestion(conAudit.getId(), 2064);
				for (OshaAudit oshaAudit : conAudit.getOshas()) {
					if (auditData != null && "Yes".equals(auditData.getAnswer()) && oshaAudit.isCorporate()
							&& oshaAudit.getType().equals(OshaType.OSHA)) {
						oshas.add(oshaAudit);
					}
				}

				annualUpdates.add(conAudit);

				boolean pendingIncomplete = false;

				for (ContractorAuditOperator cao : conAudit.getOperators()) {
					pendingIncomplete = cao.getStatus().isIncomplete() || cao.getStatus().isPending();
				}

				if (!pendingIncomplete) {
					for (AuditData d : conAudit.getData()) {
						int categoryID = d.getQuestion().getCategory().getId();
						if (categoryID != AuditCategory.CITATIONS
								|| (categoryID == AuditCategory.CITATIONS && (d.getQuestion().isRequired()) || (d
										.getQuestion().getId() >= 3565 && d.getQuestion().getId() <= 3568 && d
										.isAnswered()))) {
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
			@Override
			public int compare(ContractorAudit o1, ContractorAudit o2) {
				if (o1.getAuditFor().equals(o2.getAuditFor()))
					return o1.getCreationDate().compareTo(o2.getCreationDate());
				
				return o1.getAuditFor().compareTo(o2.getAuditFor());
			}
		});

		infoSection = auditDataDAO.findAnswersByContractor(contractor.getId(),
				Arrays.<Integer> asList(69, 71, 1616, 57, 103, 104, 123, 124, 125));
		return SUCCESS;
	}

	public String addMissingItemsToEmail() {
		StringBuffer sb = new StringBuffer("");

		for (ContractorAudit conAudit : getVerificationAudits()) {
			if (conAudit.getAuditType().isAnnualAddendum()) {
				StringBuffer sb2 = new StringBuffer("");
				for (OshaAudit oshaAudit : conAudit.getOshas()) {
					if (oshaAudit.getType().equals(OshaType.OSHA) && oshaAudit.isCorporate() && !oshaAudit.isVerified()
							&& !Strings.isEmpty(oshaAudit.getComment())) {
						sb2.append("OSHA : ");
						sb2.append(oshaAudit.getComment());
						sb2.append("\n");
					}
				}
				for (AuditData auditData : conAudit.getData()) {
					if (auditData.getQuestion().getId() != 2447 && auditData.getQuestion().getId() != 2448) {
						int categoryID = auditData.getQuestion().getCategory().getId();
						if (categoryID != AuditCategory.CITATIONS
								|| (categoryID == AuditCategory.CITATIONS && auditData.getQuestion().isRequired())) {
							if (!auditData.isVerified() && !Strings.isEmpty(auditData.getComment())) {
								sb2.append(auditData.getQuestion().getColumnHeaderOrQuestion());
								sb2.append(" : " + auditData.getComment());
								sb2.append("\n");
							}
						}
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
			if (conAudit.getAuditType().isPqf()) {
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

	public String previewEmail() throws Exception {
		this.findContractor();
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(11); // PQF Verification
		emailBuilder
				.setFromAddress(contractor.getAuditor().getName() + " <" + contractor.getAuditor().getEmail() + ">");
		emailBuilder.setPermissions(permissions);
		emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
		emailBuilder.addToken("missing_items", addMissingItemsToEmail());
		previewEmail = emailBuilder.build();

		return SUCCESS;
	}

	public String sendEmail() throws Exception {
		this.findContractor();

		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setPermissions(permissions);
		emailBuilder.setContractor(contractor, OpPerms.ContractorSafety);
		if (emailBody == null && emailSubject == null) {
			emailBuilder.setTemplate(11);
			emailBuilder.addToken("missing_items", addMissingItemsToEmail());
			emailBuilder.setFromAddress("\"" + contractor.getAuditor().getName() + "\"<"
					+ contractor.getAuditor().getEmail() + ">");
		} else {
			EmailTemplate emailTemplate = new EmailTemplate();
			emailTemplate.setId(11);
			emailTemplate.setBody(emailBody);
			emailTemplate.setSubject(emailSubject);
			emailBuilder.setFromAddress("\"" + contractor.getAuditor().getName() + "\"<"
					+ contractor.getAuditor().getEmail() + ">");
			emailBuilder.setTemplate(emailTemplate);
		}
		EmailQueue email = emailBuilder.build();
		email.setViewableById(Account.EVERYONE);
		EmailSender.send(email);
		String note = "PQF Verification email sent to " + emailBuilder.getSentTo();
		addNote(contractor, note, NoteCategory.Audits);

		output = "The email was sent and the contractor notes were stamped";
		return SUCCESS;
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
					if (t.getAuditType().getClassType().isPolicy() || t.isExpired()
							|| !t.getAuditType().getWorkFlow().isHasSubmittedStep()
							|| !(t.getAuditType().isPqf() || t.getAuditType().isAnnualAddendum()))
						return false;

					return t.hasCaoStatusBefore(AuditStatus.Complete) || t.getAuditType().isAnnualAddendum();
				}
			}.grep(getActiveAudits());
		}

		if (verificationAudits.size() > 0) {
			// Check if all the annual updates are valid
			boolean needsVerification = false;
			for (ContractorAudit audit : verificationAudits) {
				if (audit.getAuditType().isAnnualAddendum()
						&& (audit.hasCaoStatusAfter(AuditStatus.Pending) || audit
								.hasCaoStatusBefore(AuditStatus.Complete))) {
					needsVerification = true;
				}
			}

			if (!needsVerification) {
				Iterator<ContractorAudit> iterator = verificationAudits.iterator();
				while (iterator.hasNext()) {
					if (iterator.next().getAuditType().isAnnualAddendum())
						iterator.remove();
				}
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
}
