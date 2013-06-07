package com.picsauditing.actions.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AuditCategoryRuleTableBuilder extends AuditRuleTableBuilder<AuditCategoryRule> {

	private AuditCategoryRule comparisonRule;
	@Autowired
	private AuditCategoryDAO auditCategoryDAO;
	@Autowired
	private AuditCategoryRuleCache ruleCache;

	public AuditCategoryRuleTableBuilder() {
		this.ruleType = "Category";
		this.urlPrefix = "Category";
	}

	@Override
	public void checkColumns(AuditCategoryRule rule) {
		super.checkColumns(rule);
		if (rule.getAuditCategory() != null)
			columnMap.put("auditCategory", true);
		if (rule.getRootCategory() != null)
			columnMap.put("rootCategory", true);
		if (rule.getDependentAuditType() != null)
			columnMap.put("dependentAuditType", true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void findRules() {
		if ("lessGranular".equals(button)) {
			rules = ruleDAO.getLessGranular(ruleDAO.findAuditCategoryRule(id), date);
		} else if ("moreGranular".equals(button)) {
			rules = ruleDAO.getMoreGranular(ruleDAO.findAuditCategoryRule(id), date);
		} else if ("debugCategory".equals(button) && id > 0) {
			rules = new ArrayList<AuditCategoryRule>();
			comparisonRule.setAuditCategory(auditCategoryDAO.find(comparisonRule.getAuditCategory().getId()));
			ContractorAudit conAudit = (ContractorAudit) auditCategoryDAO.find(ContractorAudit.class, id);
			List<AuditCategoryRule> allRules = ruleCache.getRules(conAudit.getContractorAccount(),
					conAudit.getAuditType());
			for (AuditCategoryRule rule : allRules) {
				if (rule.isApplies(comparisonRule.getAuditCategory()))
					rules.add(rule);
			}
		} else if ("tags".equals(button) && comparisonRule.getOperatorAccount() != null) {
			List<OperatorTag> tags = operatorTagDAO.findByOperator(comparisonRule.getOperatorAccount().getId(), false);
			if (tags.size() > 0)
				rules = ruleDAO.findCategoryRulesByTags(tags);
		} else if (comparisonRule != null) {
			Set<String> whereClauses = new LinkedHashSet<String>();
			whereClauses.add("(t.effectiveDate < NOW() AND t.expirationDate > NOW())");
			if (!comparisonRule.isInclude()) {
				whereClauses.add("t.include = 0");
			}
			if (comparisonRule.getAuditCategory() != null) {
				AuditCategory category = auditCategoryDAO.find(comparisonRule.getAuditCategory().getId());
				whereClauses.add("(((t.auditType IS NULL OR t.auditType.id = " + category.getAuditType().getId()
						+ ") AND t.auditCategory IS NULL AND (t.rootCategory = "
						+ (category.getParent() == null ? 1 : 0)
						+ " OR t.rootCategory IS NULL)) OR t.auditCategory.id = " + category.getId() + ")");
			} else {
				if (comparisonRule.getAuditType() != null) {
					whereClauses
							.add("t.auditType IS NULL OR t.auditType.id = " + comparisonRule.getAuditType().getId());
				}
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
				sb.append(") OR t.trade IS NULL AND t.auditType.id =").append(AuditType.DESKTOP);
				whereClauses.add(sb.toString());
			}

			rules = (List<AuditCategoryRule>) ruleDAO.findWhere(AuditCategoryRule.class,
					Strings.implode(whereClauses, " AND "), 0);
			Collections.sort(rules);
			Collections.reverse(rules);
		} else if (id != null) {
			rules.add(ruleDAO.findAuditCategoryRule(id));
		}
	}

	public AuditCategoryRule getComparisonRule() {
		return comparisonRule;
	}

	public void setComparisonRule(AuditCategoryRule comparisonRule) {
		this.comparisonRule = comparisonRule;
	}
}
