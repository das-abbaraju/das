package com.picsauditing.jpa.entities;

import java.util.HashMap;
import java.util.List;

public class CohsStatistics extends SafetyStatistics{
	public CohsStatistics(int year, List<AuditData> data) {
		super(year, OshaType.COHS, data);

	}

	@Override
	public String toString() {
		return null;
	}
}
