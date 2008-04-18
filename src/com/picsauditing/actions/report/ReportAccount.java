package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Inputs;
import com.picsauditing.PICS.SearchBean;
import com.picsauditing.PICS.TradesBean;
import com.picsauditing.PICS.pqf.QuestionTypeList;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Industry;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.util.SpringUtils;

public class ReportAccount extends ReportActionSupport {

	protected String name;
	protected String industry;
	protected String performedBy;
	protected String trade;
	protected String city;
	protected String state;
	protected String zip;
	protected String certsOnly;
	protected String visible;
	protected String stateLicensedIn;
	protected String worksIn;
	protected String taxID;
	
	
	@Autowired
	protected SelectAccount sql = new SelectAccount();

	public SelectAccount getSql() {
		return sql;
	}

	public void setSql(SelectAccount sql) {
		this.sql = sql;
	}

	public String execute() throws Exception {
		this.autoLogin = true;
		getPermissions();
		sql.setPermissions(permissions);

		if (this.orderBy == null)
			this.orderBy = "a.name";
		sql.setType(SelectAccount.Type.Contractor);

		sql.addField("industry");
		
		this.run(sql);

		return SUCCESS;
	}

	// Getters for search lists
	public ArrayList<String> getIndustryList() {
		return Industry.getValuesWithDefault();
	}

	public Map<Integer, String> getStateLicensesList() throws Exception {
		QuestionTypeList questionList = new QuestionTypeList();
		return questionList.getQuestionMap("License", "- Licensed In -");
	}

	public Map<Integer, String> getTradeList() throws Exception {
		QuestionTypeList questionList = new QuestionTypeList();
		return questionList.getQuestionMap("Service", "- Trade -");
	}

	public Map<Integer, String> getWorksInList() throws Exception {
		QuestionTypeList questionList = new QuestionTypeList();
		return questionList.getQuestionMap("Office Location", "- Works In -");
	}

	public String[] getTradePerformedByList() throws Exception {
		return TradesBean.PERFORMED_BY_ARRAY;
	}

	public Map<Integer, String> getOperatorList() throws Exception {
		OperatorAccountDAO dao = (OperatorAccountDAO)SpringUtils.getBean("OperatorAccountDAO");
		List<OperatorAccount> operators = dao.findWhere("active='y'");
		
		Map<Integer, String> operatorMap = new TreeMap<Integer, String>();
		operatorMap.put(0, OperatorAccount.DEFAULT_NAME);
		for(OperatorAccount op : operators)
			operatorMap.put(op.getId(), op.getName());
		return operatorMap;
	}
	
	public Map<String, String> getStateList() {
		return State.getStates(true);
	}
	
	public String[] getCertsOptions() {
		return new String[] {"","- Default Certs -","Yes", "Only Certs","No","Exclude Certs"};
	}
	
	public String[] getVisibleOptions() {
		return SearchBean.VISIBLE_SEARCH_ARRAY;
	}
	
	// Getters and setters for filter criteria
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		report.addFilter(new SelectFilter("name", "a.name LIKE '%?%'", name,
				SearchBean.DEFAULT_NAME, SearchBean.DEFAULT_NAME));
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		report.addFilter(new SelectFilter("industry", "a.industry = '?'",
				industry, Industry.DEFAULT_INDUSTRY,
				Industry.DEFAULT_INDUSTRY));
		this.industry = industry;
	}

}
