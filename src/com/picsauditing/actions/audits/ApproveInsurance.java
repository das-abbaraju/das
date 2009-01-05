package com.picsauditing.actions.audits;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.PICS.ContractorBean;
import com.picsauditing.PICS.Grepper;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.AnswerMap;

public class ApproveInsurance extends ContractorActionSupport {
	protected static AuditDataDAO auditDataDAO;
	protected List<ContractorAudit> verificationAudits = null;
	protected String emailBody;
	protected String emailSubject;
	protected EmailQueue previewEmail;

	public ApproveInsurance(ContractorAccountDAO accountDao, ContractorAuditDAO contractorAuditDAO,
			AuditDataDAO auditDataDAO) {
		super(accountDao, contractorAuditDAO);
		this.auditDataDAO = auditDataDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.InsuranceApproval, OpType.Edit);
		this.findContractor();
		this.subHeading = "Approve Insurance";

		return SUCCESS;
	}

	public String addMissingItemsToEmail() {
		StringBuffer sb = new StringBuffer("");

		// public void sendEmail(List<CertificateDO> list, Permissions
		// permissions) throws Exception {
		// for (CertificateDO cdo : list) {
		//
		// AccountBean aBean = new AccountBean();
		// String conID = cdo.getContractor_id();
		// aBean.setFromDB(conID);
		// String contactName = aBean.contact;
		// String contractor = aBean.name;
		//
		// AccountBean oAccountBean = new AccountBean();
		// oAccountBean.setFromDB(cdo.getOperator_id());
		// String operator = oAccountBean.name;
		//
		// String message = "Hello " + contactName + ",\n\n" + contractor + "'s
		// " + cdo.getType()
		// + " Insurance Certificate has been " + cdo.getStatus() + " by " +
		// operator;
		// if (!Strings.isEmpty(cdo.getReason()))
		// message += " for the following reasons:\n\n" + cdo.getReason() +
		// "\n\n";
		//
		// if (cdo.getStatus().equals("Rejected")) {
		// UserDAO userDAO = (UserDAO) SpringUtils.getBean("UserDAO");
		// User user = userDAO.find(permissions.getUserId());
		// message += "Please correct these issues and re-upload your insurance
		// certificate to your "
		// + "PICS account.\n" + "If you have any specific questions about " +
		// operator
		// + "'s insurance requirements, " + "please contact " +
		// permissions.getName() + " at "
		// + user.getEmail() + ".";
		// } else {
		// message += "Please make sure that you keep up-to-date in PICS by
		// uploading your "
		// + "insurance certificate when you renew your policy.";
		// }
		// message += "\n\nHave a great day,\n" + "PICS Customer Service";
		//
		// EmailSender.send(aBean.email, operator + " insurance certificate " +
		// cdo.getStatus(), message);
		//
		// String newNote = cdo.getType() + " insurance certificate " +
		// cdo.getStatus() + " by " + operator
		// + " for reason: " + cdo.getReason();
		// Note note = new Note(cdo.getOperator_id(), cdo.getContractor_id(),
		// permissions.getUserIdString(),
		// permissions.getName(), newNote);
		// note.writeToDB();
		// }
		// }

		for (ContractorAudit conAudit : getVerificationAudits()) {
			if (conAudit.getAuditType().isPqf()) {
				List<AuditData> temp = auditDataDAO.findCustomPQFVerifications(conAudit.getId());
				for (AuditData ad : temp) {
					if (!ad.isVerified()) {
						sb.append(ad.getQuestion().getSubCategory().getCategory().getNumber() + "."
								+ ad.getQuestion().getSubCategory().getNumber() + "." + ad.getQuestion().getNumber());
						sb.append(":" + ad.getQuestion().getSubCategory().getSubCategory() + "/"
								+ ad.getQuestion().getColumnHeaderOrQuestion());
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
		emailBuilder.setPermissions(permissions);
		emailBuilder.setContractor(contractor);

		emailBuilder.addToken("missing_items", addMissingItemsToEmail());
		previewEmail = emailBuilder.build();

		return SUCCESS;
	}

	public String sendEmail() throws Exception {
		this.findContractor();

		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setPermissions(permissions);
		emailBuilder.setContractor(contractor);
		if (emailBody == null && emailSubject == null) {
			emailBuilder.setTemplate(11);
			emailBuilder.addToken("missing_items", addMissingItemsToEmail());
		} else {
			EmailTemplate emailTemplate = new EmailTemplate();
			emailTemplate.setId(11);
			emailTemplate.setBody(emailBody);
			emailTemplate.setSubject(emailSubject);
			emailBuilder.setTemplate(emailTemplate);
		}
		EmailSender.send(emailBuilder.build());
		String note = "PQF Verification email sent to " + emailBuilder.getSentTo();
		ContractorBean.addNote(contractor.getId(), permissions, note);

		output = "The email was sent at and the contractor notes were stamped";
		return SUCCESS;
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
					return t.getAuditStatus().isActiveSubmitted()
							&& (t.getAuditType().getClassType().equals(AuditTypeClass.Policy));
				}
			}.grep(getActiveAudits());
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

	public static Map<Integer, AuditData> getQuestionAnswer(int auditID) {
		Map<Integer, AuditData> insuranceQuestions = new LinkedHashMap<Integer, AuditData>();
		AnswerMap temp = auditDataDAO.findAnswers(auditID);
		for (Integer ad : temp) {
			for (AuditData auditData : temp.getAnswers(ad)) {
				insuranceQuestions.put(ad, auditData);
			}
		}
		return insuranceQuestions;
	}
}
