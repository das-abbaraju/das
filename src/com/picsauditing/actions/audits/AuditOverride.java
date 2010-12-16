package com.picsauditing.actions.audits;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.PICS.AuditTypeRuleCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AuditOverride extends AuditActionSupport {

	private AuditTypeDAO auditTypeDAO;

	private Integer auditTypeID;
	private Integer requestingOpID;
	private String auditFor;
	private Set<AuditType> overrideAudits = null;
	private AuditTypeRuleCache auditTypeRuleCache;

	public AuditOverride(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, CertificateDAO certificateDao, AuditCategoryRuleCache auditCategoryRuleCache,
			AuditTypeDAO auditTypeDAO, AuditTypeRuleCache auditTypeRuleCache) {
		super(accountDao, auditDao, catDataDao, auditDataDao, certificateDao, auditCategoryRuleCache);
		this.auditTypeDAO = auditTypeDAO;
		this.auditTypeRuleCache = auditTypeRuleCache;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		this.findContractor();
		
		tryPermissions(OpPerms.ManageAudits, OpType.Edit);
		
		if (button != null) {
			ContractorAudit conAudit = new ContractorAudit();

			if (auditTypeID != null)
				conAudit.setAuditType(auditTypeDAO.find(auditTypeID));

			if (conAudit.getAuditType() == null) {
				addActionError("You must select an audit type.");
				return SUCCESS;
			}

			if (requestingOpID != null && requestingOpID > 0) {
				conAudit.setRequestingOpAccount(new OperatorAccount());
				conAudit.getRequestingOpAccount().setId(requestingOpID);
			}

			if (!Strings.isEmpty(auditFor))
				conAudit.setAuditFor(auditFor);

			conAudit.setManuallyAdded(true);
			conAudit.setAuditColumns(permissions);
			conAudit.setContractorAccount(contractor);

			auditDao.save(conAudit);

			addNote(conAudit.getContractorAccount(), "Added " + conAudit.getAuditType().getAuditName() + " manually",
					NoteCategory.Audits, getViewableByAccount(conAudit.getAuditType().getAccount()));

			if ("Create".equals(button)) {
				this.redirect("ContractorCron.action?conID=" + id
						+ "&button=Run&steps=AuditBuilder&redirectUrl=Audit.action?auditID=" + conAudit.getId());
			} else {
				this.redirect(String.format("AuditOverride.action?id=%d&msg=%s successfully created.", id, conAudit
						.getAuditType().getAuditName()));

			}
		}
		return SUCCESS;
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

	public String getAuditFor() {
		return auditFor;
	}

	public void setAuditFor(String auditFor) {
		this.auditFor = auditFor;
	}
	
	public Set<AuditType> getOverrideAudits() {
		if (overrideAudits == null) {
			overrideAudits = new HashSet<AuditType>();
			List<AuditTypeRule> applicableAuditRules = auditTypeRuleCache.getApplicableAuditRules(contractor);
			for(AuditTypeRule auditTypeRule : applicableAuditRules) {
				if(auditTypeRule.getAuditType() != null && auditTypeRule.isInclude()) {
					if(!auditTypeRule.getAuditType().isAnnualAddendum() && (auditTypeRule.getAuditType().isHasMultiple() || auditTypeRule.isManuallyAdded())) {
						overrideAudits.add(auditTypeRule.getAuditType());
					}
				}
			}
		}

		return overrideAudits;
	}
}
