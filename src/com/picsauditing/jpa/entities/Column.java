package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.report.fields.PivotCellMethod;
import com.picsauditing.report.fields.PivotDimension;

@Entity
@Table(name = "report_column")
public class Column extends ReportElement {

	private int width = DEFAULT_WIDTH;
	private int sortIndex = DEFAULT_SORT_INDEX;

	public static final int MIN_WIDTH = 35;
	public static final int DEFAULT_WIDTH = 100;
	public static final int DEFAULT_SORT_INDEX = 1;

	public Column() {
	}

	public Column(String fieldName) {
		super(fieldName);
	}

	public int getWidth() {
		if (width < 1 && field != null) {
			return field.getWidth();
		}

		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

	@Transient
	public PivotDimension getPivotDimension() {
		// Need to move this to another table or decide to store it here
		return null;
	}

	@Transient
	public PivotCellMethod getPivotCellMethod() {
		// Need to move this to another table or decide to store it here
		return null;
	}
}
