package com.picsauditing.actions.audits;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.TranslatableString;
import com.picsauditing.report.Column;
import com.picsauditing.report.data.ReportResults;
import com.picsauditing.report.data.ReportRow;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.util.excel.ExcelBuilder;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class AuditTranslationDownload extends ContractorActionSupport {
	private static final Logger logger = LoggerFactory.getLogger(AuditTranslationDownload.class);
	private Map<String, Column> reportColumns = new HashMap<String, Column>();
	private ExcelBuilder excelBuilder = new ExcelBuilder();
	
	private static final Comparator<AuditCatData> CATEGORY_COMPARATOR = new Comparator<AuditCatData>(){
		public int compare(AuditCatData o1, AuditCatData o2) {
			return o1.getCategory().compareTo(o2.getCategory());
		}
	};

	public String execute() throws Exception {
		logger.info("Building XLS File");
		addReportColumns();
		addAuditsToSheet();
		excelBuilder.autoSizeColumns();
		writeFile("auditTranslations" + ".xls", excelBuilder.getWorkbook());
		return BLANK;
	}

	private void addReportColumns() {
		addReportColumn("msgKey", "Key");
		addReportColumn("order", "Order");
		addReportColumn("type", "Field Type");
		addReportColumn("toTranslation", permissions.getLocale().getDisplayName());
		addReportColumn("fromTranslation", "Current English");
		// addReportColumn("fromPreviousTranslation", "Previous English");
	}

	private void addReportColumn(String fieldName, String columnLabel) {
		Column reportColumn = new Column(fieldName);
		reportColumn.setField(new Field(fieldName, "", FilterType.String));
		reportColumn.getField().setText(columnLabel);
		reportColumns.put(reportColumn.getFieldName(), reportColumn);
		excelBuilder.getColumns().add(new ExcelColumn(reportColumn));
	}

	private void addAuditsToSheet() {
		Set<AuditType> auditTypesAlreadyProcessed = new HashSet<AuditType>();
		for (ContractorAudit conAudit : getActiveAudits()) {
			if (!auditTypesAlreadyProcessed.contains(conAudit.getAuditType())) {
				auditTypesAlreadyProcessed.add(conAudit.getAuditType());
				addSheet(conAudit);
			}
		}
	}

	private void addSheet(ContractorAudit conAudit) {
		String sheetName = conAudit.getAuditType().getName().toString();
		ReportResults data = new ReportResults();
		
		List<AuditCatData> sortedAuditCatData = conAudit.getCategories();
		Collections.sort(sortedAuditCatData, CATEGORY_COMPARATOR);
		for (AuditCatData auditCatData : sortedAuditCatData) {
			addCategory(data, auditCatData);
		}
		
		excelBuilder.addSheet(sheetName, data);
	}

	private void addCategory(ReportResults data, AuditCatData auditCatData) {
		if (!auditCatData.isApplies())
			return;
		
		AuditCategory category = auditCatData.getCategory();
		
		data.addRow(addRow(category.getName(), category.getFullNumber(), "Category Name"));
		if (category.getHelpText().isExists())
			data.addRow(addRow(category.getHelpText(), category.getFullNumber(), "Category Help"));
		
		List<AuditQuestion> sortedQuestions = category.getQuestions();
		Collections.sort(sortedQuestions);

		for (AuditQuestion question : sortedQuestions) {
			if (question.isValidQuestion(new Date())) {
				String order = category.getFullNumber() + "." + question.getNumber();
				if (question.getTitle().isExists())
					data.addRow(addRow(question.getTitle(), order, "Question Heading"));
				data.addRow(addRow(question.getName(), order, "Question Text"));
				if (question.getColumnHeader().isExists())
					data.addRow(addRow(question.getColumnHeader(), order, "Question Text Short"));
				if (question.getHelpText().isExists())
					data.addRow(addRow(question.getHelpText(), order, "Question Help"));
				if (question.getRequirement().isExists())
					data.addRow(addRow(question.getRequirement(), order, "Question Requirement"));
			}
		}
	}

	private ReportRow addRow(TranslatableString translatableString, String order, String fieldType) {
		Map<Column, Object> row = new HashMap<Column, Object>();
		row.put(reportColumns.get("msgKey"), translatableString.getKey());
		row.put(reportColumns.get("order"), order);
		row.put(reportColumns.get("type"), fieldType);
		String toTranslation = translatableString.toString(permissions.getLocale());
		String fromTranslation = translatableString.toString(Locale.ENGLISH);
		if (translatableString.getKey().equals(toTranslation) || fromTranslation.equals(toTranslation)) {
			toTranslation = "";
		}
		row.put(reportColumns.get("toTranslation"), toTranslation);
		row.put(reportColumns.get("fromTranslation"), fromTranslation);
		// row.put(reportColumns.get("fromPreviousTranslation"), "");
		return new ReportRow(row);
	}

	private void writeFile(String filename, HSSFWorkbook workbook) throws IOException {
		logger.info("Streaming XLS File to response");
		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		workbook.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();
	}

}
