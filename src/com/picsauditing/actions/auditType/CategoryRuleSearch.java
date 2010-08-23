package com.picsauditing.actions.auditType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterCategoryRule;


@SuppressWarnings("serial")
public class CategoryRuleSearch extends ReportActionSupport implements Preparable {

	private ReportFilterCategoryRule filter = new ReportFilterCategoryRule();
	private SelectSQL sql = new SelectSQL("audit_category_rule acr");
	private LowMedHigh risk = null;
	
	private String fieldName = "";
	private String search = "";

	@Override
	public void prepare() throws Exception {
		String[] qA = (String[]) ActionContext.getContext().getParameters().get("q");
		if (qA != null)
			search = qA[0];		
	}
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;		
		
		if("getAjax".equals(button)){
			String str = "";
			Database db = new Database();
			if("auditType".equals(fieldName)){
				str = "SELECT auditName name FROM audit_type WHERE auditName LIKE '"+search+"%'";
			} else if("category".equals(fieldName)){
				str = "SELECT name FROM audit_category WHERE name LIKE '"+search+"%'";
			} else if("operator".equals(fieldName)){
				str = "SELECT a.name FROM accounts a WHERE a.name LIKE '"+search+"%' AND type='Operator'";
			} else if("tag".equals(fieldName)){
				str = "SELECT tag name FROM operator_tag WHERE tag LIKE '"+search+"%'";
			}	
			List<BasicDynaBean> re = db.select(str, false);
			StringBuilder sb = new StringBuilder();
			for(BasicDynaBean bdb : re){
				sb.append(bdb.get("name").toString()).append("\n");
			}
			output = sb.toString();
			
			return BLANK;
		}

		sql.addField("acr.id");
		sql.addField("acr.include");
		sql.addField("IFNULL(aty.auditName,'*') audit_type");
		sql.addField("IFNULL(ac.name,'*') category");
		sql.addField("IFNULL(acr.accountType,'*') account_type");
		sql.addField("IFNULL(a.name,'*') operator");
		sql.addField("IFNULL(acr.risk,'*') risk");
		sql.addField("IFNULL(ot.tag,'*') tag");
		sql.addJoin("LEFT JOIN audit_type aty ON aty.id = acr.auditTypeID");
		sql.addJoin("LEFT JOIN audit_category ac ON ac.id = acr.catID");
		sql.addJoin("LEFT JOIN operator_tag ot ON ot.id = acr.tagID");
		sql.addJoin("LEFT JOIN accounts a ON a.id = acr.opID");
		addFilterToSQL();
		
		run(sql);
		
		return SUCCESS;
	}

	private void addFilterToSQL() {
		if(filterOn(filter.getAccountType())){
			report.addFilter(new SelectFilter("accountType", "accountType = ?", String.valueOf(ContractorType.valueOf(filter.getAccountType()).ordinal())));
		}
		if(filterOn(filter.getRiskLevel())){
			report.addFilter(new SelectFilter("riskLevel", "risk = ?", String.valueOf(filter.getRiskLevel())));
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

	public ReportFilterCategoryRule getFilter() {
		return filter;
	}

	public void setFilter(ReportFilterCategoryRule filter) {
		this.filter = filter;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
