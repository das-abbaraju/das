package com.picsauditing.PICS;

import jxl.*; 
import jxl.write.*;
import jxl.format.Colour;
import java.sql.*;
import java.util.*;


public class ExcelWriterBean {
	static final String[] CP_REPORT_TITLES_ARRAY = {"License Number","Company Name","FedTaxIDNn",
			"WWW Site Address","E-Mail Address","Street Address","City","State","Zip","Contact","Phone",
			"Fax","Auditor","Activity Status","Audit Date"};	
	static final String[] CP_REPORT_DB_COLUMNS_ARRAY = {"","name","",
			"web_URL","email","address","city","state","zip","contact","phone",
			"fax","PICS","status","auditDate"};	
	private static String rootPath = null;
	private static String fileNamePre = "temp_7";
	private static String fileNamePost = "1.xls";
	private static String fileDir = "tempFiles/";
	private WritableWorkbook workbook = null;
	private WritableSheet sheet = null;
	int rowCount = 0;
//	static WritableFont pendingFont = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.);
	static WritableFont activeFont = new WritableFont(WritableFont.TIMES);
	static WritableFont inactiveFont = new WritableFont(WritableFont.TIMES);

	static WritableCellFormat grayActiveCell = null;
	static WritableCellFormat grayInactiveCell = null;
	static WritableCellFormat whiteActiveCell = null;
	static WritableCellFormat whiteInactiveCell = null;
	WritableCellFormat tempCell = null;

	int nameLength = 1;
	int mainTradeLength = 1;
	int allTradesLength = 1;
	int addressLength = 1;
	int contactLength = 1;
	int emailLength = 1;
	int phoneLength = 1;

	public static void init(javax.servlet.ServletConfig config) throws Exception {
	// called eBean.init(config);
		if (null == rootPath) {
			rootPath = config.getServletContext().getRealPath("/");
//			activeFont.setColour(Colour.DARK_BLUE);
			activeFont.setColour(Colour.GREEN);
			inactiveFont.setColour(Colour.ROSE);

			grayActiveCell = new WritableCellFormat(activeFont);
			grayInactiveCell = new WritableCellFormat(inactiveFont);
			whiteActiveCell = new WritableCellFormat(activeFont);
			whiteInactiveCell = new WritableCellFormat(inactiveFont);

			grayActiveCell.setBackground(Colour.GRAY_25);
			grayInactiveCell.setBackground(Colour.GRAY_25);
		}//if
	}//init

	public void openTempExcelFile(String id) throws Exception {
		if (null == rootPath)
			throw new Exception("ExcelWriterBean Bean not initialized with path");
		if ("-1".equals(id))
			id="1";
		String fileName = fileNamePre + id + fileNamePost;
		String filePath = fileDir + fileName;
		java.io.File outFile = new java.io.File(rootPath + filePath);
		workbook = Workbook.createWorkbook(outFile);

		workbook.setColourRGB(Colour.GOLD,0xCC,0x99,0);
		workbook.setColourRGB(Colour.DARK_BLUE,0,0x33,0x66);
		workbook.setColourRGB(Colour.ROSE,0x99,0,0);
		workbook.setColourRGB(Colour.GRAY_25,0xDD,0xDD,0xDD);
// green 006600		
		sheet = workbook.createSheet("PICS data", 0);
	}//openTempExcelFile

