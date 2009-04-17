package com.picsauditing.util.excel;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.DynaBean;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelSheet {
	private String name = "Report";
	private Map<Integer, ExcelColumn> columns = new TreeMap<Integer, ExcelColumn>();
	private List<DynaBean> data;

	private int lastDisplayOrderAdded = 0;

	/**
	 * Add the column after the last column you added
	 * 
	 * @param column
	 */
	public void addColumn(ExcelColumn column) {
		addColumn(column, lastDisplayOrderAdded + 10);
	}

	public void addColumn(ExcelColumn column, int displayOrder) {
		lastDisplayOrderAdded = displayOrder;
		columns.put(displayOrder, column);
	}

	public void removeColumn(String name) {
		// TODO
	}

	public HSSFWorkbook buildWorkbook() {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet s = wb.createSheet();

		HSSFCellStyle cs = wb.createCellStyle();
		HSSFCellStyle headerStyle = wb.createCellStyle();
		HSSFCellStyle cs3 = wb.createCellStyle();
		HSSFDataFormat df = wb.createDataFormat();
		HSSFFont f = wb.createFont();
		f.setFontHeightInPoints((short) 12);
		cs.setFont(f);
		cs.setDataFormat(df.getFormat("#,##0.0"));

		wb.setSheetName(0, name);

		int rowNumber = 0;

		{
			// Add the Column Headers to the top of the report
			int columnCount = 0;
			HSSFRow r = s.createRow(rowNumber);
			rowNumber++;
			for (ExcelColumn column : this.columns.values()) {
				HSSFCell c = r.createCell(columnCount);
				c.setCellValue(column.getColumnHeader());
				c.setCellStyle(headerStyle);
				columnCount++;
			}
		}
		if (true) {
			// Add in another row to show column sequence
			int columnCount = 0;
			HSSFRow r = s.createRow(rowNumber);
			rowNumber++;
			for (ExcelColumn column : this.columns.values()) {
				HSSFCell c = r.createCell(columnCount);
				c.setCellValue(column.getColumnHeader());
				c.setCellStyle(headerStyle);
				columnCount++;
			}
		}

		for (int i = 0; i < data.size(); i++) {
			DynaBean row = data.get(i);
			HSSFRow r = s.createRow(i + 1);
			int columnCount = 0;
			for (ExcelColumn column : this.columns.values()) {
				HSSFCell c = r.createCell(columnCount);
				c = r.createCell(0);
				// TODO Look at data type here
				c.setCellValue(row.get(column.getName()).toString());
				c.setCellStyle(cs);
				columnCount++;
			}
		}
		return wb;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DynaBean> getData() {
		return data;
	}

	public void setData(List<DynaBean> data) {
		this.data = data;
	}

}
