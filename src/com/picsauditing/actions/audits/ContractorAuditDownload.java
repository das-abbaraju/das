package com.picsauditing.actions.audits;

import java.io.IOException;
import java.util.ArrayList;
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

import com.picsauditing.auditBuilder.AuditCategoriesBuilder;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOptionValue;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorAuditDownload extends ContractorAuditController {
	private AuditCategoriesBuilder builder;

	private HSSFWorkbook workbook = new HSSFWorkbook();
	private HSSFSheet sheet;
	// Styles
	private HSSFFont boldFont;
	private HSSFCellStyle headerStyle;
	private HSSFCellStyle categoryStyle;
	private HSSFCellStyle questionStyle;
	private HSSFCellStyle hyperlinkStyle;

	@Override
	public String execute() throws Exception {
		findConAudit();

		sheet = workbook.createSheet(conAudit.getAuditType().getName().toString().replaceAll("[^\\w\\s]", "-"));

		initializeStyles();

		// Audit Name - Contractor header
		int rowNum = 0;
		HSSFRow row = sheet.createRow(rowNum);
		HSSFCell cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
		cell.setCellStyle(headerStyle);

		rowNum++;

		String header = getText(
				"Audit.auditFor",
				new Object[] {
						conAudit.getAuditType().getName(),
						Strings.isEmpty(conAudit.getAuditFor()) ? 0 : 1,
						Strings.isEmpty(conAudit.getAuditFor()) ? conAudit.getEffectiveDateLabel() : conAudit
								.getAuditFor() })
				+ " - " + contractor.getName();

		cell.setCellValue(header);

		if (builder == null) {
			builder = new AuditCategoriesBuilder(auditCategoryRuleCache, contractor);
		}

		List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
		if (permissions.isOperatorCorporate()) {
			OperatorAccount operator = operatorDAO.find(permissions.getAccountId());
			operators.add(operator);
		} else {
			// Get all operators?
			operators.addAll(conAudit.getContractorAccount().getOperatorAccounts());
		}

		for (CategoryNode node : getCategoryNodes()) {
			rowNum = fillExcelCategoryNode(node, rowNum);
		}

		setColumnSize();
		setDownloadResponse(header);

		return null;
	}

	private void initializeStyles() {
		setBoldFont();
		// Header
		initializeHeaderStyle();
		// Category
		initializeCategoryStyle();
		// Question
		initializeQuestionStyle();
		// Hyperlinks
		initializeHyperlinkStyle();
	}

	private void setBoldFont() {
		boldFont = workbook.createFont();
		boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	}

	private void initializeHeaderStyle() {
		headerStyle = workbook.createCellStyle();
		headerStyle.setFont(boldFont);
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	}

	private void initializeCategoryStyle() {
		categoryStyle = workbook.createCellStyle();
		categoryStyle.setFont(boldFont);
	}

	private void initializeQuestionStyle() {
		questionStyle = workbook.createCellStyle();
		questionStyle.setWrapText(true);
	}

	private void initializeHyperlinkStyle() {
		HSSFFont hyperlinkFont = workbook.createFont();
		hyperlinkFont.setUnderline(HSSFFont.U_SINGLE);
		hyperlinkFont.setColor(HSSFColor.BLUE.index);
		hyperlinkStyle = workbook.createCellStyle();
		hyperlinkStyle.setFont(hyperlinkFont);
	}

	private int fillExcelCategoryNode(CategoryNode node, int rowNum) {
		HSSFRow row = sheet.createRow(rowNum++);
		HSSFCell cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(node.category.getFullNumber() + " - " + node.category.getName().toString());
		cell.setCellStyle(categoryStyle);

		rowNum = fillExcelQuestions(node.category.getEffectiveQuestions(conAudit.getEffectiveDate()), rowNum);

		for (CategoryNode subcat : node.subCategories) {
			rowNum = fillExcelCategoryNode(subcat, rowNum);
		}
		return rowNum;
	}

	private int fillExcelQuestions(List<AuditQuestion> questions, int rowNum) {

		for (AuditQuestion question : questions) {
			if (question.isCurrent() && question.isValidQuestion(conAudit.getValidDate())
					&& question.isVisibleInAudit(conAudit) && satisfiesRequiredQuestion(question)
					&& satisfiesVisibleQuestion(question)) {
				HSSFRow row = sheet.createRow(rowNum++);
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
					cell.setCellStyle(questionStyle);

					// Match links
					Pattern href = Pattern.compile("<a href=\"(.*?)\" target=\"[\\w]*?\">(.*?)</a>");
					Matcher links = href.matcher(cellValue);

					while (links.find()) {
						row = sheet.createRow(rowNum++);
						cell = row.createCell(0);
						cell.setCellFormula("HYPERLINK(\"" + links.group(1) + "\",\"" + links.group(2) + "\")");
						cell.setCellStyle(hyperlinkStyle);
					}
				} else {
					cell.setCellValue(cellValue);
					cell.setCellStyle(questionStyle);
				}

				addAnswerCell(question, row);
			}
		}

		return rowNum;
	}

	private boolean satisfiesVisibleQuestion(AuditQuestion question) {
		boolean satisfiesVisibleQuestionAnswer = true;
		AuditQuestion visibleQuestion = question.getVisibleQuestion();
		if (visibleQuestion != null) {
			AuditData visibleQuestionAuditData = findAnswer(visibleQuestion);
			if (visibleQuestionAuditData == null
					|| !visibleQuestionAuditData.getAnswer().equals(question.getVisibleAnswer())) {
				satisfiesVisibleQuestionAnswer = false;
			}
		}
		return satisfiesVisibleQuestionAnswer;
	}

	private boolean satisfiesRequiredQuestion(AuditQuestion question) {
		boolean satisfiesRequiredQuestionAnswer = true;
		AuditQuestion requiredQuestion = question.getRequiredQuestion();
		if (requiredQuestion != null) {
			AuditData requiredQuestionAuditData = findAnswer(requiredQuestion);

			if (requiredQuestionAuditData == null
					|| !requiredQuestionAuditData.getAnswer().equals(question.getRequiredAnswer())) {
				satisfiesRequiredQuestionAnswer = false;
			}
		}
		return satisfiesRequiredQuestionAnswer;
	}

	private void addAnswerCell(AuditQuestion question, HSSFRow row) {
		AuditData answer = findAnswer(question);

		HSSFCell cell;
		if (answer != null) {
			if (!Strings.isEmpty(answer.getAnswer())) {
				cell = row.createCell(1);

				setCellValue(question, answer, cell);
			}

			addComment(row, answer);
		}
	}

	private AuditData findAnswer(AuditQuestion question) {
		// Find the corresponding audit data
		AuditData answer = null;
		for (AuditData data : conAudit.getData()) {
			if (question.equals(data.getQuestion())) {
				answer = data;
			}
		}
		return answer;
	}

	private void setCellValue(AuditQuestion question, AuditData answer, HSSFCell cell) {
		// Is the answer using an audit option value?
		if (question.getOption() != null) {
			for (AuditOptionValue value : question.getOption().getValues()) {
				if (answer.getAnswer().equals(value.getIdentifier())) {
					cell.setCellValue(value.getName().toString());
				}
			}
		} else {
			cell.setCellValue(answer.getAnswer());
		}
	}

	private void addComment(HSSFRow row, AuditData answer) {
		HSSFCell cell;
		if (!Strings.isEmpty(answer.getComment())) {
			cell = row.createCell(2);
			cell.setCellValue(answer.getComment());
			cell.setCellStyle(questionStyle);
		}
	}

	private void setColumnSize() {
		sheet.autoSizeColumn(0, true);
		sheet.autoSizeColumn(1, true);
		sheet.autoSizeColumn(2, true);
		sheet.autoSizeColumn(3, true);
		sheet.autoSizeColumn(4, true);

		if (sheet.getColumnWidth(0) > 256 * 100) {
			sheet.setColumnWidth(0, 256 * 100);
		}
	}

	private void setDownloadResponse(String header) throws IOException {
		// Download
		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletActionContext.getResponse().setHeader("Content-Disposition",
				"attachment; filename=" + header.replaceAll("[^\\w\\s]", "-") + ".xls");
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		workbook.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();
	}
}