	public void writeHeaders(PermissionsBean pBean) throws Exception {
		WritableFont times16BoldFont = new WritableFont(WritableFont.TIMES, 16, WritableFont.BOLD); 
		WritableCellFormat times16BoldFormat = new WritableCellFormat (times16BoldFont); 
		WritableFont times12BoldFont = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD); 
		WritableCellFormat times12BoldFormat = new WritableCellFormat (times12BoldFont); 
		int colCount = 0;
		sheet.addCell(new Label(colCount++, 0, "Contractor Contact Info", times16BoldFormat));
		sheet.addCell(new Label(colCount++, 1, "Contractor", times12BoldFormat));
		sheet.addCell(new Label(colCount++, 1, "Main Trade", times12BoldFormat));
		sheet.addCell(new Label(colCount++, 1, "PICS Status", times12BoldFormat));
		if (pBean.oBean.canSeePQF()){
			sheet.addCell(new Label(colCount++, 1, "PQF Status", times12BoldFormat));
			sheet.addCell(new Label(colCount++, 1, "Last PQF Update", times12BoldFormat));
		}//if
		if (pBean.oBean.canSeeDesktop()){
			sheet.addCell(new Label(colCount++, 1, "Desktop Status", times12BoldFormat));
			sheet.addCell(new Label(colCount++, 1, "Desktop RQs Closed", times12BoldFormat));
		}//if
		if (pBean.oBean.canSeeOffice()){
			sheet.addCell(new Label(colCount++, 1, "Office Audit Status", times12BoldFormat));
			sheet.addCell(new Label(colCount++, 1, "Office Audit Performed", times12BoldFormat));
			sheet.addCell(new Label(colCount++, 1, "Office Audit RQs Closed", times12BoldFormat));		
		}//if
		sheet.addCell(new Label(colCount++, 1, "Address", times12BoldFormat));
		sheet.addCell(new Label(colCount++, 1, "Contact", times12BoldFormat));
		sheet.addCell(new Label(colCount++, 1, "Email", times12BoldFormat));
		sheet.addCell(new Label(colCount++, 1, "Phone", times12BoldFormat));
		sheet.addCell(new Label(colCount++, 1, "Phone2", times12BoldFormat));
		sheet.addCell(new Label(colCount++, 1, "All Trades", times12BoldFormat));
		rowCount = 1;
	}//writeHeaders

	public void writeLine(AccountBean aBean, ContractorBean cBean, PermissionsBean pBean) throws Exception {
		if (null == workbook || null == sheet)
			throw new Exception("ExcelWriterBean workbook or sheet is null");
		rowCount++;
		int colCount = 0;

		if (rowCount % 2 == 0) {
			tempCell = whiteActiveCell;
			if ("Inactive".equals(cBean.calcPICSStatusForOperator(pBean.oBean))) tempCell = whiteInactiveCell;
		}//if
		else {
			tempCell = grayActiveCell;
			if ("Inactive".equals(cBean.calcPICSStatusForOperator(pBean.oBean))) tempCell = grayInactiveCell;
		}//else

		sheet.addCell(new jxl.write.Number(colCount++, rowCount, rowCount-1, tempCell));
		sheet.addCell(new Label(colCount++, rowCount, aBean.name, tempCell));
		sheet.addCell(new Label(colCount++, rowCount, cBean.main_trade, tempCell));
		sheet.addCell(new Label(colCount++, rowCount, cBean.calcPICSStatusForOperator(pBean.oBean), tempCell));
		if (pBean.oBean.canSeePQF()){
			sheet.addCell(new Label(colCount++, rowCount, cBean.calcPQFStatus(), tempCell));
			sheet.addCell(new Label(colCount++, rowCount, cBean.pqfSubmittedDate, tempCell));
		}//if
		if (pBean.oBean.canSeeDesktop()){
			sheet.addCell(new Label(colCount++, rowCount, cBean.calcDesktopStatus(), tempCell));
			sheet.addCell(new Label(colCount++, rowCount, cBean.desktopClosedDate, tempCell));
		}//if
		if (pBean.oBean.canSeeOffice()){
			if (cBean.isNewOfficeAudit()){
				sheet.addCell(new Label(colCount++, rowCount, cBean.calcOfficeStatusNew(), tempCell));
				sheet.addCell(new Label(colCount++, rowCount, cBean.officeSubmittedDate, tempCell));
				sheet.addCell(new Label(colCount++, rowCount, cBean.officeClosedDate, tempCell));
			}else{
				sheet.addCell(new Label(colCount++, rowCount, cBean.calcOfficeStatus(), tempCell));
				sheet.addCell(new Label(colCount++, rowCount, cBean.auditCompletedDate, tempCell));
				sheet.addCell(new Label(colCount++, rowCount, cBean.auditClosedDate, tempCell));
			}//else			
		}//if
		sheet.addCell(new Label(colCount++, rowCount, aBean.getFullAddress(), tempCell));
		sheet.addCell(new Label(colCount++, rowCount, aBean.contact, tempCell));
		sheet.addCell(new Label(colCount++, rowCount, aBean.email, tempCell));
		sheet.addCell(new Label(colCount++, rowCount, aBean.phone, tempCell));
		sheet.addCell(new Label(colCount++, rowCount, aBean.phone2, tempCell));
		sheet.addCell(new Label(colCount++, rowCount, cBean.getTradesList() + cBean.getSubTradesList(), tempCell));
	
		if (aBean.name.length() > nameLength)	nameLength = aBean.name.length();
		if (cBean.main_trade.length() > mainTradeLength)	mainTradeLength = cBean.main_trade.length();
		if (aBean.getFullAddress().length() > addressLength)	addressLength = aBean.getFullAddress().length();
		if (aBean.contact.length() > contactLength)	contactLength = aBean.contact.length();
		if (aBean.email.length() > emailLength)	emailLength = aBean.email.length();
		if (aBean.phone.length() > phoneLength)	phoneLength = aBean.phone.length();
	}//writeLine

	public void setColumnLengths(PermissionsBean pBean) throws Exception {
		int colCount = 0;
	    sheet.getSettings().setVerticalFreeze(2);
	    sheet.getSettings().setHorizontalFreeze(2);
		sheet.setColumnView(colCount++,5);
		sheet.setColumnView(colCount++,nameLength);
		sheet.setColumnView(colCount++,mainTradeLength);
		sheet.setColumnView(colCount++,12);
		if (pBean.oBean.canSeePQF()){
			sheet.setColumnView(colCount++,12);
			sheet.setColumnView(colCount++,17);
		}//if
		if (pBean.oBean.canSeeDesktop()){
			sheet.setColumnView(colCount++,16);
			sheet.setColumnView(colCount++,21);
		}//if
		if (pBean.oBean.canSeeOffice()){
			sheet.setColumnView(colCount++,18);
			sheet.setColumnView(colCount++,18);
			sheet.setColumnView(colCount++,18);
		}//if
		sheet.setColumnView(colCount++,addressLength);
		sheet.setColumnView(colCount++,contactLength);
		sheet.setColumnView(colCount++,emailLength);
		sheet.setColumnView(colCount++,phoneLength);
		sheet.setColumnView(colCount++,14);
		sheet.setColumnView(colCount++,14);	
	}//setColumnLengths

	public void closeTempExcelFile() throws Exception {
		workbook.write();
		workbook.close();
		sheet = null;
		workbook = null;		
		rowCount = 0;
	}//closeTempExcelFile

	public static String getLink(String id) throws Exception {
		if ("-1".equals(id))
			id="1";
		String fileName = fileNamePre + id + fileNamePost;
		String filePath = fileDir + fileName;
		return "<a href=\"/" + filePath + "\" target=\"_blank\" class=\"blueMain\">Download Excel File</a>";
	}//getLink

	public void writeCPReport(javax.servlet.ServletConfig config, ResultSet RS, String ID) throws Exception {
		init(config);
		ArrayList<String> columnsAL = new ArrayList<String>();
		columnsAL.addAll(Arrays.asList(CP_REPORT_DB_COLUMNS_ARRAY));
		ArrayList<String> titlesAL = new ArrayList<String>();
		titlesAL.addAll(Arrays.asList(CP_REPORT_TITLES_ARRAY));
		writeRSToFile(RS, columnsAL, titlesAL, ID);
	}//writeCPReport

	public void writeRSToFile(ResultSet RS, ArrayList columnsAL, ArrayList titlesAL, String ID) throws Exception {
//		ewBean.init(config);
		WritableFont timesFont = new WritableFont(WritableFont.TIMES);
		WritableCellFormat timesFormat = new WritableCellFormat(timesFont);

		openTempExcelFile(ID);
		RS.beforeFirst();
		if (null == workbook || null == sheet)
			throw new Exception("ExcelWriterBean workbook or sheet is null");
		int colCount = 0;
		int rowCount = 0;
		int numCols = columnsAL.size();
		for (colCount=0;colCount < numCols;colCount++) {
			sheet.addCell(new Label(colCount, rowCount, (String)titlesAL.get(colCount), timesFormat));
		}//for
		while (RS.next()) {
			colCount++;
			rowCount++;
			if (rowCount % 2 == 0) {
				tempCell = whiteActiveCell;
//				if ("Inactive".equals(cBean.status)) tempCell = whiteInactiveCell;
			}//if
			else {
				tempCell = grayActiveCell;
//				if ("Inactive".equals(cBean.status)) tempCell = grayInactiveCell;
			}//else
			for (colCount=0;colCount < numCols;colCount++) {
				String temp = (String)columnsAL.get(colCount);
				String cellValue = "";
				if ("PICS".equals(temp))
					cellValue = "PICS";
				else if (!"".equals(temp))
					cellValue = RS.getString(temp);
				if ("auditDate".equals(temp))
					cellValue = DateBean.toShowFormat(cellValue);
				sheet.addCell(new Label(colCount, rowCount, cellValue, tempCell));
			}//for
//			sheet.addCell(new jxl.write.Number(colCount++, rowCount, rowCount-1, tempCell));
//			sheet.addCell(new Label(colCount++, rowCount, aBean.phone2, tempCell));
		}//whlie
	    sheet.getSettings().setVerticalFreeze(1);
		for (colCount=0;colCount < numCols;colCount++) {
			sheet.setColumnView(colCount,14);
		}//for
		closeTempExcelFile();
	}//writeLine
}//ExcelWriterBean
/*
//		times16BoldFont.setColour(jxl.format.Colour.getInternalColour(003366));
		jxl.format.Colour[] colArray = jxl.format.Colour.getAllColours();
		for (int i = 0; i < colArray.length; i++) {
				WritableFont wcf = new WritableFont(WritableFont.TIMES);
				wcf.setColour(colArray[i]);
				WritableCellFormat wcf2 = new WritableCellFormat(wcf); 
				WritableCellFormat wcf3 = new WritableCellFormat(wcf); 
				wcf3.setBackground(colArray[i]);
				sheet.addCell(new Label(0,i,colArray[i].getDescription(),wcf2));
				sheet.addCell(new Label(1,i,colArray[i].getDescription(),wcf3));
				sheet.addCell(new jxl.write.Number(2,i,colArray[i].getValue(), times12BoldFormat));
	//		colArray[i]
		}//for
//		wcf.setShrinkToFit(true);
//		wcf.setBackground(jxl.format.Colour.GRAY_25);
*/
