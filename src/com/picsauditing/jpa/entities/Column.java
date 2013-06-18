package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.report.fields.*;

@Entity
@Table(name = "report_column")
public class Column extends ReportElement implements Comparable<Column> {

	private String columnId;
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

	@Transient
	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

    @ReportField(type = FieldType.Integer)
	public int getWidth() {
		if (shouldOverrideDefaultWidthWithFieldAnnotation()) {
			width = field.getWidth();
		}

		return width;
	}

	private boolean shouldOverrideDefaultWidthWithFieldAnnotation() {
		return (width == DEFAULT_WIDTH) && (field != null) && (field.getWidth() > 0);
	}

	public void setWidth(int width) {
		this.width = width;
	}

    @ReportField(type = FieldType.Integer)
	public int getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

	@Transient
	public DisplayType getDisplayType() {
		if (sqlFunction != null) {
			return sqlFunction.getDisplayType(getField());
		}
		return field.getDisplayType();
	}

	@Override
	public int compareTo(Column otherColumn) {
		return Double.compare(sortIndex, otherColumn.getSortIndex());
	}

}
