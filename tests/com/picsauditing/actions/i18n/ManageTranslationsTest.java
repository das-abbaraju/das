package com.picsauditing.actions.i18n;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.actions.converters.JsonObjectConverter;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.search.Report;
import com.picsauditing.search.SelectSQL;

public class ManageTranslationsTest extends PicsActionTest {
	private ManageTranslations manageTranslations;
	
	@Mock
	private AppTranslation translation;
	@Mock
	private BasicDAO dao;
	@Mock
	private UserDAO userDAO;
	@Mock
	private Report report;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		manageTranslations = new ManageTranslations();
		super.setUp(manageTranslations);
		
		PicsTestUtil.autowireDAOsFromDeclaredMocks(manageTranslations, this);
		
		manageTranslations.setTranslation(translation);
		manageTranslations.setReport(report);

		when(permissions.getLocale()).thenReturn(Locale.FRANCE);
		when(permissions.getUserId()).thenReturn(1);
		when(permissions.getAdminID()).thenReturn(1);
	}
	
	@Test
	public void execute_emptyLocaleTo () throws Exception {
		setProperty("localeTo", null);
		setProperty("button", null);
		setProperty("data", new ArrayList<BasicDynaBean> ());
		setProperty("download", false);
		
		String resultCode = manageTranslations.execute();
		
		assertEquals(resultCode, Action.SUCCESS);
		verify(permissions).getLocale();
		verify(report).getPage(anyBoolean());
	}
	
	@Test
	public void execute_SaveAttemptWithNullTranslationDoesNotSave() throws Exception {
		setProperty("localeTo", null);
		setProperty("button", "save");
		setProperty("translation", null);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);

		String resultCode = manageTranslations.execute();

		assertEquals(resultCode, Action.SUCCESS);
		verify(dao, never()).save((AppTranslation) any());
	}

	@Test
	public void execute_tracingOnSetsTracingInSession() throws Exception {
		setProperty("localeTo", Locale.FRENCH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "tracing On");

		String resultCode = manageTranslations.execute();

		assertEquals(resultCode, Action.SUCCESS);
		assertEquals(session.get("i18nTracing"), true);
	}

	@Test
	public void execute_tracingOffSetsTracingToFalseInSession() throws Exception {
		setProperty("localeTo", Locale.FRENCH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "tracing Off");

		String resultCode = manageTranslations.execute();

		assertEquals(resultCode, Action.SUCCESS);
		assertEquals(session.get("i18nTracing"), false);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void execute_tracingClear() throws Exception {
		setProperty("localeTo", Locale.FRENCH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "tracing Clear");

		String resultCode = manageTranslations.execute();

		assertEquals(resultCode, Action.SUCCESS);
		assertTrue(((Set<String>) session.get("usedI18nKeys")).isEmpty());
	}

	@Test
	public void execute_tracingAjaxReturnsBlank() throws Exception {
		setProperty("localeTo", Locale.FRENCH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "tracing Ajax");

		assertEquals(manageTranslations.execute(), PicsActionSupport.BLANK);
	}

	@Test
	public void execute_saveFunctions_EmptyTranslationValueIsDeleted() throws Exception {
		setProperty("localeTo", Locale.FRENCH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "Save");

		when(translation.getId()).thenReturn(1);
		when(translation.getValue()).thenReturn("");
		when(translation.getKey()).thenReturn("UnitTest");

		String resultCode = manageTranslations.execute();

		verify(dao).deleteData(eq(AppTranslation.class), anyString());
		assertEquals(resultCode, Action.SUCCESS);
	}

	@Test
	public void execute_ajaxSaveFunctions_emptyKeyError() throws Exception {
		setProperty("localeTo", Locale.FRENCH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "Save");

		when(request.getRequestURL()).thenReturn(new StringBuffer("ajax"));
		when(translation.getId()).thenReturn(1);
		when(translation.getValue()).thenReturn("UnitTest");

		String resultCode = manageTranslations.execute();

		JSONObject out =
				(JSONObject) new JSONParser().parse((String)
						getInternalState(manageTranslations, "output"));

		assertEquals(resultCode, PicsActionSupport.BLANK);
		assertTrue(out.get("success").toString().equals("false"));
		assertTrue(out.get("reason").toString().equals("Missing Translation Key"));
	}

	@Test
	public void execute_ajaxSaveFunctions_emptySourceLanguage() throws Exception {
		setProperty("localeTo", Locale.FRENCH);
		setProperty("localeFrom", Locale.ENGLISH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "Save");

		when(request.getRequestURL()).thenReturn(new StringBuffer("ajax"));
		when(translation.getId()).thenReturn(1);
		when(translation.getKey()).thenReturn("TestKey");
		when(translation.getValue()).thenReturn("Test Value");
		when(translation.getSourceLanguage()).thenReturn("");
		when(translation.getLocale()).thenReturn("french");

		String resultCode = manageTranslations.execute();
		JSONObject out =
				(JSONObject) new JSONParser().parse((String)
						getInternalState(manageTranslations, "output"));

		assertEquals(resultCode, PicsActionSupport.BLANK);
		verify(translation).setAuditColumns(any(Permissions.class));
		verify(translation).setSourceLanguage(anyString());
		verify(dao).save((AppTranslation) any());
		assertTrue(out.get("success").toString().equals("true"));
		assertTrue(out.get("id").toString().equals("1"));
	}

	@Test
	public void execute_ajaxSaveFunctions_emptyQualityRating() throws Exception {
		setProperty("localeTo", Locale.FRENCH);
		setProperty("localeFrom", Locale.ENGLISH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "Save");

		when(request.getRequestURL()).thenReturn(new StringBuffer("ajax"));
		when(translation.getId()).thenReturn(1);
		when(translation.getKey()).thenReturn("TestKey");
		when(translation.getValue()).thenReturn("Test Value");
		when(translation.getSourceLanguage()).thenReturn("french");
		when(translation.getQualityRating()).thenReturn(null);

		String resultCode = manageTranslations.execute();
		JSONObject out =
				(JSONObject) new JSONParser().parse((String)
						getInternalState(manageTranslations, "output"));

		assertEquals(resultCode, PicsActionSupport.BLANK);
		verify(translation).setAuditColumns(any(Permissions.class));
		verify(translation, never()).setSourceLanguage(anyString());
		verify(translation).setQualityRating(TranslationQualityRating.Good);
		assertTrue(out.get("success").toString().equals("true"));
		assertTrue(out.get("id").toString().equals("1"));
	}

	@Test
	public void execute_nonAjaxSaveFunction_translationHasDot() throws Exception {
		setProperty("localeTo", Locale.FRENCH);
		setProperty("localeFrom", Locale.ENGLISH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "Save");

		when(request.getRequestURL()).thenReturn(new StringBuffer("zilch"));
		when(translation.getId()).thenReturn(1);
		when(translation.getKey()).thenReturn("Test.Key");
		when(translation.getValue()).thenReturn("Test Value");
		when(translation.getSourceLanguage()).thenReturn("french");
		when(translation.getQualityRating()).thenReturn(TranslationQualityRating.Good);

		String resultCode = manageTranslations.execute();

		assertEquals(resultCode, Action.SUCCESS);
		assertEquals("Test", getInternalState(manageTranslations, "key"));
	}

	@Test
	public void execute_nonAjaxSaveFunction_translationWithoutDot() throws Exception {
		setProperty("localeTo", Locale.FRENCH);
		setProperty("localeFrom", Locale.ENGLISH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "Save");

		when(request.getRequestURL()).thenReturn(new StringBuffer("zilch"));
		when(translation.getId()).thenReturn(1);
		when(translation.getKey()).thenReturn("TestKey");
		when(translation.getValue()).thenReturn("Test Value");
		when(translation.getSourceLanguage()).thenReturn("french");
		when(translation.getQualityRating()).thenReturn(TranslationQualityRating.Good);

		String resultCode = manageTranslations.execute();

		assertEquals(resultCode, Action.SUCCESS);
		assertEquals("TestKey", getInternalState(manageTranslations, "key"));
	}

	@Test
	public void testUpdate_nullWillCreateAndSaveNewTranslation() {
		when(translation.getKey()).thenReturn(null);
		when(request.getParameter("key2")).thenReturn("Pending");
		when(request.getParameter("locale")).thenReturn("Pending");

		manageTranslations.update();

		ArgumentCaptor<AppTranslation> captor = ArgumentCaptor.forClass(AppTranslation.class);
		verify(dao).save(captor.capture());
		assertFalse(captor.getValue().equals(translation));
	}
	  
	@Test
	public void testUpdate_NotNull() {
		when(translation.getKey()).thenReturn("TestKey");
		when(request.getParameter("key2")).thenReturn("Pending");
		when(request.getParameter("locale")).thenReturn("Pending");

		manageTranslations.update();

		verify(dao).save(translation);
	}

	// TODO: write tests to test validation

	private void setProperty (String field, Object value) {
		setInternalState(manageTranslations, field, value);
	}

}
