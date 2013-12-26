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
import org.apache.poi.ss.usermodel.DataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.jpa.entities.Column;
import com.picsauditing.report.data.ReportCell;
import com.picsauditing.report.data.ReportResults;
import com.picsauditing.report.data.ReportRow;

public class ExcelBuilder {
	private HSSFWorkbook workbook = new HSSFWorkbook();;
	private List<ExcelColumn> columns = new ArrayList<ExcelColumn>();
	private static short FONT_SIZE = 12;
    private int maxRows;

    private static final Logger logger = LoggerFactory.getLogger(ExcelBuilder.class);

    public HSSFWorkbook getWorkbook() {
		return workbook;
	}

	public HSSFSheet addSheet(String sheetName, ReportResults data) {
		sheetName = cleanSheetName(sheetName);

		HSSFSheet sheet = workbook.createSheet();
		workbook.setSheetName(workbook.getNumberOfSheets() - 1, sheetName);

		setColumnStyles(sheet);
		addColumnHeadings(sheet);
		buildRows(sheet, data);
		autoSizeColumns(sheet);
		return sheet;
	}

	static private String cleanSheetName(String sheetName) {
		sheetName = sheetName.replace("/", " ");
		sheetName = sheetName.replace("\\", " ");
		sheetName = sheetName.replace("?", " ");
		sheetName = sheetName.replace("*", " ");
		sheetName = sheetName.replace("[", " ");
		sheetName = sheetName.replace("]", " ");
		return sheetName;
	}

	private void setColumnStyles(HSSFSheet sheet) {
		HSSFFont font = createFont();

		short colCounter = 0;
		for (ExcelColumn column : columns) {
			HSSFCellStyle cellStyle = workbook.createCellStyle();
            DataFormat df = workbook.getCreationHelper().createDataFormat();
            String format = column.getCellType().getFormat();
            cellStyle.setDataFormat(df.getFormat(format));
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
			if (column.isHidden()) {
				sheet.setColumnHidden(columnCount, true);
			}
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
            if (maxRows > 0 && rowCounter == maxRows) {
                break;
            }

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
            try {
                cell.setCellValue((Date) dataValue);
            }
            catch(ClassCastException cce) {
                cell.setCellValue(new HSSFRichTextString(dataValue.toString()));
            }
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

	private void autoSizeColumns(HSSFSheet sheet) {
		for (int c = 0; c < columns.size(); c++) {
			sheet.autoSizeColumn((short) c);
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

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }
}
