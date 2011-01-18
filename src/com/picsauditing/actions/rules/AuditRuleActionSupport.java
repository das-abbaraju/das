package com.picsauditing.actions.rules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.PICS.AuditTypeRuleCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public abstract class AuditRuleActionSupport<T extends AuditRule> extends PicsActionSupport implements Preparable {

	protected AuditDecisionTableDAO dao;
	protected OperatorAccountDAO operatorDAO;
	protected OperatorTagDAO opTagDAO;
	protected AuditTypeDAO auditTypeDAO;
	protected OperatorTagDAO tagDAO;
	protected AuditQuestionDAO questionDAO;
	protected AuditTypeRuleCache auditTypeRuleCache;
	protected AuditCategoryRuleCache auditCategoryRuleCache;

	protected int id;
	protected String ruleType;
	protected String urlPrefix;

	protected Integer ruleAuditTypeId;
	protected Integer ruleOperatorAccountId;
	protected Integer ruleOperatorTagId;
	protected Integer ruleQuestionId;
	protected Boolean ruleAcceptsBids;
	protected boolean ruleInclude = true;

	protected OpPerms requiredPermission;

	protected T rule;
	protected Date date = new Date();

	@Override
	public void prepare() throws Exception {
		// cleans up empty string to be null
		// probably not the best way but works for now.
		parameterCleanUp("ruleAcceptsBids");
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (ActionContext.getContext().getSession().containsKey("actionErrors")) {
			Collection<String> actionErrors = (Collection<String>) ActionContext.getContext().getSession()
					.get("actionErrors");
			ActionContext.getContext().getSession().remove("actionErrors");

			for (String error : actionErrors) {
				addActionError(error);
			}

			return SUCCESS;
		}

		if (button != null) {
			if ("New".equals(button)) {
				rule = newRule();
				saveFields();
				return SUCCESS;
			}
			if ("Clear".equals(button)) {
				clear();
			}
			if ("Save".equals(button)) {
				save();
				redirectTo();
				return BLANK;
			}
			if ("Delete".equals(button)) {
				delete();
				onDeleteRedirectTo();
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
			if (rule.getCreatedBy() != null && permissions.getUserId() == rule.getCreatedBy().getId())
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

	public List<BasicDynaBean> getOperatorList() {
		String where = "";
		if (permissions.hasGroup(User.GROUP_MARKETING))
			where = "a.id IN (SELECT accountID FROM account_user WHERE userID = " + permissions.getUserId() + ")";
		
		return operatorDAO.findWhereNatively(true, where);
	}

	public List<OperatorTag> getOperatorTagList() {
		List<OperatorTag> opTagList = new ArrayList<OperatorTag>();
		if (rule != null) {
			if (rule.getOperatorAccount() != null) {
				opTagList.addAll(opTagDAO.findByOperator(rule.getOperatorAccount().getId(), true));
			}
		}
		return opTagList;
	}

	protected boolean isOperatorRequired() {
		if (permissions.hasPermission(requiredPermission, OpType.Grant))
			return false;
		return true;
	}

	protected abstract void clear();

	protected void saveFields() {
		rule.setInclude(ruleInclude);
		rule.setAcceptsBids(ruleAcceptsBids);
		if (ruleAuditTypeId != null) {
			rule.setAuditType(auditTypeDAO.find(ruleAuditTypeId));
		} else
			rule.setAuditType(null);
		if (ruleOperatorAccountId != null) {
			rule.setOperatorAccount(operatorDAO.find(ruleOperatorAccountId));
		} else
			rule.setOperatorAccount(null);
		if (ruleOperatorTagId != null) {
			rule.setTag(tagDAO.find(ruleOperatorTagId));
		} else
			rule.setTag(null);
		if (ruleQuestionId != null) {
			rule.setQuestion(questionDAO.find(ruleQuestionId));
		} else
			rule.setQuestion(null);
	}

	protected abstract void redirectTo() throws IOException;

	protected abstract void onDeleteRedirectTo() throws IOException;

	public abstract boolean isAuditTypeRule();

	protected abstract T newRule();

	protected abstract void save();

	protected void delete() {
		rule.setExpirationDate(new Date());
		rule.setAuditColumns(permissions);
		dao.save(rule);
		clear();
	}

	protected abstract void copy();

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

	public Integer getRuleAuditTypeId() {
		return ruleAuditTypeId;
	}

	public void setRuleAuditTypeId(Integer ruleAuditTypeId) {
		this.ruleAuditTypeId = ruleAuditTypeId;
	}

	public Integer getRuleOperatorAccountId() {
		return ruleOperatorAccountId;
	}

	public void setRuleOperatorAccountId(Integer ruleOperatorAccountId) {
		this.ruleOperatorAccountId = ruleOperatorAccountId;
	}

	public Integer getRuleOperatorTagId() {
		return ruleOperatorTagId;
	}

	public void setRuleOperatorTagId(Integer ruleOperatorTagId) {
		this.ruleOperatorTagId = ruleOperatorTagId;
	}

	public Integer getRuleQuestionId() {
		return ruleQuestionId;
	}

	public void setRuleQuestionId(Integer ruleQuestionId) {
		this.ruleQuestionId = ruleQuestionId;
	}

	public Boolean getRuleAcceptsBids() {
		return ruleAcceptsBids;
	}

	public void setRuleAcceptsBids(Boolean ruleAcceptsBids) {
		this.ruleAcceptsBids = ruleAcceptsBids;
	}

	public String getUrlPrefix() {
		return urlPrefix;
	}

	public boolean isRuleInclude() {
		return ruleInclude;
	}

	public void setRuleInclude(boolean ruleInclude) {
		this.ruleInclude = ruleInclude;
	}

}
