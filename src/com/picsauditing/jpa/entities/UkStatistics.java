package com.picsauditing.jpa.entities;

import java.util.HashMap;
import java.util.List;

public class UkStatistics extends SafetyStatistics{
	public UkStatistics(int year, List<AuditData> data) {
		super(year, OshaType.UK_HSE, data);
		answerMap = new HashMap<OshaRateType, String>();
		for (AuditData answer: data) {
			if (answer.getQuestion().getId() == 9060) {
				answerMap.put(OshaRateType.IFR, answer.getAnswer());
			}
		}	
	}

	@Override
	public String toString() {
		return "|UK Stats|";
	}
}
