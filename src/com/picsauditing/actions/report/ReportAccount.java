package com.picsauditing.actions.report;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Inputs;
import com.picsauditing.PICS.SearchBean;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectFilter;

public class ReportAccount extends ReportActionSupport {

	protected String name;
	protected String industry;

	@Autowired
	protected SelectAccount sql = new SelectAccount();

	public SelectAccount getSql() {
		return sql;
	}

	public void setSql(SelectAccount sql) {
		this.sql = sql;
	}

	public String execute() throws Exception {
		getPermissions();
		sql.setPermissions(permissions);

		if (this.orderBy == null)
			this.orderBy = "a.name";
		sql.setType(SelectAccount.Type.Contractor);

		sql.addField("industry");
		
		this.run(sql);

		return SUCCESS;
	}

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
		this.industry = industry;
		report.addFilter(new SelectFilter("industry", "a.industry = '?'",
				industry, SearchBean.DEFAULT_INDUSTRY,
				SearchBean.DEFAULT_INDUSTRY));
	}

	public String[] getIndustryList() throws Exception{
		return SearchBean.INDUSTRY_SEARCH_ARRAY;
	}

}
