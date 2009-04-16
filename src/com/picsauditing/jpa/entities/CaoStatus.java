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
		if (this.equals(NotApplicable)) {
			return "";
		}		

		StringBuilder icon = new StringBuilder("<img src=\"images/");
		
		if (this.isApproved())
			icon.append("okCheck");
		else if (this.isRejected())
			icon.append("notOkCheck");
		else
			icon.append("help");
		
		icon.append(".gif\" width=\"18\" height=\"15\" border=\"0\" title=\"").append(this.toString()).append("\" />");

		return icon.toString();
	}

	static public String getIcon(String status) {
		if ("NotApplicable".equals(status))
			return "";
		
		StringBuilder icon = new StringBuilder("<img src=\"images/");
		
		if ("Approved".equals(status)) 
			icon.append("okCheck");
		else if ("Rejected".equals(status)) 
			icon.append("notOkCheck");
		else
			icon.append("help");
		
		icon.append(".gif\" width=\"18\" height=\"15\" border=\"0\" title=\"").append(status).append("\" />");

		return icon.toString();
	}

}
