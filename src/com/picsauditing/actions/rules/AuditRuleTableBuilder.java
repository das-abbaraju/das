package com.picsauditing.actions.rules;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.OperatorAccount;

@SuppressWarnings("serial")
public abstract class AuditRuleTableBuilder<T extends AuditRule> extends PicsActionSupport {

	protected AuditDecisionTableDAO ruleDAO;

	protected Integer id;
	protected String ruleType;
	protected String urlPrefix;

	protected boolean showPriority = true;
	protected boolean showWho = true;
	protected Map<String, Boolean> columnMap = new HashMap<String, Boolean>();
	protected List<T> rules = new ArrayList<T>();

	protected Date date = new Date();

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if ("DeleteRule".equals(button)) {
			if ("category".equals(ruleType)) {
				AuditCategoryRule acr = ruleDAO.findAuditCategoryRule(id);
				if (acr != null && this.isCanEditRule(acr))
					ruleDAO.remove(acr);
			} else if ("audittype".equals(ruleType)) {
				AuditTypeRule atr = ruleDAO.findAuditTypeRule(id);
				if (atr != null && this.isCanEditRule(atr))
					ruleDAO.remove(atr);
			}

			return SUCCESS;
		}

		setup();
		return SUCCESS;
	}

	public abstract void findRules();

	public void checkColumns(T rule) {
		if (rule.getAuditType() != null)
			columnMap.put("auditType", true);
		if (rule.getContractorType() != null)
			columnMap.put("contractorType", true);
		if (rule.getOperatorAccount() != null)
			columnMap.put("operatorAccount", true);
		if (rule.getRisk() != null)
			columnMap.put("risk", true);
		if (rule.getTag() != null)
			columnMap.put("tag", true);
		if (rule.getAcceptsBids() != null)
			columnMap.put("bidOnly", true);
		if (rule.getQuestion() != null)
			columnMap.put("question", true);
		if(rule.getTrade() != null)
			columnMap.put("trade", true);
		if (isCanEditRule(rule))
			columnMap.put("delete", true);

		if (showWho) {
			if (rule.getCreatedBy() != null)
				columnMap.put("createdBy", false);
			if (rule.getUpdatedBy() != null)
				columnMap.put("updatedBy", true);
		}
	}

	public void setup() {
		findRules();

		columnMap.put("include", true);

		if (showPriority)
			columnMap.put("priority", true);

		for (T rule : rules) {
			checkColumns(rule);
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getUrlPrefix() {
		return urlPrefix;
	}

	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}

	public void setShowPriority(boolean showPriority) {
		this.showPriority = showPriority;
	}

	public void setShowWho(boolean showWho) {
		this.showWho = showWho;
	}

	public Map<String, Boolean> getColumnMap() {
		return columnMap;
	}

	public List<T> getRules() {
		return rules;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * If user has a permission to edit the rule, created the rule, or is
	 * associated with the operator then they can edit the rule
	 * 
	 * @return
	 */
	public boolean isCanEditRule(AuditRule rule) {
		if (rule != null) {
			// If user has AuditRuleAdmin and rule is above threshold let user
			// modify rule
			if (permissions.hasPermission(OpPerms.AuditRuleAdmin)
					&& ((rule instanceof AuditCategoryRule && rule.getPriority() >= 300) || (rule instanceof AuditTypeRule && rule
							.getPriority() >= 230))) {
				return true;
			} else if (!permissions.hasPermission(OpPerms.AuditRuleAdmin)
					&& (permissions.hasPermission(OpPerms.ManageAuditTypeRules, OpType.Edit) || permissions
							.hasPermission(OpPerms.ManageCategoryRules, OpType.Edit))) {
				// Otherwise if the user has editing privileges and created
				// the rule or the rule falls within their scope of accounts
				// let them modify it
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
		}

		return false;
	}
}
