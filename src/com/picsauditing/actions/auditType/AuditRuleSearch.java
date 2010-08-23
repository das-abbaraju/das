package com.picsauditing.actions.auditType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterAuditRule;


@SuppressWarnings("serial")
public class AuditRuleSearch extends ReportActionSupport implements Preparable {

	protected ReportFilterAuditRule filter = new ReportFilterAuditRule();
	protected SelectSQL sql;
	protected LowMedHigh risk = null;
	protected String actionUrl = "";
	
	protected String fieldName = "";
	protected String search = "";
	protected String ruleType = "";

	@Override
	public void prepare() throws Exception {
		String[] qA = (String[]) ActionContext.getContext().getParameters().get("q");
		if (qA != null)
			search = qA[0];		
	}
	
	public String execute() throws Exception {	

		if("searchAuto".equals(button)){
			return runAutoAjax();
		}
		buildQuery();		
		addFilterToSQL();		
		run(sql);
		
		return SUCCESS;
	}

	protected void buildQuery() {
		sql.addField("a_search.id");
		sql.addField("a_search.include");
		sql.addField("IFNULL(aty.auditName,'*') audit_type");
		sql.addField("IFNULL(a_search.accountType,'*') account_type");
		sql.addField("IFNULL(a.name,'*') operator");
		sql.addField("IFNULL(a_search.risk,'*') risk");
		sql.addField("IFNULL(ot.tag,'*') tag");
		sql.addJoin("LEFT JOIN audit_type aty ON aty.id = a_search.auditTypeID");
		sql.addJoin("LEFT JOIN operator_tag ot ON ot.id = a_search.tagID");
		sql.addJoin("LEFT JOIN accounts a ON a.id = a_search.opID");
	}

	protected String runAutoAjax() throws SQLException {
		String str = "";
		Database db = new Database();
		if("auditType".equals(fieldName)){
			str = "SELECT auditName name FROM audit_type WHERE auditName LIKE '"+search+"%'";
		} else if("operator".equals(fieldName)){
			str = "SELECT a.name FROM accounts a WHERE a.name LIKE '"+search+"%' AND type='Operator'";
		} else if("tag".equals(fieldName)){
			str = "SELECT tag name FROM operator_tag WHERE tag LIKE '"+search+"%'";
		}	
		List<BasicDynaBean> re = db.select(str, false);
		Set<String> se = new HashSet<String>();
		StringBuilder sb = new StringBuilder();
		for(BasicDynaBean bdb : re){
			String name = bdb.get("name").toString();
			if(se.add(name))
				sb.append(name).append("\n");
		}
		output = sb.toString();
		
		return "autocomp";
	}

	protected void addFilterToSQL() {
		if(filterOn(filter.getAccountType())){
			report.addFilter(new SelectFilter("accountType", "a_search.accountType = ?", String.valueOf(ContractorType.valueOf(filter.getAccountType()).ordinal())));
		}
		if(filterOn(filter.getRiskLevel())){
			report.addFilter(new SelectFilter("riskLevel", "a_search.risk = ?", String.valueOf(filter.getRiskLevel())));
		}
		if(filterOn(filter.getAuditType())){
			report.addFilter(new SelectFilter("audit_type", "aty.auditName = '?'", String.valueOf(filter.getAuditType())));
		}
		if(filterOn(filter.getOperator())){
			report.addFilter(new SelectFilter("operator", "a.name = '?'", String.valueOf(filter.getOperator())));
		}
		if(filterOn(filter.getTag())){
			report.addFilter(new SelectFilter("tag", "ot.tag = '?'", String.valueOf(filter.getTag())));
		}
	}

	@SuppressWarnings("static-access")
	public String getRisk(int id){
		String r = risk.getName(id);
		if(r.equalsIgnoreCase("none"))
			return "*";
		else
			return r;
	}

	public ReportFilterAuditRule getFilter() {
		return filter;
	}

	public void setFilter(ReportFilterAuditRule filter) {
		this.filter = filter;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getActionUrl() {
		return actionUrl;
	}

	public void setActionUrl(String actionUrl) {
		this.actionUrl = actionUrl;
	}
}
