package com.picsauditing.PICS;

import com.picsauditing.jpa.entities.SafetyStatistics;

public interface OshaVisitor {
	public void gatherData(SafetyStatistics safetyStatistics);
}
