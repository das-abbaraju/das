package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.report.fields.PivotCellMethod;
import com.picsauditing.report.fields.PivotDimension;

@SuppressWarnings("serial")
@Entity
@Table(name = "report_column")
public class Column extends ReportElement {

	private int width = 100;

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
