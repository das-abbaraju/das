package com.picsauditing.actions.rules;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.OpPerms;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.jpa.entities.AuditCategoryRule;

@SuppressWarnings("serial")
public class AuditCategoryRuleEditor extends AuditRuleActionSupport<AuditCategoryRule> {

	protected Integer ruleAuditCategoryId;

	@Autowired
	protected AuditCategoryDAO auditCategoryDAO;
	@Autowired
	protected AuditCategoryRuleCache auditCategoryRuleCache;

	public AuditCategoryRuleEditor() {
		this.requiredPermission = OpPerms.ManageCategoryRules;
		this.ruleType = "Category";
		this.urlPrefix = "Category";
	}

	@Override
	public void prepare() throws Exception {
		parameterCleanUp("rule.rootCategory");
		int ruleID = getParameter("id");
		if (ruleID > 0)
			rule = dao.findAuditCategoryRule(ruleID);
	}

	@Override
	protected AuditCategoryRule newRule() {
		return new AuditCategoryRule();
	}

	@Override
	public boolean isAuditTypeRule() {
		return false;
	}

	@Override
	protected void redirectTo() throws IOException {
		if (getActionErrors().size() > 0)
			ActionContext.getContext().getSession().put("actionErrors", getActionErrors());

		if (rule != null)
			this.redirect(urlPrefix + "RuleEditor.action?id=" + rule.getId());
		else
			this.redirect("CategoryRuleSearch.action");
	}

	@Override
	protected void onDeleteRedirectTo() throws IOException {
		String redirect = "";
		List<AuditCategoryRule> lessGranular = getLessGranular();
		if (lessGranular.size() > 1)
			redirect = "CategoryRuleEditor.action?id=" + lessGranular.get(lessGranular.size() - 1).getId();
		else {
			redirect = "CategoryRuleSearch.action?";
			if (rule.getAuditType() != null)
				redirect += "filter.auditType=" + rule.getAuditType().getName().toString();
			if (rule.getAuditCategory() != null)
				redirect += "filter.category=" + ((AuditCategoryRule) rule).getAuditCategory().getName();
		}
		this.redirect(redirect);
	}

	@Override
	protected void copy() {
		AuditCategoryRule ruleToSave = new AuditCategoryRule();
		ruleToSave.update(rule);
		ruleToSave.calculatePriority();
		ruleToSave.setAuditColumns(permissions);
		ruleToSave.defaultDates();
		dao.save(ruleToSave);
		rule = ruleToSave;
	}

	@Override
	protected boolean save() {
		saveFields();

		if (isOperatorRequired()) {
			if (rule.getOperatorAccount() == null) {
				addActionError("You must specify an operator for this rule");
				return false;
			}
		}
		if (rule.getId() == 0)
			rule.defaultDates();
		rule.calculatePriority();
		rule.setAuditColumns(permissions);
		dao.save(rule);
		clear();
		return true;
	}

	@Override
	protected void clear() {
		auditCategoryRuleCache.clear();
		clearAppProperties();
	}

	@Override
	protected void saveFields() {
		super.saveFields();
		if (ruleAuditCategoryId != null) {
			rule.setAuditCategory(auditCategoryDAO.find(ruleAuditCategoryId));
		} else
			rule.setAuditCategory(null);
	}

	public AuditCategoryRule getRule() {
		return rule;
	}

	public void setRule(AuditCategoryRule rule) {
		this.rule = rule;
	}

	@Override
	public List<AuditCategoryRule> getLessGranular() {
		return dao.getLessGranular(rule, date);
	}

	@Override
	public List<AuditCategoryRule> getMoreGranular() {
		return dao.getLessGranular(rule, date);
	}

	public Integer getRuleAuditCategoryId() {
		return ruleAuditCategoryId;
	}

	public void setRuleAuditCategoryId(Integer ruleAuditCategoryId) {
		this.ruleAuditCategoryId = ruleAuditCategoryId;
	}
}
