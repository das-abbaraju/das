package com.picsauditing.actions;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import com.picsauditing.PICS.AuditRuleCache;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.ContractorAccount;

@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {

	private AuditDecisionTableDAO auditRuleDAO;
	private ContractorAccountDAO accountDAO;
	private AuditRuleCache cache;
	private int conID;
	private Set<AuditCategoryRule> applicable;

	public Trevor(ContractorAccountDAO accountDAO, AuditDecisionTableDAO auditRuleDAO, AuditRuleCache cache) {
		this.accountDAO = accountDAO;
		this.auditRuleDAO = auditRuleDAO;
		this.cache = cache;
	}

	public String execute() throws SQLException {

		if ("fill".equals(button)) {
			List<AuditCategoryRule> rules = auditRuleDAO.findRules();
			cache.fill(rules);
		}
		if ("print".equals(button)) {
			output = cache.print();
		}
		if (conID > 0) {
			ContractorAccount contractor = accountDAO.find(conID);
			applicable = cache.getApplicable(contractor, null);
		}

		return SUCCESS;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	public Set<AuditCategoryRule> getApplicable() {
		return applicable;
	}
}
