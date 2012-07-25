package com.picsauditing.actions.audits;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOptionValue;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorAuditDownload extends AuditActionSupport {
	@Override
	public String execute() throws Exception {
		findConAudit();

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(conAudit.getAuditType().getName().toString().replaceAll("[^\\w\\s]", "-"));

		// Header
		HSSFFont boldedFont = wb.createFont();
		boldedFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		HSSFCellStyle boldedStyle = wb.createCellStyle();
		boldedStyle.setFont(boldedFont);
		boldedStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		// Category
		HSSFCellStyle boldedStyleLeft = wb.createCellStyle();
		boldedStyleLeft.setFont(boldedFont);

		// Question
		HSSFCellStyle wrapped = wb.createCellStyle();
		wrapped.setWrapText(true);

		// Hyperlinks
		HSSFFont hyperlinkFont = wb.createFont();
		hyperlinkFont.setUnderline(HSSFFont.U_SINGLE);
		hyperlinkFont.setColor(HSSFColor.BLUE.index);
		HSSFCellStyle hyperlink = wb.createCellStyle();
		hyperlink.setFont(hyperlinkFont);

		// Audit Name - Contractor header
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
		cell.setCellStyle(boldedStyle);

		String header = getText(
				"Audit.auditFor",
				new Object[] {
						conAudit.getAuditType().getName(),
						Strings.isEmpty(conAudit.getAuditFor()) ? 0 : 1,
						Strings.isEmpty(conAudit.getAuditFor()) ? conAudit.getEffectiveDateLabel() : conAudit
								.getAuditFor() })
				+ " - " + contractor.getName();

		cell.setCellValue(header);

		SheetStatus sheetStatus = new SheetStatus();
		sheetStatus.rownum = 1;
		sheetStatus.sheet = sheet;
		sheetStatus.bold = boldedStyleLeft;
		sheetStatus.wrapped = wrapped;
		sheetStatus.hyperlink = hyperlink;

		Set<Integer> viewableCats = new HashSet<Integer>();
		for (AuditCatData catData : conAudit.getCategories()) {
			if (catData.isApplies() || catData.isOverride())
				viewableCats.add(catData.getCategory().getId());
		}

		for (AuditCategory category : conAudit.getAuditType().getTopCategories()) {
			sheetStatus = fillExcelCategories(sheetStatus, viewableCats, category);
		}

		sheet = sheetStatus.sheet;
		sheet.autoSizeColumn(0, true);
		sheet.autoSizeColumn(1, true);
		sheet.autoSizeColumn(2, true);
		sheet.autoSizeColumn(3, true);
		sheet.autoSizeColumn(4, true);

		if (sheet.getColumnWidth(0) > 256 * 100) {
			sheet.setColumnWidth(0, 256 * 100);
		}

		// Download
		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletActionContext.getResponse().setHeader("Content-Disposition",
				"attachment; filename=" + header.replaceAll("[^\\w\\s]", "-") + ".xls");
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		wb.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();

		return null;
	}

	private SheetStatus fillExcelCategories(SheetStatus sheetStatus, Set<Integer> viewableCats, AuditCategory start) {
		if (viewableCats.contains(start.getId())) {
			HSSFRow row = sheetStatus.sheet.createRow(sheetStatus.rownum++);
			HSSFCell cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(start.getFullNumber() + " - " + start.getName().toString());
			cell.setCellStyle(sheetStatus.bold);

			sheetStatus = fillExcelQuestions(sheetStatus, start.getQuestions());

			for (AuditCategory subcat : start.getSubCategories()) {
				sheetStatus = fillExcelCategories(sheetStatus, viewableCats, subcat);
			}
		}

		return sheetStatus;
	}

	private SheetStatus fillExcelQuestions(SheetStatus sheetStatus, List<AuditQuestion> questions) {
		for (AuditQuestion question : questions) {
			if (question.isCurrent()) {
				HSSFRow row = sheetStatus.sheet.createRow(sheetStatus.rownum++);
				HSSFCell cell = row.createCell(0);
				String cellValue = question.getExpandedNumber() + " - " + question.getName().toString();

				// There is HTML
				if (cellValue.toLowerCase().contains("<a href") || cellValue.toLowerCase().contains("<br")
						|| cellValue.toLowerCase().contains("<ul")) {
					// Remove breaks
					cellValue = cellValue.replaceAll("<br\\s?\\/?>", "");
					String questionText = cellValue.replaceAll("<a href=\"(.*?)\" target=\"[\\w]*?\">(.*?)</a>", "")
							.trim();
					questionText = questionText.replaceAll("<.*?>", "");
					cell.setCellValue(questionText);
					cell.setCellStyle(sheetStatus.wrapped);

					// Match links
					Pattern href = Pattern.compile("<a href=\"(.*?)\" target=\"[\\w]*?\">(.*?)</a>");
					Matcher links = href.matcher(cellValue);

					while (links.find()) {
						row = sheetStatus.sheet.createRow(sheetStatus.rownum++);
						cell = row.createCell(0);
						cell.setCellFormula("HYPERLINK(\"" + links.group(1) + "\",\"" + links.group(2) + "\")");
						cell.setCellStyle(sheetStatus.hyperlink);
					}
				} else {
					cell.setCellValue(cellValue);
					cell.setCellStyle(sheetStatus.wrapped);
				}

				// Find the corresponding audit data
				AuditData answer = null;
				for (AuditData data : conAudit.getData()) {
					if (question.equals(data.getQuestion()))
						answer = data;
				}

				if (answer != null) {
					if (!Strings.isEmpty(answer.getAnswer())) {
						cell = row.createCell(1);

						// Is the answer using an audit option value?
						if (question.getOption() != null) {
							for (AuditOptionValue value : question.getOption().getValues()) {
								if (answer.getAnswer().equals(value.getIdentifier()))
									cell.setCellValue(value.getName().toString());
							}
						} else {
							cell.setCellValue(answer.getAnswer());
						}
					}

					if (!Strings.isEmpty(answer.getComment())) {
						cell = row.createCell(2);
						cell.setCellValue(answer.getComment());
						cell.setCellStyle(sheetStatus.wrapped);
					}
				}
			}
		}

		return sheetStatus;
	}

	private class SheetStatus {
		public HSSFSheet sheet;
		public int rownum;
		// Common
		public HSSFCellStyle bold;
		public HSSFCellStyle wrapped;
		public HSSFCellStyle hyperlink;
	}
}