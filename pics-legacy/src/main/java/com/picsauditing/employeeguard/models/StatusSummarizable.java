package com.picsauditing.employeeguard.models;

public interface StatusSummarizable extends StatusCountSummary {

	int getEmployees();

	void setEmployees(int employees);

}
