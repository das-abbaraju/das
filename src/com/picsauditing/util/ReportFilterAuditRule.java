package com.picsauditing.util;

import java.util.Date;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.LowMedHigh;

@SuppressWarnings("serial")
public class ReportFilterAuditRule extends ReportFilter {

	protected Integer include = null;
	protected Integer bid = null;

	protected String operator = null;
	protected int opID;
	protected int tradeID;

	protected String contractorType = null;
	protected LowMedHigh safetyRisk;
	protected LowMedHigh productRisk;
	protected Boolean soleProprietor;

	protected Date checkDate = null;

	public String getContractorType() {
		return contractorType;
	}

	public void setContractorType(String contractorType) {
		this.contractorType = contractorType;
	}

	public ContractorType[] getContractorTypeList() {
		return ContractorType.values();
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

	public LowMedHigh[] getRiskLevelList() {
		return LowMedHigh.values();
	}

	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(String checkDate) {
		this.checkDate = DateBean.parseDate(checkDate);
	}

	public int getTradeID() {
		return tradeID;
	}

	public void setTradeID(int tradeID) {
		this.tradeID = tradeID;
	}

	public LowMedHigh getSafetyRisk() {
		return safetyRisk;
	}

	public void setSafetyRisk(LowMedHigh safetyRisk) {
		this.safetyRisk = safetyRisk;
	}

	public LowMedHigh getProductRisk() {
		return productRisk;
	}

	public void setProductRisk(LowMedHigh productRisk) {
		this.productRisk = productRisk;
	}

	public Boolean getSoleProprietor() {
		return soleProprietor;
	}

	public void setSoleProprietor(Boolean soleProprietor) {
		this.soleProprietor = soleProprietor;
	}

}
