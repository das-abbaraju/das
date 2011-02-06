package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.PICS.AuditTypeRuleCache;
import com.picsauditing.PICS.AuditBuilder.AuditCategoriesDetail;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;

@SuppressWarnings("serial")
public class ContractorSimulator extends PicsActionSupport {

	private ContractorAccount contractor;
	private Set<Integer> operatorIds;
	private List<OperatorAccount> operators;
	private List<AuditType> audits;
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
			OperatorAccount operator = new OperatorAccount();
			operator.setId(opID);
			operators.add(operator);
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
		audits = new ArrayList<AuditType>();
		List<AuditTypeRule> rules = auditTypeRuleCache.getApplicableAuditRules(contractor);
		audits.addAll(builder.calculateRequiredAuditTypes(rules, operators).keySet());
		// audits = auditTypeDAO.findWhere("id IN (1,2,3,4,5)");
		// Collections.sort(audits);
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

	public List<AuditType> getAudits() {
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
