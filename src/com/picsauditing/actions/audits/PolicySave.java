package com.picsauditing.actions.audits;

import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.CaoStatus;
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
public class PolicySave extends AuditActionSupport {

	protected ContractorAuditOperatorDAO caoDAO;

	protected int opID;

	protected ContractorAuditOperator cao;
	protected String caoNotes;

	public PolicySave(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, ContractorAuditDAO contractorAuditDAO, ContractorAuditOperatorDAO caoDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.caoDAO = caoDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findConAudit();

		cao = caoDAO.find(conAudit.getId(), opID);

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

		return SUCCESS;
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
}
