package com.picsauditing.util.excel;

import java.util.ArrayList;
import java.util.Collection;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.report.Column;
import com.picsauditing.report.data.ReportCell;
import com.picsauditing.report.data.ReportResults;
import com.picsauditing.report.data.ReportRow;

public class ExcelBuilder {
	private HSSFWorkbook workbook = new HSSFWorkbook();;
	private List<ExcelColumn> columns = new ArrayList<ExcelColumn>();
	private static short FONT_SIZE = 12;

	private static final Logger logger = LoggerFactory.getLogger(ExcelBuilder.class);

	public HSSFWorkbook getWorkbook() {
		return workbook;
	}
	
	public HSSFSheet addSheet(String sheetName, ReportResults data) {
		HSSFSheet sheet = workbook.createSheet();
		sheetName = sheetName.replace("/", " ");
		sheetName = sheetName.replace("\\", " ");
		sheetName = sheetName.replace("?", " ");
		sheetName = sheetName.replace("*", " ");
		sheetName = sheetName.replace("[", " ");
		sheetName = sheetName.replace("]", " ");
		
		workbook.setSheetName(workbook.getNumberOfSheets() - 1, sheetName);

		setColumnStyles(sheet);
		addColumnHeadings(sheet);
		buildRows(sheet, data);

		return sheet;
	}

	private void setColumnStyles(HSSFSheet sheet) {
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

	private void addColumnHeadings(HSSFSheet sheet) {
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

	private void buildRows(HSSFSheet sheet, ReportResults data) {
		int rowCounter = 1;
		for (ReportRow row : data.getRows()) {
			HSSFRow r = sheet.createRow(rowCounter++);
			buildRow(row, r);
		}
	}

	private void buildRow(ReportRow row, HSSFRow r) {
		int columnCount = 0;
		for (ExcelColumn column : columns) {
			HSSFCell sheetCell = r.createCell(columnCount);

			try {
				ReportCell reportCell = row.getCellByColumn(column.getReportColumn());
				setCellValue(reportCell.getValue(), column, sheetCell);
			} catch (Exception e) {
				logger.error("Failed to build row cell");
				sheetCell.setCellValue(new HSSFRichTextString("error"));
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

	public void autoSizeColumns() {
		for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
			HSSFSheet sheet = workbook.getSheetAt(sheetIndex);

			int c = 0;
			for (ExcelColumn column : columns) {
				sheet.autoSizeColumn((short) c);
				if (column.isHidden()) {
					sheet.setColumnHidden(c, true);
				}
				c++;
			}
		}
	}

	public List<ExcelColumn> getColumns() {
		return columns;
	}

	public void addColumns(Collection<Column> reportColumns) {
		for (Column column : reportColumns) {
			columns.add(new ExcelColumn(column));
		}
	}
}
