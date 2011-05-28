package com.picsauditing.actions.rules;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.util.Strings;

import edu.emory.mathcs.backport.java.util.Collections;

@SuppressWarnings("serial")
public class AuditTypeRuleTableBuilder extends AuditRuleTableBuilder<AuditTypeRule> {

	protected AuditTypeRule comparisonRule;

	public AuditTypeRuleTableBuilder() {
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
		} else if ("tags".equals(button) && comparisonRule.getOperatorAccount() != null) {
			List<OperatorTag> tags = operatorTagDAO.findByOperator(comparisonRule.getOperatorAccount().getId(), false);
			rules = ruleDAO.findAuditTypeRulesByTags(tags);
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
			if (comparisonRule.getTag() != null) {
				whereClauses.add("t.tag.id = " + comparisonRule.getTag().getId());
			}

			if (comparisonRule.getQuestion() != null) {
				whereClauses.add("t.question.id = " + comparisonRule.getQuestion().getId());
			}

			if (comparisonRule.getTrade() != null) {
				List<Trade> trades = tradeDAO.findListByTrade(comparisonRule.getTrade().getId(), 0);
				StringBuilder sb = new StringBuilder("t.trade.id IN (");
				for (Trade t : trades) {
					sb.append(t.getId()).append(",");
				}
				sb.setLength(sb.lastIndexOf(","));
				sb.append(")");
				whereClauses.add(sb.toString());
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
