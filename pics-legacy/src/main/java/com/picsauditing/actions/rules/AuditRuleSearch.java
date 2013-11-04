package com.picsauditing.actions.rules;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterAuditRule;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AuditRuleSearch extends ReportActionSupport {

	protected SelectSQL sql;
	protected ReportFilterAuditRule filter = new ReportFilterAuditRule();

	protected boolean categoryRule = false;

	public String execute() throws Exception {
		buildQuery();
		addFilterToSQL();
		run(sql);

		return SUCCESS;
	}

	protected void buildQuery() {
		sql.addField("a_search.*");

		sql.addJoin("LEFT JOIN audit_type aty ON aty.id = a_search.auditTypeID");
		sql.addField("IFNULL(aty.id,'*') audit_type");

		sql.addJoin("LEFT JOIN operator_tag ot ON ot.id = a_search.tagID");
		sql.addField("IFNULL(ot.tag,'*') tag");

		sql.addField("IFNULL(a_search.contractorType,'*') con_type_name");

		sql.addJoin("LEFT JOIN accounts op ON op.id = a_search.opID");
		sql.addField("IFNULL(op.name,'*') operator");
		sql.addField("op.status operatorStatus");
		
		sql.addJoin("LEFT JOIN audit_question aq ON aq.id = a_search.questionID");
	
		sql.addOrderBy("a_search.priority");
	}

	protected void addFilterToSQL() throws Exception {
		if (filterOn(filter.getContractorType())) {
			sql.addWhere("a_search.contractorType = '" + filter.getContractorType() + "'");
		}
		if (filter.getSafetyRisk() > 0) {
			sql.addWhere("a_search.safetyRisk = '" + LowMedHigh.getMap().get(filter.getSafetyRisk()) + "'");
		}
		if (filter.getProductRisk() > 0) {
			sql.addWhere("a_search.productRisk = '" + LowMedHigh.getMap().get(filter.getProductRisk()) + "'");
		}
		if (filter.getOpID() > 0) {
			sql.addWhere("op.id = " + filter.getOpID());
		}
		if (filterOn(filter.getOperator())) {
			sql.addWhere("op.name LIKE '%" + Strings.escapeQuotesAndSlashes(filter.getOperator()) + "%'");
		}
		if (filterOn(filter.getInclude())) {
			sql.addWhere("a_search.include = " + filter.getInclude());
		}
		if (filterOn(filter.isBid())) {
			sql.addWhere("a_search.acceptsBids = " + filter.isBid());
		}
		if (filterOn(filter.getCheckDate())) {
			String checkDate = DateBean.toDBFormat(filter.getCheckDate());
			sql.addWhere("a_search.effectiveDate <= '" + checkDate + " 23:59:59' AND a_search.expirationDate >= '"
					+ checkDate + " 00:00:00'");
		} else {
			sql.addWhere("a_search.effectiveDate <= NOW() AND a_search.expirationDate >= NOW()");
		}
		if (filter.getTrade() != null) {
			sql.addWhere("a_search.tradeID = " + filter.getTrade().getId());
		}
		if (filter.getSoleProprietor() != null) {
			sql.addWhere("a_search.soleProprietor = " + filter.getSoleProprietor());
		}
	}

	public String getRisk(int id) {
		String r = LowMedHigh.getName(id);
		if (r.equalsIgnoreCase("none"))
			return "*";
		else
			return r;
	}

	public ReportFilterAuditRule getFilter() {
		return filter;
	}

	public String getActionUrl() {
		return categoryRule ? "CategoryRuleEditor.action?id=" : "AuditTypeRuleEditor.action?id=";
	}

	public boolean isCategoryRule() {
		return categoryRule;
	}
}
