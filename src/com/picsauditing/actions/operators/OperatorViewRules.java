package com.picsauditing.actions.operators;

import java.util.List;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditRule;

@SuppressWarnings("serial")
public class OperatorViewRules extends OperatorActionSupport {
	
	protected List<? extends AuditRule> relatedRules;
	protected boolean categoryRule;	
	protected String type = "";
	
	protected AuditDecisionTableDAO dao;

	public OperatorViewRules(OperatorAccountDAO operatorDao, AuditDecisionTableDAO dao) {
		super(operatorDao);
		this.dao = dao;
	}
	
	@Override
	public String execute() throws Exception {
		if(!forceLogin())
			return LOGIN;
		
		operator = operatorDao.find(id);
		if(operator==null){
			addActionError("Error with Operator");
			return SUCCESS;
		}			
		
		if("category".equals(type)){
			if(!permissions.hasPermission(OpPerms.ManageCategoryRules))
				throw new NoRightsException(OpPerms.ManageCategoryRules, OpType.View);
			categoryRule = true;
			relatedRules = dao.findAuditCategoryRulesByOperator(id);
		} else if("auditType".equals(type)){
			if(!permissions.hasPermission(OpPerms.ManageCategoryRules))
				throw new NoRightsException(OpPerms.ManageCategoryRules, OpType.View);
			categoryRule = false;
			relatedRules = dao.findAuditTypeRulesByOperator(id);
		}
		return SUCCESS;
	}

	public List<? extends AuditRule> getRelatedRules() {
		return relatedRules;
	}

	public void setRelatedRules(List<? extends AuditRule> relatedRules) {
		this.relatedRules = relatedRules;
	}

	public boolean isCategoryRule() {
		return categoryRule;
	}

	public void setCategoryRule(boolean categoryRule) {
		this.categoryRule = categoryRule;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
