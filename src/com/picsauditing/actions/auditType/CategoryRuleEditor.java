package com.picsauditing.actions.auditType;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class CategoryRuleEditor extends PicsActionSupport {

	private int id = 0;
	private boolean categoryRule = true;

	private AuditCategoryRule rule = null;
	private List<AuditCategoryRule> lessGranular;
	private List<AuditCategoryRule> moreGranular;
	private List<AuditCategoryRule> similar;
	private Date date = new Date();

	private AuditDecisionTableDAO dao;

	private Map<String, Map<String, String>> columns = new LinkedHashMap<String, Map<String, String>>();

	public CategoryRuleEditor(AuditDecisionTableDAO auditDecisionTableDAO) {
		this.dao = auditDecisionTableDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (id == 0)
			return BLANK;

		if (rule == null) {
			if (id == 0)
				return SUCCESS;
			rule = dao.findAuditCategoryRule(id);
		}

		addFields();

		if (button != null) {
			if ("new".equals(button)) {
				rule.defaultDates();
				rule.calculatePriority();
				rule.setAuditColumns(permissions);
				dao.save(rule);
				this.redirect("CategoryRuleEditor.action?id=" + rule.getId());
				return BLANK;
			}
			if ("create".equals(button)) {
				AuditCategoryRule source = dao.findAuditCategoryRule(id);
				rule.merge(source);
				rule.defaultDates();
				rule.calculatePriority();
				rule.setAuditColumns(permissions);
				dao.save(rule);
				this.redirect("CategoryRuleEditor.action?id=" + rule.getId());
				return BLANK;
			}
			if ("delete".equals(button)) {
				String redirect = "";
				List<AuditCategoryRule> lGranular = dao.getLessGranular(rule, date);
				if (lGranular.size() > 0)
					redirect = "CategoryRuleEditor.action?id=" + lGranular.get(lGranular.size() - 1).getId();
				else {
					redirect = "CategoryRuleSearch.action";
					if (rule.getAuditCategory() != null)
						redirect += "filter.category=" + rule.getAuditCategory().getName() + "&";
					if (rule.getAuditType() != null)
						redirect += "filter.auditType=" + rule.getAuditType().getAuditName() + "&";
				}

				rule.setExpirationDate(new Date());
				rule.setAuditColumns(permissions);
				dao.save(rule);
				this.redirect(redirect);
				return BLANK;
			}
			if ("deleteChildren".equals(button)) {
				int count = dao.deleteChildren(rule, permissions);
				addActionMessage("Archived " + count + (count == 1 ? " rule" : " rules"));
			}
		}

		lessGranular = dao.getLessGranular(rule, date);
		moreGranular = dao.getMoreGranular(rule, date);
		// similar = dao.getSimilar(rule, new Date());
		return SUCCESS;
	}

	private void addFields() {
		// include
		columns.put("include", null);
		// audit_type
		columns.put("audit_type", null);
		// category
		if (rule.getAuditCategory() == null) {
			Map<String, String> m = new HashMap<String, String>();
			m.put("catID", "rule.auditCategory.id=");
			columns.put("category", m);
		} else
			columns.put("category", null);
		// account
		columns.put("account", null);
		// operator
		if (rule.getOperatorAccount() == null) {
			Map<String, String> m = new HashMap<String, String>();
			m.put("opID", "rule.operatorAccount.id=");
			columns.put("operator", m);
		} else
			columns.put("operator", null);
		// risk
		if (rule.getRisk() == null) {
			Map<String, String> m = new HashMap<String, String>();
			m.put("risk", "rule.risk=");
			columns.put("risk", m);
		} else
			columns.put("risk", null);
		// tag
		if (rule.getTag() == null) {
			Map<String, String> m = new HashMap<String, String>();
			m.put("tagID", "rule.tag.id=");
			columns.put("tag", m);
		} else
			columns.put("tag", null);
		// bid-onl7
		columns.put("bid", null);
		// question
		columns.put("question", null);
		// comp
		columns.put("comp", null);
		// answer
		columns.put("answer", null);
	}

	public List<BasicDynaBean> getPercentOn(String field) throws SQLException {
		Database db = new Database();
		SelectSQL sql = new SelectSQL("audit_category_rule");
		if (rule.getAuditType() != null)
			sql.addWhere("auditTypeID = " + rule.getAuditType().getId());
		if (rule.getAuditCategory() != null)
			sql.addWhere("catID = " + rule.getAuditCategory().getId());
		if (rule.getOperatorAccount() != null)
			sql.addWhere("opID = " + rule.getOperatorAccount().getId());
		if (rule.getRisk() != null)
			sql.addWhere("risk = " + rule.getRisk().ordinal());
		if (rule.getTag() != null)
			sql.addWhere("tagID = " + rule.getTag().getId());
		if (rule.getQuestion() != null)
			sql.addWhere("questionID = " + rule.getQuestion().getId());
		if (rule.getContractorType() != null)
			sql.addWhere("contractorType = '" + rule.getContractorType() + "'");
		if (rule.getAcceptsBids() != null)
			sql.addWhere("acceptsBids = " + (rule.getAcceptsBids() ? 1 : 0));

		sql.addWhere("id <> " + id);
		sql.addWhere(field + " IS NOT NULL");
		sql.addWhere("effectiveDate <= NOW() AND expirationDate > NOW()");
		sql.addGroupBy(field);
		sql.setHavingClause("COUNT(*) > 1");

		if ("risk".equals(field))
			sql.addField("CASE risk WHEN 1 THEN 'Low' WHEN 2 THEN 'Med' WHEN 3 THEN 'High' ELSE NULL END AS risk");
		else
			sql.addField(field);
		sql.addField("SUM(include) includeTotal");
		sql.addField("COUNT(*) total");
		sql.addField("SUM(include)/COUNT(*) percentOn");

		sql.addOrderBy("percentOn DESC");

		return db.select(sql.toString(), false);
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

	public boolean isCategoryRule() {
		return categoryRule;
	}

	public void setCategoryRule(boolean categoryRule) {
		this.categoryRule = categoryRule;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(String dateString) {
		this.date = DateBean.parseDate(dateString);
	}

	public Map<String, Map<String, String>> getColumns() {
		return columns;
	}

	public void setColumns(Map<String, Map<String, String>> columns) {
		this.columns = columns;
	}

}
