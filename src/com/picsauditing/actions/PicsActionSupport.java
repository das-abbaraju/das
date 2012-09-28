package com.picsauditing.actions;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Transient;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.RequestAware;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.SecurityAware;
import com.picsauditing.actions.users.ChangePassword;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectUser;
import com.picsauditing.strutsutil.AdvancedValidationAware;
import com.picsauditing.util.LocaleController;
import com.picsauditing.util.PicsDateFormat;
import com.picsauditing.util.PicsOrganizerVersion;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.URLUtils;

@SuppressWarnings("serial")
public class PicsActionSupport extends TranslationActionSupport implements RequestAware, SecurityAware,
		AdvancedValidationAware {

	protected static Boolean CONFIG = null;

	public static final String PLAIN_TEXT = "plain-text";
	public static final String ACTION_MESSAGES = "action-messages";
	public static final String LOGIN_AJAX = "LoginAjax";
	public static final String BLANK = "blank";
	public static final String JSON = "json";
	public static final String JSONP = "jsonp";
	public static final String JSON_ARRAY = "jsonArray";
	public static final String CHART_XML = "chartXML";
	public static final String REDIRECT = "redirect";

	@Autowired
	protected BasicDAO dao;
	@Autowired
	protected AppPropertyDAO propertyDAO;
	@Autowired
	protected UserDAO userDAO;

	protected Collection<String> alertMessages;

	protected String requestURL = null;

	protected Permissions permissions = null;

	/**
	 * String that is used for simple messages.
	 * 
	 * This is also used for plain-text type results.
	 * 
	 * @see com.picsauditing.strutsutil.PlainTextResult
	 */
	protected String output = null;

	protected String url = null;

	/**
	 * This is rarely used now because of limitations with i18n on Button names.
	 * This could still be used for non-translated pages such as PICS facing
	 * pages.
	 */
	protected String button = null;

	/**
	 * JSONObject used to return JSON strings.
	 * 
	 * @see com.picsauditing.strutsutil.JSONResult
	 */
	protected JSONObject json = new JSONObject();

	/**
	 * Callback used for jsonp requests
	 * 
	 * @see com.picsauditing.strutsutil.JSONPResult
	 */
	protected String callback;

	/**
	 * JSONArray used to return JSON array.
	 * 
	 * @see com.picsauditing.strutsutil.JSONArrayResult
	 */
	protected JSONArray jsonArray = new JSONArray();

	/**
	 * Current logged in user
	 */
	private User user;
	/**
	 * TODO Kyle, please research this field and document it. Also review if we
	 * need setAccount on AccountActionSupport Maybe we should move the account
	 * field to AccountActionSupport???
	 */
	protected Account account; // Current logged in user's account
	private Set<User> auditorList;
	private Set<User> safetyList;

	private final Logger logger = LoggerFactory.getLogger(PicsActionSupport.class);

	@Deprecated
	public static final String getVersion() {
		return PicsOrganizerVersion.getVersion();
	}

	public boolean isShowConfigMessage() {
		return !isConfigEnvironment();
	}

	public boolean isConfigEnvironment() {
		if (CONFIG == null) {
			CONFIG = "1".equals(propertyDAO.getProperty("PICS.config"));
		}
		return CONFIG;
	}

	public boolean isAlphaEnvironment() {
		Boolean isAlpha = getRequestHost().contains("alpha");

		return isAlpha;
	}

	public boolean isBetaEnvironment() throws UnknownHostException {
		Boolean isBeta = getRequestHost().contains("beta");
		if (!(isBeta || isAlphaEnvironment() || isConfigurationEnvironment() || isLocalhostEnvironment())) {
			// its not beta, alpha, config, and localhost
			if (isBetaVersion()) {
				isBeta = true;
			}
		}

		return isBeta;
	}

	public boolean isBetaVersion() {
		int major = NumberUtils.toInt(propertyDAO.getProperty("VERSION.major"), 0);
		int minor = NumberUtils.toInt(propertyDAO.getProperty("VERSION.minor"), 0);

		return PicsOrganizerVersion.greaterThan(major, minor);
	}

	public boolean isConfigurationEnvironment() {
		Boolean isConfiguration = getRequestHost().contains("config");

		return isConfiguration;
	}

	public boolean isLiveEnvironment() throws UnknownHostException {
		Boolean isStable = getRequestHost().contains("stable");
		if (!(isStable || isAlphaEnvironment() || isConfigurationEnvironment() || isLocalhostEnvironment() || isBetaEnvironment())) {
			if (!isBetaVersion()) {
				isStable = true;
			}
		}

		return isStable;
	}

	public boolean isLocalhostEnvironment() {
		Boolean isLocalhost = getRequestHost().contains(":8080");

		return isLocalhost;
	}

	public boolean isI18nReady() {
		return "1".equals(propertyDAO.getProperty("PICS.i18nReady"));
	}

	public boolean isLiveChatEnabled() {
		return "1".equals(propertyDAO.getProperty("PICS.liveChat"));
	}

	/**
	 * This method is used to set the clear_cache flag in the AppProperty table
	 * to allow the contractor daemon to reset caches on all 3 servers.
	 * 
	 */
	protected void flagClearCache() {
		propertyDAO.setProperty(ClearCacheAction.CLEAR_CACHE_PROPERTY, "1");
	}

	public boolean isLoggedIn(boolean anonymous) {
		if (!anonymous) {
			if (!forceLogin()) {
				return false;
			}
		}
		return true;
	}

	protected void loadPermissions() {
		loadPermissions(true);
	}

	protected void loadPermissions(boolean autoLogin) {
		if (permissions != null) {
			// Already set
			return;
		}

		if (ActionContext.getContext().getSession() == null) {
			addActionError("Failed to get session");
		} else {
			permissions = (Permissions) ActionContext.getContext().getSession().get("permissions");
		}

		if (permissions == null) {
			permissions = new Permissions();
		}

		if (permissions.isLoggedIn()) {
			return;
		}

		int clientSessionUserID = getClientSessionUserID();
		if (clientSessionUserID > 0) {
			logger.info("Session timed out but HMAC cookie says the user {} is still logged in.", clientSessionUserID);
			login(clientSessionUserID);
			return;
		}

		if (autoLogin) {
			String autoLoginID = System.getProperty("pics.autoLogin");
			if (Strings.isNotEmpty(autoLoginID)) {
				logger.info("Autologging In user {} . Remove pics.autoLogin from startup to remove this feature.",
						autoLoginID);
				login(Integer.parseInt(autoLoginID));
			}
		}
	}

	private void login(int userID) {
		try {
			UserDAO userDAO = SpringUtils.getBean("UserDAO");
			User user = userDAO.find(userID);

			permissions.login(user);
			LocaleController.setLocaleOfNearestSupported(permissions);
			ActionContext.getContext().getSession().put("permissions", permissions);
		} catch (Exception e) {
			logger.error("Problem autologging in.  Id supplied was: {}", userID);
		}
	}

	protected boolean forceLogin() {
		loadPermissions();

		try {
			if (permissions.isLoggedIn() && permissions.getAdminID() == 0 && permissions.isForcePasswordReset()) {
				ChangePassword cp = new ChangePassword();
				cp.resetPasswordLink(permissions.getUserId(), ServletActionContext.getRequest().getRequestURL()
						.toString());
				return true;
			}

			if (!permissions.loginRequired(ServletActionContext.getResponse(), ServletActionContext.getRequest())) {
				return false;
			}
		} catch (Exception e) {
			logger.error("PicsActionSupport: Error occurred trying to login: {}", e.getMessage());
			return false;
		}

		return true;
	}

	protected boolean forceLogin(String alternateReturnURL) {
		loadPermissions();

		try {
			if (permissions.isLoggedIn() && permissions.getAdminID() == 0 && permissions.isForcePasswordReset()) {
				ChangePassword cp = new ChangePassword();
				cp.resetPasswordLink(permissions.getUserId(), alternateReturnURL);

				return true;
			}

			if (!permissions.loginRequired(ServletActionContext.getResponse(), alternateReturnURL)) {
				return false;
			}
		} catch (Exception e) {
			logger.error("PicsActionSupport: Error occurred trying to login: {}", e.getMessage());
			return false;
		}

		return true;
	}

	public void tryPermissions(OpPerms opPerms) throws NoRightsException {
		permissions.tryPermission(opPerms, OpType.View);
	}

	public void tryPermissions(OpPerms opPerms, OpType opType) throws NoRightsException {
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
		UserDAO dao = SpringUtils.getBean("UserDAO");
		try {
			User user = dao.find(userId);
			return user;
		} catch (Exception e) {
			logger.error("Error finding user: {}", e.getMessage());
			return null;
		}
	}

	public Account getAccount() {
		if (account == null) {
			loadPermissions();
			AccountDAO dao = SpringUtils.getBean("AccountDAO");
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
			DateFormat dateFormat = new SimpleDateFormat(PicsDateFormat.American);
			return dateFormat.format(date);
		} catch (Exception e) {
			return null;
		}
	}

	public String getFuzzyDate(Date d) {
		Calendar now = Calendar.getInstance();
		Calendar then = Calendar.getInstance();
		then.setTime(d);
		long diff = now.getTimeInMillis() - then.getTimeInMillis();

		long SECONDS = 1000;
		long MINUTES = SECONDS * 60;
		long HOURS = MINUTES * 60;
		long DAYS = HOURS * 24;
		long WEEKS = DAYS * 7;
		long MONTHS = DAYS * 30;
		long YEARS = DAYS * 365;

		String fuzzy = "";
		if (diff < MINUTES)
			fuzzy = getText("date.Today");
		else if (diff < HOURS) {
			long n = diff / MINUTES;
			fuzzy = getTextParameterized("date.fuzzy.minute", n);
		} else if (diff < DAYS) {
			long n = diff / HOURS;
			fuzzy = getTextParameterized("date.fuzzy.hour", n);
		} else if (diff < WEEKS) {
			long n = diff / DAYS;
			fuzzy = getTextParameterized("date.fuzzy.day", n);
		} else if (diff < MONTHS) {
			long n = diff / WEEKS;
			fuzzy = getTextParameterized("date.fuzzy.week", n);
		} else if (diff < YEARS) {
			long n = diff / MONTHS;
			fuzzy = getTextParameterized("date.fuzzy.month", n);
		} else {
			long n = diff / YEARS;
			fuzzy = getTextParameterized("date.fuzzy.year", n);
		}

		return fuzzy;
	}

	public Date parseDate(String date) {
		SimpleDateFormat americanFormat = new SimpleDateFormat(PicsDateFormat.American);

		try {
			return americanFormat.parse(date);
		} catch (Exception e) {
			logger.warn("Problem parsing date for American format: '{}'", date);
		}

		try {
			SimpleDateFormat isoFormat = new SimpleDateFormat(PicsDateFormat.Iso);
			return isoFormat.parse(date);
		} catch (Exception e) {
			logger.warn("Problem parsing date for ISO format: '{}'", date);
		}

		return new Date();
	}

	public String getQueryString() {
		return ServletActionContext.getRequest().getQueryString() != null ? ServletActionContext.getRequest()
				.getQueryString() : "";
	}

	public String getRequestHost() {
		String requestURL = getRequestURL().toString();
		String requestURI = getRequestURI();
		String requestHost = requestURL.replace(requestURI, "");

		return requestHost;
	}

	public String getRequestString() {
		return requestURL;
	}

	public String getRequestURI() {
		return ServletActionContext.getRequest().getRequestURI();
	}

	public String getRequestURL() {
		return ServletActionContext.getRequest().getRequestURL().toString();
	}

	public String getServletPath() {
		return ServletActionContext.getRequest().getServletPath();
	}

	public String getIP() {
		return ServletActionContext.getRequest().getRemoteAddr();
	}

	public String getReferer() {
		return ServletActionContext.getRequest().getHeader("Referer");
	}

	public List<BasicDynaBean> getDepartmentList() throws SQLException {
		String like = ((String[]) ActionContext.getContext().getParameters().get("q"))[0];
		if (like == null)
			like = "";
		like = like.trim();

		SelectUser sql = new SelectUser();
		sql.addWhere("u.department LIKE '%" + Strings.escapeQuotes(like) + "%'");
		sql.addGroupBy("u.department");
		sql.addField("u.department");
		sql.addOrderBy("u.department");
		Database db = new Database();
		return db.select(sql.toString(), true);
	}

	public Set<User> getAuditorList() {
		if (auditorList == null) {
			auditorList = new TreeSet<User>();
			UserDAO dao = SpringUtils.getBean("UserDAO");
			auditorList.addAll(dao.findByGroup(User.GROUP_AUDITOR));
			auditorList.addAll(dao.findByGroup(User.GROUP_CSR));
		}
		return auditorList;
	}

	public Set<User> getSafetyList() {
		if (safetyList == null) {
			safetyList = new TreeSet<User>();
			UserDAO dao = SpringUtils.getBean("UserDAO");
			safetyList.addAll(dao.findByGroup(User.GROUP_SAFETY));
		}
		return safetyList;
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

	public String getUrl() {
		return url;
	}

	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public JSONArray getJsonArray() {
		return jsonArray;
	}

	public void setJsonArray(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
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
					logger.error("Error cleaning up parameter");
					logger.error("{}", e.getStackTrace());
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

	@SuppressWarnings("rawtypes")
	@Override
	public void setRequest(Map arg0) {
		if (requestURL == null)
			requestURL = ServletActionContext.getRequest().getRequestURL().toString();
	}

	public String setUrlForRedirect(String url) throws IOException {
		this.url = url;

		if (!Strings.isEmpty(this.url)) {
			return REDIRECT;
		}

		return BLANK;
	}

	public String getFormattedDollarAmount(String answer) {
		String response = "$0";

		try {
			String temp = answer.replaceAll(",", "");
			DecimalFormat decimalFormat = new DecimalFormat("$#,##0");

			Long input = new Long(temp);

			response = decimalFormat.format(input);
		} catch (Exception e) {
			logger.error("unable to format as money: {}", answer);
		}
		return response;
	}

	public boolean isDebugging() {
		return "1".equals(System.getProperty("pics.debug"))
				|| (permissions != null && permissions.hasPermission(OpPerms.DevelopmentEnvironment));
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

	public synchronized void clearMessage() {
		alertMessages = null;
	}

	public synchronized void clearMessages() {
		setActionErrors(null);
		setAlertMessages(null);
		setActionMessages(null);
	}

	public void setAlertMessages(Collection<String> messages) {
		alertMessages = messages;
	}

	protected void setClientSessionCookie() {
		Cookie cookie = new Cookie("PICS_ORG_SESSION", permissions.getUserIdString());
		cookie.setMaxAge(24 * 60 * 60);
		if (!isLocalhostEnvironment()) {
			cookie.setDomain("picsorganizer.com");
		}
		ServletActionContext.getResponse().addCookie(cookie);
	}

	private int getClientSessionUserID() {
		Cookie sessionCookie = getClientSessionCookie();
		if (sessionCookie == null)
			return 0;

		return Integer.parseInt(sessionCookie.getValue());
	}

	private Cookie getClientSessionCookie() {
		Cookie[] cookies = getRequest().getCookies();
		for (Cookie cookie : cookies) {
			if ("PICS_ORG_SESSION".equals(cookie.getName())) {
				return cookie;
			}
		}
		return null;
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

	@Transient
	public String getHelpURL() {
		String helpUrl = "http://help.picsorganizer.com/login.action?os_destination=homepage.action&";

		if (permissions.isOperatorCorporate()) {
			helpUrl += "os_username=operator&os_password=oper456ator";
		} else if (permissions.isContractor()) {
			helpUrl += "os_username=contractor&os_password=con123tractor";
		} else {
			helpUrl += "os_username=admin&os_password=ad9870mins";
		}

		return helpUrl;
	}

	public String getActionName() {
		return ServletActionContext.getActionMapping().getName();
	}

	public String getMethodName() {
		return ServletActionContext.getActionMapping().getMethod();
	}

	public String getProtocol() {
		return URLUtils.getProtocol(ServletActionContext.getRequest());
	}

	protected HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}
}