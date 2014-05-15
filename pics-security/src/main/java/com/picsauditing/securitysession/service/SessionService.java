package com.picsauditing.securitysession.service;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.Anonymous;
import com.picsauditing.securitysession.PermissionBuilder;
import com.picsauditing.securitysession.Permissions;
import com.picsauditing.securitysession.UserAgentParser;
import com.picsauditing.securitysession.cookie.CookieSupport;
import com.picsauditing.securitysession.cookie.SessionCookie;
import com.picsauditing.securitysession.cookie.SessionSecurity;
import com.picsauditing.securitysession.dao.*;
import com.picsauditing.securitysession.entities.*;
import com.picsauditing.securitysession.util.system.PicsEnvironment;
import com.picsauditing.util.Strings;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("serial")
public class SessionService /*extends TranslationActionSupport implements RequestAware, SecurityAware,
        AdvancedValidationAware, ViewNamespaceAware*/ {

    private static final String NO_SESSION = "NO SESSION";

    private static final String HAS_SESSION = "HAS SESSION";

    private static final Pattern TARGET_IP_PATTERN = Pattern.compile("^"
            + CookieSupport.TARGET_IP_COOKIE_NAME + "-([^-]*)-81$");

    protected static final int DELETE_COOKIE_AGE = 0;
    protected static final int SESSION_COOKIE_AGE = -1;
    protected static final int TWENTY_FOUR_HOURS = 24 * 60 * 60;

    public static final String JSON = "json";

    @Autowired
    protected UserLoginLogDAO loginLogDAO;
    @Autowired
    protected ProfileDAO profileDAO;
    @Autowired
    protected AppUserDAO appUserDAO;
    @Autowired
    protected UserDAO userDAO;
    @Autowired
    protected AppPropertyDAO propertyDAO;
    @Autowired
    private PermissionBuilder permissionBuilder;

    private PicsEnvironment picsEnvironment;

    protected JSONObject json = new JSONObject();
    protected User user;
    protected Permissions permissions = null;


    private final Logger logger = LoggerFactory.getLogger(SessionService.class);

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

    public boolean isLiveEnvironment() {
        return getEnvironmentDeterminer().isStable();
    }

    public boolean isBetaEnvironment() {
        return getEnvironmentDeterminer().isBeta();
    }

    public boolean isLocalhostEnvironment() {
        return getEnvironmentDeterminer().isLocalhost();
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

    protected int getClientSessionAppUserID() {
        SessionCookie sessionCookie = validSessionCookie();
        if (sessionCookie != null) {
            return sessionCookie.getAppUserID();
        }

        return 0;
    }

    protected boolean isRememberMeSetInCookie() {
        return isRememberMeSetInCookie(validSessionCookie());
    }

    private boolean isRememberMeSetInCookie(SessionCookie sessionCookie) {
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
    public boolean sessionCookieIsValidAndNotExpired() {
        SessionCookie sessionCookie = validSessionCookie();
        if (sessionCookie == null) {
            return false;
        } else {
            boolean cookieIsTimedOut = !cookieIsNotTimedOut(sessionCookie);
            if (isRememberMeSetInCookie(sessionCookie)) {
                if (cookieIsTimedOut) {
                    logRememberMeLogin(getUser());
                }
                return true;
            } else {
                return !cookieIsTimedOut;
            }
        }
    }

    protected void logRememberMeLogin(User user) {
        UserLoginLog loginLog = new UserLoginLog();
        loginLog.setLoginMethod(LoginMethod.RememberMeCookie);
        loginLog.setUser(user);
        logLoginAttempt(loginLog);
    }

    private void logLoginAttempt(UserLoginLog loginLog) {
        if (loginLog.getUser() == null) {
            return;
        }

        loginLog.setLoginDate(new Date());
        loginLog.setRemoteAddress(getRequest().getRemoteAddr());
        String serverName = getRequest().getLocalName();
        UserAgentParser uap = new UserAgentParser(getRequest().getHeader("User-Agent"));
        loginLog.setBrowser(uap.getBrowserName() + " " + uap.getBrowserVersion());
        loginLog.setUserAgent(getRequest().getHeader("User-Agent"));
        if (isLiveEnvironment() || isBetaEnvironment()) {
            // Need computer name instead of www
            try {
                serverName = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                logger.error("Error determining host: {}", e);
                serverName = "Live or Beta";
            }
        }

        loginLog.setServerAddress(serverName);

        String targetIp = extractTargetIpFromCookie();
        if (!Strings.isEmpty(targetIp)) {
            loginLog.setTargetIP(targetIp);
        }

        loginLog.setSuccessful((permissions == null) ? false : permissions.isLoggedIn());
        if (permissions != null && permissions.getAdminID() > 0) {
            loginLog.setAdmin(new User(permissions.getAdminID()));
        }

        loginLogDAO.save(loginLog);
    }

    private boolean cookieIsNotTimedOut(SessionCookie sessionCookie) {
        // in case the http session was reset
        loadPermissions();
        long timeRemaing = calculateTimeRemaining(sessionCookie, permissions);
        return (timeRemaing > -1);
    }

    private long calculateTimeRemaining(SessionCookie sessionCookie, Permissions permissions) {
        if (permissions == null || sessionCookie == null) {
            return -1;
        }

        long nowInSeconds = new Date().getTime() / 1000;
        long cookieCreatedSeconds = sessionCookie.getCookieCreationTime().getTime() / 1000;

        return remainingTimeInSeconds(permissions, nowInSeconds, cookieCreatedSeconds);
    }

    private long remainingTimeInSeconds(Permissions permissions, long nowInSeconds,
                                        long cookieCreatedSeconds) {
        long timeSinceCookieCreatedInSeconds = nowInSeconds - cookieCreatedSeconds;
        return permissions.getSessionCookieTimeoutInSeconds() - timeSinceCookieCreatedInSeconds;
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

    public void updateClientSessionCookieExpiresTime() {
        SessionCookie sessionCookie = validSessionCookie();
        if (sessionCookie != null) {
            addClientSessionCookieToResponse(sessionCookie);
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

    protected void loadPermissions() {
        loadPermissions(true);
    }

    protected void loadPermissions(boolean autoLogin) {
        if (permissions != null) {
            // Already set
            return;
        }

        if (ActionContext.getContext().getSession() == null) {
            // TODO: Action error?  Or logged message
//            addActionError("Failed to get session");
        } else {
            permissions = (Permissions) ActionContext.getContext().getSession()
                    .get(Permissions.SESSION_PERMISSIONS_COOKIE_KEY);
        }

        if (permissions == null) {
            permissions = new Permissions();
        }

        if (permissions.isLoggedIn()) {
            return;
        }

        // This happens when the server has been restarted and their HttpSession is gone, or they've moved between
        // servers in a server cookie cluster and the session was not clustered, possibly because of being completely
        // different application instances. We will still honor the cookie in these cases. We do not want to log
        // this case in loginlog. See PICS-11696
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

        int clientSessionAppUserID = getClientSessionAppUserID();
        if (clientSessionAppUserID > 0) {
            logger.info("Logging in user {} from a valid session cookie.", clientSessionUserID);
            loginAppUser(clientSessionAppUserID);
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

    private void loginAppUser(int appUserID) {
        try {
            // Try finding a normal PICSORG user first
            int userID = userDAO.findUserIDByAppUserID(appUserID);

            if (userID > 0) {
                login(userID);
            } else {
                // Try profile
                Profile profile = profileDAO.findByAppUserId(appUserID);

                if (profile != null) {
                    AppUser appUser = appUserDAO.findById(appUserID);

                    permissions = permissionBuilder.employeeUserLogin(appUser, profile);

                    ActionContext.getContext().getSession().put(Permissions.SESSION_PERMISSIONS_COOKIE_KEY, permissions);
                }
            }
        } catch (Exception e) {
            logger.error("Problem logging in. App User Id supplied was: {}", appUserID);
        }
    }

    private void login(int userID) {
        try {
            User user = userDAO.find(userID);

            permissions = permissionBuilder.login(user);
            ActionContext.getContext().getSession().put(Permissions.SESSION_PERMISSIONS_COOKIE_KEY, permissions);
        } catch (Exception e) {
            logger.error("Problem autologging in.  Id supplied was: {}", userID, e);
        }
    }

    protected void doSetCookie(String sessionCookieContent, int maxAge) {
        try {
            String cookieContent = URLEncoder.encode(sessionCookieContent, "US-ASCII");
            addClientSessionCookieToResponse(cookieContent, maxAge);
        } catch (UnsupportedEncodingException e) {
            // this won't happen unless somehow US-ASCII is removed from java...
            logger.error("URLEncoder was given a bad encoding format: {}", e.getMessage());
        }
    }

    private void addClientSessionCookieToResponse(String sessionCookieContent, int maxAge) {
        Cookie cookie = new Cookie(CookieSupport.SESSION_COOKIE_NAME, sessionCookieContent);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        if (!isLocalhostEnvironment()) {
            cookie.setDomain(SessionSecurity.SESSION_COOKIE_DOMAIN);
        }
        ServletActionContext.getResponse().addCookie(cookie);
    }

    private String sessionCookieContent(boolean rememberMe, int switchToUser) {
        SessionCookie sessionCookie = new SessionCookie();
        Date now = new Date();
        sessionCookie.setUserID(permissions.getUserId());
        sessionCookie.setCookieCreationTime(now);
        if (switchToUser > 0 && switchToUser != permissions.getUserId()) {
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

    private String extractTargetIpFromCookie() {
        List<Cookie> matchingCookies = CookieSupport.cookiesFromRequestThatStartWith(getRequest(),
                CookieSupport.TARGET_IP_COOKIE_NAME);
        for (Cookie cookie : matchingCookies) {
            Matcher matcher = TARGET_IP_PATTERN.matcher(cookie.getName());
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        return "";
    }

    protected HttpServletRequest getRequest() {
        return ServletActionContext.getRequest();
    }

    @Anonymous
    public String getSessionTimeRemaining() {
        SessionCookie sessionCookie = validSessionCookie();
        if (sessionCookie == null) {
            json = buildSessionTimeRemainingResponse(NO_SESSION, null);
        } else {
            Permissions permissions = getPermissionsFromSession();
            long timeRemaining = calculateTimeRemaining(sessionCookie, permissions);
            json = buildSessionTimeRemainingResponse(HAS_SESSION, timeRemaining);
        }

        return JSON;
    }

    @SuppressWarnings("unchecked")
    private JSONObject buildSessionTimeRemainingResponse(String status, Long timeRemaining) {
        JSONObject response = new JSONObject();
        response.put("status", status);
        response.put("timeRemaining", timeRemaining);
        return response;
    }

    private Permissions getPermissionsFromSession() {
        if (ActionContext.getContext() == null || ActionContext.getContext().getSession() == null) {
            return null;
        }

        return (Permissions) ActionContext.getContext().getSession()
                .get(Permissions.SESSION_PERMISSIONS_COOKIE_KEY);
    }

}
