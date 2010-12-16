package com.picsauditing.actions.rules;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;

import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.PICS.AuditTypeRuleCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.WorkflowStep;

@SuppressWarnings("serial")
public class AuditTypeRuleEditor extends AuditRuleActionSupport<AuditTypeRule> {

	public AuditTypeRuleEditor(AuditDecisionTableDAO dao, OperatorAccountDAO opDAO, AuditTypeDAO auditTypeDAO,
			OperatorTagDAO tagDAO, AuditTypeRuleCache auditTypeRuleCache, AuditCategoryRuleCache auditCategoryRuleCache) {
		this.dao = dao;
		this.opDAO = opDAO;
		this.auditTypeDAO = auditTypeDAO;
		this.tagDAO = tagDAO;
		this.auditTypeRuleCache = auditTypeRuleCache;
		this.auditCategoryRuleCache = auditCategoryRuleCache;

		this.requiredPermission = OpPerms.ManageAuditTypeRules;

		this.ruleType = "Audit Type";
	}

	public LinkedHashSet<AuditStatus> getDependentAuditStatus() {
		LinkedHashSet<AuditStatus> set = new LinkedHashSet<AuditStatus>();
		if (rule.getDependentAuditType() != null) {
			for (WorkflowStep step : rule.getDependentAuditType().getWorkFlow().getSteps())
				set.add(step.getNewStatus());
		}
		return set;
	}

	@Override
	public void prepare() throws Exception {
		int ruleID = getParameter("id");
		if (ruleID > 0)
			rule = dao.findAuditTypeRule(ruleID);
	}

	@Override
	public boolean isAuditTypeRule() {
		return true;
	}

	@Override
	protected void redirectTo() throws IOException {
		this.redirect("AuditTypeRuleEditor.action?id=" + id);
	}

	@Override
	protected void copy() {
	}

	@Override
	protected void delete() {
	}

	@Override
	protected void edit() {
	}

	@Override
	protected void save() {
	}

	@Override
	public AuditTypeRule getRule() {
		return rule;
	}

	@Override
	public void setRule(AuditTypeRule rule) {
		this.setRule(rule);
	}

	@Override
	public List<AuditTypeRule> getLessGranular() {
		return dao.getLessGranular(rule, date);
	}

	@Override
	public List<AuditTypeRule> getMoreGranular() {
		return dao.getLessGranular(rule, date);
	}
}
