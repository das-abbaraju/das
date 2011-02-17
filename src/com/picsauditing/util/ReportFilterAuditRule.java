package com.picsauditing.util;

import java.util.Date;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.LowMedHigh;

@SuppressWarnings("serial")
public class ReportFilterAuditRule extends ReportFilter {

	protected Integer include = null;
	protected Integer bid = null;

	protected String auditType = null;
	protected String dependentAuditType = null;
	protected String category = null;
	protected String operator = null;
	protected int opID;

	protected String contractorType = null;
	protected int riskLevel;

	protected Date checkDate = null;

	public int getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(int riskLevel) {
		this.riskLevel = riskLevel;
	}

	public String getContractorType() {
		return contractorType;
	}

	public void setContractorType(String contractorType) {
		this.contractorType = contractorType;
	}

	public ContractorType[] getContractorTypeList() {
		return ContractorType.values();
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

	public Integer getInclude() {
		return include;
	}

	public void setInclude(Integer include) {
		if (include < 0)
			this.include = null;
		else
			this.include = include;
	}

	public int getOpID() {
		return opID;
	}

	public void setOpID(int opID) {
		this.opID = opID;
	}

	public Integer isBid() {
		return bid;
	}

	public void setBid(Integer bid) {
		if (bid < 0)
			this.bid = null;
		else
			this.bid = bid;
	}

	public String getDependentAuditType() {
		return dependentAuditType;
	}

	public void setDependentAuditType(String dependentAuditType) {
		this.dependentAuditType = dependentAuditType;
	}

	public LowMedHigh[] getRiskLevelList() {
		return LowMedHigh.values();
	}

	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(String checkDate) {
		this.checkDate = DateBean.parseDate(checkDate);
	}

}
