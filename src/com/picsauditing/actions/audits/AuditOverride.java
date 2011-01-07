package com.picsauditing.actions.audits;

import com.picsauditing.PICS.AuditTypeRuleCache;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.contractors.ContractorDocuments;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AuditOverride extends ContractorDocuments {

	public AuditOverride(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditTypeDAO auditTypeDAO,
			ContractorAuditOperatorDAO caoDAO, AuditTypeRuleCache auditTypeRuleCache) {
		super(accountDao, auditDao, auditTypeDAO, caoDAO, auditTypeRuleCache);

		subHeading = "Manually Add Audit";
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		this.findContractor();
		
		if (!isManuallyAddAudit()) {
			throw new NoRightsException("Cannot Manually Add Audits");
		}

		if (button != null) {
			ContractorAudit conAudit = new ContractorAudit();

			if (selectedAudit != null)
				conAudit.setAuditType(auditTypeDAO.find(selectedAudit));

			if (conAudit.getAuditType() == null) {
				addActionError("You must select an audit type.");
				return SUCCESS;
			}

			if (selectedOperator != null && selectedOperator > 0) {
				conAudit.setRequestingOpAccount(new OperatorAccount());
				conAudit.getRequestingOpAccount().setId(selectedOperator);
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
			}
			/*
			 * else {this.redirect(String.format(
			 * "AuditOverride.action?id=%d&msg=%s successfully created.", id,
			 * conAudit .getAuditType().getAuditName()));
			 * 
			 * }
			 */
		}

		return SUCCESS;
	}

	@Override
	public Integer getSelectedAudit() {
		if (selectedAudit == null && getManuallyAddAudits().size() == 1)
			selectedAudit = getManuallyAddAudits().iterator().next().getId();
		return selectedAudit;
	}

}
