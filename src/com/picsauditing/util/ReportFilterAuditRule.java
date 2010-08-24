package com.picsauditing.util;

import com.picsauditing.jpa.entities.ContractorType;

@SuppressWarnings("serial")
public class ReportFilterAuditRule extends ReportFilter {

	protected boolean showCategory = true;
	
	protected Integer riskLevel = null;
	protected Integer include;
	
	protected String accountType = null;
	protected String auditType = null;
	protected String category = null;
	protected String operator = null;
	protected String tag = null;
	
	protected int auditTypeID;
	protected int catID;
	protected int opID;
	protected int tagID;
		
	protected String[] accountTypeList = ContractorType.getValues(true);

	public Integer getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(int riskLevel) {
		if(riskLevel==0)
			this.riskLevel = null;
		else
			this.riskLevel = riskLevel;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		if("*".equals(accountType))
			this.accountType = null;
		else
			this.accountType = accountType;
	}

	public String[] getAccountTypeList() {
		return accountTypeList;
	}

	public void setAccountTypeList(String[] accountTypeList) {
		this.accountTypeList = accountTypeList;
	}

	public String getAuditType() {
		return auditType;
	}

	public void setAuditType(String auditType) {
		if(auditType==null || auditType.isEmpty())
			auditTypeID = 0;
		this.auditType = auditType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		if(category==null || category.isEmpty())
			catID = 0;
		this.category = category;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		if(operator==null || operator.isEmpty())
			opID = 0;
		this.operator = operator;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		if(tag==null || tag.isEmpty())
			tagID = 0;
		this.tag = tag;
	}

	public boolean isShowCategory() {
		return showCategory;
	}

	public void setShowCategory(boolean showCategory) {
		this.showCategory = showCategory;
	}

	public Integer getInclude() {
		return include;
	}

	public void setInclude(Integer include) {
		if(include==2)
			this.include = null;
		else
			this.include = include;
	}

	public int getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
	}

	public int getCatID() {
		return catID;
	}

	public void setCatID(int catID) {
		this.catID = catID;
	}

	public int getOpID() {
		return opID;
	}

	public void setOpID(int opID) {
		this.opID = opID;
	}

	public int getTagID() {
		return tagID;
	}

	public void setTagID(int tagID) {
		this.tagID = tagID;
	}

	public void setRiskLevel(Integer riskLevel) {
		this.riskLevel = riskLevel;
	}

}
