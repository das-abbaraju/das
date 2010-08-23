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

	private AuditCategoryRule rule = null;
	private List<AuditCategoryRule> lessGranular;
	private List<AuditCategoryRule> moreGranular;
	private List<AuditCategoryRule> similar;

	private AuditDecisionTableDAO dao;

	public CategoryRuleEditor(AuditDecisionTableDAO auditDecisionTableDAO) {
		this.dao = auditDecisionTableDAO;
	}

	public String execute() throws Exception {

		if (id == 0)
			return BLANK;

		rule = dao.findAuditCategoryRule(id);

		if (button != null) {
			if ("create".equals(button)) {
				dao.save(rule);
				this.redirect("?id=" + rule.getId());
				return BLANK;
			}
		}

		lessGranular = dao.getLessGranular(rule, new Date());
		moreGranular = dao.getMoreGranular(rule, new Date());
		// similar = dao.getSimilar(rule, new Date());

		return SUCCESS;
	}

	public List<BasicDynaBean> getPercentOn(String field) throws SQLException {
		Database db = new Database();
		SelectSQL sql = new SelectSQL("audit_category_rule");
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

}
