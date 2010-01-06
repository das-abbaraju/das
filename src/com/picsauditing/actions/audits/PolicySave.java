package com.picsauditing.actions.audits;

import java.util.Date;
import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.AuditCriteriaAnswer;
import com.picsauditing.PICS.AuditCriteriaAnswerBuilder;
import com.picsauditing.PICS.FlagCalculatorSingle;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.AnswerMapByAudits;
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

	protected ContractorAuditOperator cao;
	protected int certID = 0;
	protected Certificate certificate;
	protected String mode;

	public PolicySave(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, ContractorAuditDAO contractorAuditDAO, ContractorAuditOperatorDAO caoDAO,
			CertificateDAO certificateDao) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.caoDAO = caoDAO;
		this.certificateDao = certificateDao;
	}

	@Override
	public void prepare() throws Exception {
		int caoID = this.getParameter("cao.id");
		if (caoID > 0)
			this.cao = caoDAO.find(caoID);

		int certID = this.getParameter("certID");
		if (certID > 0)
			certificate = certificateDao.find(certID);

	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		boolean statusChanged = false;

		if (button != null) {
			if (cao != null) {
				conAudit = cao.getAudit();
				contractor = cao.getAudit().getContractorAccount();

				if ("Submit".equals(button) || "Resubmit".equals(button)) {
					if (cao.getAudit().getPercentComplete() == 100 && cao.isCanContractorSubmit()) {
						cao.setStatus(CaoStatus.Submitted);
						statusChanged = true;
						button = "Save";
						addActionMessage("The <strong>" + cao.getAudit().getAuditType().getAuditName()
								+ "</strong> Policy has been submitted tor <strong>" + cao.getOperator().getName()
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
						if (cao.getFlag() == FlagColor.Green)
							cao.setStatus(CaoStatus.Approved);
						else if (cao.getFlag() == FlagColor.Red)
							cao.setStatus(CaoStatus.Rejected);
					}
					cao.setAuditColumns(permissions);
					statusChanged = true;
					button = "Save";

					addActionMessage("The <strong>" + cao.getAudit().getAuditType().getAuditName()
							+ "</strong> Policy has been verified for <strong>" + cao.getOperator().getName()
							+ "</strong>.");
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
						emailBuilder.setTemplate(52); // Insurance Policy
						// rejected
						// by PICS
						emailBuilder.setPermissions(permissions);
						emailBuilder.setFromAddress(permissions.getEmail());
						emailBuilder.setContractor(cao.getAudit().getContractorAccount());
						emailBuilder.addToken("cao", cao);
						EmailSender.send(emailBuilder.build());

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
						if (certificate == null || certificate.getId() == 0)
							cao.setCertificate(null);
						else
							cao.setCertificate(certificate);

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
							// TODO this is a duplicate of FlagCalculator2
							// because we need the color to change instantly
							FlagCalculatorSingle calculator = new FlagCalculatorSingle();
							List<Integer> criteriaQuestionIDs = cao.getOperator().getQuestionIDs();
							AnswerMapByAudits answerMapByAudits = auditDataDao.findAnswersByAudits(contractor
									.getAudits(), criteriaQuestionIDs);
							AnswerMapByAudits answerMapForOperator = new AnswerMapByAudits(answerMapByAudits, cao
									.getOperator());
							AuditCriteriaAnswerBuilder acaBuilder = new AuditCriteriaAnswerBuilder(
									answerMapForOperator, cao.getOperator().getFlagQuestionCriteriaInherited());
							List<AuditCriteriaAnswer> acaList = acaBuilder.getAuditCriteriaAnswers();
							calculator.setAcaList(acaList);
							FlagColor flagColor = calculator.calculateCaoRecommendedFlag(cao);
							cao.setFlag(flagColor);
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

	public List<Certificate> getCertificates() {
		return certificateDao.findByConId(contractor.getId(), permissions, false);
	}
}
