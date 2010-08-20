package com.picsauditing.actions.auditType;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditCategoryRule;

@SuppressWarnings("serial")
public class CategoryRuleEditor extends PicsActionSupport {

	private int id = 0;

	private AuditCategoryRule rule = null;
	private List<AuditCategoryRule> lessGranular;
	private List<AuditCategoryRule> moreGranular;
	private List<AuditCategoryRule> similar;

	private AuditDecisionTableDAO dao;

	public CategoryRuleEditor(AuditDecisionTableDAO auditDecisionTableDAO) {
		this.dao = auditDecisionTableDAO;
	}

	public String execute() throws Exception {

		if (id == 0)
			return BLANK;

		rule = dao.findAuditCategoryRule(id);
		
		if (button != null) {
			// TODO handle things like merge and delete right here
		}
		
		lessGranular = dao.getLessGranular(rule);
		moreGranular = dao.getMoreGranular(rule);
		similar = dao.getSimilar(rule);

		return SUCCESS;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AuditCategoryRule getRule() {
		return rule;
	}

	public void setRule(AuditCategoryRule rule) {
		this.rule = rule;
	}

	public List<AuditCategoryRule> getLessGranular() {
		return lessGranular;
	}

	public List<AuditCategoryRule> getMoreGranular() {
		return moreGranular;
	}

	public List<AuditCategoryRule> getSimilar() {
		return similar;
	}

}
