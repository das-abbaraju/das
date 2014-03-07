package com.picsauditing.employeeguard.models;

public interface StatusSummarizable {

//	StatusSummary getStatus();
//	void setStatus(StatusSummary summary);

	int getCompleted();

	void setCompleted(int completed);

	int getPending();

	void setPending(int completed);

	int getExpiring();

	void setExpiring(int completed);

	int getExpired();

	void setExpired(int completed);

}
