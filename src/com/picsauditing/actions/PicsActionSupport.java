package com.picsauditing.actions;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Inject;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.MainPage;
import com.picsauditing.access.*;
import com.picsauditing.actions.users.ChangePassword;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectUser;
import com.picsauditing.security.CookieSupport;
import com.picsauditing.security.SessionCookie;
import com.picsauditing.security.SessionSecurity;
import com.picsauditing.strutsutil.AdvancedValidationAware;
import com.picsauditing.strutsutil.FileDownloadContainer;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.*;
import com.picsauditing.util.system.PicsEnvironment;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.RequestAware;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Transient;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("serial")
public class PicsActionSupport extends TranslationActionSupport implements RequestAware, SecurityAware,
		AdvancedValidationAware {

	protected static final int DELETE_COOKIE_AGE = 0;
	protected static final int SESSION_COOKIE_AGE = -1;
	protected static final int TWENTY_FOUR_HOURS = 24 * 60 * 60;
	protected static Boolean CONFIG = null;

	public static final String PLAIN_TEXT = "plain-text";
	public static final String ACTION_MESSAGES = "action-messages";
	public static final String LOGIN_AJAX = "LoginAjax";
	public static final String BLANK = "blank";
	public static final String JSON = "json";
	public static final String JSONP = "jsonp";
	public static final String XML = "xml";
	public static final String FILE_DOWNLOAD = "file";
	public static final String JSON_ARRAY = "jsonArray";
	public static final String CHART_XML = "chartXML";
	public static final String REDIRECT = "redirect";
	public static final String INPUT_ERROR = "inputError";
	public static final String[] DATAFEED_FORMATS = {JSON, XML};

	@Autowired
	protected AppPropertyDAO propertyDAO;
	@Autowired
	protected BasicDAO dao;
	@Autowired
	protected CountryDAO countryDAO;
	@Autowired
	protected UserDAO userDAO;
	@Autowired
	protected FeatureToggle featureToggleChecker;
	@Autowired
	private PermissionBuilder permissionBuilder;

	protected Collection<String> alertMessages;

	private String actionMessageHeader;
	private String alertMessageHeader;
	private String actionErrorHeader;

	private long maxFileUploadBytes;

	protected String requestURL = null;

	protected Permissions permissions = null;

	/**
	 * String that is used for simple messages.
	 * <p/>
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
	 * Container to hold a file being downloaded by the user.
	 *
	 * @see com.picsauditing.strutsutil.FileResult
	 */
	protected FileDownloadContainer fileContainer = null;

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

	public boolean isShowConfigMessage() {
		return !isConfigEnvironment();
	}

	private PicsEnvironment picsEnvironment;

	private PicsEnvironment getEnvironmentDeterminer() {
		/*
		 * Note: Lazy-loading like this is often overused in action code
		 * (because the class only stays instantiated for as long as it takes to
		 * render the page, and get-accessors normally only get called once in
		 * that time); however, in this case, the PICS enviromnent is queried
		 * numerous times during every page load.
		 */
		if (picsEnvironment == null) {
			picsEnvironment = new PicsEnvironment(propertyDAO.getProperty(AppProperty.VERSION_MAJOR),
					propertyDAO.getProperty(AppProperty.VERSION_MINOR));
		}

		return picsEnvironment;
	}

	public String getPicsEnvironment() {
		return getEnvironmentDeterminer().getEnvironment();
	}

	public boolean isConfigEnvironment() {
		// FIXME How is this different than isConfigurationEnvironment()?  Is this one deprecated?
		if (CONFIG == null) {
			CONFIG = "1".equals(propertyDAO.getProperty("PICS.config"));
		}

		return CONFIG;
	}

	public boolean isAlphaEnvironment() {
		return getEnvironmentDeterminer().isAlpha();
	}

	public boolean isBetaEnvironment() {
		return getEnvironmentDeterminer().isBeta();
	}

	public boolean isQaEnvironment() {
		return getEnvironmentDeterminer().isQa();
	}

	public boolean isConfigurationEnvironment() {
		return getEnvironmentDeterminer().isConfiguration();
	}

	public boolean isLiveEnvironment() throws UnknownHostException {
		return getEnvironmentDeterminer().isStable();
	}

	public boolean isLocalhostEnvironment() {
		return getEnvironmentDeterminer().isLocalhost();
	}

	public boolean isI18nReady() {
		return "1".equals(propertyDAO.getProperty("PICS.i18nReady"));
	}

	public boolean isLiveChatEnabled() {
		return "1".equals(propertyDAO.getProperty("PICS.liveChat"));
	}

	public String getVersion() {
		return AppVersion.current.getVersion();
	}

	/**
	 * This method is used to set the clear_cache flag in the AppProperty table
	 * to allow the contractor daemon to reset caches on all 3 servers.
	 */
	protected void flagClearCache() {
		propertyDAO.setProperty(ClearCacheAction.CLEAR_CACHE_PROPERTY, "1");
	}

	@Override
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
			permissions = (Permissions) ActionContext.getContext().getSession().get(Permissions.SESSION_PERMISSIONS_COOKIE_KEY);
		}

		if (permissions == null) {
			permissions = new Permissions();
		}

		if (permissions.isLoggedIn()) {
			return;
		}

		int clientSessionUserID = getClientSessionUserID();
		if (clientSessionUserID > 0) {
			logger.info("Logging in user {} from a valid session cookie.", clientSessionUserID);
			login(clientSessionUserID);
			int originalUserId = getClientSessionOriginalUserID();
			if (clientSessionUserID != originalUserId) {
				User originalUser = getUser(originalUserId);
				permissions.setRememberMeTimeInSeconds(originalUser.getAccount().getRememberMeTimeInDays()
						* TWENTY_FOUR_HOURS);
			}
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

			permissions = permissionBuilder.login(user);
			ActionContext.getContext().getSession().put(Permissions.SESSION_PERMISSIONS_COOKIE_KEY, permissions);
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

	@Override
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
		try {
			User user = userDAO.find(userId);
			return user;
		} catch (Exception e) {
			logger.error("Error finding user where id = : {}", userId, e);
			return null;
		}
	}

	protected String apiKey;

	public String getApiKey() {
		return apiKey;
	}

	/**
	 * IMPORTANT NOTE: This setter does not normally get called in time for the
	 * SecurityInterceptor to make use of it. (It's a chicken-and-egg thing.)
	 * So, this setter must be called mannually -- by making your action class
	 * ParameterAware and then implementing the setParameters method. See
	 * DataFeed, for example.
	 */
	public void setApiKey(final String apiKey) {
		logger.debug("Setting apiKey = {}", apiKey);
		this.apiKey = apiKey;
	}

	@Override
	public boolean isApiUser() throws AjaxNotLoggedInException {
		if (apiKey == null) {
			return false;
		}
		User user = null;
		try {
			user = userDAO.findByApiKey(apiKey);
		} catch (SecurityException e) {
			throw new AjaxNotLoggedInException(e.getMessage());
		}
		try {
			permissions = permissionBuilder.login(user);
			this.user = user;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public Account getAccount() {
		if (account == null) {
			loadPermissions();
			AccountDAO dao = SpringUtils.getBean(SpringUtils.ACCOUNT_DAO);
			account = dao.find(permissions.getAccountId(), permissions.getAccountType());
		}
		return account;
	}

	public OperatorAccount getOperatorAccount() {
		Account operator = getAccount();
		if (Account.OPERATOR_ACCOUNT_TYPE.equals(operator.getType()) || Account.CORPORATE_ACCOUNT_TYPE.equals(operator.getType())) {
			return (OperatorAccount) operator;
		}

		return null;
	}

	public Permissions getPermissions() {
		try {
			if (permissions == null) {
				loadPermissions();
			}
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

		if (permissions == null || permissions.getTimezone() == null) {
			return dateFormat.format(serverDate);
		}

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
		if (diff < MINUTES) {
			fuzzy = getText("date.Today");
		} else if (diff < HOURS) {
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
				.getQueryString() : Strings.EMPTY_STRING;
	}

	private String getServerName() {
		return ServletActionContext.getRequest().getServerName();
	}


	public String getRequestHost() {
		String requestURL = getRequestURL().toString();
		String requestURI = getRequestURI();
		String requestHost = requestURL.replace(requestURI, Strings.EMPTY_STRING);

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
		if (like == null) {
			like = "";
		}
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
			auditorList.addAll(userDAO.findByGroup(User.GROUP_AUDITOR));
			auditorList.addAll(userDAO.findByGroup(User.GROUP_CSR));
		}
		return auditorList;
	}

	public Set<User> getSafetyList() {
		if (safetyList == null) {
			safetyList = new TreeSet<User>();
			if (permissions.isAdmin() && !permissions.isOperatorCorporate()) {
				safetyList.addAll(userDAO.findByGroup(User.GROUP_SAFETY));
			} else {
				safetyList.addAll(userDAO.findCorporateAuditors(permissions.getAccountId()));
			}
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
		if (ftpDir != null && ftpDir.length() > 0) {
			return ftpDir;
		}

		try {
			ftpDir = ServletActionContext.getServletContext().getInitParameter("FTP_DIR");
		} catch (Exception exception) {
			// Most likely thrown during testing
			Logger logger = LoggerFactory.getLogger(PicsActionSupport.class);
			logger.error("Error getting ftp dir", exception);
		}

		if (ftpDir != null && ftpDir.length() > 0) {
			return ftpDir;
		}

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
			return Strings.EMPTY_STRING;
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

	public FileDownloadContainer getFileContainer() {
		return fileContainer;
	}

	public void setFileContainer(FileDownloadContainer fileContainer) {
		this.fileContainer = fileContainer;
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
	 * @param name Name of the parameter you want to check in the map
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
		if (invoiceDate == null) {
			return 0;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(invoiceDate);
		cal.add(Calendar.DAY_OF_YEAR, 90);
		return DateBean.getDateDifference(cal.getTime());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setRequest(Map arg0) {
		if (requestURL == null) {
			requestURL = ServletActionContext.getRequest().getRequestURL().toString();
		}
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
			String temp = answer.replaceAll(",", Strings.EMPTY_STRING);
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

	@Override
	public synchronized void addAlertMessage(String anAlertMessage) {
		internalGetAlertMessages().add(anAlertMessage);
	}

	@Override
	public synchronized boolean hasAlertMessages() {
		return (alertMessages != null) && !alertMessages.isEmpty();
	}

	@Override
	public synchronized Collection<String> getAlertMessages() {
		return alertMessages;
	}

	public synchronized void clearMessage() {
		alertMessages = null;
	}

	@Override
	public synchronized void clearMessages() {
		setActionErrors(null);
		setAlertMessages(null);
		setActionMessages(null);
	}

	@Override
	public void setAlertMessages(Collection<String> messages) {
		alertMessages = messages;
	}

	protected int getClientSessionOriginalUserID() {
		SessionCookie sessionCookie = validSessionCookie();
		return (sessionCookie == null) ? 0 : sessionCookie.getUserID();
	}

	protected int getClientSessionUserID() {
		SessionCookie sessionCookie = validSessionCookie();
		if (sessionCookie == null) {
			return 0;
		}
		if (sessionCookie.getData("switchTo") == null) {
			return sessionCookie.getUserID();
		} else {
			int switchToUserId = (Integer) sessionCookie.getData("switchTo");
			return switchToUserId;
		}
	}

	protected boolean isRememberMeSetInCookie() {
		SessionCookie sessionCookie = validSessionCookie();
		if (sessionCookie == null) {
			return false;
		}
		if (sessionCookie.getData("rememberMe") == null || !(Boolean) sessionCookie.getData("rememberMe")) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * If the cookie is not valid (tampered with or the server key is the wrong
	 * one) then return false If the cookie is valid and the user is being
	 * persistently remembered then return true If the cookie is valid and the
	 * user is not being persistently remembered then check to see if the cookie
	 * session (in the cookie itself) is expired
	 *
	 * @see
	 * com.picsauditing.access.SecurityAware#sessionCookieIsValidAndNotExpired()
	 */
	@Override
	public boolean sessionCookieIsValidAndNotExpired() {
		if (!featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_SESSION_COOKIE)) {
			return true;
		}
		String sessionCookieValue = clientSessionCookieValue();
		if (!SessionSecurity.cookieIsValid(sessionCookieValue)) {
			return false;
		} else if (isRememberMeSetInCookie()) {
			return true;
		} else {
			loadPermissions();
			SessionCookie sessionCookie = SessionSecurity.parseSessionCookie(sessionCookieValue);
			long nowInSeconds = new Date().getTime() / 1000;
			long cookieCreatedSeconds = sessionCookie.getCookieCreationTime().getTime() / 1000;
			return (permissions != null && nowInSeconds - cookieCreatedSeconds < permissions
					.getSessionCookieTimeoutInSeconds());
		}
	}

	private SessionCookie validSessionCookie() {
		String sessionCookieValue = clientSessionCookieValue();
		if (sessionCookieValue == null || !SessionSecurity.cookieIsValid(sessionCookieValue)) {
			clearPicsOrgCookie();
			return null;
		}
		SessionCookie sessionCookie = SessionSecurity.parseSessionCookie(sessionCookieValue);
		return sessionCookie;
	}

	@Override
	public void updateClientSessionCookieExpiresTime() {
		SessionCookie sessionCookie = validSessionCookie();
		if (sessionCookie != null) {
			addClientSessionCookieToResponse(sessionCookie);
		}
	}

	@Override
	public String clearPermissionsSessionAndCookie() throws Exception {
		if (permissions != null) {
			permissions.clear();
		}
		ActionContext.getContext().getSession().clear();
		clearPicsOrgCookie();
		return SUCCESS;
	}

	public String logout() throws Exception {
		invalidateSession();
		return clearPermissionsSessionAndCookie();
	}

	public void invalidateSession() {
		ActionContext.getContext().getSession().clear();
		HttpSession session = ServletActionContext.getRequest().getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}

	protected void clearPicsOrgCookie() {
		Cookie cookie = new Cookie(CookieSupport.SESSION_COOKIE_NAME, "");
		cookie.setMaxAge(DELETE_COOKIE_AGE);
		if (!isLocalhostEnvironment()) {
			cookie.setDomain(SessionSecurity.SESSION_COOKIE_DOMAIN);
		}
		ServletActionContext.getResponse().addCookie(cookie);
	}

	private String clientSessionCookieValue() {
		return SessionSecurity.clientSessionCookieValue(getRequest());
	}

	private void addClientSessionCookieToResponse(SessionCookie sessionCookie) {
		loadPermissions(false);
		String sessionCookieContent = sessionCookieContent(sessionCookie);
		int maxAge = SESSION_COOKIE_AGE;
		if (permissions != null && isRememberMeSetInCookie()) {
			maxAge = permissions.getRememberMeTimeInSeconds();
		}
		doSetCookie(sessionCookieContent, maxAge);
	}

	private void doSetCookie(String sessionCookieContent, int maxAge) {
		try {
			String cookieContent = URLEncoder.encode(sessionCookieContent, "US-ASCII");
			addClientSessionCookieToResponse(cookieContent, maxAge);
		} catch (UnsupportedEncodingException e) {
			// this won't happen unless somehow US-ASCII is removed from java...
			logger.error("URLEncoder was given a bad encoding format: {}", e.getMessage());
		}
	}

	protected void addClientSessionCookieToResponse() {
		addClientSessionCookieToResponse(false, 0);
	}

	protected void addClientSessionCookieToResponse(boolean rememberMe, int switchToUser) {
		String sessionCookieContent = sessionCookieContent(rememberMe, switchToUser);
		int maxAge = SESSION_COOKIE_AGE;
		if (permissions != null && (rememberMe || isRememberMeSetInCookie())) {
			maxAge = permissions.getRememberMeTimeInSeconds();
		}
		if (rememberMe && maxAge < 0) {
			addAlertMessage(getText("Login.NoPermissionToRememberMe"));
		}
		doSetCookie(sessionCookieContent, maxAge);
	}

	private void addClientSessionCookieToResponse(String sessionCookieContent, int maxAge) {
		if (featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_SESSION_COOKIE)) {
			Cookie cookie = new Cookie(CookieSupport.SESSION_COOKIE_NAME, sessionCookieContent);
			cookie.setMaxAge(maxAge);
			if (!isLocalhostEnvironment()) {
				cookie.setDomain(SessionSecurity.SESSION_COOKIE_DOMAIN);
			}
			ServletActionContext.getResponse().addCookie(cookie);
		}
	}

	private String sessionCookieContent(boolean rememberMe, int switchToUser) {
		SessionCookie sessionCookie = new SessionCookie();
		Date now = new Date();
		sessionCookie.setUserID(permissions.getUserId());
		sessionCookie.setCookieCreationTime(now);
		if (switchToUser > 0) {
			sessionCookie.putData("switchTo", switchToUser);
		}
		sessionCookie.putData("rememberMe", rememberMe);
		SessionSecurity.addValidationHashToSessionCookie(sessionCookie);
		return sessionCookie.toString();
	}

	private String sessionCookieContent(SessionCookie sessionCookie) {
		Date now = new Date();
		sessionCookie.setUserID(sessionCookie.getUserID());
		sessionCookie.setCookieCreationTime(now);
		sessionCookie.setEmbeddedData(sessionCookie.getEmbeddedData());
		SessionSecurity.addValidationHashToSessionCookie(sessionCookie);
		return sessionCookie.toString();
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

	public String getChatUrl() {
		String scheme = getRequest().getScheme();
		Locale locale = TranslationActionSupport.getLocaleStatic();

		// We're using a whitelist strategy because we don't want to pass junk downstream
		String language = Locale.ENGLISH.getDisplayLanguage();

		if (supportedLanguages.isLanguageVisible(locale)) {
			language = locale.getDisplayLanguage();
		}

		String chatUrl = scheme + "://server.iad.liveperson.net/hc/90511184/" +
				"?cmd=file" +
				"&amp;file=visitorWantsToChat" +
				"&amp;site=90511184" +
				"&amp;imageUrl=" + scheme + "://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/" + language + "/General/3a" +
				"&amp;referrer=";

		return chatUrl;
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

	@Override
	public String getActionMessageHeader() {
		return actionMessageHeader;
	}

	@Override
	public void setActionMessageHeader(String header) {
		actionMessageHeader = header;
	}

	@Override
	public String getAlertMessageHeader() {
		return alertMessageHeader;
	}

	@Override
	public void setAlertMessageHeader(String header) {
		alertMessageHeader = header;
	}

	@Override
	public String getActionErrorHeader() {
		return actionErrorHeader;
	}

	@Override
	public void setActionErrorHeader(String header) {
		actionErrorHeader = header;
	}

	protected void addFieldErrorIfMessage(String fieldName, String errorMessageKey) {
		if (StringUtils.isNotEmpty(errorMessageKey)) {
			addFieldError(fieldName, getText(errorMessageKey));
		}
	}

	protected JSONObject getJsonFromRequestPayload() {
		JSONObject jsonObject = new JSONObject();
		HttpServletRequest request = getRequest();
		if (request == null) {
			return jsonObject;
		}

		BufferedReader bufferedReader = null;
		try {
			bufferedReader = request.getReader();
			jsonObject = JSONUtilities.parseJsonFromInput(bufferedReader);
		} catch (Exception e) {
			logger.error("There was an sqlException parsing the JSON from the request", e);
		} finally {
			closeBufferedReader(bufferedReader);
		}

		return jsonObject;
	}

	protected void closeBufferedReader(BufferedReader bufferedReader) {
		try {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		} catch (Exception e) {
			logger.error("There was an sqlException closing the bufferedReader", e);
		}
	}

	public long getMaxFileUploadBytes() {
		return maxFileUploadBytes;
	}

	@Inject("struts.multipart.maxSize")
	public void setMaxFileUploadBytes(String value) {
		this.maxFileUploadBytes = Long.parseLong(value);
	}

	public String getMaxFileUploadSize() {
		BigDecimal value = new BigDecimal(maxFileUploadBytes);
		value = value.divide(new BigDecimal((1024 * 1024 * 1024))).setScale(2);
		return value.toString();
	}

	public String getPicsPhoneNumber() {
		return getPicsPhoneNumber(permissions.getCountry());
	}

	public String getPicsPhoneNumber(String country) {
		// Rolled up phone number logic into MainPage
		return getPhoneNumberFor(PhoneNumberType.MAIN, country);
	}

	public String getSalesPhoneNumber() {
		return getSalesPhoneNumber(permissions.getCountry());
	}

	public String getSalesPhoneNumber(String country) {
		return getPhoneNumberFor(PhoneNumberType.SALES, country);
	}

	public String getFaxNumber() {
		return getFaxNumber(permissions.getCountry());
	}

	public String getFaxNumber(String country) {
		// Rolled up phone number logic into MainPage
		return getPhoneNumberFor(PhoneNumberType.FAX, country);
	}

	private String getPhoneNumberFor(PhoneNumberType type, String country) {
		MainPage mainPage = new MainPage();
		mainPage.setCountryDAO(countryDAO);
		mainPage.setPermissions(permissions);

		String number = null;
		switch (type) {
			case FAX:
				number = mainPage.getFaxNumber(country);
				break;
			case MAIN:
				number = mainPage.getPhoneNumber(country);
				break;
			case SALES:
				number = mainPage.getSalesPhoneNumber(country);
				break;
		}

		return number;
	}

	private enum PhoneNumberType {
		FAX, MAIN, SALES
	}

	public String getLocalizedPhoneNumberForUser(User user) {
		return getLocalizedPhoneNumberForUser(user, null);
	}

	public String getLocalizedPhoneNumberForUser(User user, Country country) {
		if (featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_COUNTRY_PHONE_NUMBER)
				&& user.getPhone().length() <= 4) {
			String format = "%s x%s";

			if (country != null) {
				return String.format(format, country.getPhone(), user.getPhone());
			} else {
				return String.format(format, getPicsPhoneNumber(), user.getPhone());
			}
		}

		return user.getPhone();
	}
}
