package com.picsauditing.util.chart;

import java.util.ArrayList;
import java.util.List;

public class ChartSingleSeries extends Chart {

	protected List<Set> sets = new ArrayList<Set>();

	protected void addData() {
		for (Set set : sets)
			xml.append(set.toString());
	}


	public void addSet(Set set) {
		sets.add(set);
	}

	public List<Set> getSets() {
		return sets;
	}

	public void setSets(List<Set> sets) {
		this.sets = sets;
	}

	public boolean hasData() {
		return sets.size() > 0;
	}
}
