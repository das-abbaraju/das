package com.picsauditing.actions.audits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.util.excel.ExcelColumn;
import com.picsauditing.util.excel.MultipleSheetExcelBuilder;

@SuppressWarnings("serial")
public class AuditTranslationDownload extends ContractorActionSupport {
	private static final Logger logger = LoggerFactory.getLogger(AuditTranslationDownload.class);

	public String execute() throws Exception {
		logger.info("Building XLS File");
		MultipleSheetExcelBuilder excelBuilder = new MultipleSheetExcelBuilder();

		{
			List<ExcelColumn> columns = new ArrayList<ExcelColumn>();
			columns.add(new ExcelColumn("msgKey", "Key"));
			columns.add(new ExcelColumn("order", "Order"));
			columns.add(new ExcelColumn("type", "Field Type"));
			columns.add(new ExcelColumn("toTranslation", permissions.getLocale().getDisplayLanguage()));
			columns.add(new ExcelColumn("fromTranslation", "Current English"));
			columns.add(new ExcelColumn("fromPreviousTranslation", "Previous English"));
			excelBuilder.getColumns().addAll(columns);
		}

		Set<AuditType> auditTypesAlreadyProcessed = new HashSet<AuditType>();

		for (ContractorAudit conAudit : getActiveAudits()) {
			if (!auditTypesAlreadyProcessed.contains(conAudit.getAuditType())) {
				auditTypesAlreadyProcessed.add(conAudit.getAuditType());
				String sheetName = conAudit.getAuditType().getName().toString();
				JSONArray data = new JSONArray();
				
				for (AuditCatData auditCatData : conAudit.getCategories()) {
					JSONObject row = new JSONObject();
					row.put("type", "Category Name");
					row.put("fromTranslation", auditCatData.getCategory().getName());
					data.add(row);
				}
				
				excelBuilder.addSheet(sheetName, data);
			}
		}

		writeFile("auditTranslations" + ".xls", excelBuilder.getWorkbook());
		return BLANK;
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
