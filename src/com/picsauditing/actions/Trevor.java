package com.picsauditing.actions;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.ContractorAccount;

@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {

	private AuditDecisionTableDAO auditRuleDAO;
	private ContractorAccountDAO accountDAO;
	private AuditCategoryRuleCache cache;
	private int conID;
	private Set<AuditCategoryRule> applicable;
	public String rules;

	public Trevor(ContractorAccountDAO accountDAO, AuditDecisionTableDAO auditRuleDAO, AuditCategoryRuleCache cache) {
		this.accountDAO = accountDAO;
		this.auditRuleDAO = auditRuleDAO;
		this.cache = cache;
	}

	public String execute() throws SQLException {

		if ("clear".equals(button)) {
			List<AuditCategoryRule> rules = auditRuleDAO.findCategoryRules();
			cache.clear();
		}
		if ("print".equals(button)) {
			output = cache.print();
		}
		if (conID > 0) {
			ContractorAccount contractor = accountDAO.find(conID);
			//applicable = cache.getApplicable(contractor, null);
		}
		if ("showlist".equals(button)) {
			String[] rulesList = rules.split(",");
			applicable = new LinkedHashSet<AuditCategoryRule>();
			for (String string : rulesList) {
				try {
					applicable.add(auditRuleDAO.findAuditCategoryRule(Integer.parseInt(string)));
				} catch (NumberFormatException e) {
					// ignore
					System.out.println("problem: " + string);
				}
			}
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
