package com.picsauditing.util;

import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.LowMedHigh;

@SuppressWarnings("serial")
public class ReportFilterAuditRule extends ReportFilter {

	protected boolean showCategory = true;
	protected boolean showDependentAuditType = true;
	protected boolean showDependentAuditStatus = true;
	
	protected Integer include;
	
	protected String auditType = null;
	protected String dependentAuditType = null;
	protected String category = null;
	protected String operator = null;
	protected String tag = null;
	
	protected Integer dependentAuditStatus = null;
	protected Integer accountType = null;
	protected Integer riskLevel = null;
	
	protected int auditTypeID;
	protected int catID;
	protected int opID;
	protected int tagID;
	protected int dependentAuditTypeID;
	
	protected boolean bid;
		
	protected ContractorType[] accountTypeList = ContractorType.values();
	protected AuditStatus[] dependentAuditStatusList = AuditStatus.values(); //.getValuesWithDefault();
	protected LowMedHigh[] riskLevelList = LowMedHigh.values();

	public Integer getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(int riskLevel) {
		this.riskLevel = riskLevel;
	}
	
	public Integer getDependentAuditStatus() {
		return dependentAuditStatus;
	}

	public void setDependentAuditStatus(int dependentAuditStatus) {
			this.dependentAuditStatus = dependentAuditStatus; // adjust for default value in list
	}

	public Integer getAccountType() {
		return accountType;
	}

	public void setAccountType(int accountType) {
		this.accountType = accountType;
	}

	public ContractorType[] getAccountTypeList() {
		return accountTypeList;
	}

	public void setAccountTypeList(ContractorType[] accountTypeList) {
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

	public boolean isBid() {
		return bid;
	}

	public void setBid(boolean bid) {
		this.bid = bid;
	}

	public boolean isShowDependentAuditType() {
		return showDependentAuditType;
	}

	public void setShowDependentAuditType(boolean showDependentAuditType) {
		this.showDependentAuditType = showDependentAuditType;
	}

	public boolean isShowDependentAuditStatus() {
		return showDependentAuditStatus;
	}

	public void setShowDependentAuditStatus(boolean showDependentAuditStatus) {
		this.showDependentAuditStatus = showDependentAuditStatus;
	}

	public String getDependentAuditType() {
		return dependentAuditType;
	}

	public void setDependentAuditType(String dependentAuditType) {
		if(dependentAuditType==null || dependentAuditType.isEmpty())
			dependentAuditTypeID = 0;
		this.dependentAuditType = dependentAuditType;
	}

	public int getDependentAuditTypeID() {
		return dependentAuditTypeID;
	}

	public void setDependentAuditTypeID(int dependentAuditTypeID) {
		this.dependentAuditTypeID = dependentAuditTypeID;
	}

	public AuditStatus[] getDependentAuditStatusList() {
		return dependentAuditStatusList;
	}

	public void setDependentAuditStatusList(AuditStatus[] dependentAuditStatusList) {
		this.dependentAuditStatusList = dependentAuditStatusList;
	}

	public LowMedHigh[] getRiskLevelList() {
		return riskLevelList;
	}

	public void setRiskLevelList(LowMedHigh[] riskLevelList) {
		this.riskLevelList = riskLevelList;
	}

}
