package com.picsauditing.report;

import com.picsauditing.report.fields.PivotCellMethod;
import com.picsauditing.report.fields.PivotDimension;

public class Column extends ReportElement {

	private PivotDimension pivotDimension = null;
	private PivotCellMethod pivotCellMethod = null;

	public Column() {
	}

	public Column(String fieldName) {
		super(fieldName);
	}

	public PivotDimension getPivotDimension() {
		return pivotDimension;
	}

	public PivotCellMethod getPivotCellMethod() {
		return pivotCellMethod;
	}
}
