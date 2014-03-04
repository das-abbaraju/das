package com.picsauditing.employeeguard.models;

public interface StatusSummary {

	int getCompleted();
	void setCompleted(int completed);
	int getPending();
	void setPending(int pending);
	int getExpiring();
	void setExpiring(int expiring);
	int getExpired();
	void setExpired(int expired);

}
