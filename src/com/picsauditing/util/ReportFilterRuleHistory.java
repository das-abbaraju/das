package com.picsauditing.util;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.DateBean;

@SuppressWarnings("serial")
public class ReportFilterRuleHistory extends ReportFilter {

	protected String changedBy = null;
	protected String findStatus = null;
	protected String findType = null;
	protected Date fromDate = null;
	protected Date toDate = null;

	protected List<String> types = Arrays.asList("AuditTypeRule",
			"CategoryRule");
	protected List<String> statuses = Arrays
			.asList("Updated", "New", "Deleted");

	public String getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}

	public String getFindStatus() {
		return findStatus;
	}

	public void setFindStatus(String findStatus) {
		this.findStatus = findStatus;
	}

	public String getFindType() {
		return findType;
	}

	public void setFindType(String findType) {
		this.findType = findType;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = DateBean.parseDate(fromDate);
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = DateBean.parseDate(toDate);
	}

	public List<String> getTypes() {
		return types;
	}

	public List<String> getStatuses() {
		return statuses;
	}

}
