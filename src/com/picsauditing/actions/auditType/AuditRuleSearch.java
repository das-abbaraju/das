package com.picsauditing.actions.auditType;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.WorkflowStep;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterAuditRule;
import com.picsauditing.util.SpringUtils;


@SuppressWarnings("serial")
public class AuditRuleSearch extends ReportActionSupport implements Preparable {

	protected ReportFilterAuditRule filter = new ReportFilterAuditRule();
	protected SelectSQL sql;
	protected LowMedHigh risk = null;
	protected String actionUrl = "";
	
	protected String fieldName = "";
	protected String search = "";
	protected String ruleType = "";
	
	protected Date DefaultDate = new Date();
	
	protected AuditTypeDAO auditTypeDao;
	protected AuditCategoryDAO auditCatDao;
	protected OperatorAccountDAO operator;
	protected OperatorTagDAO opTagDao;

	public AuditRuleSearch(AuditTypeDAO auditTypeDao, AuditCategoryDAO auditCatDao, 
			OperatorAccountDAO operator, OperatorTagDAO opTagDao){
		this.auditTypeDao = auditTypeDao;
		this.auditCatDao = auditCatDao;
		this.operator = operator;
		this.opTagDao = opTagDao;		
	}
	
	@Override
	public void prepare() throws Exception {
		String[] qA = (String[]) ActionContext.getContext().getParameters().get("q");
		if (qA != null)
			search = Utilities.escapeQuotes(qA[0]);		
	}
	
	@SuppressWarnings("unchecked")
	public String execute() throws Exception {	
		if("searchAuto".equals(button)){
			return runAutoAjax();
		}
		if("opTagFind".equals(button)){
			int opID = 0;
			String[] qA = (String[]) ActionContext.getContext().getParameters().get("opID");
			if (qA != null)
				opID = Integer.parseInt(qA[0]);
			if(opID==0)
				return null;
			JSONArray jarray = new JSONArray();
			for(final OperatorTag ot : opTagDao.findByOperator(opID, true)){
				jarray.add(new JSONObject(){
					{
						put("tag",ot.getTag());
						put("tagID",ot.getId());
					}
				});
			}
			json.put("tags", jarray);
			return JSON;
		}	
		buildQuery();		
		addFilterToSQL();		
		run(sql);
		
		checkFields();
		
		return SUCCESS;
	}

	protected void checkFields() {
		if(filter.getOpID()>0)
			filter.setOperator(operator.find(filter.getOpID()).getName());
		if(filter.getAuditTypeID()>0)
			filter.setAuditType(auditTypeDao.find(filter.getAuditTypeID()).getAuditName());
		if(filter.getTagID()>0)
			filter.setAuditType(opTagDao.find(filter.getTagID()).getTag());
	}

	protected void buildQuery() {
		sql.addField("a_search.id");
		sql.addField("a_search.include");
		sql.addField("IFNULL(aty.auditName,'*') audit_type");
		sql.addField("IFNULL(a_search.contractorType,'*') con_type");
		sql.addField("IFNULL(a.name,'*') operator");
		sql.addField("IFNULL(a_search.risk,'*') risk");
		sql.addField("IFNULL(ot.tag,'*') tag");
		sql.addField("IFNULL(aq.name,'*') question");
		sql.addField("CASE a_search.acceptsBids WHEN 1 THEN 'True' WHEN 0 THEN 'False' ELSE '*' END bid");
		sql.addJoin("LEFT JOIN audit_type aty ON aty.id = a_search.auditTypeID");
		sql.addJoin("LEFT JOIN operator_tag ot ON ot.id = a_search.tagID");
		sql.addJoin("LEFT JOIN accounts a ON a.id = a_search.opID");
		sql.addJoin("LEFT JOIN audit_question aq ON aq.id = a_search.questionID");
		sql.addOrderBy("a_search.priority");
	}

