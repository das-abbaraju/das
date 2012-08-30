package com.picsauditing.actions.rules;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.auditBuilder.AuditTypesBuilder;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AuditTypeRuleTableBuilder extends AuditRuleTableBuilder<AuditTypeRule> {

	protected AuditTypeRule comparisonRule;

	private boolean includeExpired = false;

	@Autowired
	protected AuditTypeRuleCache auditTypeRuleCache;

	public AuditTypeRuleTableBuilder() {
		this.ruleType = "Audit Type";
		this.urlPrefix = "AuditType";
	}

	@Override
	public void checkColumns(AuditTypeRule rule) {
		super.checkColumns(rule);
		if (rule.isManuallyAdded())
			columnMap.put("manuallyAdded", true);
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
		} else if ("debugContractor".equals(button)) {
			setShowWho(false);
			auditTypeRuleCache.initialize(auditRuleDAO);
			ContractorAccount contractor = contractorDAO.find(conID);
			AuditTypesBuilder builder = new AuditTypesBuilder(auditTypeRuleCache, contractor);
			builder.calculate();
			rules = builder.getRules();
		} else if ("tags".equals(button) && comparisonRule.getOperatorAccount() != null) {
			List<OperatorTag> tags = operatorTagDAO.findByOperator(comparisonRule.getOperatorAccount().getId(), false);
			rules = ruleDAO.findAuditTypeRulesByTags(tags);
		} else if (comparisonRule != null) {
			Set<String> whereClauses = new LinkedHashSet<String>();

			whereClauses.add("(t.effectiveDate < NOW())");

			if (!includeExpired) {
				whereClauses.add("t.expirationDate > NOW()");
			}

			if (!comparisonRule.isInclude()) {
				whereClauses.add("t.include = 0");
			}

			if (comparisonRule.getAuditType() != null) {
				whereClauses.add("(t.auditType IS NULL OR t.auditType.id = " + comparisonRule.getAuditType().getId()
						+ " OR t.dependentAuditType.id = " + comparisonRule.getAuditType().getId() + ")");
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

	public boolean getIncludeExpired() {
		return includeExpired;
	}

	public void setIncludeExpired(boolean includeExpired) {
		this.includeExpired = includeExpired;
	}
}
