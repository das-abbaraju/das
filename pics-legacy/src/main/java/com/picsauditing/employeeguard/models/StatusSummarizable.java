package com.picsauditing.employeeguard.models;

public interface StatusSummarizable {

	int getCompleted();

	void setCompleted(int completed);

	@Deprecated
	int getPending();

	@Deprecated
	void setPending(int completed);

	int getExpiring();

	void setExpiring(int completed);

	int getExpired();

	void setExpired(int completed);

}
