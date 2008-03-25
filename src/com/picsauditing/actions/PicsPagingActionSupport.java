package com.picsauditing.actions;

public class PicsPagingActionSupport extends PicsActionSupport {
	protected String orderBy = null;
	protected int showPage = 1;
	protected String startsWith = null;
	
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public int getShowPage() {
		return showPage;
	}
	public void setShowPage(int showPage) {
		this.showPage = showPage;
	}

	public String getStartsWith() {
		return startsWith;
	}
	public void setStartsWith(String startsWith) {
		this.startsWith = startsWith;
	}

}
