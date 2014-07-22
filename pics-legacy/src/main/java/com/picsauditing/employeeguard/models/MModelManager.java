package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.models.operations.MOperations;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class MModelManager {

	protected Set<MOperations> mOperations = new LinkedHashSet();

/*
	protected List<MOperations> mOldOperations;

	public void setmOldOperations(List<MOperations> mOldOperations) {
		this.mOldOperations = mOldOperations;
	}
*/

}


