package com.picsauditing.actions.audits;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

/**
 * Class used to edit a ContractorAudit record with virtually no restrictions
 * 
 * @author Trevor
 * 
 */
@SuppressWarnings("serial")
public class PolicySave extends AuditActionSupport implements Preparable {

	protected ContractorAuditOperatorDAO caoDAO;
	protected CertificateDAO certificateDao;
	protected AuditQuestionDAO questionDAO;

	protected ContractorAuditOperator cao;
	protected int certID = 0;
	protected Certificate certificate;
	protected String mode;

	// Custom policy categories and questions
	protected AuditQuestion question;
	protected int dataID = 0;
	protected int catDataID = 0;

	public PolicySave(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, ContractorAuditDAO contractorAuditDAO, ContractorAuditOperatorDAO caoDAO,
			CertificateDAO certificateDao, AuditQuestionDAO questionDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.caoDAO = caoDAO;
		this.certificateDao = certificateDao;
		this.questionDAO = questionDAO;
	}

	@Override
	public void prepare() throws Exception {
		int caoID = this.getParameter("cao.id");
		if (caoID > 0)
			this.cao = caoDAO.find(caoID);

		int questionID = this.getParameter("question.id");
		if (questionID > 0)
			this.question = questionDAO.find(questionID);
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		boolean statusChanged = false;

		if (button != null) {
			if (question != null) {
				this.findConAudit();

				if ("Status".equals(button)) {
					statusChanged = true;
					button = "Save";
				}

				if ("Save".equals(button)) {
					AuditData data = new AuditData();
					data.setAuditColumns(permissions);
					data.setAnswer(certID + "");
					data.setAudit(conAudit);
					data.setQuestion(question);
					// TODO Do we need to set the auditor here?

					auditDataDao.save(data);

					if (certificate != null) {
						certificate.updateExpirationDate();
						certificateDao.save(certificate);
					}
				}

				if ("Detach".equals(button) && dataID > 0) {
					AuditData data = auditDataDao.find(dataID);
					auditDataDao.remove(data);
				}

				return redirect("AuditCatAjax.action?button=Quick&auditID=" + conAudit.getId() 
						+ "&catDataID=" + catDataID + "&question.id=" + question.getId());
			}

			if (cao != null) {
				conAudit = cao.getAudit();
				contractor = cao.getAudit().getContractorAccount();

				if ("Submit".equals(button) || "Resubmit".equals(button)) {
					if (cao.getAudit().getPercentComplete() == 100 && cao.isCanContractorSubmit()) {
						cao.setStatus(CaoStatus.Submitted);
						statusChanged = true;
						button = "Save";
						addActionMessage("The <strong>" + cao.getAudit().getAuditType().getAuditName()
								+ "</strong> Policy has been submitted for <strong>" + cao.getOperator().getName()
								+ "</strong>.");
					} else {
						addActionError("Please enter all required questions before submitting the policy.");
						caoDAO.refresh(cao);
						return SUCCESS;
					}
				}

				if ("Verify".equals(button)) {
					cao.setStatus(CaoStatus.Verified);
					if (cao.getOperator().isAutoApproveInsurance()) {
						if (cao.getFlag() == null) {
							caoDAO.save(cao);
							String redirectURL = URLEncoder.encode(String.format(
									"PolicySaveAjax.action?cao.id=%d&button=Verify&certID=%d", cao.getId(), certID),
									"UTF-8");
							return redirect("ContractorCronAjax.action?conID=" + contractor.getId()
									+ "&opID=0&steps=ContractorETL&steps=Flag&steps=Policies&redirectUrl="
									+ redirectURL);
						}
						if (cao.getFlag() == FlagColor.Green)
							cao.setStatus(CaoStatus.Approved);
						else if (cao.getFlag() == FlagColor.Red)
							cao.setStatus(CaoStatus.Rejected);
					}
					cao.setAuditColumns(permissions);
					statusChanged = true;
					button = "Save";

					addActionMessage(String.format(
							"The <strong>%s</strong> Policy has been %s for <strong>%s</strong>.", cao.getAudit()
									.getAuditType().getAuditName(), cao.getStatus(), cao.getOperator().getName()));
				}

				if ("Reject".equals(button)) {
					if (Strings.isEmpty(cao.getNotes())) {
						addActionError("You must enter notes if you are rejecting a contractor's policy.");
						caoDAO.refresh(cao);
						return SUCCESS;
					} else {
						cao.setStatus(CaoStatus.Rejected);
						cao.setAuditColumns(permissions);
						statusChanged = true;
						button = "Save";

						EmailBuilder emailBuilder = new EmailBuilder();
						// Insurance Policy rejected by PICS
						emailBuilder.setTemplate(52);
						emailBuilder.setPermissions(permissions);
						emailBuilder
								.setFromAddress("\"" + permissions.getName() + "\"<" + permissions.getEmail() + ">");
						emailBuilder.setContractor(cao.getAudit().getContractorAccount(), OpPerms.ContractorInsurance);
						emailBuilder.addToken("cao", cao);
						EmailQueue email = emailBuilder.build();
						email.setViewableBy(cao.getOperator());
						EmailSender.send(email);

						addActionMessage("The <strong>" + cao.getAudit().getAuditType().getAuditName()
								+ "</strong> Policy has been rejected for <strong>" + cao.getOperator().getName()
								+ "</strong>. Note: " + Strings.htmlStrip(cao.getNotes()));
					}
				}

				if ("Approve".equals(button)) {
					cao.setStatus(CaoStatus.Approved);
					statusChanged = true;
					button = "Save";
					addActionMessage("The <strong>" + cao.getAudit().getAuditType().getAuditName()
							+ "</strong> Policy has been approved for <strong>" + cao.getOperator().getName()
							+ "</strong>");
				}

				if ("NotApplicable".equals(button)) {
					cao.setStatus(CaoStatus.NotApplicable);
					statusChanged = true;
					button = "Save";
				}

				if ("Status".equals(button)) {
					statusChanged = true;
					button = "Save";
				}

				if ("visible".equals(button)) {
					cao.setVisible(true);

					if (cao.getStatus().isNotApplicable()) {
						cao.setStatus(CaoStatus.Pending);
						statusChanged = true;
					}

					if (cao.getCertificate() != null)
						certificate = cao.getCertificate();

					button = "Save";
				}

				if ("Save".equals(button)) {
					if (cao != null) {

						if (certID > 0)
							cao.setCertificate(certificateDao.find(certID));
						else
							cao.setCertificate(null);

						if (Strings.isEmpty(cao.getReason()))
							cao.setReason(null);
						if (Strings.isEmpty(cao.getNotes()))
							cao.setNotes(null);

						if (statusChanged) {
							cao.setStatusChangedBy(getUser());
							cao.setStatusChangedDate(new Date());
							String text = " Changed " + cao.getAudit().getAuditType().getAuditName() + " status to "
									+ cao.getStatus() + " for " + cao.getOperator().getName();
							addNote(contractor, text, NoteCategory.Insurance, LowMedHigh.Low, true, cao.getOperator()
									.getId(), null);
						}
						caoDAO.save(cao);

						if (certificate != null) {
							certificate.updateExpirationDate();
							certificateDao.save(certificate);
						}
					}
				}
			}
		}

		return SUCCESS;
	}

	public ContractorAuditOperator getCao() {
		return cao;
	}

	public void setCao(ContractorAuditOperator cao) {
		this.cao = cao;
	}

	public int getCertID() {
		return certID;
	}

	public void setCertID(int certID) {
		this.certID = certID;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
	}

	public int getDataID() {
		return dataID;
	}

	public void setDataID(int dataID) {
		this.dataID = dataID;
	}
	
	public int getCatDataID() {
		return catDataID;
	}
	
	public void setCatDataID(int catDataID) {
		this.catDataID = catDataID;
	}

	public List<Certificate> getCertificates() {
		return certificateDao.findByConId(contractor.getId(), permissions, false);
	}
}
