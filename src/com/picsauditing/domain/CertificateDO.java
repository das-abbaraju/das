package com.picsauditing.domain;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.SpringUtils;

public class CertificateDO implements IPicsDO {
	public String cert_id = "";
	public String contractor_id = "";
	public String operator_id = "";
	public String type = "";
	int numTypes = 0;
	public String expDay = "";
	public String expMonth = "";
	public String expYear = "";
	public String expDate = "";
	public String sent = "";
	public String lastSentDate = "";
	private long liabilityLimit = 0l;
	private String subrogationWaived = "No";
	private String namedInsured = "";
	private String status = "Neither";
	private String verified = "No";
	private String reason = "";
	private String isDirty = "No";
	private String ext = "pdf";

	public String getCert_id() {
		return cert_id;
	}

	public void setCert_id(String cert_id) {
		this.cert_id = cert_id;
	}

	public String getContractor_id() {
		return contractor_id;
	}

	public void setContractor_id(String contractor_id) {
		this.contractor_id = contractor_id;
	}

	public String getExpDate() {
		return expDate;
	}

	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}

	public String getExpDay() {
		return expDay;
	}

	public void setExpDay(String expDay) {
		this.expDay = expDay;
	}

	public String getExpMonth() {
		return expMonth;
	}

	public void setExpMonth(String expMonth) {
		this.expMonth = expMonth;
	}

	public String getExpYear() {
		return expYear;
	}

	public void setExpYear(String expYear) {
		this.expYear = expYear;
	}

	public String getLastSentDate() {
		return lastSentDate;
	}

	public void setLastSentDate(String lastSentDate) {
		this.lastSentDate = lastSentDate;
	}

	public long getLiabilityLimit() {
		return liabilityLimit;
	}

	public void setLiabilityLimit(long liabilityLimit) {
		this.liabilityLimit = liabilityLimit;
	}

	public String getNamedInsured() {
		return namedInsured;
	}

	public void setNamedInsured(String namedInsured) {
		this.namedInsured = namedInsured;
	}

	public int getNumTypes() {
		return numTypes;
	}

	public void setNumTypes(int numTypes) {
		this.numTypes = numTypes;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getSent() {
		return sent;
	}

	public void setSent(String sent) {
		this.sent = sent;
	}

	public String getSubrogationWaived() {
		return subrogationWaived;
	}

	public void setSubrogationWaived(String subrogationWaived) {
		this.subrogationWaived = subrogationWaived;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVerified() {
		return verified;
	}

	public void setVerified(String verified) {
		this.verified = verified;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getIsDirty() {
		return isDirty;
	}

	public void setIsDirty(String isDirty) {
		this.isDirty = isDirty;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getOperatorName(String id) {
		OperatorAccountDAO opAcctDAO = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
		OperatorAccount operatorAccount = opAcctDAO.find(Integer.parseInt(id));
		return operatorAccount.getName();
	}

	public String getContractorName(String id) {
		ContractorAccountDAO conAcctDAO = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
		ContractorAccount contractorAccount = conAcctDAO.find(Integer.parseInt(id));
		return contractorAccount.getName();
	}

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		cert_id = SQLResult.getString("cert_id");
		contractor_id = SQLResult.getString("contractor_id");
		operator_id = SQLResult.getString("operator_id");
		type = SQLResult.getString("type");
		expDate = SQLResult.getString("expDate");
		sent = SQLResult.getString("sent");
		lastSentDate = DateBean.toShowFormat(SQLResult.getString("lastSentDate"));
		expDay = expDate.substring(8);
		expMonth = expDate.substring(5, 7);
		expYear = expDate.substring(0, 4);
		liabilityLimit = SQLResult.getLong("liabilityLimit");
		namedInsured = SQLResult.getString("namedInsured");
		subrogationWaived = SQLResult.getString("subrogationWaived");
		status = SQLResult.getString("status");
		verified = SQLResult.getString("verified");
		reason = SQLResult.getString("reason");
		ext = SQLResult.getString("ext");
	}

	public void setFromRequest(HttpServletRequest request) {
		cert_id = request.getParameter("cert_id");
		contractor_id = request.getParameter("contractor_id");
		type = request.getParameter("type");
		expDate = request.getParameter("expDate");
		sent = request.getParameter("sent");
		lastSentDate = request.getParameter("lastSentDate");
		expDay = expDate.substring(8);
		expMonth = expDate.substring(5, 7);
		expYear = expDate.substring(0, 4);
		liabilityLimit = Long.parseLong(request.getParameter("liabilityLimit"));
		namedInsured = request.getParameter("namedInsured");
		subrogationWaived = request.getParameter("subrogationWaived");
		status = request.getParameter("status");
		verified = request.getParameter("verified");
		reason = request.getParameter("reason");
		ext = request.getParameter("ext");
	}
}