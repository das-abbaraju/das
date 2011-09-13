package com.picsauditing.actions.audits;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSenderSpring;

@SuppressWarnings("serial")
public class IGVerification extends ContractorActionSupport {
	@Autowired
	protected ContractorAuditOperatorDAO caoDAO;
	@Autowired
	protected EmailTemplateDAO templateDAO;
	@Autowired
	private EmailSenderSpring emailSender;

	protected String body;
	protected String subject;
	protected EmailQueue previewEmail;
	protected List<ContractorAuditOperator> caoList;

	@Override
	@RequiredPermission(value = OpPerms.AuditVerification)
	public String execute() throws Exception {
		this.subHeading = "Insurance Verification";
		buildPreviewEmail();

		return SUCCESS;
	}

	public String sendEmail() throws Exception {
		buildPreviewEmail();

		previewEmail.setBody(body);
		previewEmail.setSubject(subject);
		previewEmail.setPriority(50);

		try {
			emailSender.send(previewEmail);
			addActionMessage("Successfully sent email to " + previewEmail.getToAddresses() + " and stamped notes");
			String note = "Insurance Verification email sent to " + previewEmail.getToAddresses();
			addNote(contractor, note, NoteCategory.Insurance);
		} catch (Exception e) {
			addActionError("Could not send email to " + previewEmail.getToAddresses());
		}

		return SUCCESS;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public EmailQueue getPreviewEmail() {
		return previewEmail;
	}

	public void setPreviewEmail(EmailQueue previewEmail) {
		this.previewEmail = previewEmail;
	}

	public List<ContractorAuditOperator> getCaoList() {
		return caoList;
	}

	public void buildPreviewEmail() throws Exception {
		if (contractor == null)
			findContractor();

		EmailBuilder emailBuilder = new EmailBuilder();
		EmailTemplate template = templateDAO.find(132); // Insurance Policies rejected by PICS
		caoList = caoDAO.findByCaoStatus(1000, permissions, "cao.status = 'Incomplete' "
				+ "AND cao.audit.auditType.classType = 'Policy' " + "AND cao.audit.contractorAccount.id = "
				+ contractor.getId(), "cao.audit.contractorAccount");

		emailBuilder.setTemplate(template);
		emailBuilder.addToken("caoList", caoList);
		emailBuilder.setPermissions(permissions);
		emailBuilder.setFromAddress("\"" + permissions.getName() + "\"<" + permissions.getEmail() + ">");
		emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
		previewEmail = emailBuilder.build();
	}
}
