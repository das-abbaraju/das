package com.picsauditing.actions;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.RequestAware;

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
import com.picsauditing.util.URLUtils;

@SuppressWarnings("serial")
public class PicsActionSupport extends ActionSupport implements RequestAware {
	protected static String LOGIN_AJAX = "LoginAjax";
	protected static String BLANK = "blank";
	protected static String JSON = "json";
	protected static String CHART_XML = "chartXML";

	protected String requestURL = null;

	protected Permissions permissions = null;

	// replaces the obsolete message in cases where we need to print out a
	// single string to the page
	protected String output = null;
	protected String button = null;

	private User user; // Current logged in user
	private Account account; // Current logged in user's account
	private Set<User> auditorList;

	protected void loadPermissions() {
		loadPermissions(true);
	}

	@SuppressWarnings("unchecked")
	protected void loadPermissions(boolean autoLogin) {
		if (permissions != null)
			// Already set
			return;

		if (ActionContext.getContext().getSession() == null) {
			addActionError("Failed to get session");
		} else
			permissions = (Permissions) ActionContext.getContext().getSession().get("permissions");

		if (permissions == null) {
			permissions = new Permissions();
		}

		if (autoLogin && !permissions.isLoggedIn()) {

			String autoLoginID = System.getProperty("pics.autoLogin");

			if (autoLoginID != null && autoLoginID.length() != 0) {
				try {
					System.out.println("Autologging In user " + autoLoginID
							+ ". Remove pics.autoLogin from startup to remove this feature.");
					UserDAO userDAO = (UserDAO) SpringUtils.getBean("UserDAO");
					User user = userDAO.find(Integer.parseInt(autoLoginID));
					permissions.login(user);
					ActionContext.getContext().getSession().put("permissions", permissions);
				} catch (Exception e) {
					System.out.println("Problem autologging in.  Id supplied was: " + autoLoginID);
				}
			}
		}
	}

	protected boolean forceLogin() {

		loadPermissions();
		try {
			// TJA I tried using getUser() but it doesn't work from the
			// UsersManage
			// class because there's a getUser() method defined there already
			if (permissions.isLoggedIn() && permissions.getAdminID() == 0 && permissions.isForcePasswordReset()) {
				redirect("ProfileEdit.action?url=" + ServletActionContext.getRequest().getRequestURL());
				return true;
			}

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
			if (permissions.isLoggedIn() && permissions.getAdminID() == 0 && permissions.isForcePasswordReset()) {
				redirect("ProfileEdit.action?url=" + alternateReturnURL);
				return true;
			}

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
		if (user == null)
			user = getUser(permissions.getUserId());
		return user;
	}

	public User getUser(int userId) {
		UserDAO dao = (UserDAO) SpringUtils.getBean("UserDAO");
		try {
			User user = dao.find(userId);
			return user;
		} catch (Exception e) {
			System.out.println("Error finding user: " + e.getMessage());
			return null;
		}
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

	public String formatDate(Date serverDate) {
		return formatDate(serverDate, "MMMMM d, yyyy, h:mm a");
	}

	public String formatDate(Date serverDate, String format) {
		loadPermissions();

		DateFormat dateFormat = new SimpleDateFormat(format);

		if (permissions == null || permissions.getTimezone() == null)
			return dateFormat.format(serverDate);

		dateFormat.setTimeZone(permissions.getTimezone());

		return dateFormat.format(serverDate);
	}

	public String getRequestURI() {
		return ServletActionContext.getRequest().getRequestURI();
	}

	public String getRequestURL() {
		return ServletActionContext.getRequest().getRequestURL().toString();
	}

	public Set<User> getAuditorList() {
		if (auditorList == null) {
			auditorList = new TreeSet<User>();
			UserDAO dao = (UserDAO) SpringUtils.getBean("UserDAO");
			auditorList.addAll(dao.findByGroup(User.GROUP_AUDITOR));
			auditorList.addAll(dao.findByGroup(User.GROUP_CSR));
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
	 * Get the directory to store file uploads Use the System property or the
	 * Init parameter or C:/temp/ To set the System property add
	 * -Dpics.ftpDir=folder_location to your startup command
	 * 
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
		if (invoiceDate == null)
			return 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(invoiceDate);
		cal.add(Calendar.DAY_OF_YEAR, 90);
		return DateBean.getDateDifference(cal.getTime());
	}

	public String getActionName() {
		return ServletActionContext.getActionMapping().getName();
	}

	public String getProtocol() {
		return URLUtils.getProtocol(ServletActionContext.getRequest());
	}

	@Override
	public void setRequest(Map arg0) {
		if (requestURL == null)
			requestURL = ServletActionContext.getRequest().getRequestURL().toString();
	}

	public String getRequestString() {
		return requestURL;
	}

	public String redirect(String url) throws IOException {
		ServletActionContext.getResponse().sendRedirect(url);
		return BLANK;
	}

	public void setMsg(String message) {
		addActionMessage(message);
	}

	public String getFormattedDollarAmount(String answer) {
		String response = "$0";

		try {
			String temp = answer.replaceAll(",", "");
			DecimalFormat decimalFormat = new DecimalFormat("$#,##0");

			Long input = new Long(temp);

			response = decimalFormat.format(input);
		} catch (Exception e) {
			// System.out.println("unable to format as money: " + answer);
		}
		return response;
	}

	public boolean isDebugging() {
		return "1".equals(System.getProperty("pics.debug"));
	}
}