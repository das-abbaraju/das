package com.picsauditing.util.excel;

public class ExcelColumn {
	private String name;
	private String columnHeader;
	private boolean hidden = false;
	private ExcelCellType cellType = ExcelCellType.String;

	public ExcelColumn(String name) {
		this.name = name;
	}

	public ExcelColumn(String name, String columnHeader) {
		this.name = name;
		this.columnHeader = columnHeader;
	}

	public ExcelColumn(String name, ExcelCellType cellType) {
		this.name = name;
		this.cellType = cellType;
	}

	public ExcelColumn(String name, String columnHeader, ExcelCellType cellType) {
		this.name = name;
		this.columnHeader = columnHeader;
		this.cellType = cellType;
	}

	/**
	 * Database column from an SQL result. This is usually unique, unless you
	 * want to include the same column twice.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColumnHeader() {
		if (columnHeader == null)
			return name;

		return columnHeader;
	}

	public void setColumnHeader(String columnHeader) {
		this.columnHeader = columnHeader;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public ExcelCellType getCellType() {
		return cellType;
	}

	public void setCellType(ExcelCellType cellType) {
		this.cellType = cellType;
	}

}
