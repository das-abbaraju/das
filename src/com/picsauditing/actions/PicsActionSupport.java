package com.picsauditing.actions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;

public class PicsActionSupport extends ActionSupport {
	protected static String LOGIN_AJAX = "LoginAjax";
	protected static String BLANK = "blank";
	
	protected Permissions permissions = null;

	// replaces the obsolete message in cases where we need to print out a
	// single string to the page
	protected String output = null;
	protected String button = null;

	private User user; // Current logged in user
	private Account account; // Current logged in user's account
	private List<User> auditorList;

	protected void loadPermissions() {
		if (permissions != null)
			// Already set
			return;

		if (ActionContext.getContext().getSession() == null)
			System.out.println("Failed to get Struts session");
		else
			permissions = (Permissions) ActionContext.getContext().getSession().get("permissions");
		if (permissions == null) {
			permissions = new Permissions();

			String autoLogin = System.getProperty("pics.autoLogin");

			if (autoLogin != null && autoLogin.length() != 0) {
				try {
					UserDAO userDAO = (UserDAO) SpringUtils.getBean("UserDAO");
					User user = userDAO.find(Integer.parseInt(autoLogin));
					permissions.login(user);
					ActionContext.getContext().getSession().put("permissions", permissions);
				} catch (Exception e) {
					System.out.println("Problem autologging in.  Id supplied was: " + autoLogin);
				}
			}
		}

	}

	protected boolean forceLogin() {
		loadPermissions();
		try {
			if (!permissions.loginRequired(ServletActionContext.getResponse(), ServletActionContext.getRequest())) {
				return false;
			}
		} catch (Exception e) {
			System.out.println("PicsActionSupport: Error occurred trying to login:" + e.getMessage());
			return false;
		}
		return true;
	}

	protected boolean forceLogin(String alternateReturnURL) {
		loadPermissions();
		try {
			if (!permissions.loginRequired(ServletActionContext.getResponse(), alternateReturnURL)) {
				return false;
			}
		} catch (Exception e) {
			System.out.println("PicsActionSupport: Error occurred trying to login:" + e.getMessage());
			return false;
		}
		return true;
	}

	protected void tryPermissions(OpPerms opPerms) throws Exception {
		permissions.tryPermission(opPerms, OpType.View);
	}

	protected void tryPermissions(OpPerms opPerms, OpType opType) throws Exception {
		loadPermissions();
		permissions.tryPermission(opPerms, opType);
	}

	public User getUser() {
		if (user == null) {
			user = getUser(permissions.getUserId());
		}
		return user;
	}

	public User getUser(int userId) {
		UserDAO dao = (UserDAO) SpringUtils.getBean("UserDAO");
		return dao.find(userId);
	}

	public Account getAccount() {
		loadPermissions();
		if (account == null) {
			AccountDAO dao = (AccountDAO) SpringUtils.getBean("AccountDAO");
			account = dao.find(permissions.getAccountId(), permissions.getAccountType());
		}
		return account;
	}

	public OperatorAccount getOperatorAccount() {
		Account operator = getAccount();
		if (operator.getType().equals("Operator"))
			return (OperatorAccount) operator;
		return null;
	}

	public Permissions getPermissions() {
		try {
			if (permissions == null)
				loadPermissions();
			return permissions;
		} catch (Exception e) {
			return new Permissions();
		}
	}

	public String getRequestURI() {
		return ServletActionContext.getRequest().getRequestURI();
	}

	public List<User> getAuditorList() {
		if (auditorList == null) {
			auditorList = new ArrayList<User>();
			auditorList.add(new User(User.DEFAULT_AUDITOR));
			UserDAO dao = (UserDAO) SpringUtils.getBean("UserDAO");
			auditorList.addAll(dao.findAuditors());
		}
		return auditorList;
	}

	static public String format(float number) {
		return new DecimalFormat("#,##0.00").format(number);
	}

	static public String format(float number, String mask) {
		return new DecimalFormat(mask).format(number);
	}

	/**
	 * Get the directory to store file uploads
	 * Use the System property or the Init parameter or C:/temp/
	 * To set the System property add -Dpics.ftpDir=folder_location to your startup command
	 * @return
	 */
	static protected String getFtpDir() {
		String ftpDir = System.getProperty("pics.ftpDir");
		if (ftpDir != null && ftpDir.length() > 0)
			return ftpDir;
		
		ftpDir = ServletActionContext.getServletContext().getInitParameter("FTP_DIR");
		if (ftpDir != null && ftpDir.length() > 0)
			return ftpDir;
		
		return "C:/temp";
	}

	public String getButton() {
		return button;
	}

	public void setButton(String button) {
		this.button = button;
	}

	public String getOutput() {
		if (output == null) {
			return "";
		}
		return output;
	}

	protected int getParameter(String name) {
		String[] ids = (String[]) ActionContext.getContext().getParameters().get(name);

		if (ids != null && ids.length > 0) {
			return new Integer(ids[0]).intValue();
		}
		return 0;
	}
	
	public int getDaysLeft(Date invoiceDate) {
		if(invoiceDate == null)
			return 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(invoiceDate);
		cal.add(cal.DAY_OF_YEAR, 120);
		return DateBean.getDateDifference(cal.getTime());
	}
	
	public String getActionName() {
		return ServletActionContext.getActionMapping().getName();
	}

}
