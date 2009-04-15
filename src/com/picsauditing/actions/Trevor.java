package com.picsauditing.actions;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;

@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		HSSFWorkbook wb = buildExcel();

		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=tester.xls");
		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		wb.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();
		return null;
	}

	public HSSFWorkbook buildExcel() throws IOException {

		// create a new workbook
		HSSFWorkbook wb = new HSSFWorkbook();
		// create a new sheet
		
		HSSFSheet s = wb.createSheet();
		// declare a row object reference
		HSSFRow r = null;
		// declare a cell object reference
		HSSFCell c = null;
		// create 3 cell styles
		HSSFCellStyle cs = wb.createCellStyle();
		HSSFCellStyle cs2 = wb.createCellStyle();
		HSSFCellStyle cs3 = wb.createCellStyle();
		HSSFDataFormat df = wb.createDataFormat();
		// create 2 fonts objects
		HSSFFont f = wb.createFont();
		HSSFFont f2 = wb.createFont();

		// set font 1 to 12 point type
		f.setFontHeightInPoints((short) 12);
		// make it blue
		f.setColor((short) 0xc);
		// make it bold
		// arial is the default font
		f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		// set font 2 to 10 point type
		f2.setFontHeightInPoints((short) 10);
		// make it red
		f2.setColor((short) HSSFFont.COLOR_RED);
		// make it bold
		f2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		f2.setStrikeout(true);

		// set cell stlye
		cs.setFont(f);
		// set the cell format
		cs.setDataFormat(df.getFormat("#,##0.0"));

		// set a thin border
		cs2.setBorderBottom(cs2.BORDER_THIN);
		// fill w fg fill color
		cs2.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
		// set the cell format to text see DataFormat for a full list
		cs2.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));

		// set the font
		cs2.setFont(f2);

		// set the sheet name in Unicode
		wb.setSheetName(0, "\u0422\u0435\u0441\u0442\u043E\u0432\u0430\u044F "
				+ "\u0421\u0442\u0440\u0430\u043D\u0438\u0447\u043A\u0430");
		// in case of plain ascii
		// wb.setSheetName(0, "HSSF Test");
		// create a sheet with 30 rows (0-29)
		int rownum;
		for (rownum = (short) 0; rownum < 30; rownum++) {
			// create a row
			r = s.createRow(rownum);
			// on every other row
			if ((rownum % 2) == 0) {
				// make the row height bigger (in twips - 1/20 of a point)
				r.setHeight((short) 0x249);
			}

			// r.setRowNum(( short ) rownum);
			// create 10 cells (0-9) (the += 2 becomes apparent later
			for (short cellnum = (short) 0; cellnum < 10; cellnum += 2) {
				// create a numeric cell
				c = r.createCell(cellnum);
				// do some goofy math to demonstrate decimals
				c.setCellValue(rownum * 10000 + cellnum + (((double) rownum / 1000) + ((double) cellnum / 10000)));

				String cellValue;

				// create a string cell (see why += 2 in the
				c = r.createCell((short) (cellnum + 1));

				// on every other row
				if ((rownum % 2) == 0) {
					// set this cell to the first cell style we defined
					c.setCellStyle(cs);
					// set the cell's string value to "Test"
					c.setCellValue("Test");
				} else {
					c.setCellStyle(cs2);
					// set the cell's string value to "\u0422\u0435\u0441\u0442"
					c.setCellValue("\u0422\u0435\u0441\u0442");
				}

				// make this column a bit wider
				s.setColumnWidth((short) (cellnum + 1), (short) ((50 * 8) / ((double) 1 / 20)));
			}
		}

		// draw a thick black border on the row at the bottom using BLANKS
		// advance 2 rows
		rownum++;
		rownum++;

		r = s.createRow(rownum);

		// define the third style to be the default
		// except with a thick black border at the bottom
		cs3.setBorderBottom(cs3.BORDER_THICK);

		// create 50 cells
		for (short cellnum = (short) 0; cellnum < 50; cellnum++) {
			// create a blank type cell (no value)
			c = r.createCell(cellnum);
			// set it to the thick black border style
			c.setCellStyle(cs3);
		}

		// end draw thick black border

		// demonstrate adding/naming and deleting a sheet
		// create a sheet, set its title then delete it
		s = wb.createSheet();
		wb.setSheetName(1, "DeletedSheet");
		wb.removeSheetAt(1);
		// end deleted sheet

		// write the workbook to the output stream
		// close our file (don't blow out our file handles)
		// create a new file
		return wb;
	}
}
