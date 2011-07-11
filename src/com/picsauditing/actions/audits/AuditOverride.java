package com.picsauditing.actions.audits;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.contractors.ContractorDocuments;
import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AuditOverride extends ContractorDocuments {
	@Autowired
	AuditDecisionTableDAO auditRuleDAO;

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

		auditTypeRuleCache.initialize(auditRuleDAO);

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

			if (selectedOperator == null || selectedOperator == 0) {
				if (permissions.isOperator())
					selectedOperator = permissions.getAccountId();
				else {
					addActionError("You must select an operator.");
					return SUCCESS;
				}
			}
			conAudit.setRequestingOpAccount(new OperatorAccount());
			conAudit.getRequestingOpAccount().setId(selectedOperator);

			ContractorAuditOperator cao = new ContractorAuditOperator();
			cao.setAudit(conAudit);
			cao.setOperator(conAudit.getRequestingOpAccount());
			cao.setAuditColumns(permissions);
			// This is almost always Pending
			AuditStatus firstStatus = conAudit.getAuditType().getWorkFlow().getFirstStep().getNewStatus();
			cao.changeStatus(firstStatus, null);
			conAudit.getOperators().add(cao);
			conAudit.setLastRecalculation(null);

			if (!Strings.isEmpty(auditFor))
				conAudit.setAuditFor(auditFor);

			conAudit.setManuallyAdded(true);
			conAudit.setAuditColumns(permissions);
			conAudit.setContractorAccount(contractor);

			auditDao.save(conAudit);

			addNote(conAudit.getContractorAccount(), "Added " + conAudit.getAuditType().getName().toString()
					+ " manually", NoteCategory.Audits, getViewableByAccount(conAudit.getAuditType().getAccount()));

			if ("Create".equals(button)) {
				this.redirect("ContractorCron.action?conID=" + id
						+ "&button=Run&steps=AuditBuilder&redirectUrl=Audit.action?auditID=" + conAudit.getId());
			}
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
