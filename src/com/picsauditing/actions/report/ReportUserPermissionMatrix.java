package com.picsauditing.actions.report;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;
import com.picsauditing.util.DoubleMap;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class ReportUserPermissionMatrix extends ReportActionSupport {
	private int accountID;
	private List<User> users;
	private Set<OpPerms> perms;
	private UserDAO userDAO;

	private TableDisplay tableDisplay;

	public ReportUserPermissionMatrix(UserDAO userDAO) {
		setReportName("User Permissions Matrix");
		this.userDAO = userDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (accountID == 0 || !permissions.hasPermission(OpPerms.AllOperators))
			accountID = permissions.getAccountId();

		PicsLogger.start("ReportUserPermissionMatrix");
		perms = new TreeSet<OpPerms>();
		users = userDAO.findByAccountID(accountID, "Yes", "");
		for (User user : users) {
			PicsLogger.log("User: " + user.getId() + user.getName());
			for (UserAccess access : user.getPermissions()) {
				PicsLogger.log("  perm " + access.getOpPerm() + " V:" + access.getViewFlag() + " E:"
						+ access.getEditFlag() + " D:" + access.getDeleteFlag() + " G:" + access.getGrantFlag());
				perms.add(access.getOpPerm());
			}
		}
		PicsLogger.stop();

		if ("download".equals(button)) {
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
		}

		return SUCCESS;
	}

	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	public List<User> getUsers() {
		return users;
	}

	public Set<OpPerms> getPerms() {
		return perms;
	}

	public TableDisplay getTableDisplay() {
		if (tableDisplay == null) {
			tableDisplay = new TableDisplay(users, perms);
		}

		return tableDisplay;
	}

	public class TableDisplay {

		private Set<User> rows = new LinkedHashSet<User>();
		private Set<OpPerms> cols = new LinkedHashSet<OpPerms>();

		private DoubleMap<User, OpPerms, UserAccess> data = new DoubleMap<User, OpPerms, UserAccess>();

		public TableDisplay(Collection<User> uList, Collection<OpPerms> pList) {
			rows.addAll(uList);

			for (User u : uList) {
				for (UserAccess ua : u.getPermissions()) {
					data.put(u, ua.getOpPerm(), ua);
				}
			}

			for (OpPerms p : pList) {
				for (User u : rows) {
					if (get(u, p) != null) {
						cols.add(p);
						break;
					}
				}
			}
		}

		public UserAccess get(User u, OpPerms p) {
			return data.get(u, p);
		}

		public Set<User> getRows() {
			return rows;
		}

		@SuppressWarnings("unchecked")
		public JSONArray getRowsJSON() {
			JSONArray j = new JSONArray();
			for (final User u : rows) {
				j.add(new JSONObject() {
					{
						put("id", u.getId());
						put("name", u.getName());
					}
				});
			}

			return j;
		}

		public Set<OpPerms> getCols() {
			return cols;
		}

		@SuppressWarnings("unchecked")
		public JSONArray getColsJSON() {
			JSONArray j = new JSONArray();
			for (final OpPerms perm : cols) {
				j.add(new JSONObject() {
					{
						put("id", perm.toString());
						put("name", perm.getDescription());
					}
				});
			}

			return j;
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
				for (OpPerms col : this.cols) {
					HSSFCell c = r.createCell(columnCount++);
					c.setCellValue(new HSSFRichTextString(col.toString()));
					c.setCellStyle(headerStyle);
				}
			}

			for (User row : rows) {
				HSSFRow r = sheet.createRow(rowNumber++);
				int colNumber = 0;
				HSSFCell header = r.createCell(colNumber++);
				header.setCellValue(new HSSFRichTextString(row.getName()));
				for (OpPerms col : cols) {
					HSSFCell c = r.createCell(colNumber++);
					if (data.get(row, col) != null)
						c.setCellValue(new HSSFRichTextString(data.get(row, col).toString()));
				}
			}

			for (short c = 0; c < cols.size(); c++) {
				HSSFCellStyle cellStyle = wb.createCellStyle();
				cellStyle.setDataFormat(df.getFormat("@"));
				sheet.setDefaultColumnStyle(c, cellStyle);
				sheet.autoSizeColumn(c);
			}

			return wb;
		}
	}

}
