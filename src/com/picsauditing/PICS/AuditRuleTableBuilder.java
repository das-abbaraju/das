package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.auditType.AuditRuleColumn;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class AuditRuleTableBuilder extends PicsActionSupport {
	protected AuditDecisionTableDAO adtDAO;
	protected AuditCategoryDAO catDAO;
	protected OperatorAccountDAO opDAO;

	protected List<Integer> ruleIDs;
	protected List<AuditRuleColumn> columns;
	protected DoubleMap<Integer, AuditRuleColumn, List<String>> map;

	protected boolean showPriority = true;
	protected boolean showWho = true;
	protected int auditTypeID;
	protected int categoryID;
	protected String type = "AuditType";
	protected OperatorAccount operator;

	public AuditRuleTableBuilder(AuditDecisionTableDAO adtDAO, AuditCategoryDAO catDAO, OperatorAccountDAO opDAO) {
		this.adtDAO = adtDAO;
		this.catDAO = catDAO;
		this.opDAO = opDAO;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (operator != null && operator.getId() > 0) {
			operator = opDAO.find(operator.getId());

			if (auditTypeID > 0) {
				AuditType a = new AuditType();
				a.setId(auditTypeID);
				List<AuditTypeRule> rules = adtDAO.findByAuditType(a, operator);

				setup(rules);
			}

			if (categoryID > 0) {
				type = "Category";
				AuditCategory c = catDAO.find(categoryID);
				List<AuditCategoryRule> rules = adtDAO.findByCategory(c, operator);

				setup(rules);
			}
		}

		return SUCCESS;
	}

	public List<Integer> getRuleIDs() {
		return ruleIDs;
	}

	public List<AuditRuleColumn> getColumns() {
		return columns;
	}

	public DoubleMap<Integer, AuditRuleColumn, List<String>> getMap() {
		return map;
	}

	public boolean isShowPriority() {
		return showPriority;
	}

	public void setShowPriority(boolean showPriority) {
		this.showPriority = showPriority;
	}

	public boolean isShowWho() {
		return showWho;
	}

	public void setShowWho(boolean showWho) {
		this.showWho = showWho;
	}

	public int getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
	}

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	public String getType() {
		return type;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	private void setup(List<? extends AuditRule> rules) {
		ruleIDs = new ArrayList<Integer>();
		columns = new ArrayList<AuditRuleColumn>();
		Set<AuditRuleColumn> usedColumns = new HashSet<AuditRuleColumn>();
		map = new DoubleMap<Integer, AuditRuleColumn, List<String>>();

		for (AuditRule rule : rules) {
			ruleIDs.add(rule.getId());
			Map<AuditRuleColumn, List<String>> values = rule.getMapping();

			for (AuditRuleColumn c : values.keySet()) {
				if ((!showPriority && c.equals(AuditRuleColumn.Priority))
						|| (!showWho && (c.equals(AuditRuleColumn.CreatedBy) || c.equals(AuditRuleColumn.UpdatedBy))))
					continue;

				if (values.get(c).size() > 0) {
					map.put(rule.getId(), c, values.get(c));
					usedColumns.add(c);
				}
			}
		}

		columns.addAll(usedColumns);
		Collections.sort(columns);
	}
}
