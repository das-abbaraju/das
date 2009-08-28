package com.picsauditing.util.chart;

public enum ChartType {
	Area2D, Bar2D, Column2D, Column3D, Line, 
	MSArea(true), MSBar2D(true), MSBar3D(true), 
	MSColumn2D(true), MSColumn3D(true), MSColumn3DLineDY(true), MSColumnLine3D(true), 
	MSCombi2D(true), MSCombiDY2D(true), MSLine(true),
	MSStackedColumn2D(true), MSStackedColumn2DLineDY(true),
	Pie2D, Pie3D, Scatter,
	ScrollArea2D, ScrollColumn2D, ScrollCombiDY2D, ScrollLine2D, ScrollStackedColumn2D, SSGrid,
	StackedArea2D, StackedBar2D, StackedBar3D,
	StackedColumn2D, StackedColumn3D, StackedColumn3DLineDY;
	
	boolean multiSeries = false;
	
	private ChartType() {
	}
	private ChartType(boolean multiSeries) {
		this.multiSeries = multiSeries;
	}

}
