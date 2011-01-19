package com.picsauditing.actions.rules;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

import edu.emory.mathcs.backport.java.util.Collections;

@SuppressWarnings("serial")
public class AuditTypeRuleTableBuilder extends AuditRuleTableBuilder<AuditTypeRule> {

	protected AuditTypeRule comparisonRule;
	protected OperatorAccountDAO operatorDAO;

	public AuditTypeRuleTableBuilder(AuditDecisionTableDAO ruleDAO, OperatorAccountDAO operatorDAO) {
		this.ruleDAO = ruleDAO;
		this.operatorDAO = operatorDAO;
		this.ruleType = "Audit Type";
		this.urlPrefix = "AuditType";
	}

	@Override
	public void checkColumns(AuditTypeRule rule) {
		super.checkColumns(rule);
		if (rule.getDependentAuditType() != null)
			columnMap.put("dependentAuditType", true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void findRules() {
		if ("lessGranular".equals(button)) {
			rules = ruleDAO.getLessGranular(ruleDAO.findAuditTypeRule(id), date);
		} else if ("moreGranular".equals(button)) {
			rules = ruleDAO.getMoreGranular(ruleDAO.findAuditTypeRule(id), date);
		} else if (comparisonRule != null) {
			Set<String> whereClauses = new LinkedHashSet<String>();
			whereClauses.add("(t.effectiveDate < NOW() AND t.expirationDate > NOW())");
			if (!comparisonRule.isInclude()) {
				whereClauses.add("t.include = 0");
			}
			if (comparisonRule.getAuditType() != null) {
				whereClauses.add("t.auditType.id = " + comparisonRule.getAuditType().getId());
			}
			if (comparisonRule.getOperatorAccount() != null) {
				if (!comparisonRule.isInclude()) {
					// If we're only looking for exclude rules, then we probably
					// only want rules specific to this operator
					whereClauses.add("t.operatorAccount.id = " + comparisonRule.getOperatorAccount().getId());
				} else {
					OperatorAccount operator = operatorDAO.find(comparisonRule.getOperatorAccount().getId());
					whereClauses.add("(t.operatorAccount IS NULL OR t.operatorAccount.id IN ("
							+ Strings.implode(operator.getOperatorHeirarchy()) + "))");
				}
			}

			rules = (List<AuditTypeRule>) ruleDAO.findWhere(AuditTypeRule.class,
					Strings.implode(whereClauses, " AND "), 0);
			Collections.sort(rules);
			Collections.reverse(rules);
		} else if (id != null) {
			rules.add(ruleDAO.findAuditTypeRule(id));
		}
	}

	public AuditTypeRule getComparisonRule() {
		return comparisonRule;
	}

	public void setComparisonRule(AuditTypeRule comparisonRule) {
		this.comparisonRule = comparisonRule;
	}
}
