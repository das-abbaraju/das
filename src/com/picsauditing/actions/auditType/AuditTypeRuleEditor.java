package com.picsauditing.actions.auditType;

import java.util.Date;
import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditTypeRule;

@SuppressWarnings("serial")
public class AuditTypeRuleEditor extends PicsActionSupport {
	
	private int id = 0;
	private boolean categoryRule = false;

	private AuditTypeRule rule = null;
	private List<AuditTypeRule> lessGranular;
	private List<AuditTypeRule> moreGranular;
	private List<AuditCategoryRule> similar;

	private AuditDecisionTableDAO dao;

	public AuditTypeRuleEditor(AuditDecisionTableDAO auditDecisionTableDAO) {
		this.dao = auditDecisionTableDAO;
	}

	public String execute() throws Exception {

		if (id == 0)
			return BLANK;

		rule = dao.findAuditTypeRule(id);

		if (button != null) {
			// TODO handle things like merge and delete right here
		}

		lessGranular = dao.getLessGranular(rule, new Date());
		moreGranular = dao.getMoreGranular(rule, new Date());
		//similar = dao.getSimilar(rule, new Date());

		return SUCCESS;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AuditTypeRule getRule() {
		return rule;
	}

	public void setRule(AuditTypeRule rule) {
		this.rule = rule;
	}

	public List<AuditTypeRule> getLessGranular() {
		return lessGranular;
	}

	public List<AuditTypeRule> getMoreGranular() {
		return moreGranular;
	}

	public boolean isCategoryRule() {
		return categoryRule;
	}

	public void setCategoryRule(boolean categoryRule) {
		this.categoryRule = categoryRule;
	}

	/*public List<AuditTypeRule> getSimilar() {
		return similar;
	}*/

}
