package com.picsauditing.actions.auditType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class CategoryRuleEditor extends PicsActionSupport {

	protected int id = 0;
	protected boolean categoryRule = true;
	protected boolean canEditDelete = true;

	protected AuditCategoryRule rule = null;
	protected List<AuditCategoryRule> lessGranular;
	protected List<AuditCategoryRule> moreGranular;
	protected List<AuditRule> similar;
	protected Date date = new Date();
	protected Integer bidOnly = null;
	protected Integer rootCat = null;

	protected AuditDecisionTableDAO dao;
	protected AuditTypeDAO typeDAO;
	protected AuditCategoryDAO catDAO;

	protected Map<String, Map<String, String>> columns = new LinkedHashMap<String, Map<String, String>>();

	public CategoryRuleEditor(AuditDecisionTableDAO dao, AuditTypeDAO typeDAO, AuditCategoryDAO catDAO) {
		this.dao = dao;
		this.typeDAO = typeDAO;
		this.catDAO = catDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (rule == null) {
			rule = (AuditCategoryRule) dao.findAuditCategoryRule(id);
			if (rule == null) {
				throw new RecordNotFoundException("rule");
			}
			rule.calculatePriority();
			dao.save(rule);

			if (rule.getEffectiveDate().after(new Date())) { // rule is not in
				// effect yet
				addAlertMessage("This rule will not go into effect until: "
						+ rule.getEffectiveDate());
			} else if (rule.getExpirationDate().before(new Date())) {
				addAlertMessage("This rule is no longer in effect, it was removed by "
						+ rule.getUpdatedBy().getName());
				canEditDelete = false;
			}
		}

		addFields();

		if (button != null) {
			if ("edit".equals(button)) {
				if (rule.getAuditType() != null && rule.getAuditType().getId() > 0)
					rule.setAuditType(typeDAO.find(rule.getAuditType().getId()));
				
				if (rule.getAuditCategory() != null && rule.getAuditCategory().getId() > 0)
					rule.setAuditCategory(catDAO.find(rule.getAuditCategory().getId()));
				
				return SUCCESS;
			}
			if ("Save".equals(button)) {
				if (rule.getId() == 0) {
					setAcceptsBids();
					setRootCategory();
					rule.defaultDates();
					rule.calculatePriority();
					rule.setAuditColumns(permissions);
					dao.save(rule);
				} else {
					AuditRule acr = dao.findAuditCategoryRule(rule.getId());
					setAcceptsBids();
					setRootCategory();
					acr.update(rule);
					acr.calculatePriority();
					acr.setAuditColumns(permissions);
					dao.save(acr);
				}
				this.redirect("CategoryRuleEditor.action?id=" + rule.getId());
				return BLANK;
			}
			if ("copy".equals(button)) {
				AuditCategoryRule acr = rule;
				rule = new AuditCategoryRule();
				rule.update(acr);
				rule.setId(0);
				button = "edit";
				return SUCCESS;
			}
			if ("create".equals(button)) {
				AuditRule source = dao.findAuditCategoryRule(id);
				rule.merge(source);
				if (rule.getId() == 0) {
					rule.defaultDates();
					rule.calculatePriority();
					rule.setAuditColumns(permissions);
					dao.save(rule);
				} else {
					AuditRule acr = dao.findAuditCategoryRule(rule.getId());
					acr.update(rule);
					acr.calculatePriority();
					acr.setAuditColumns(permissions);
					dao.save(acr);
				}
				dao.deleteChildren(rule, permissions);
				this.redirect("CategoryRuleEditor.action?id=" + rule.getId());
				return BLANK;
			}
			if ("delete".equals(button)) {
				String redirect = "";
				List<AuditCategoryRule> lGranular = dao.getLessGranular(rule,
						date);
				if (lGranular.size() > 0)
					redirect = "CategoryRuleEditor.action?id="
							+ lGranular.get(lGranular.size() - 1).getId();
				else {
					redirect = "CategoryRuleSearch.action";
					if (((AuditCategoryRule) rule).getAuditCategory() != null)
						redirect += "filter.category="
								+ ((AuditCategoryRule) rule).getAuditCategory()
										.getName() + "&";
					if (rule.getAuditType() != null)
						redirect += "filter.auditType="
								+ rule.getAuditType().getAuditName() + "&";
				}
				rule.setExpirationDate(new Date());
				rule.setAuditColumns(permissions);
				dao.save(rule);
				this.redirect(redirect);
				return BLANK;
			}
			if ("deleteChildren".equals(button)) {
				int count = dao.deleteChildren(rule, permissions);
				addActionMessage("Archived " + count
						+ (count == 1 ? " rule" : " rules"));
			}
		}

		lessGranular = dao.getLessGranular(rule, date);
		moreGranular = dao.getMoreGranular(rule, date);
		// similar = dao.getSimilar(rule, new Date());
		return SUCCESS;
	}

	protected List<OperatorTag> getOpTagList(){
		List<OperatorTag> opTagList = new ArrayList<OperatorTag>();
		if(rule.getOperatorAccount()!=null){
			for(OperatorTag ot : rule.getOperatorAccount().getTags())
					opTagList.add(ot);
		}
		return opTagList;
	}

	private void setAcceptsBids() {
		if (bidOnly >= 0) {
			if (bidOnly == 1)
				rule.setAcceptsBids(true);
			else
				rule.setAcceptsBids(false);
		} else
			rule.setAcceptsBids(null);
	}
	
	private void setRootCategory(){
		if (rootCat >= 0) {
			if (rootCat == 1)
				rule.setRootCategory(true);
			else
				rule.setRootCategory(false);
		} else
			rule.setRootCategory(null);
	}

	protected void addFields() {
		// include
		columns.put("include", null);
		// audit_type
		if (rule.getAuditType() == null) {
			Map<String, String> m = new HashMap<String, String>();
			m.put("auditTypeID", "rule.auditType.id=");
			columns.put("auditType", m);
		} else {
			columns.put("auditType", null);
		}
		// category
		if (((AuditCategoryRule) rule).getAuditCategory() == null) {
			Map<String, String> m = new HashMap<String, String>();
			m.put("catID", "rule.auditCategory.id=");
			columns.put("category", m);
			columns.put("rootCat", null);			
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

		if (rule.getAuditCategory() != null)
			sql.addWhere("catID = " + rule.getAuditCategory().getId());

		return db.select(sql.toString(), false);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isCategoryRule() {
		return categoryRule;
	}

	public void setCategoryRule(boolean categoryRule) {
		this.categoryRule = categoryRule;
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

	public void setLessGranular(List<AuditCategoryRule> lessGranular) {
		this.lessGranular = lessGranular;
	}

	public List<AuditCategoryRule> getMoreGranular() {
		return moreGranular;
	}

	public void setMoreGranular(List<AuditCategoryRule> moreGranular) {
		this.moreGranular = moreGranular;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Map<String, Map<String, String>> getColumns() {
		return columns;
	}

	public void setColumns(Map<String, Map<String, String>> columns) {
		this.columns = columns;
	}

	public boolean isCanEditDelete() {
		return canEditDelete;
	}

	public void setCanEditDelete(boolean canEditDelete) {
		this.canEditDelete = canEditDelete;
	}

	public Integer getBidOnly() {
		Integer result = -1;
		if (rule.getAcceptsBids() != null) {
			if (rule.getAcceptsBids())
				result = 1;
			else
				result = 0;
		}
		return result;
	}

	public void setBidOnly(Integer bidOnly) {
		this.bidOnly = bidOnly;
	}

	public Integer getRootCat() {
		Integer result = -1;
		if(rule.getRootCategory()!=null){
			if(rule.getRootCategory())
				result = 1;
			else
				result = 0;
		}
		return result;
	}

	public void setRootCat(Integer rootCat) {
		this.rootCat = rootCat;
	}
}
