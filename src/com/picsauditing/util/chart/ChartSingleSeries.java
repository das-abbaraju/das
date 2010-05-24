package com.picsauditing.util.chart;

import java.util.ArrayList;
import java.util.List;

public class ChartSingleSeries extends Chart {

	protected List<Set> sets = new ArrayList<Set>();
	protected List<TrendLine> trendLines = new ArrayList<TrendLine>();

	protected void addData() {
		for (Set set : sets)
			xml.append(set.toString());
		
		if (trendLines.size() > 0) {
			xml.append("<trendLines>");
			
			for (TrendLine trend : trendLines)
				xml.append(trend.toString());
			
			xml.append("</trendLines>");
		}
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
	
	public void addTrendLine(TrendLine trendLine) {
		trendLines.add(trendLine);
	}
	
	public List<TrendLine> getTrendLines() {
		return trendLines;
	}
	
	public void setTrendLines(List<TrendLine> trendLines) {
		this.trendLines = trendLines;
	}

	public boolean hasData() {
		return sets.size() > 0;
	}
}
