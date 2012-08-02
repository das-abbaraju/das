package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class IGVerification extends ContractorActionSupport {
	@Autowired
	protected ContractorAuditOperatorDAO caoDAO;
	@Autowired
	protected EmailTemplateDAO templateDAO;
	@Autowired
	private EmailSender emailSender;

	protected String body;
	protected String subject;
	protected EmailQueue previewEmail;

	protected List<ContractorAuditOperatorWorkflow> caowList;

	@Override
	public String execute() {
		this.subHeading = "Insurance Verification";
		return SUCCESS;
	}

	@Before
	@RequiredPermission(value = OpPerms.AuditVerification)
	public void start() throws Exception {
		if (contractor != null) {
			id = contractor.getId();
		}

		EmailBuilder emailBuilder = new EmailBuilder();
		// Insurance Policies rejected by PICS
		EmailTemplate template = templateDAO.find(132);

		List<ContractorAuditOperator> caos = caoDAO.findByCaoStatus(1000, permissions,
				"cao.status = 'Incomplete' AND cao.audit.auditType.classType = 'Policy' AND ca.expiresDate > NOW()"
						+ "AND cao.audit.contractorAccount.id = " + contractor.getId(), "cao.audit.contractorAccount");

		caowList = new ArrayList<ContractorAuditOperatorWorkflow>();

		Set<OperatorAccount> operators = new TreeSet<OperatorAccount>();
		Set<AuditType> auditTypes = new TreeSet<AuditType>();
		for (ContractorAuditOperator cao : caos) {
			operators.add(cao.getOperator());
			auditTypes.add(cao.getAudit().getAuditType());
		}

		DoubleMap<AuditType, OperatorAccount, ContractorAuditOperatorWorkflow> recent = new DoubleMap<AuditType, OperatorAccount, ContractorAuditOperatorWorkflow>();
		for (ContractorAuditOperator cao : caos) {
			AuditType auditType = cao.getAudit().getAuditType();
			OperatorAccount operator = cao.getOperator();

			for (ContractorAuditOperatorWorkflow caow : cao.getCaoWorkflow()) {
				if (recent.get(auditType, operator) == null) {
					recent.put(auditType, operator, caow);
				} else {
					if (recent.get(auditType, operator).getCreationDate().before(caow.getCreationDate())) {
						recent.put(auditType, operator, caow);
					}
				}
			}
		}

		for (AuditType auditType : auditTypes) {
			for (OperatorAccount operator : operators) {
				caowList.add(recent.get(auditType, operator));
			}
		}
		
		emailBuilder.setTemplate(template);
		emailBuilder.addToken("caowList", caowList);
		emailBuilder.setPermissions(permissions);
		emailBuilder.setFromAddress("\"" + permissions.getName() + "\"<" + permissions.getEmail() + ">");
		emailBuilder.setContractor(contractor, OpPerms.ContractorInsurance);
		previewEmail = emailBuilder.build();
	}
	
	@RequiredPermission(value = OpPerms.AuditVerification)
	public String sendEmail() throws Exception {
		previewEmail.setBody(body);
		previewEmail.setSubject(subject);
		previewEmail.setMediumPriority();

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

	public List<ContractorAuditOperatorWorkflow> getCaowList() {
		return caowList;
	}
}
