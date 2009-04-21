package com.picsauditing.jpa.entities;

public enum CaoStatus {
	NotApplicable, Pending, Submitted, Verified, Approved, Rejected;

	/**
	 * 
	 * @return TRUE if NotApplicable or Pending
	 */
	public boolean isTemporary() {
		if (this.equals(NotApplicable))
			return true;
		if (this.equals(Pending))
			return true;
		return false;
	}

	public boolean isNotApplicable() {
		return this.equals(CaoStatus.NotApplicable);
	}

	public boolean isPending() {
		return this.equals(CaoStatus.Pending);
	}

	public boolean isSubmitted() {
		return this.equals(CaoStatus.Submitted);
	}

	public boolean isVerified() {
		return this.equals(CaoStatus.Verified);
	}

	public boolean isApproved() {
		return this.equals(CaoStatus.Approved);
	}

	public boolean isRejected() {
		return this.equals(CaoStatus.Rejected);
	}

	public String getIcon() {
		String title;
		if (this.equals(NotApplicable)) {
			return "";
		}

		StringBuilder icon = new StringBuilder("<img src=\"images/");

		if (this.isApproved()) {
			icon.append("okCheck");
			title = this.toString();
		} else if (this.isRejected()) {
			icon.append("notOkCheck");
			title = this.toString();
		} else {
			icon.append("help");
			title = "No Recommendation";
		}

		icon.append(".gif\" title=\"").append(title).append("\" />");

		return icon.toString();
	}

	static public String getIcon(String status) {
		return valueOf(status).getIcon();
	}

}
