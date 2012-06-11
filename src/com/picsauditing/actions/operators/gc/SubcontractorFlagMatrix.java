package com.picsauditing.actions.operators.gc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.ServletOutputStream;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.report.ReportAccount;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class SubcontractorFlagMatrix extends ReportAccount {
	private SelectSQL sql = new SelectSQL("accounts a");

	private Map<ContractorAccount, Map<OperatorAccount, FlagColor>> table = new TreeMap<ContractorAccount, Map<OperatorAccount, FlagColor>>();
	private Set<OperatorAccount> distinctOperators = new TreeSet<OperatorAccount>();

	public String execute() throws Exception {
		if (!permissions.isLoggedIn()) {
			return LOGIN_AJAX;
		}

		if (!permissions.isGeneralContractor()
				&& (permissions.isOperatorCorporate() && permissions.getLinkedGeneralContractors().size() == 0)) {
			throw new NoRightsException("General Contractor");
		}

		getFilter().setShowFlagStatus(true);
		getFilter().setShowWorkStatus(true);
		getFilter().setShowTaxID(false);
		getFilter().setShowOpertorTagName(false);
		getFilter().setShowGeneralContractors(true);

		buildQuery();
		run(sql);
		buildMap();

		return SUCCESS;
	}

	public String download() throws Exception {
		buildQuery();
		run(sql);
		buildMap();

		String filename = "SubcontractorFlagMatrix";
		HSSFWorkbook wb = buildWorkbook(filename);

		filename += ".xls";
		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		wb.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();

		return null;
	}

	public Map<ContractorAccount, Map<OperatorAccount, FlagColor>> getTable() {
		return table;
	}

	public void setTable(Map<ContractorAccount, Map<OperatorAccount, FlagColor>> table) {
		this.table = table;
	}

	public Set<OperatorAccount> getDistinctOperators() {
		return distinctOperators;
	}

	public void setDistinctOperators(Set<OperatorAccount> distinctOperators) {
		this.distinctOperators = distinctOperators;
	}

	public boolean isAjax() {
		return AjaxUtils.isAjax(ServletActionContext.getRequest());
	}

	@Override
	protected void buildQuery() {
		String visibleOperators = permissions.getAccountIdString();
		if (permissions.isGeneralContractor()) {
			visibleOperators = Strings.implode(permissions.getLinkedClients());
		} else if (getFilter().getGeneralContractor() != null) {
			visibleOperators = Strings.implode(getFilter().getGeneralContractor());
		} else if (permissions.getLinkedGeneralContractors().size() > 0) {
			visibleOperators = Strings.implode(permissions.getLinkedGeneralContractors());
		}

		sql.addJoin("JOIN contractor_info c ON a.id = c.id");
		sql.addJoin("LEFT JOIN users contact ON contact.id = a.contactID");
		sql.addJoin("JOIN generalcontractors gc ON gc.subID = a.id AND gc.genID IN (" + visibleOperators + ")");
		sql.addJoin("JOIN operators gco ON gco.id = gc.genID");
		sql.addJoin("LEFT JOIN flag_data_override fdo ON fdo.conID = a.id AND fdo.forceEnd > NOW() AND fdo.opID IN ("
				+ visibleOperators + ")");

		sql.addWhere("a.type = 'Contractor'");
		sql.addWhere("a.status = 'Active'");
		sql.addWhere("gc.subID IN (SELECT subID FROM generalcontractors WHERE genID = " + permissions.getAccountId()
				+ ")");

		sql.addField("a.id, a.name, a.status, a.type, a.phone, a.fax, a.creationDate");
		sql.addField("c.riskLevel, c.safetyRisk, c.productRisk");
		sql.addField("gc.workStatus, gc.genID, gc.subID, gc.forceEnd");
		sql.addField("CASE gco.doContractorsPay WHEN 'Yes' THEN gc.flag ELSE '' END flag");
		sql.addField("CASE gco.doContractorsPay WHEN 'Yes' THEN gc.flag ELSE '' END flag");
		sql.addField("CASE gco.doContractorsPay WHEN 'Yes' THEN lower(gc.flag) ELSE '' END lflag");
		sql.addField("fdo.forceEnd as 'dataForceEnd'");

		addFilterToSQL();
	}

	private void buildMap() {
		for (BasicDynaBean bean : data) {
			ContractorAccount contractor = dao.find(ContractorAccount.class,
					Integer.parseInt(bean.get("subID").toString()));
			OperatorAccount operator = dao.find(OperatorAccount.class, Integer.parseInt(bean.get("genID").toString()));
			FlagColor flag = FlagColor.valueOf(bean.get("flag").toString());

			if (table.get(contractor) != null) {
				table.get(contractor).put(operator, flag);
			} else {
				Map<OperatorAccount, FlagColor> temp = new HashMap<OperatorAccount, FlagColor>();
				temp.put(operator, flag);
				table.put(contractor, temp);
			}

			distinctOperators.add(operator);
		}
	}

	private HSSFWorkbook buildWorkbook(String filename) throws Exception {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(getText("SubcontractorFlagMatrix.title"));

		createExcelHeader(sheet);
		createExcelMatrix(sheet);

		for (int column = 0; column <= sheet.getLastRowNum(); column++) {
			sheet.autoSizeColumn(column);
		}

		return workbook;
	}

	private void createExcelHeader(HSSFSheet sheet) {
		// Set header font and style
		HSSFWorkbook workbook = sheet.getWorkbook();
		CreationHelper creationHelper = workbook.getCreationHelper();

		HSSFFont headerFont = workbook.createFont();
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		HSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFont(headerFont);
		// Header
		HSSFRow row = sheet.createRow(0);
		// Header cells
		int headerCellIndex = 0;
		HSSFCell cell = row.createCell(headerCellIndex);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(creationHelper.createRichTextString(getText("GeneralContractor.SubContractor")));

		for (OperatorAccount operator : distinctOperators) {
			headerCellIndex++;
			cell = row.createCell(headerCellIndex);
			cell.setCellStyle(headerStyle);
			cell.setCellValue(creationHelper.createRichTextString(operator.getName()));
		}
	}

	private void createExcelMatrix(HSSFSheet sheet) {
		int rowIndex = 1;
		CreationHelper creationHelper = sheet.getWorkbook().getCreationHelper();

		HSSFCellStyle centerCell = sheet.getWorkbook().createCellStyle();
		centerCell.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		for (ContractorAccount contractor : table.keySet()) {
			HSSFRow row = sheet.createRow(rowIndex);
			int cellIndex = 0;

			HSSFCell cell = row.createCell(cellIndex);
			cell.setCellValue(creationHelper.createRichTextString(contractor.getName()));

			for (OperatorAccount operator : distinctOperators) {
				cellIndex++;

				FlagColor flag = table.get(contractor).get(operator);
				if (flag != null) {
					cell = row.createCell(cellIndex);
					cell.setCellStyle(centerCell);
					cell.setCellValue(creationHelper.createRichTextString(getText(flag.getI18nKey())));
				}
			}

			rowIndex++;
		}

	}
}
