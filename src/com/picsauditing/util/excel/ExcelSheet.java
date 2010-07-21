package com.picsauditing.util.excel;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.util.Strings;

public class ExcelSheet {
	private String name = "Report";
	private Map<Integer, ExcelColumn> columns = new TreeMap<Integer, ExcelColumn>();
	private List<BasicDynaBean> data;

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
		Iterator<ExcelColumn> i = columns.values().iterator();

		while (i.hasNext()) {
			ExcelColumn column = i.next();
			if (column.getName().equals(name))
				i.remove();
		}
	}

	public HSSFWorkbook buildWorkbook() {
		return buildWorkbook(false);
	}

	public HSSFWorkbook buildWorkbook(boolean showOrder) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();

		HSSFDataFormat df = wb.createDataFormat();
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 12);

		short col = 0;
		for (ExcelColumn column : columns.values()) {

			HSSFCellStyle cellStyle = wb.createCellStyle();

			if (ExcelCellType.Date.equals(column.getCellType()))
				cellStyle.setDataFormat(df.getFormat("m/d/yyyy"));
			else if (ExcelCellType.Integer.equals(column.getCellType()))
				cellStyle.setDataFormat(df.getFormat("0"));
			else if (ExcelCellType.Double.equals(column.getCellType()))
				cellStyle.setDataFormat(df.getFormat("#,##0.00"));
			else if (ExcelCellType.Money.equals(column.getCellType()))
				cellStyle.setDataFormat(df.getFormat("($#,##0_);($#,##0)"));
			else
				cellStyle.setDataFormat(df.getFormat("@"));

			cellStyle.setFont(font);

			sheet.setDefaultColumnStyle(col, cellStyle);
			col++;
		}

		HSSFFont headerFont = wb.createFont();
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		HSSFCellStyle headerStyle = wb.createCellStyle();
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		wb.setSheetName(0, name);

		int rowNumber = 0;

		{
			// Add the Column Headers to the top of the report
			int columnCount = 0;
			HSSFRow r = sheet.createRow(rowNumber);
			rowNumber++;
			for (Map.Entry<Integer, ExcelColumn> entry : this.columns.entrySet()) {
				HSSFCell c = r.createCell(columnCount);
				if (showOrder)
					c.setCellValue(new HSSFRichTextString(entry.getValue().getColumnHeader() + " - " + entry.getKey()));
				else
					c.setCellValue(new HSSFRichTextString(entry.getValue().getColumnHeader()));
				c.setCellStyle(headerStyle);
				columnCount++;
			}
		}

		for (int i = 0; i < data.size(); i++) {
			DynaBean row = data.get(i);
			HSSFRow r = sheet.createRow(i + 1);
			int columnCount = 0;
			for (ExcelColumn column : this.columns.values()) {
				HSSFCell c = r.createCell(columnCount);
				// TODO Look at data type here

				try {
					if (row.get(column.getName()) == null)
						c.setCellValue(new HSSFRichTextString(""));
					else if (ExcelCellType.Date.equals(column.getCellType())) {
						c.setCellValue((Date) row.get(column.getName()));
					} else if (ExcelCellType.Integer.equals(column.getCellType())
							|| ExcelCellType.Double.equals(column.getCellType())
							|| ExcelCellType.Money.equals(column.getCellType()))
						c.setCellValue(Double.parseDouble(row.get(column.getName()).toString()));
					else if (ExcelCellType.Enum.equals(column.getCellType())) {
						String value = "";
						if ("riskLevel".equals(column.getName()))
							value = LowMedHigh.getName((Integer) row.get(column.getName()));
						else if ("waitingOn".equals(column.getName()))
							value = WaitingOn.valueOf((Integer) row.get(column.getName())).toString();
						c.setCellValue(new HSSFRichTextString(value));
					} else
						c.setCellValue(new HSSFRichTextString(row.get(column.getName()).toString()));
				} catch (Exception e) {
					if (!Strings.isEmpty(row.get(column.getName()).toString()))
						c.setCellValue(new HSSFRichTextString(row.get(column.getName()).toString()));
					else
						c.setCellValue(new HSSFRichTextString(""));
				}
				columnCount++;
			}
		}

		int c = 0;
		for (ExcelColumn column : columns.values()) {
			sheet.autoSizeColumn((short) c);
			sheet.setColumnHidden(c, column.isHidden());
			c++;
		}

		return wb;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<BasicDynaBean> getData() {
		return data;
	}

	public void setData(List<BasicDynaBean> data) {
		this.data = data;
	}

	public Map<Integer, ExcelColumn> getColumns() {
		return columns;
	}

}
