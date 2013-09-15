package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletOutputStream;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.util.DoubleMap;
import com.picsauditing.util.PermissionQueryBuilder;

@SuppressWarnings("serial")
public class OperatorFlagMatrix extends ReportAccount {
	protected String flagColor;
	protected String category;
	private Set<FlagCriteria> flagCriteria = new TreeSet<FlagCriteria>();

	private TableDisplay tableDisplay;

	@Override
	protected void checkPermissions() throws Exception {
		super.checkPermissions();

		if(permissions.isOperatorCorporate())
			tryPermissions(OpPerms.OperatorFlagMatrix);
	}

	public OperatorFlagMatrix(OperatorAccountDAO operatorDAO) {
		this.listType = ListType.Operator;
		this.orderByDefault = "fc.displayOrder";
	}

	@Override
	protected void buildQuery() {
		setReportName(getText("OperatorFlagMatrix.title"));
		sql = new SelectAccount();
		sql.setType(SelectAccount.Type.Contractor);
		sql.addJoin("JOIN flag_data fd ON fd.conID = a.id");
		sql.addJoin("JOIN flag_criteria fc ON fd.criteriaID = fc.id AND fc.insurance = 0");

		if (permissions.isCorporate())
			sql.addJoin("JOIN facilities f on fd.opID = f.opID AND f.corporateID = " + permissions.getAccountId());
		else if (permissions.isOperator())
			sql.addWhere("fd.opID = " + permissions.getAccountId());
		else if(permissions.hasGroup(User.GROUP_CSR)) {
			sql.addWhere("a.status = 'Active'");
            sql.addJoin("JOIN account_user au on au.accountID = a.id and au.role='PICSCustomerServiceRep' and au.startDate < now() and au.endDate > now()");
			sql.addWhere("au.userID = "+ permissions.getUserId());
			sql.addWhere("fd.flag = '"+ flagColor + "'");
			sql.addWhere("fc.category = '" + category + "'");
		}
		
		sql.addJoin("JOIN generalcontractors gc ON gc.subID = a.id "
				+ "AND gc.genID = fd.opID AND gc.flag IN ('Red', 'Amber')");

		sql.addWhere("fd.flag in('Red','Amber')");

		sql.addGroupBy("a.id, fc.id");

		sql.addField("fc.id criteriaID");
		sql.addField("CONCAT('FlagCriteria.', fc.id, '.label') label");
		sql.addField("CONCAT('FlagCriteria.', fc.id, '.description') description");
		sql.addField("MAX(fd.flag) flag");

		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		sql.addWhere("1 " + permQuery.toString());

		report.setLimit(100000);
	}

	@Override
	protected String returnResult() throws IOException {
		if (download) {
			HSSFWorkbook wb = getTableDisplay().buildWorkbook();
			String filename = getReportName();
			filename += ".xls";

			ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
			ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
			ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
			wb.write(outstream);
			outstream.flush();
			ServletActionContext.getResponse().flushBuffer();

			return null;
		} else {
			return super.returnResult();
		}
	}

	public Set<FlagCriteria> getFlagCriteria() {
		return flagCriteria;
	}

	public TableDisplay getTableDisplay() {
		if (tableDisplay == null)
			tableDisplay = new TableDisplay(data);

		return tableDisplay;
	}

	public class TableDisplay {

		private Set<String> rows = new TreeSet<String>();
		private Map<String, String> rowIds = new HashMap<String, String>();
		private Set<String> columns = new LinkedHashSet<String>();
		private Map<String, String> columnHover = new HashMap<String, String>();

		private DoubleMap<String, String, String> content = new DoubleMap<String, String, String>();

		public TableDisplay(List<BasicDynaBean> data) {
			for (final BasicDynaBean d : data) {
				rows.add(d.get("name").toString());

				rowIds.put(d.get("name").toString(), d.get("id").toString());

				columns.add(getText(d.get("label").toString()));
				columnHover.put(getText(d.get("label").toString()), getText(d.get("description").toString()));

				content.put(d.get("name").toString(), getText(d.get("label").toString()), d.get("flag").toString());
			}
		}

		public String getContent(String k1, String k2) {
			return content.get(k1, k2);
		}

		public String getContentIcon(String k1, String k2) {
			try {
				if (k2 !=null)
					return FlagColor.valueOf(content.get(k1, k2)).getSmallIcon(k2);
				else
					return FlagColor.valueOf(content.get(k1, k2)).getSmallIcon("");
			} catch (Exception e) {
				return "";
			}
		}

		public Set<String> getRows() {
			return rows;
		}

		public Map<String, String> getRowIds() {
			return rowIds;
		}

		public Set<String> getColumns() {
			return columns;
		}

		public Map<String, String> getColumnHover() {
			return columnHover;
		}

		public HSSFWorkbook buildWorkbook() {
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();

			HSSFDataFormat df = wb.createDataFormat();
			HSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short) 12);

			HSSFFont headerFont = wb.createFont();
			headerFont.setFontHeightInPoints((short) 12);
			headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

			HSSFCellStyle headerStyle = wb.createCellStyle();
			headerStyle.setFont(headerFont);
			headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			wb.setSheetName(0, getReportName());

			int rowNumber = 0;

			{
				// Add the Column Headers to the top of the report
				int columnCount = 1;
				HSSFRow r = sheet.createRow(rowNumber);
				rowNumber++;
				for (String col : this.columns) {
					HSSFCell c = r.createCell(columnCount++);
					c.setCellValue(new HSSFRichTextString(col));
					c.setCellStyle(headerStyle);
				}
			}

			for (String row : rows) {
				HSSFRow r = sheet.createRow(rowNumber++);
				int colNumber = 0;
				HSSFCell header = r.createCell(colNumber++);
				header.setCellValue(new HSSFRichTextString(row));
				for (String col : columns) {
					HSSFCell c = r.createCell(colNumber++);
					c.setCellValue(new HSSFRichTextString(content.get(row, col)));
				}
			}

			for (short c = 0; c < columns.size(); c++) {
				HSSFCellStyle cellStyle = wb.createCellStyle();
				cellStyle.setDataFormat(df.getFormat("@"));
				sheet.setDefaultColumnStyle(c, cellStyle);
				sheet.autoSizeColumn(c);
			}

			return wb;
		}
	}
	
	public String getFlagColor() {
		return flagColor;
	}

	public void setFlagColor(String flagColor) {
		this.flagColor = flagColor;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}