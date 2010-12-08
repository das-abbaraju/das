package com.picsauditing.actions.audits;

import java.util.List;

import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AuditOverride extends AuditActionSupport {

	private AuditTypeDAO auditTypeDAO;
	private List<AuditType> overrideAudits;

	private int auditTypeID;

	public AuditOverride(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, CertificateDAO certificateDao, AuditCategoryRuleCache auditCategoryRuleCache,
			AuditTypeDAO auditTypeDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao, certificateDao, auditCategoryRuleCache);
		this.auditTypeDAO = auditTypeDAO;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		this.findContractor();

		if (conAudit != null) {
			AuditType auditType = auditTypeDAO.find(auditTypeID);
			if (auditType == null) {
				addActionError("You must select an audit type.");
				return SUCCESS;
			}
			conAudit.setAuditType(auditType);
			conAudit.setManuallyAdded(true);
			conAudit.setAuditColumns(permissions);
			conAudit.setContractorAccount(contractor);
			auditDao.save(conAudit);
			addNote(conAudit.getContractorAccount(), "Added " + auditType.getAuditName() + " manually",
					NoteCategory.Audits, getViewableByAccount(conAudit.getAuditType().getAccount()));
			if ("Create".equals(button)) {
				this.redirect("ContractorCron.action?conID=" + id
						+ "&button=Run&steps=AuditBuilder&redirectUrl=Audit.action?auditID=" + conAudit.getId());
			} else {
				addActionMessage("Audit Successfully created. <a href=\"Audit.action?auditID=" + conAudit.getId()
						+ "\">Click here to view it</a>");
			}
		}
		return SUCCESS;
	}

	public List<AuditType> getOverrideAudits() {
		if (overrideAudits == null) {
			if (permissions.isOperatorCorporate())
				overrideAudits = auditTypeDAO.findWhere("hasMultiple = 1 AND id IN ("
						+ Strings.implode(permissions.getVisibleAuditTypes()) + ")");
			else
				overrideAudits = auditTypeDAO.findWhere("hasMultiple = 1");
		}

		return overrideAudits;
	}

	public void setConAudit(ContractorAudit conAudit) {
		this.conAudit = conAudit;
	}

	public int getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
	}
}
