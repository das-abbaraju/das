package com.picsauditing.actions.rules;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.OpPerms;
import com.picsauditing.audits.AuditBuilderFactory;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.WorkflowStep;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;

@SuppressWarnings("serial")
public class AuditCategoryRuleEditor extends AuditRuleActionSupport<AuditCategoryRule> {

	protected Integer ruleAuditCategoryId;
	protected Integer ruleDependentAuditTypeId;

	@Autowired
	protected AuditCategoryDAO auditCategoryDAO;
    @Autowired
    private AuditBuilderFactory auditBuilderFactory;

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

	public LinkedHashSet<AuditStatus> getDependentAuditStatus() {
		LinkedHashSet<AuditStatus> set = new LinkedHashSet<AuditStatus>();

		AuditType auditType;

		if (getParameter("audit_id") > 0)
			auditType = auditTypeDAO.find(getParameter("audit_id"));
		else {
			auditType = rule.getDependentAuditType();
		}
		if (auditType == null)
			return set;
		for (WorkflowStep step : auditType.getWorkFlow().getSteps())
			set.add(step.getNewStatus());

		return set;
	}

	@Override
	protected String redirectTo() throws IOException {
		if (getActionErrors().size() > 0)
			ActionContext.getContext().getSession().put("actionErrors", getActionErrors());

		if (rule != null) {
			return this.setUrlForRedirect(urlPrefix + "RuleEditor.action?id=" + rule.getId());
		} else {
			return this.setUrlForRedirect("CategoryRuleSearch.action");
		}
	}

	@Override
	protected String onDeleteRedirectTo() throws IOException {
		String redirect = "";
		List<AuditCategoryRule> lessGranular = getLessGranular();
		if (lessGranular.size() > 1) {
			redirect = "CategoryRuleEditor.action?id=" + lessGranular.get(lessGranular.size() - 1).getId();
		} else {
			redirect = "CategoryRuleSearch.action?";
			if (rule.getAuditType() != null)
				redirect += "filter.auditType=" + rule.getAuditType().getName().toString();
			if (rule.getAuditCategory() != null)
				redirect += "filter.category=" + ((AuditCategoryRule) rule).getAuditCategory().getName();
		}

		return this.setUrlForRedirect(redirect);
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
        auditBuilderFactory.clearCache();
		flagClearCache();

		addActionMessage("Clearing Cache...");
	}

	@Override
	protected void saveFields() {
		super.saveFields();
		if (ruleAuditCategoryId != null) {
			rule.setAuditCategory(auditCategoryDAO.find(ruleAuditCategoryId));
		} else
			rule.setAuditCategory(null);
		if (ruleDependentAuditTypeId != null) {
			rule.setDependentAuditType(auditTypeDAO.find(ruleDependentAuditTypeId));
		} else {
			rule.setDependentAuditType(null);
			rule.setDependentAuditStatus(null);
		}
	}

	public String dependentAuditStatusSelect() {
		return "dependentAuditStatusSelect";
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

	public Integer getRuleDependentAuditTypeId() {
		return ruleDependentAuditTypeId;
	}

	public void setRuleDependentAuditTypeId(Integer ruleDependentAuditTypeId) {
		this.ruleDependentAuditTypeId = ruleDependentAuditTypeId;
	}
}
