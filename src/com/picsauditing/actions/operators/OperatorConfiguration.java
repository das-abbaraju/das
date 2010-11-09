package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class OperatorConfiguration extends OperatorActionSupport implements Preparable {
	protected AuditDecisionTableDAO adtDAO;

	private Map<AuditType, List<AuditTypeRule>> typeMap = new TreeMap<AuditType, List<AuditTypeRule>>();
	private Map<AuditCategory, List<AuditCategoryRule>> categoryMap = new TreeMap<AuditCategory, List<AuditCategoryRule>>();
	private List<AuditTypeRule> excludeTypes = new ArrayList<AuditTypeRule>();
	private List<AuditCategoryRule> excludeCategories = new ArrayList<AuditCategoryRule>();

	private List<OperatorAccount> allParents = null;

	public OperatorConfiguration(OperatorAccountDAO operatorDao, AuditDecisionTableDAO adtDAO) {
		super(operatorDao);
		this.adtDAO = adtDAO;
	}

	public void prepare() throws Exception {
		findOperator();
		subHeading = "Operator Configuration";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		List<Integer> inheritance = operator.getOperatorHeirarchy();
		inheritance.remove((Integer) operator.getId());

		allParents = operatorDao.findWhere(true, "a.id IN (" + Strings.implode(inheritance) + ")", permissions);

		Collections.sort(allParents, new Comparator<OperatorAccount>() {
			public int compare(OperatorAccount o1, OperatorAccount o2) {
				return (o1.getId() - o2.getId());
			}
		});

		List<AuditTypeRule> typeRules = adtDAO.findAuditTypeRulesByOperator(operator.getId());
		List<AuditCategoryRule> categoryRules = adtDAO.findAuditCategoryRulesByOperator(operator.getId());

		for (AuditTypeRule typeRule : typeRules) {
			if (typeRule.getAuditType() != null) {
				if (typeMap.get(typeRule.getAuditType()) == null)
					typeMap.put(typeRule.getAuditType(), new ArrayList<AuditTypeRule>());

				typeMap.get(typeRule.getAuditType()).add(typeRule);
			}

			if (!typeRule.isInclude() && typeRule.getOperatorAccount() != null
					&& typeRule.getOperatorAccount().getId() == operator.getId())
				excludeTypes.add(typeRule);
		}

		for (AuditCategoryRule catRule : categoryRules) {
			if (catRule.getAuditCategory() != null) {
				if (categoryMap.get(catRule.getAuditCategory()) == null)
					categoryMap.put(catRule.getAuditCategory(), new ArrayList<AuditCategoryRule>());

				categoryMap.get(catRule.getAuditCategory()).add(catRule);
			}

			if (!catRule.isInclude() && catRule.getOperatorAccount() != null
					&& catRule.getOperatorAccount().getId() == operator.getId())
				excludeCategories.add(catRule);
		}

		return SUCCESS;
	}

	public List<OperatorAccount> getAllParents() {
		return allParents;
	}

	public Map<AuditType, List<AuditTypeRule>> getTypeMap() {
		return typeMap;
	}

	public Map<AuditCategory, List<AuditCategoryRule>> getCategoryMap() {
		return categoryMap;
	}

	public List<AuditTypeRule> getExcludeTypes() {
		return excludeTypes;
	}

	public List<AuditCategoryRule> getExcludeCategories() {
		return excludeCategories;
	}
}