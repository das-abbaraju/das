package com.picsauditing.report;

import java.util.Collection;

import com.picsauditing.report.fields.PivotDimension;

public class ReportPivotDefinition {
	private Column row = null;
	private Column column = null;
	private Column cell = null;

	public ReportPivotDefinition(Collection<Column> columns) {
		for (Column columnIn : columns) {
			if (columnIn.getPivotDimension() == PivotDimension.Row) {
				row = columnIn;
			}
			if (columnIn.getPivotDimension() == PivotDimension.Column) {
				column = columnIn;
			}
			if (columnIn.getPivotDimension() == PivotDimension.Cell) {
				cell = columnIn;
			}
		}
	}

	public boolean isPivotable() {
		if (row == null)
			return false;
		if (column == null)
			return false;
		if (cell == null)
			return false;
		return true;
	}

	public Column getRow() {
		return row;
	}

	public Column getColumn() {
		return column;
	}

	public Column getCell() {
		return cell;
	}

}
