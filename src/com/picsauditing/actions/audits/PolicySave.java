package com.picsauditing.actions.audits;

import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
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

	protected int opID;

	protected ContractorAuditOperator cao;
	protected String caoNotes;
	protected Certificate certificate;

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
		if (caoID > 0) {
			this.cao = caoDAO.find(caoID);
		}
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findConAudit();

		// cao = caoDAO.find(conAudit.getId(), opID);
		if (button != null) {
			if ("Verify".equals(button)) {
				cao.setStatus(CaoStatus.Verified);
				cao.setNotes(caoNotes);
				cao.setAuditColumns(permissions);
				caoDAO.save(cao);

				addActionMessage("The <strong>" + cao.getAudit().getAuditType().getAuditName()
						+ "</strong> Policy has been verified for <strong>" + cao.getOperator().getName()
						+ "</strong>.");
			}

			if ("Reject".equals(button)) {
				if (Strings.isEmpty(caoNotes)) {
					addActionError("You must enter notes if you are rejecting a contractor's policy.");
				} else {
					cao.setStatus(CaoStatus.Rejected);
					cao.setNotes(caoNotes);
					cao.setAuditColumns(permissions);
					caoDAO.save(cao);

					EmailBuilder emailBuilder = new EmailBuilder();
					emailBuilder.setTemplate(52); // Insurance Policy rejected by PICS
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

			if ("Save".equals(button)) {
				if (cao != null) {
					if (cao.getCertificate() == null || cao.getCertificate().getId() == 0) {
						cao.setCertificate(null);
					}
					caoDAO.save(cao);
				}
			}

			if ("Attach".equals(button)) {
				if (certificate != null) {
					cao.setCertificate(certificate);
					caoDAO.save(cao);
				}
			}

			if ("Remove".equals(button)) {
				cao.setCertificate(null);
				caoDAO.save(cao);
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

	public int getOpID() {
		return opID;
	}

	public void setOpID(int opID) {
		this.opID = opID;
	}

	public String getCaoNotes() {
		return caoNotes;
	}

	public void setCaoNotes(String caoNotes) {
		if (Strings.isEmpty(caoNotes))
			caoNotes = null;
		this.caoNotes = caoNotes;
	}

	public Certificate getCertificate() {
		return certificate;
	}

	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
	}

	public List<Certificate> getCertificates() {
		return certificateDao.findByConId(contractor.getId(), permissions);
	}
}
