package com.picsauditing.actions.rules;

import java.io.IOException;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.PICS.AuditTypeRuleCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.AuditCategoryRule;

@SuppressWarnings("serial")
public class AuditCategoryRuleEditor extends AuditRuleActionSupport<AuditCategoryRule> {

	public AuditCategoryRuleEditor(AuditDecisionTableDAO dao, OperatorAccountDAO opDAO, AuditTypeDAO auditTypeDAO,
			OperatorTagDAO tagDAO, AuditTypeRuleCache auditTypeRuleCache, AuditCategoryRuleCache auditCategoryRuleCache) {
		this.dao = dao;
		this.opDAO = opDAO;
		this.auditTypeDAO = auditTypeDAO;
		this.tagDAO = tagDAO;
		this.auditTypeRuleCache = auditTypeRuleCache;
		this.auditCategoryRuleCache = auditCategoryRuleCache;

		this.requiredPermission = OpPerms.ManageCategoryRules;
		this.ruleType = "Audit Category";
	}

	@Override
	public void prepare() throws Exception {
		int ruleID = getParameter("id");
		if (ruleID > 0)
			rule = dao.findAuditCategoryRule(ruleID);
	}

	@Override
	public boolean isAuditTypeRule() {
		return false;
	}

	@Override
	protected void redirectTo() throws IOException {
		this.redirect("AuditCategoryRuleEditor.action?id=" + id);
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
	public AuditCategoryRule getRule() {
		return rule;
	}

	@Override
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
}
