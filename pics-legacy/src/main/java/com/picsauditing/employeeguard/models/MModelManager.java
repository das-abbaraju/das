package com.picsauditing.employeeguard.models;

import java.util.List;

public abstract class MModelManager {

	List<MOperations> mOperations;

	public List<MOperations> getmOperations() {
		return mOperations;
	}

	public void setmOperations(List<MOperations> mOperations) {
		this.mOperations = mOperations;
	}
}
