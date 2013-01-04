package com.picsauditing.report;

import com.picsauditing.report.fields.PivotCellMethod;
import com.picsauditing.report.fields.PivotDimension;

public class Column extends ReportElement {

	private int width = 200;
	private PivotDimension pivotDimension = null;
	private PivotCellMethod pivotCellMethod = null;

	public Column() {
	}

	public Column(String fieldName) {
		super(fieldName);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public PivotDimension getPivotDimension() {
		return pivotDimension;
	}

	public void setPivotDimension(PivotDimension pivotDimension) {
		this.pivotDimension = pivotDimension;
	}

	public PivotCellMethod getPivotCellMethod() {
		return pivotCellMethod;
	}

	public void setPivotCellMethod(PivotCellMethod pivotCellMethod) {
		this.pivotCellMethod = pivotCellMethod;
	}
}
