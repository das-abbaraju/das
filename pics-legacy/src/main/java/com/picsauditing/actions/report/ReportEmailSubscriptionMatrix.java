package com.picsauditing.actions.report;

import java.util.Collections;
import java.util.List;

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
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.Subscription;
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class ReportEmailSubscriptionMatrix extends ReportActionSupport {
	@Autowired
	private AccountDAO accountDAO;
	@Autowired
	private EmailSubscriptionDAO emailSubscriptionDAO;

	private Account account;

	private List<User> users;
	private DoubleMap<User, Subscription, EmailSubscription> table;

	@Before
	public void build() throws Exception {
		if (account == null || !permissions.hasPermission(OpPerms.AllOperators))
			account = accountDAO.find(permissions.getAccountId());

		users = account.getUsers();
		Collections.sort(users);

		table = new DoubleMap<User, Subscription, EmailSubscription>();
		List<EmailSubscription> emailSubscriptions = emailSubscriptionDAO.findByAccountID(account.getId());

		for (EmailSubscription emailSubscription : emailSubscriptions) {
			table.put(emailSubscription.getUser(), emailSubscription.getSubscription(), emailSubscription);
		}
	}

	public String download() throws Exception {
		// Setting up workbook
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(getText("ReportEmailSubscriptionMatrix.title"));

		HSSFDataFormat df = wb.createDataFormat();
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 12);

		HSSFFont headerFont = wb.createFont();
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		HSSFCellStyle headerStyle = wb.createCellStyle();
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		int rowNumber = 0;
		{
			// Add the Column Headers to the top of the report
			int columnCount = 1;
			HSSFRow r = sheet.createRow(rowNumber);
			rowNumber++;

			for (Subscription s : Subscription.values()) {
				HSSFCell c = r.createCell(columnCount++);
				c.setCellValue(new HSSFRichTextString(s.name()));
				c.setCellStyle(headerStyle);
			}
		}

		for (User u : users) {
			HSSFRow r = sheet.createRow(rowNumber++);
			int colNumber = 0;
			HSSFCell header = r.createCell(colNumber++);
			header.setCellValue(new HSSFRichTextString(u.getName()));
			for (Subscription s : Subscription.values()) {
				HSSFCell c = r.createCell(colNumber++);
				if (table.get(u, s) != null)
					c.setCellValue(new HSSFRichTextString(getText(table.get(u, s).getTimePeriod().getI18nKey("short"))));
			}
		}

		for (short c = 0; c <= Subscription.values().length; c++) {
			HSSFCellStyle cellStyle = wb.createCellStyle();
			cellStyle.setDataFormat(df.getFormat("@"));
			sheet.setDefaultColumnStyle(c, cellStyle);
			sheet.autoSizeColumn(c);
		}

		String filename = this.getClass().getSimpleName();
		filename += ".xls";

		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		wb.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();

		return null;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public List<User> getUsers() {
		return users;
	}

	@SuppressWarnings("unchecked")
	public JSONArray getUsersJSON() {
		JSONArray j = new JSONArray();
		for (final User u : users) {
			j.add(new JSONObject() {
				{
					put("id", u.getId());
					put("name", u.getName());
				}
			});
		}

		return j;
	}

	@SuppressWarnings("unchecked")
	public JSONArray getSubsJSON() {
		JSONArray j = new JSONArray();
		for (final Subscription s : Subscription.values()) {
			j.add(new JSONObject() {
				{
					put("id", s.name());
					put("name", getText(s.getI18nKey("description")));
				}
			});
		}

		return j;
	}

	public DoubleMap<User, Subscription, EmailSubscription> getTable() {
		return table;
	}
}
