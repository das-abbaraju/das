package com.picsauditing.util;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.picsauditing.jpa.entities.AuditStatus;

@SuppressWarnings("serial")
public class ReportFilterCAOW extends ReportFilterCAO {

	protected boolean showCaowStatus = true;
	protected boolean showCaowUpdateDate = true;
	protected boolean showCaowDetail = false;

	protected AuditStatus[] caowStatus;
	protected Date caowUpdateDate1;
	protected Date caowUpdateDate2;
	protected Boolean caowDetailLevel = null;

	public boolean isShowCaowStatus() {
		return showCaowStatus;
	}

	public void setShowCaowStatus(boolean showCaowStatus) {
		this.showCaowStatus = showCaowStatus;
	}

	public AuditStatus[] getCaowStatus() {
		return caowStatus;
	}

	public void setCaowStatus(AuditStatus[] caowStatus) {
		this.caowStatus = caowStatus;
	}

	public boolean isShowCaowUpdateDate() {
		return showCaowUpdateDate;
	}

	public void setShowCaowUpdateDate(boolean showCaowUpdateDate) {
		this.showCaowUpdateDate = showCaowUpdateDate;
	}

	public Date getCaowUpdateDate1() {
		return caowUpdateDate1;
	}

	public void setCaowUpdateDate1(Date caowUpdateDate1) {
		this.caowUpdateDate1 = caowUpdateDate1;
	}

	public Date getCaowUpdateDate2() {
		return caowUpdateDate2;
	}

	public void setCaowUpdateDate2(Date caowUpdateDate2) {
		this.caowUpdateDate2 = caowUpdateDate2;
	}

	public boolean getShowCaowDetail() {
		return showCaowDetail;
	}

	public void setShowCaowDetail(boolean showCaowDetail) {
		this.showCaowDetail = showCaowDetail;
	}

	public Boolean getCaowDetailLevel() {
		return caowDetailLevel;
	}

	public void setCaowDetailLevel(Boolean caowDetailLevel) {
		this.caowDetailLevel = caowDetailLevel;
	}

	public Map<String, String> getCaowDetailList() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("", "Audit Only");
		map.put("false", "Audits with Status");
		map.put("true", "Audits with Status Detail");
		return map;
	}
}
