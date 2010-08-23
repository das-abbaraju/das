package com.picsauditing.util;

import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.LowMedHigh;

@SuppressWarnings("serial")
public class ReportFilterCategoryRule extends ReportFilter {

	protected Integer riskLevel = null;
	protected String accountType = null;
	protected String auditType = null;
	protected String category = null;
	protected String operator = null;
	protected String tag = null;
	
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
		this.auditType = auditType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
