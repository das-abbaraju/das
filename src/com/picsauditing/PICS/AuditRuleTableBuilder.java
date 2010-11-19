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
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.DoubleMap;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AuditRuleTableBuilder extends PicsActionSupport {
	protected AuditDecisionTableDAO adtDAO;
	protected AuditCategoryDAO catDAO;
	protected AuditTypeDAO typeDAO;
	protected OperatorAccountDAO opDAO;

	protected List<Integer> ruleIDs;
	protected List<AuditRuleColumn> columns;
	protected DoubleMap<Integer, AuditRuleColumn, List<String>> map;

	protected boolean showPriority = true;
	protected boolean showWho = true;
	protected boolean excluded = false;
	protected boolean checkCat = false;
	protected int auditTypeID;
	protected int categoryID;
	protected String type = "AuditType";
	protected String where;
	protected OperatorAccount operator;

	public AuditRuleTableBuilder(AuditDecisionTableDAO adtDAO, AuditCategoryDAO catDAO, AuditTypeDAO typeDAO,
			OperatorAccountDAO opDAO) {
		this.adtDAO = adtDAO;
		this.catDAO = catDAO;
		this.typeDAO = typeDAO;
		this.opDAO = opDAO;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		List<? extends AuditRule> rules = null;

		if (operator != null && operator.getId() > 0) {
			operator = opDAO.find(operator.getId());

			if (excluded) {
				if (Strings.isEmpty(where))
					where = "";
				else
					where = " AND " + where;

				if (type.equals("AuditType"))
					rules = adtDAO.findAuditTypeRulesByOperator(operator.getId(), "r.include = 0" + where);
				else
					rules = adtDAO.findAuditCategoryRulesByOperator(operator.getId(), "r.include = 0" + where);
			}

			if (auditTypeID > 0) {
				AuditType a = typeDAO.find(auditTypeID);
				rules = adtDAO.findByAuditType(a, operator);
				if(checkCat){
					for(AuditCategory cat : catDAO.findByAuditTypeID(auditTypeID)){
						if(cat.getName().equals(operator.getName())){
							checkCat = false;
							break;
						}
					}
				}
			}

			if (categoryID > 0) {
				type = "Category";
				AuditCategory c = catDAO.find(categoryID);
				rules = adtDAO.findByCategory(c, operator);
			}
		}

		if (rules != null)
			setup(rules);

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

	public boolean isExcluded() {
		return excluded;
	}

	public void setExcluded(boolean excluded) {
		this.excluded = excluded;
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

	public void setType(String type) {
		this.type = type;
	}

	public void setWhere(String where) {
		this.where = where;
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

	public boolean isCheckCat() {
		return checkCat;
	}

	public void setCheckCat(boolean checkCat) {
		this.checkCat = checkCat;
	}
}