package com.picsauditing.util;

import java.util.Date;

import com.picsauditing.access.Permissions;

@SuppressWarnings("serial")
public class ReportFilterNewClientSite extends ReportFilterAccount {
	protected boolean showViewAll = false;
	protected boolean showCreationDate = true;
	protected boolean showClosedOnDate = true;
	protected boolean showReferralStatus = true;

	protected boolean viewAll = false;
	protected Date creationDate1;
	protected Date creationDate2;
	protected Date closedOnDate1;
	protected Date closedOnDate2;
	protected String referralStatus = "Active";

	@Override
	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}

	public boolean isShowViewAll() {
		return showViewAll;
	}

	public void setShowViewAll(boolean showViewAll) {
		this.showViewAll = showViewAll;
	}

	public boolean isShowCreationDate() {
		return showCreationDate;
	}

	public void setShowCreationDate(boolean showCreationDate) {
		this.showCreationDate = showCreationDate;
	}

	public boolean isShowClosedOnDate() {
		return showClosedOnDate;
	}

	public void setShowClosedOnDate(boolean showClosedOnDate) {
		this.showClosedOnDate = showClosedOnDate;
	}

	public boolean isShowReferralStatus() {
		return showReferralStatus;
	}

	public void setShowReferralStatus(boolean showReferralStatus) {
		this.showReferralStatus = showReferralStatus;
	}

	public boolean isViewAll() {
		return viewAll;
	}

	public void setViewAll(boolean viewAll) {
		this.viewAll = viewAll;
	}

	public Date getCreationDate1() {
		return creationDate1;
	}

	public void setCreationDate1(Date creationDate1) {
		this.creationDate1 = creationDate1;
	}

	public Date getCreationDate2() {
		return creationDate2;
	}

	public void setCreationDate2(Date creationDate2) {
		this.creationDate2 = creationDate2;
	}

	public Date getClosedOnDate1() {
		return closedOnDate1;
	}

	public void setClosedOnDate1(Date closedOnDate1) {
		this.closedOnDate1 = closedOnDate1;
	}

	public Date getClosedOnDate2() {
		return closedOnDate2;
	}

	public void setClosedOnDate2(Date closedOnDate2) {
		this.closedOnDate2 = closedOnDate2;
	}

	public String getReferralStatus() {
		return referralStatus;
	}

	public void setReferralStatus(String referralStatus) {
		this.referralStatus = referralStatus;
	}
}