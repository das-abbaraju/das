package com.picsauditing.oshadisplay;

import java.util.ArrayList;
import java.util.List;

public abstract class OshaDisplayRow {

	private List<String> cells;
	
	public OshaDisplayRow() {
		cells = new ArrayList<String>();
	}
	
	abstract public String getTitle();
	abstract public boolean isHurdleRate();

	public void addCell(String display) {
		cells.add(display);
	}

	public int size() {
		return cells.size();
	}

	public List<String> getCells() {
		return cells;
	}
}
