package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.List;

public class StatisticsDisplayRow {

	private boolean hurdleRates;
	private List<String> cells;

	public StatisticsDisplayRow(boolean hurdleRates) {
		this.hurdleRates = hurdleRates;
		cells = new ArrayList<String>();
	}

	public boolean isHurdleRates() {
		return hurdleRates;
	}

	public void setHurdleRates(boolean hurdleRates) {
		this.hurdleRates = hurdleRates;
	}

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
