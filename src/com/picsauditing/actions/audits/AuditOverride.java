package com.picsauditing.actions.audits;

import java.util.List;

import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AuditOverride extends AuditActionSupport {

	private AuditTypeDAO auditTypeDAO;
	private List<AuditType> overrideAudits;

	private Integer auditTypeID;
	private Integer requestingOpID;

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

		tryPermissions(OpPerms.ManageAudits, OpType.Edit);

		this.findContractor();

		if (conAudit != null) {
			AuditType auditType = null;
			if (auditTypeID != null)
				auditTypeDAO.find(auditTypeID);
			if (auditType == null) {
				addActionError("You must select an audit type.");
				return SUCCESS;
			}

			if (permissions.isOperator()) {
				conAudit.setRequestingOpAccount(new OperatorAccount());
				conAudit.getRequestingOpAccount().setId(permissions.getAccountId());
			} else if (requestingOpID != null && requestingOpID > 0) {
				conAudit.setRequestingOpAccount(new OperatorAccount());
				conAudit.getRequestingOpAccount().setId(requestingOpID);
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
						+ Strings.implode(permissions.getVisibleAuditTypes()) + ") AND id != 11");
			else
				overrideAudits = auditTypeDAO.findWhere("hasMultiple = 1 AND id != 11");
		}

		return overrideAudits;
	}

	public void setConAudit(ContractorAudit conAudit) {
		this.conAudit = conAudit;
	}

	public Integer getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(Integer auditTypeID) {
		this.auditTypeID = auditTypeID;
	}

	public Integer getRequestingOpID() {
		return requestingOpID;
	}

	public void setRequestingOpID(Integer requestingOpID) {
		this.requestingOpID = requestingOpID;
	}
}
