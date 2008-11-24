package com.picsauditing.jpa.entities;

import java.util.ArrayList;

public enum AuditStatus {
	Pending,
	Submitted,
	Active,
	Exempt,
	Expired;

	public static String DEFAULT = "- Audit Status -";
	public static ArrayList<String> getValuesWithDefault() {
		ArrayList<String> values =  new ArrayList<String>();
		values.add(AuditStatus.DEFAULT);
		for(AuditStatus value : AuditStatus.values())
			values.add(value.name());
		return values;
	}
	
	/**
	 * Is the status Active or Exempt
	 * @return
	 */
	public boolean isActiveExempt() {
		if (this.equals(Active))
			return true;
		if (this.equals(Exempt))
			return true;
		return false;
	}

	/**
	 * Is the status Active or Exempt or Submitted
	 * @return
	 */
	public boolean isActiveExemptSubmitted() {
		if (this.equals(Submitted))
			return true;
		return isActiveExempt();
	}


	/**
	 * Is the status Active or Exempt or Submitted
	 * @return
	 */
	public boolean isActiveSubmitted() {
		if (this.equals(Submitted))
			return true;
		if (this.equals(Active))
			return true;
		return false;
	}
}
