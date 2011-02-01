package com.picsauditing.actions.audits;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;

@SuppressWarnings("serial")
public class IGVerification extends ContractorActionSupport {
	protected ContractorAuditOperatorDAO caoDAO;
	protected EmailTemplateDAO templateDAO;

	protected EmailTemplate template;
	protected EmailQueue previewEmail;

	protected List<ContractorAuditOperator> caoList;

	public IGVerification(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorAuditOperatorDAO caoDAO, EmailTemplateDAO templateDAO) {
		super(accountDao, auditDao);
		this.caoDAO = caoDAO;
		this.templateDAO = templateDAO;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.AuditVerification);
		this.findContractor();
		this.subHeading = "Insurance Verification";

		String subject = null;
		String body = null;
		if (previewEmail != null) {
			subject = previewEmail.getSubject();
			body = previewEmail.getBody();
		}

		buildPreviewEmail();

		if ("Send Email".equals(button)) {
			previewEmail.setBody(body);
			previewEmail.setSubject(subject);
			previewEmail.setPriority(50);

			try {
				EmailSender.send(previewEmail);
				addActionMessage("Successfully sent email to " + previewEmail.getToAddresses());
			} catch (Exception e) {
				addActionError("Could not send email to " + previewEmail.getToAddresses());
			}
		}

		return SUCCESS;
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
		EmailBuilder emailBuilder = new EmailBuilder();
		template = templateDAO.find(132); // Insurance Policies rejected by PICS
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
