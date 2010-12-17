package com.picsauditing.actions.rules;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;

import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.PICS.AuditTypeRuleCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.WorkflowStep;

@SuppressWarnings("serial")
public class AuditTypeRuleEditor extends AuditRuleActionSupport<AuditTypeRule> {

	protected Integer ruleDependentAuditTypeId;

	public AuditTypeRuleEditor(AuditDecisionTableDAO dao, OperatorAccountDAO opDAO, AuditTypeDAO auditTypeDAO,
			OperatorTagDAO tagDAO, AuditQuestionDAO questionDAO, AuditTypeRuleCache auditTypeRuleCache,
			AuditCategoryRuleCache auditCategoryRuleCache) {
		this.dao = dao;
		this.operatorDAO = opDAO;
		this.auditTypeDAO = auditTypeDAO;
		this.tagDAO = tagDAO;
		this.questionDAO = questionDAO;
		this.auditTypeRuleCache = auditTypeRuleCache;
		this.auditCategoryRuleCache = auditCategoryRuleCache;

		this.requiredPermission = OpPerms.ManageAuditTypeRules;
		this.ruleType = "Audit Type";
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
		if (rule!=null && rule.getDependentAuditType() != null) {
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
		if(rule!=null)
			this.redirect("AuditTypeRuleEditor.action?id=" + rule.getId());
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
			redirect = "AuditTypeRuleSearch.action";
			if(rule.getAuditType()!=null)
				redirect += "filter.auditType=" + rule.getAuditType().getAuditName();
		}
		this.redirect(redirect);
	}

	@Override
	protected void copy() {
	}

	@Override
	protected void edit() {
	}

	@Override
	protected void save() {
		if (isOperatorRequired()) {
			if (rule.getOperatorAccount() == null) {
				addActionError("You must specify an operator for this rule");
				return;
			}
		}
		saveFields();
		if (rule.getId() == 0) {
			rule.defaultDates();
			rule.calculatePriority();
			rule.setAuditColumns(permissions);
			dao.save(rule);
		} else {
			rule.calculatePriority();
			rule.setAuditColumns(permissions);
			dao.save(rule);
		}
	}

	@Override
	protected void saveFields() {
		super.saveFields();
		if (ruleDependentAuditTypeId != null) {
			rule.setDependentAuditType(auditTypeDAO.find(ruleDependentAuditTypeId));
		} else
			rule.setDependentAuditType(null);
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
