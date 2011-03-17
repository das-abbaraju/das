package com.picsauditing.util;

import com.picsauditing.jpa.entities.AuditStatus;

@SuppressWarnings("serial")
public class ReportFilterCAOW extends ReportFilterCAO {

	protected boolean showCaowStatus = true;

	protected AuditStatus[] caowStatus;

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

}
