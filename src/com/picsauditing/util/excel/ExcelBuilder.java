package com.picsauditing.util.excel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.report.Column;

public class ExcelBuilder {
	private HSSFWorkbook workbook;
	private HSSFSheet sheet;
	private List<ExcelColumn> columns = new ArrayList<ExcelColumn>();
	private static short FONT_SIZE = 12;

	private static final Logger logger = LoggerFactory.getLogger(ExcelBuilder.class);

	public HSSFWorkbook buildWorkbook(String name, JSONArray data) {
		workbook = new HSSFWorkbook();
		sheet = workbook.createSheet();
		workbook.setSheetName(0, name);

		setColumnStyles();
		addColumnHeadings();
		buildRows(data);
		autoSizeColumns();

		return workbook;
	}

	private void setColumnStyles() {
		HSSFFont font = createFont();
		HSSFDataFormat df = workbook.createDataFormat();

		short colCounter = 0;
		for (ExcelColumn column : columns) {
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setDataFormat(df.getFormat(column.getCellType().getFormat()));
			cellStyle.setFont(font);
			sheet.setDefaultColumnStyle(colCounter, cellStyle);
			colCounter++;
		}
	}

	private void addColumnHeadings() {
		HSSFCellStyle headerStyle = createHeaderStyle();
		HSSFRow row = sheet.createRow(0);

		int columnCount = 0;
		for (ExcelColumn column : columns) {
			HSSFCell c = row.createCell(columnCount);
			c.setCellValue(new HSSFRichTextString(column.getColumnHeader()));
			c.setCellStyle(headerStyle);
			columnCount++;
		}
	}

	private HSSFCellStyle createHeaderStyle() {
		HSSFFont headerFont = createFont();
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		HSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		return headerStyle;
	}

	private HSSFFont createFont() {
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints(FONT_SIZE);
		return font;
	}

	private void buildRows(JSONArray data) {
		for (int i = 0; i < data.size(); i++) {
			JSONObject row = (JSONObject) data.get(i);
			HSSFRow r = sheet.createRow(i + 1);
			buildRow(row, r);
		}
	}

	private void buildRow(JSONObject row, HSSFRow r) {
		int columnCount = 0;
		for (ExcelColumn column : columns) {
			HSSFCell cell = r.createCell(columnCount);

			try {
				Object dataValue = row.get(column.getName());
				setCellValue(dataValue, column, cell);
			} catch (Exception e) {
				logger.error("Failed to build row cell");
				cell.setCellValue(new HSSFRichTextString("error"));
			}
			columnCount++;
		}
	}

	private void setCellValue(Object dataValue, ExcelColumn column, HSSFCell cell) {
		if (dataValue == null) {
			cell.setCellValue(new HSSFRichTextString(""));
			return;
		}

		switch (column.getCellType()) {
		case Date:
			cell.setCellValue((Date) dataValue);
			break;
		case Double:
		case Integer:
		case Money:
			cell.setCellValue(Double.parseDouble(dataValue.toString()));
			break;
		default:
			cell.setCellValue(new HSSFRichTextString(dataValue.toString()));
			break;
		}
	}

	private void autoSizeColumns() {
		int c = 0;
		for (ExcelColumn column : columns) {
			sheet.autoSizeColumn((short) c);
			if (column.isHidden()) {
				sheet.setColumnHidden(c, true);
			}
			c++;
		}
	}

	public void addColumns(List<Column> reportColumns) {
		for (Column reportColumn : reportColumns) {
			columns.add(new ExcelColumn(reportColumn));
		}
	}
}
