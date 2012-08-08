package com.picsauditing.actions.rules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public abstract class AuditRuleActionSupport<T extends AuditRule> extends PicsActionSupport implements Preparable {

	@Autowired
	protected AuditDecisionTableDAO dao;
	@Autowired
	protected OperatorAccountDAO operatorDAO;
	@Autowired
	protected OperatorTagDAO opTagDAO;
	@Autowired
	protected AuditTypeDAO auditTypeDAO;
	@Autowired
	protected OperatorTagDAO tagDAO;
	@Autowired
	protected AuditQuestionDAO questionDAO;
	@Autowired
	protected AppPropertyDAO appPropertyDAO;

	protected int id;
	protected String ruleType;
	protected String urlPrefix;

	protected Integer ruleAuditTypeId;
	protected Integer ruleOperatorAccountId;
	protected Integer ruleOperatorTagId;
	protected Integer ruleQuestionId;
	protected AccountLevel ruleAccountLevel;
	protected boolean ruleInclude = true;

	protected OpPerms requiredPermission;

	protected T rule;
	protected Date date = new Date();

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
				if (save())
					redirectTo();
				return REDIRECT;
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
		if (permissions.hasPermission(OpPerms.AuditRuleAdmin))
			return true;
		if (rule != null && rule.getId() > 0) {
			if (rule.getCreatedBy() != null && permissions.getUserId() == rule.getCreatedBy().getId())
				return true;
			OperatorAccount opAccount = rule.getOperatorAccount();
			if (opAccount != null) {
				if (permissions.isPicsEmployee() && (opAccount.isDemo() || opAccount.getStatus().isPending()))
					return true;

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
		} else if (permissions.hasPermission(requiredPermission, OpType.Edit)) {
			return true;
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
		if (permissions.hasPermission(OpPerms.AuditRuleAdmin))
			return false;
		return true;
	}

	abstract protected void clear();

	protected void saveFields() {
		rule.setInclude(ruleInclude);
		rule.setAccountLevel(ruleAccountLevel);
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

	protected abstract String redirectTo() throws IOException;

	protected abstract String onDeleteRedirectTo() throws IOException;

	public abstract boolean isAuditTypeRule();

	protected abstract T newRule();

	protected abstract boolean save();

	protected void delete() {
		rule.expire();
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

	public AccountLevel getRuleAccountLevel() {
		return ruleAccountLevel;
	}

	public void setRuleAccountLevel(AccountLevel ruleAccountLevel) {
		this.ruleAccountLevel = ruleAccountLevel;
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

	public Map<String, String> getAccountTypeList() {
		Map<String, String> map = new TreeMap<String, String>();
		map.put("", "Any");

		for (ContractorType type : ContractorType.values()) {
			map.put(type.name(), type.getType());
		}

		return map;
	}
}
