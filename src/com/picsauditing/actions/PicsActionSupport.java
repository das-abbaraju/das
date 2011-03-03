package com.picsauditing.actions;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.RequestAware;
import org.json.simple.JSONObject;

import com.opensymphony.xwork2.ActionContext;
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
import com.picsauditing.util.Strings;
import com.picsauditing.util.URLUtils;

@SuppressWarnings("serial")
public class PicsActionSupport extends TranslationActionSupport implements RequestAware {

	protected static String LOGIN_AJAX = "LoginAjax";
	protected static String BLANK = "blank";
	protected static String JSON = "json";
	protected static String CHART_XML = "chartXML";
	protected Collection<String> alertMessages;

	protected String requestURL = null;

	protected Permissions permissions = null;

	// replaces the obsolete message in cases where we need to print out a
	// single string to the page
	protected String output = null;
	protected String button = null;
	protected JSONObject json = new JSONObject();

	private User user; // Current logged in user
	protected Account account; // Current logged in user's account
	private Set<User> auditorList;

	// Should we put this in another file? so we don't always check in the PicsActionSupport file?
	public static final String getVersion() {
		// Released 2011-03-03
		return "5.4";
	}

	protected void loadPermissions() {
		loadPermissions(true);
	}

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
		if (user == null) {
			loadPermissions();
			user = getUser(permissions.getUserId());
		}
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

	// TODO remove this
	public Account getAccount() {
		if (account == null) {
			loadPermissions();
			AccountDAO dao = (AccountDAO) SpringUtils.getBean("AccountDAO");
			account = dao.find(permissions.getAccountId(), permissions.getAccountType());
		}
		return account;
	}

	public OperatorAccount getOperatorAccount() {
		Account operator = getAccount();
		if (operator.getType().equals("Operator") || operator.getType().equals("Corporate"))
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

	public String maskDateFormat(Date date) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			return dateFormat.format(date);
		} catch (Exception e) {
			return null;
		}
	}

	public String getFuzzyDate(Date d) {
		return DateBean.getFuzzyDate(d);
	}

	public Date parseDate(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

		try {
			return sdf.parse(date);
		} catch (Exception e) {
			return new Date();
		}
	}

	public String getRequestURI() {
		return ServletActionContext.getRequest().getRequestURI();
	}

	public String getRequestURL() {
		return ServletActionContext.getRequest().getRequestURL().toString();
	}

	public String getReferer() {
		return ServletActionContext.getRequest().getHeader("Referer");
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
		// return new DecimalFormat("#,##0.00").format(number);
		return Strings.formatDecimalComma(number + "");
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

	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}

	protected int getParameter(String name) {
		String[] ids = (String[]) ActionContext.getContext().getParameters().get(name);

		if (ids != null && ids.length > 0) {
			return new Integer(ids[0]).intValue();
		}
		return 0;
	}

	// This should be moved into an intercepter at a later date
	/**
	 * Checks to see if this value is in the parameter map. If it is and the
	 * value is an empty string ("") then we will replace that value with a null
	 * 
	 * @param name
	 *            Name of the parameter you want to check in the map
	 */
	protected void parameterCleanUp(String name) {
		String[] para = (String[]) ActionContext.getContext().getParameters().get(name);
		if (para != null && para.length > 0) {
			if (para[0] == null || para[0].isEmpty()) {
				try {
					ActionContext.getContext().getParameters().remove(name);
					ActionContext.getContext().getParameters().put(name, null);
				} catch (Exception e) {
					System.out.println("Error cleaning up parameter");
					e.printStackTrace();
				}
			}
		}
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

	@SuppressWarnings("unchecked")
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

	public Collection<String> internalGetAlertMessages() {
		if (alertMessages == null) {
			alertMessages = new ArrayList<String>();
		}
		return alertMessages;
	}

	public synchronized void addAlertMessage(String anAlertMessage) {
		internalGetAlertMessages().add(anAlertMessage);
	}

	public synchronized boolean hasAlertMessages() {
		return (alertMessages != null) && !alertMessages.isEmpty();
	}

	public synchronized Collection<String> getAlertMessages() {
		return alertMessages;
	}

	/**
	 * @return the i18n text to use for this page's title
	 */
	public String getPageTitle() {
		return getActionName() + ".title";
	}
	
	public boolean isStringEmpty(String s) {
		return Strings.isEmpty(s);
	}
}