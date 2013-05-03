package com.picsauditing;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.search.Database;
import org.apache.struts2.StrutsStatics;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class PicsActionTest {
	protected ActionContext actionContext;
	protected Cookie[] cookies;
	protected Map<String, Object> session;
	private String userAgent = "Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B334b Safari/531.21.102011-10-16 20:23:50";
	protected Map<String, Object> parameters;
	protected int GREATER_THAN_ONE = 941;

	@Mock
	protected HttpServletRequest request;
	@Mock
	protected HttpServletResponse response;
	@Mock
	protected HttpSession httpSession;
	@Mock
	protected ServletContext servletContext;
	@Mock
	protected Permissions permissions;
	@Mock
	protected I18nCache i18nCache;
	@Mock
	protected AppPropertyDAO propertyDAO;
	@Mock
	protected LanguageModel languageModel;

	@SuppressWarnings("rawtypes")
	@Mock
	private Enumeration enumeration;

	@BeforeClass
	public static void classSetUp() throws Exception {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", mock(Database.class));
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
		Whitebox.setInternalState(I18nCache.class, "INSTANCE", (I18nCache) null);
		ActionContext.setContext(null);
	}

	protected void setUp(PicsActionSupport controller) throws Exception {
		setupMocks();
		setObjectUnderTestState(controller);
	}

	protected void setObjectUnderTestState(PicsActionSupport controller) {
		Whitebox.setInternalState(controller, "supportedLanguages", languageModel);
		Whitebox.setInternalState(controller, "permissions", permissions);
		Whitebox.setInternalState(controller, "i18nCache", i18nCache);
		Whitebox.setInternalState(controller, "propertyDAO", propertyDAO);
	}

	protected void setupMocks() {
		parameters = new HashMap<String, Object>();

		session = new HashMap<String, Object>();
		Map<String, Object> context = new HashMap<String, Object>();
		context.put(StrutsStatics.HTTP_REQUEST, request);
		context.put(StrutsStatics.HTTP_RESPONSE, response);
		context.put(StrutsStatics.SERVLET_CONTEXT, servletContext);
		context.put(ActionContext.SESSION, session);
		context.put(ActionContext.LOCALE, Locale.ENGLISH);
		context.put(ActionContext.PARAMETERS, parameters);
		context.put(ActionContext.ACTION_NAME, "UnitTest");
		context.put(StrutsStatics.HTTP_RESPONSE, response);

		actionContext = new ActionContext(context);
		ActionContext.setContext(actionContext);
		cookies = new Cookie[1];
		cookies[0] = new Cookie("foo", "foo");
		when(languageModel.getClosestVisibleLocale(any(Locale.class))).thenReturn(Locale.US);
		when(request.getCookies()).thenReturn(cookies);
		when(request.getRequestURL()).thenReturn(new StringBuffer("www.example.com/example.html"));
		when(request.getRequestURI()).thenReturn("/example.html");
		when(request.getHeader("User-Agent")).thenReturn(userAgent);
		when(request.getSession()).thenReturn(httpSession);
		when(request.getHeaderNames()).thenReturn(enumeration);
		when(i18nCache.hasKey(anyString(), eq(Locale.ENGLISH))).thenReturn(true);
		when(permissions.getLocale()).thenReturn(Locale.ENGLISH);
		when(servletContext.getInitParameter("FTP_DIR")).thenReturn("/tmp/ftp_dir");
		session.put("permissions", permissions);
	}
}
