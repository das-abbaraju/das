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
}
