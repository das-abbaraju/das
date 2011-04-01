package com.picsauditing.util;

import java.util.Date;

import com.picsauditing.jpa.entities.AuditStatus;

@SuppressWarnings("serial")
public class ReportFilterCAOW extends ReportFilterCAO {

	protected boolean showCaowStatus = true;
	protected boolean showCaowUpdateDate = true;

	protected AuditStatus[] caowStatus;
	protected Date caowUpdateDate1;
	protected Date caowUpdateDate2;

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

}
