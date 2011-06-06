package com.picsauditing.actions.rules;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.WorkflowStep;

@SuppressWarnings("serial")
public class AuditTypeRuleEditor extends AuditRuleActionSupport<AuditTypeRule> {

	protected Integer ruleDependentAuditTypeId;
	@Autowired
	protected AuditTypeRuleCache auditTypeRuleCache;

	public AuditTypeRuleEditor() {
		this.requiredPermission = OpPerms.ManageAuditTypeRules;
		this.ruleType = "Audit Type";
		this.urlPrefix = "AuditType";
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		int ruleID = getParameter("id");
		if (ruleID > 0)
			rule = dao.findAuditTypeRule(ruleID);
	}

	@Override
	protected AuditTypeRule newRule() {
		return new AuditTypeRule();
	}

	public LinkedHashSet<AuditStatus> getDependentAuditStatus() {
		LinkedHashSet<AuditStatus> set = new LinkedHashSet<AuditStatus>();
		if (rule != null && rule.getDependentAuditType() != null) {
			for (WorkflowStep step : rule.getDependentAuditType().getWorkFlow().getSteps())
				set.add(step.getNewStatus());
		}
		return set;
	}

	@Override
	public boolean isAuditTypeRule() {
		return true;
	}

	@Override
	protected void redirectTo() throws IOException {
		if (rule != null)
			this.redirect(urlPrefix + "RuleEditor.action?id=" + rule.getId());
		else
			this.redirect("AuditTypeRuleSearch.action");
	}

	@Override
	protected void onDeleteRedirectTo() throws IOException {
		String redirect = "";
		List<AuditTypeRule> lessGranular = getLessGranular();
		if (lessGranular.size() > 1)
			redirect = "AuditTypeRuleEditor.action?id=" + lessGranular.get(lessGranular.size() - 1).getId();
		else {
			redirect = "AuditTypeRuleSearch.action?";
			if (rule.getAuditType() != null)
				redirect += "filter.auditType=" + rule.getAuditType().getName().toString();
		}
		this.redirect(redirect);
	}

	@Override
	protected void copy() {
		AuditTypeRule ruleToSave = new AuditTypeRule();
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
		auditTypeRuleCache.clear();
		clearAppProperties();
	}

	@Override
	protected void saveFields() {
		super.saveFields();
		if (ruleDependentAuditTypeId != null) {
			rule.setDependentAuditType(auditTypeDAO.find(ruleDependentAuditTypeId));
		} else
			rule.setDependentAuditType(null);
	}

	public AuditTypeRule getRule() {
		return rule;
	}

	public void setRule(AuditTypeRule rule) {
		this.rule = rule;
	}

	@Override
	public List<AuditTypeRule> getLessGranular() {
		return dao.getLessGranular(rule, date);
	}

	@Override
	public List<AuditTypeRule> getMoreGranular() {
		return dao.getLessGranular(rule, date);
	}

	public Integer getRuleDependentAuditTypeId() {
		return ruleDependentAuditTypeId;
	}

	public void setRuleDependentAuditTypeId(Integer ruleDependentAuditTypeId) {
		this.ruleDependentAuditTypeId = ruleDependentAuditTypeId;
	}
}
