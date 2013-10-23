package com.picsauditing.actions.operators.gc;

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

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
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

	private Map<ContractorAccount, FlagColor> clientFlags = new TreeMap<ContractorAccount, FlagColor>();
	private Table<ContractorAccount, OperatorAccount, FlagColor> table = TreeBasedTable.create();

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

	public Map<ContractorAccount, FlagColor> getClientFlags() {
		return clientFlags;
	}

	public void setClientFlags(Map<ContractorAccount, FlagColor> clientFlags) {
		this.clientFlags = clientFlags;
	}

	public Table<ContractorAccount, OperatorAccount, FlagColor> getTable() {
		return table;
	}

	public void setTable(Table<ContractorAccount, OperatorAccount, FlagColor> table) {
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
		if (!Strings.isEmpty(orderByDefault)) {
			orderByDefault += ", gen.name";
		} else {
			orderByDefault = "gen.name";
		}

		String visibleOperators = permissions.getAccountIdString();
		if (getFilter().getGeneralContractor() != null) {
			visibleOperators = Strings.implode(getFilter().getGeneralContractor());
		} else if (permissions.isGeneralContractor()) {
			visibleOperators = Strings.implode(permissions.getLinkedClients());
		} else if (permissions.getLinkedGeneralContractors().size() > 0) {
			visibleOperators = Strings.implode(permissions.getLinkedGeneralContractors());
		}

		String status = "'Active'";
		if (permissions.getAccountStatus().isDemo()) {
			status += ", 'Demo'";
		}

		sql.addJoin("JOIN contractor_operator co ON co.conID = a.id");
		sql.addJoin("JOIN contractor_info c ON c.id = a.id");
		sql.addJoin("LEFT JOIN users contact ON contact.id = a.contactID");
		sql.addJoin("LEFT JOIN flag_data_override fdo ON fdo.opID = co.opID AND fdo.conID = co.conID");

		SelectSQL innerJoin = new SelectSQL("contractor_operator co");
		innerJoin.addJoin("JOIN accounts o ON o.id = co.opID");
		innerJoin.addWhere("co.opID IN ("
				+ (permissions.isGeneralContractor() ? permissions.getAccountId() : visibleOperators) + ")");
		innerJoin.addField("o.id");
		innerJoin.addField("o.name");
		innerJoin.addField("co.conID");

		sql.addJoin("JOIN (" + innerJoin.toString() + ") gen ON gen.conID = a.id");
		sql.addJoin("JOIN accounts o ON o.id = co.opID");

		sql.addWhere("a.type = 'Contractor'");
		sql.addWhere("a.status IN (" + status + ")");
		sql.addWhere("co.opID IN ("
				+ (permissions.isGeneralContractor() ? visibleOperators : permissions.getAccountId()) + ")");

		sql.addField("a.id, a.name, a.status, a.type, a.phone, a.fax, a.creationDate");
		sql.addField("c.riskLevel, c.safetyRisk, c.productRisk");
		sql.addField("co.workStatus, co.opID, co.conID, co.forceEnd, co.flag, LOWER(co.flag) lflag");
		sql.addField("fdo.forceEnd dataForceEnd");

		if (permissions.isGeneralContractor()) {
			sql.addField("o.id coID, o.name coName");
		} else {
			sql.addField("gen.id coID, gen.name coName");
		}

		addFilterToSQL();
	}

	private void buildMap() {
		for (BasicDynaBean bean : data) {
			ContractorAccount contractor = new ContractorAccount();
			contractor.setId(Integer.parseInt(bean.get("id").toString()));
			contractor.setName(bean.get("name").toString());
			contractor.setType("Contractor");

			OperatorAccount operator = new OperatorAccount();
			operator.setId(Integer.parseInt(bean.get("coID").toString()));
			operator.setName(bean.get("coName").toString());

			FlagColor flag = null;

			if (bean.get("flag") != null && !Strings.isEmpty(bean.get("flag").toString())) {
				flag = FlagColor.valueOf(bean.get("flag").toString());
			}

			if (!permissions.isGeneralContractor() && clientFlags.get(contractor) == null) {
				clientFlags.put(contractor, flag);
			}

			table.put(contractor, operator, flag);
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

		if (!permissions.isGeneralContractor()) {
			headerCellIndex++;
			cell = row.createCell(headerCellIndex);
			cell.setCellStyle(headerStyle);
			cell.setCellValue(creationHelper.createRichTextString(getText("global.Flag")));
		}

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

		for (ContractorAccount contractor : table.rowKeySet()) {
			HSSFRow row = sheet.createRow(rowIndex);
			int cellIndex = 0;

			HSSFCell cell = row.createCell(cellIndex);
			cell.setCellValue(creationHelper.createRichTextString(contractor.getName()));

			if (!permissions.isGeneralContractor()) {
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(creationHelper
						.createRichTextString(getText(clientFlags.get(contractor).getI18nKey())));
			}

			for (OperatorAccount operator : distinctOperators) {
				cellIndex++;

				FlagColor flag = table.get(contractor, operator);
				if (flag != null) {
					cell = row.createCell(cellIndex);
					cell.setCellStyle(centerCell);

					if (permissions.isGeneralContractor()) {
						cell.setCellValue(creationHelper.createRichTextString(getText(flag.getI18nKey())));
					} else {
						cell.setCellValue("X");
					}
				}
			}

			rowIndex++;
		}

	}
}