	protected String runAutoAjax() throws SQLException {
		List<? extends BaseTable> returnAjax = null;
		if("auditType".equals(fieldName)){
			returnAjax = auditTypeDao.findWhere("t.auditName LIKE '"+search+"%'");
		} else if("category".equals(fieldName)){
			returnAjax = auditCatDao.findCategoryNames(search, 100);
		} else if("dependentAuditType".equals(fieldName)){
			returnAjax = auditTypeDao.findWhere("t.auditName LIKE '"+search+"%'");
		} else if("operator".equals(fieldName)){
			returnAjax = operator.findWhere(true, "a.name LIKE '"+search+"%'");
		} else if("tag".equals(fieldName)){
			returnAjax = opTagDao.findWhere(OperatorTag.class, "t.tag LIKE '"+search+"%'", 50);
		} else if("question".equals(fieldName)){
			returnAjax = ((AuditQuestionDAO)SpringUtils.getBean("AuditQuestionDAO")).findWhere("t.name LIKE '"+search+"%'");
		} else if("dAuditType".equals(fieldName)){
			returnAjax = auditTypeDao.findWhere("t.auditName LIKE '"+search+"%'");
		}
		StringBuilder sb = new StringBuilder();
		for(BaseTable bt : returnAjax){
			if(bt instanceof AuditType){
				sb.append(fieldName).append("|").append(((AuditType)bt).getAuditName()).append("|").append(((AuditType)bt).getId()).append("\n");
			} else if(bt instanceof AuditCategory){
				sb.append("cat").append("|").append(((AuditCategory)bt).getFullyQualifiedName()).append("|").append(((AuditCategory)bt).getId()).append("\n");
			} else if(bt instanceof OperatorAccount){
				sb.append("op").append("|").append(((OperatorAccount)bt).getName()).append("|").append(((OperatorAccount)bt).getId()).append("\n");
			} else if(bt instanceof OperatorTag){
				sb.append("tag").append("|").append(((OperatorTag)bt).getTag()).append("|").append(((OperatorTag)bt).getId()).append("\n");
			} else if(bt instanceof AuditQuestion){
				sb.append("question").append("|").append(((AuditQuestion)bt).getName()).append("|").append(((AuditQuestion)bt).getId()).append("\n");
			}
		}
		output = sb.toString();
		
		return "autocomp";
	}

	protected void addFilterToSQL() throws Exception {
		if(filterOn(filter.getContractorType()) && filter.getContractorType()>0){
			report.addFilter(new SelectFilter("accountType", "a_search.contractorType = ?", String.valueOf(filter.getContractorType())));
		}
		if(filterOn(filter.getRiskLevel()) && filter.getRiskLevel()>0){
			report.addFilter(new SelectFilter("riskLevel", "a_search.risk = ?", String.valueOf(filter.getRiskLevel())));
		}
		if(filterOn(filter.getAuditTypeID()) && filter.getAuditTypeID()>0){
			report.addFilter(new SelectFilter("audit_type", "aty.id= ?", String.valueOf(filter.getAuditTypeID())));
		}
		if(filterOn(filter.getOpID()) && filter.getOpID()>0){
			report.addFilter(new SelectFilter("operator", "a.id = ?", String.valueOf(filter.getOpID())));
		}
		if(filterOn(filter.getTagID()) && filter.getTagID()>0){
			report.addFilter(new SelectFilter("tag", "ot.id = ?", String.valueOf(filter.getTagID())));
		}
		if(filterOn(filter.getInclude())){
			report.addFilter(new SelectFilter("include", "include = ?", String.valueOf(filter.getInclude())));
		}
		if(filterOn(filter.isBid())){
			report.addFilter(new SelectFilter("bidOnly", "a_search.acceptsBids = ?", String.valueOf(filter.isBid())));
		}
		if(filterOn(filter.getCheckDate())){
			report.addFilter(new SelectFilter("effectiveDate", "a_search.effectiveDate <= '? 24:00:00' AND a_search.expirationDate >= '? 00:00:00'", String.valueOf(DateBean.toDBFormat(filter.getCheckDate()))));
		} else{
			report.addFilter(new SelectFilter("effectiveDate", "a_search.effectiveDate <= '? 24:00:00' AND a_search.expirationDate >= '? 00:00:00'", String.valueOf(DateBean.toDBFormat(DefaultDate))+" 24:00:00"));
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
