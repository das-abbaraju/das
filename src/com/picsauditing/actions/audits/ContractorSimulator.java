package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.PICS.AuditTypeRuleCache;
import com.picsauditing.PICS.AuditBuilder.AuditCategoriesDetail;
import com.picsauditing.PICS.AuditBuilder.AuditTypeDetail;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;

@SuppressWarnings("serial")
public class ContractorSimulator extends PicsActionSupport {

	private ContractorAccount contractor;
	private Set<Integer> operatorIds;
	private List<OperatorAccount> operators;
	private Map<AuditType, List<AuditTypeRule>> audits;
	private AuditType auditType;
	private List<AuditCategory> categories = new ArrayList<AuditCategory>();
	private AuditBuilder builder = new AuditBuilder();
	private AuditCategoryRuleCache auditCategoryRuleCache;
	private AuditTypeRuleCache auditTypeRuleCache;

	private AuditTypeDAO auditTypeDAO;

	public ContractorSimulator(AuditTypeDAO auditTypeDAO, AuditCategoryRuleCache auditCategoryRuleCache,
			AuditTypeRuleCache auditTypeRuleCache) {
		this.auditCategoryRuleCache = auditCategoryRuleCache;
		this.auditTypeRuleCache = auditTypeRuleCache;
		this.auditTypeDAO = auditTypeDAO;
	}

	@Override
	public String execute() throws Exception {

		if (contractor == null) {
			return SUCCESS;
		}

		operators = new ArrayList<OperatorAccount>();
		for (Integer opID : operatorIds) {
			ContractorOperator co = new ContractorOperator();
			OperatorAccount operator = (OperatorAccount) auditTypeDAO.findWhere(OperatorAccount.class, "id = " + opID,
					1).get(0);
			operators.add(operator);
			co.setContractorAccount(contractor);
			co.setOperatorAccount(operator);
			contractor.getOperators().add(co);
		}

		if (auditType != null && auditType.getId() > 0) {
			fillAuditCategories();
			return "categories";
		} else {
			fillAuditTypes();
			return "audits";
		}
	}

	private void fillAuditTypes() {
		audits = new TreeMap<AuditType, List<AuditTypeRule>>();
		List<AuditTypeRule> rules = auditTypeRuleCache.getApplicableAuditRules(contractor);

		Map<AuditType, AuditTypeDetail> requiredAuditTypes = builder.calculateRequiredAuditTypes(rules, operators);
		for (AuditType auditType : requiredAuditTypes.keySet()) {
			boolean includeAlways = false;
			List<AuditTypeRule> list = new ArrayList<AuditTypeRule>();
			for (AuditTypeRule rule : rules) {
				if (rule.getAuditType() == null || rule.getAuditType().equals(auditType)) {
					// We have a matching rule
					if (includeAlways) {
						// We are already including this auditType always, so we
						// can ignore any rules after this
					} else {
						if ((rule.getAcceptsBids() != null && rule.getAcceptsBids())
								|| rule.getDependentAuditType() != null || rule.getQuestion() != null
								|| rule.getTag() != null || rule.isManuallyAdded()) {
							list.add(rule);
						} else {
							// We found a rule that will always include this
							// audit
							includeAlways = true;
							if (!rule.isInclude()) {
								// Since we always exclude this audit, then it's
								// probably worth mentioning
								// list.add(rule);
								// Actually, maybe not
							}
						}
					}
				}
			}
			audits.put(auditType, list);
		}
	}

	/**
	 * Determine which categories should be on a given audit and add ones that
	 * aren't there and remove ones that shouldn't be there
	 * 
	 * @param conAudit
	 */
	private void fillAuditCategories() {
		// We're doing this step first so categories that get added or removed
		// manually can be caught in the next block

		if (auditType.getId() == AuditType.SHELL_COMPETENCY_REVIEW) {
			AuditCategory category = new AuditCategory();
			category.setName("Previewing Categories is not supported for this audit");
			categories.add(category);
			return;
		}

		auditType = auditTypeDAO.find(auditType.getId());

		List<AuditCategoryRule> rules = auditCategoryRuleCache.getApplicableCategoryRules(contractor, auditType);
		AuditCategoriesDetail detail = builder.getDetail(auditType, rules, operators);

		for (AuditCategory category : auditType.getTopCategories()) {
			addCategory(categories, category, detail);
		}
	}

	private void addCategory(List<AuditCategory> list, AuditCategory category, AuditCategoriesDetail detail) {
		if (detail.categories.contains(category)) {
			list.add(category);
			category.setSubCategories(new ArrayList<AuditCategory>());
			for (AuditCategory subCategory : auditType.getCategories()) {
				if (category.equals(subCategory.getParent())) {
					addCategory(category.getSubCategories(), subCategory, detail);
				}
			}
		}
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public Map<AuditType, List<AuditTypeRule>> getAudits() {
		return audits;
	}

	public AuditType getAuditType() {
		return auditType;
	}

	public void setAuditType(AuditType audit) {
		this.auditType = audit;
	}

	public void setOperatorIds(Set<Integer> operatorIds) {
		this.operatorIds = operatorIds;
	}

	public List<AuditCategory> getCategories() {
		return categories;
	}

}
