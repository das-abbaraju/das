package com.picsauditing.PICS.data;

import java.util.Observable;

public class DataObservable extends Observable {

	@Override
	public synchronized void setChanged() {
		super.setChanged();
	}

	@Override
	public synchronized void clearChanged() {
		super.clearChanged();
	}
	
}
