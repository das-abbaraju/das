package com.picsauditing.actions.rules;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.OperatorAccount;

@SuppressWarnings("serial")
public abstract class AuditRuleTableBuilder<T extends AuditRule> extends PicsActionSupport {

	@Autowired
	protected AuditDecisionTableDAO ruleDAO;
	@Autowired
	protected OperatorAccountDAO operatorDAO;
	@Autowired
	protected ContractorAccountDAO contractorDAO;
	@Autowired
	protected OperatorTagDAO operatorTagDAO;
	@Autowired
	protected TradeDAO tradeDAO;
	@Autowired
	protected AuditDecisionTableDAO auditRuleDAO;

	protected Integer id;
	protected String ruleType;
	protected String urlPrefix;
	protected int conID;

	protected boolean showPriority = true;
	protected boolean showWho = true;
	protected Map<String, Boolean> columnMap = new HashMap<String, Boolean>();
	protected String[] columnsIgnore = new String[0];
	protected List<T> rules = new ArrayList<T>();

	protected Date date = new Date();

	@Override
	public final String execute() throws Exception {
		setup();
		removeColumns();
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
		if (rule.getSafetyRisk() != null)
			columnMap.put("safetyRisk", true);
		if (rule.getProductRisk() != null)
			columnMap.put("productRisk", true);
		if (rule.getTag() != null)
			columnMap.put("tag", true);
		if (rule.getAccountLevel() != null)
			columnMap.put("accountLevel", true);
		if (rule.getQuestion() != null)
			columnMap.put("question", true);
		if (rule.getTrade() != null)
			columnMap.put("trade", true);
		if (rule.getSoleProprietor() != null)
			columnMap.put("soleProprietor", true);
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

	public void removeColumns() {
		for (String column : columnsIgnore) {
			if (columnMap.containsKey(column))
				columnMap.remove(column);
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

	public String[] getColumnsIgnore() {
		return columnsIgnore;
	}

	public void setColumnsIgnore(String[] columnsIgnore) {
		this.columnsIgnore = columnsIgnore;
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
	
	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	/**
	 * TODO This should be inheriting from AuditRuleActionSupport to get access
	 * to the same method.
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
