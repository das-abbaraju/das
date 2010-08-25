package com.picsauditing.actions.auditType;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

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

	public CategoryRuleEditor(AuditDecisionTableDAO auditDecisionTableDAO) {
		this.dao = auditDecisionTableDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (rule == null) {
			if (id == 0)
				return SUCCESS;
			rule = dao.findAuditCategoryRule(id);
		}

		if (button != null) {
			if ("new".equals(button)) {
				rule.setEffectiveDate(new Date());
				rule.calculatePriority();
				rule.setAuditColumns(permissions);
				dao.save(rule);
				this.redirect("CategoryRuleEditor.action?id=" + rule.getId());
				return BLANK;
			}
			if ("create".equals(button)) {
				AuditCategoryRule source = dao.findAuditCategoryRule(id);
				rule.merge(source);
				rule.setEffectiveDate(new Date());
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
		}

		lessGranular = dao.getLessGranular(rule, date);
		moreGranular = dao.getMoreGranular(rule, date);
		// similar = dao.getSimilar(rule, new Date());

		return SUCCESS;
	}

	public List<BasicDynaBean> getPercentOn(String field) throws SQLException {
		Database db = new Database();
		SelectSQL sql = new SelectSQL("audit_category_rule");
		if (rule.getAuditType() != null)
			sql.addWhere("auditTypeID = " + rule.getAuditType().getId());
		sql.addWhere("id <> " + id);
		sql.addWhere(field + " IS NOT NULL");
		sql.addGroupBy(field);

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

	public void setDate(Date date) {
		this.date = date;
	}

}
