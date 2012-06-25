package com.picsauditing.actions.rules;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.WorkflowStep;

@SuppressWarnings("serial")
public class AuditTypeRuleEditor extends AuditRuleActionSupport<AuditTypeRule> {

	protected Integer ruleDependentAuditTypeId;
	@Autowired
	protected AuditTypeRuleCache auditTypeRuleCache;
	@Autowired
	protected AuditTypeDAO auditTypeDAO;

	public AuditTypeRuleEditor() {
		this.requiredPermission = OpPerms.ManageAuditTypeRules;
		this.ruleType = "Audit Type";
		this.urlPrefix = "AuditType";
	}

	@Override
	public void prepare() throws Exception {
		int ruleID = getParameter("id");
		if (ruleID > 0)
			rule = dao.findAuditTypeRule(ruleID);
	}

	public String dependentAuditStatusSelect() {
		return "dependentAuditStatusSelect";
	}

	@Override
	protected AuditTypeRule newRule() {
		return new AuditTypeRule();
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
	public boolean isAuditTypeRule() {
		return true;
	}

	@Override
	protected String redirectTo() throws IOException {
		if (rule != null) {
			return this.setUrlForRedirect(urlPrefix + "RuleEditor.action?id=" + rule.getId());
		} else {
			return this.setUrlForRedirect("AuditTypeRuleSearch.action");
		}
	}

	@Override
	protected String onDeleteRedirectTo() throws IOException {
		String redirect = "";
		List<AuditTypeRule> lessGranular = getLessGranular();

		if (lessGranular.size() > 1) {
			redirect = "AuditTypeRuleEditor.action?id=" + lessGranular.get(lessGranular.size() - 1).getId();
		} else {
			redirect = "AuditTypeRuleSearch.action?";
			if (rule.getAuditType() != null)
				redirect += "filter.auditType=" + rule.getAuditType().getName().toString();
		}

		return this.setUrlForRedirect(redirect);
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
		flagClearCache();

		addActionMessage("Clearing Cache...");
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
