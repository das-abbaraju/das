package com.picsauditing.actions.rules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.PICS.AuditTypeRuleCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;

@SuppressWarnings("serial")
public abstract class AuditRuleActionSupport<T extends AuditRule> extends PicsActionSupport implements Preparable {

	protected AuditDecisionTableDAO dao;
	protected OperatorAccountDAO opDAO;
	protected AuditTypeDAO auditTypeDAO;
	protected OperatorTagDAO tagDAO;
	protected AuditTypeRuleCache auditTypeRuleCache;
	protected AuditCategoryRuleCache auditCategoryRuleCache;

	protected int id;
	protected String ruleType;

	protected OpPerms requiredPermission;

	protected T rule;
	protected Date date = new Date();

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (rule == null)
			throw new RecordNotFoundException("Rule " + id);

		if (button != null) {
			if ("Clear".equals(button)) {
				clear();
			}
			if ("Edit".equals(button)) {
				edit();
			}
			if ("Save".equals(button)) {
				save();
				redirectTo();
				return BLANK;
			}
			if ("Delete".equals(button)) {
				delete();
				redirectTo();
				return BLANK;
			}
			if ("Copy".equals(button)) {
				copy();
				redirectTo();
				return BLANK;
			}
		}
		return SUCCESS;
	}

	/**
	 * If user has a permission to edit the rule, created the rule, or is
	 * associated with the operator then they can edit the rule
	 * 
	 * @return
	 */
	public boolean isCanEditRule() {
		if (permissions.hasPermission(requiredPermission, OpType.Edit))
			return true;
		if (rule != null) {
			if (permissions.getUserId() == rule.getCreatedBy().getId())
				return true;
			OperatorAccount opAccount = rule.getOperatorAccount();
			if (opAccount != null) {
				for (AccountUser accUser : opAccount.getAccountUsers()) {
					if (accUser.getUser().getId() == permissions.getUserId())
						return true;
				}
				for (OperatorAccount child : opAccount.getOperatorChildren()) {
					for (AccountUser childAccUser : child.getAccountUsers()) {
						if (childAccUser.getUser().getId() == permissions.getUserId())
							return true;
					}
				}
			}
		}
		return false;
	}

	public Map<AuditTypeClass, List<AuditType>> getAuditTypeMap() {
		return auditTypeDAO.getAuditTypeMap();
	}

	public List<OperatorAccount> getOperatorList() {
		return opDAO.findWhere(true, "", permissions);
	}

	public List<OperatorTag> getOperatorTagList() {
		List<OperatorTag> opTagList = new ArrayList<OperatorTag>();
		if (rule.getOperatorAccount() != null && rule.getOperatorAccount().getTags() != null) {
			for (OperatorTag ot : rule.getOperatorAccount().getTags())
				opTagList.add(ot);
		}
		return opTagList;
	}

	protected boolean isOperatorRequired() {
		if (permissions.hasPermission(requiredPermission, OpType.Grant))
			return false;
		return true;
	}

	protected void clear() {
		auditTypeRuleCache.clear();
		auditCategoryRuleCache.clear();
		addActionMessage("Cleared Category and Audit Type Cache.");
	}

	protected abstract void redirectTo() throws IOException;

	public abstract boolean isAuditTypeRule();

	protected abstract void edit();

	protected abstract void save();

	protected abstract void delete();

	protected abstract void copy();

	public abstract T getRule();

	public abstract void setRule(T rule);

	public abstract List<T> getLessGranular();

	public abstract List<T> getMoreGranular();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRuleType() {
		return ruleType;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
