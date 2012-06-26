package com.picsauditing.actions.i18n;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PicsTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.converters.JsonObjectConverter;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.search.SelectSQL;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ServletActionContext.class, ManageTranslations.class, ActionContext.class})
@PowerMockIgnore({"javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*"})
public class ManageTranslationsTest extends PicsTest {
	
	ManageTranslations classUnderTest;
	Map<String, Object> session;
	
	@Mock Permissions permissions;
	@Mock AppTranslation translation;
	
	@Before
	public void TestSetup () throws Exception {
		classUnderTest = new ManageTranslations();
		
		PowerMockito.mockStatic(ServletActionContext.class);
		MockitoAnnotations.initMocks(this);
		setUp();
		classUnderTest = PowerMockito.spy(new ManageTranslations());
		autowireEMInjectedDAOs(classUnderTest);
		
		session = new HashMap<String, Object>();
		
		ActionContext actionContext = mock(ActionContext.class);
		when(actionContext.getSession()).thenReturn(session);
		
		PowerMockito.mockStatic(ActionContext.class);
		when(ActionContext.getContext()).thenReturn(actionContext);		
		
		classUnderTest.setTranslation(translation);
		setProperty("permissions", permissions);
		when(permissions.getLocale()).thenReturn(Locale.FRANCE);
		when(permissions.getUserId()).thenReturn(1);
		when(permissions.getAdminID()).thenReturn(1);
	}
	
	@Test
	public void execute_emptyLocaleTo () throws Exception {
		
		decoupleDatabaseCallingMethods();
		setProperty("localeTo", null);
		setProperty("button", null);
		setProperty("data", new ArrayList<BasicDynaBean> ());
		setProperty("download", false);
		
		String resultCode = classUnderTest.execute();
		
		assertEquals(resultCode, Action.SUCCESS);
		verify(permissions).getLocale();
		verify(classUnderTest).run(any(SelectSQL.class));
	}
	
	@Test
	public void execute_saveWithNullTranslation () throws Exception {
		PowerMockito.doNothing().when(classUnderTest).run(any(SelectSQL.class));
		setProperty("localeTo", null);
		setProperty("button", "save");
		setProperty("translation", null);
		setProperty("data", new ArrayList<BasicDynaBean> ());
		setProperty("download", false);
		
		String resultCode = classUnderTest.execute();
		
		assertEquals(resultCode, Action.SUCCESS);
		verify(classUnderTest).run(any(SelectSQL.class));
	}

	@Test
	public void execute_tracingOn () throws Exception {
		PowerMockito.doNothing().when(classUnderTest).run(any(SelectSQL.class));
		setProperty("localeTo", Locale.FRENCH);
		setProperty("data", new ArrayList<BasicDynaBean> ());
		setProperty("download", false);
		setProperty("button", "tracing On");
		
		String resultCode = classUnderTest.execute();
		
		assertEquals(resultCode, Action.SUCCESS);
		assertEquals(session.get("i18nTracing"), true);
		
	}
	
	@Test
	public void execute_tracingOff () throws Exception {
		PowerMockito.doNothing().when(classUnderTest).run(any(SelectSQL.class));
		setProperty("localeTo", Locale.FRENCH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "tracing Off");
		
		String resultCode = classUnderTest.execute();
		
		assertEquals(resultCode, Action.SUCCESS);
		assertEquals(session.get("i18nTracing"), false);
	}
	
	@Test
	@SuppressWarnings("unchecked")	
	public void execute_tracingClear () throws Exception {
		PowerMockito.doNothing().when(classUnderTest).run(any(SelectSQL.class));
		setProperty("localeTo", Locale.FRENCH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "tracing Clear");
		
		String resultCode = classUnderTest.execute();
		
		assertEquals(resultCode, Action.SUCCESS);
		assertTrue(((Set<String>)session.get("usedI18nKeys")).isEmpty());
	}
	
	@Test
	public void execute_tracingAjax () throws Exception {
		PowerMockito.doNothing().when(classUnderTest).run(any(SelectSQL.class));
		setProperty("localeTo", Locale.FRENCH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "tracing Ajax");
		
		assertEquals(classUnderTest.execute(), PicsActionSupport.BLANK);
	}
	
	@Test
	public void execute_saveFunctions_delete () throws Exception {
		PowerMockito.doNothing().when(classUnderTest).run(any(SelectSQL.class));
		decoupleEntityManager();
		setProperty("localeTo", Locale.FRENCH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "Save");
		
		PowerMockito.doReturn("nothing").when(classUnderTest, "getRequestURL");
		when(translation.getId()).thenReturn(1);
		when(translation.getValue()).thenReturn("");
		when(translation.getKey()).thenReturn("UnitTest");
		
		String resultCode = classUnderTest.execute();
		
		assertEquals(resultCode, Action.SUCCESS);
		verify(em).createQuery(anyString());
	}
	
	@Test
	public void execute_ajaxSaveFunctions_emptyKeyError () throws Exception {
		PowerMockito.doNothing().when(classUnderTest).run(any(SelectSQL.class));
		decoupleEntityManager();
		setProperty("localeTo", Locale.FRENCH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "Save");
		
		PowerMockito.doReturn("ajax").when(classUnderTest, "getRequestURL");
		when(translation.getId()).thenReturn(1);
		when(translation.getValue()).thenReturn("UnitTest");
		
		String resultCode = classUnderTest.execute();
		JSONObject out = (JSONObject) new JSONParser().parse((String) getInternalState(classUnderTest, "output"));
		
		assertEquals(resultCode, PicsActionSupport.BLANK);
		assertTrue(out.get("success").toString().equals("false"));
		assertTrue(out.get("reason").toString().equals("Missing Translation Key"));
	}
	
	@Test
	public void execute_ajaxSaveFunctions_emptySourceLanguage () throws Exception {
		decoupleDatabaseCallingMethods();
		decoupleEntityManager();
		setProperty("localeTo", Locale.FRENCH);
		setProperty("localeFrom", Locale.ENGLISH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "Save");
		
		PowerMockito.doReturn("ajax").when(classUnderTest, "getRequestURL");
		when(translation.getId()).thenReturn(1);
		when(translation.getKey()).thenReturn("TestKey");
		when(translation.getValue()).thenReturn("Test Value");
		when(translation.getSourceLanguage()).thenReturn("");
		when(translation.getLocale()).thenReturn("french");
		
		String resultCode = classUnderTest.execute();
		JSONObject out = (JSONObject) new JSONParser().parse((String) getInternalState(classUnderTest, "output"));

		assertEquals(resultCode, PicsActionSupport.BLANK);
		verify(translation).setAuditColumns(any(Permissions.class));
		verify(translation).setSourceLanguage(anyString());
		verify(em).merge(any());
		assertTrue(out.get("success").toString().equals("true"));
		assertTrue(out.get("id").toString().equals("1"));
	}
	
	@Test
	public void execute_ajaxSaveFunctions_emptyQualityRating () throws Exception {
		decoupleDatabaseCallingMethods();
		decoupleEntityManager();
		setProperty("localeTo", Locale.FRENCH);
		setProperty("localeFrom", Locale.ENGLISH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "Save");
		
		PowerMockito.doReturn("ajax").when(classUnderTest, "getRequestURL");
		when(translation.getId()).thenReturn(1);
		when(translation.getKey()).thenReturn("TestKey");
		when(translation.getValue()).thenReturn("Test Value");
		when(translation.getSourceLanguage()).thenReturn("french");
		when(translation.getQualityRating()).thenReturn(null);
		
		String resultCode = classUnderTest.execute();
		JSONObject out = (JSONObject) new JSONParser().parse((String) getInternalState(classUnderTest, "output"));
		
		assertEquals(resultCode, PicsActionSupport.BLANK);
		verify(translation).setAuditColumns(any(Permissions.class));
		verify(translation, never()).setSourceLanguage(anyString());
		verify(translation).setQualityRating(TranslationQualityRating.Good);
		assertTrue(out.get("success").toString().equals("true"));
		assertTrue(out.get("id").toString().equals("1"));
	}
	
	@Test
	public void execute_nonAjaxSaveFunction_translationHasDot () throws Exception {
		PowerMockito.doNothing().when(classUnderTest).run(any(SelectSQL.class));
		decoupleEntityManager();
		setProperty("localeTo", Locale.FRENCH);
		setProperty("localeFrom", Locale.ENGLISH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "Save");
		
		PowerMockito.doReturn("zilch").when(classUnderTest, "getRequestURL");
		when(translation.getId()).thenReturn(1);
		when(translation.getKey()).thenReturn("Test.Key");
		when(translation.getValue()).thenReturn("Test Value");
		when(translation.getSourceLanguage()).thenReturn("french");
		when(translation.getQualityRating()).thenReturn(TranslationQualityRating.Good);
		
		String resultCode = classUnderTest.execute();
		
		assertEquals(resultCode, Action.SUCCESS);
		assertEquals("Test", getInternalState(classUnderTest, "key"));
	}
	
	@Test
	public void execute_nonAjaxSaveFunction_translationWithoutDot () throws Exception {
		PowerMockito.doNothing().when(classUnderTest).run(any(SelectSQL.class));
		decoupleEntityManager();
		setProperty("localeTo", Locale.FRENCH);
		setProperty("localeFrom", Locale.ENGLISH);
		setProperty("data", new ArrayList<BasicDynaBean>());
		setProperty("download", false);
		setProperty("button", "Save");
		
		PowerMockito.doReturn("zilch").when(classUnderTest, "getRequestURL");
		when(translation.getId()).thenReturn(1);
		when(translation.getKey()).thenReturn("TestKey");
		when(translation.getValue()).thenReturn("Test Value");
		when(translation.getSourceLanguage()).thenReturn("french");
		when(translation.getQualityRating()).thenReturn(TranslationQualityRating.Good);
		
		String resultCode = classUnderTest.execute();
		
		assertEquals(resultCode, Action.SUCCESS);
		assertEquals("TestKey", getInternalState(classUnderTest, "key"));
	}
	
	@Ignore
	@Test
	public void validate_nullKey() {
		when(translation.getKey()).thenReturn(null);
		classUnderTest.validate();
		assertTrue(classUnderTest.hasActionErrors());
	}
	
	@Ignore
	@Test
	public void validate_emptyKey() {
		when(translation.getKey()).thenReturn("");
		classUnderTest.validate();
		assertTrue(classUnderTest.hasActionErrors());		
	}
	
	@Ignore
	@Test
	public void validate_keyWithSpace() {
		when(translation.getKey()).thenReturn("has spaces");
		classUnderTest.validate();
		assertTrue(classUnderTest.hasActionErrors());		
	}

	@Ignore
	@Test
	public void validate_keyStartsWithSpace() {
		when(translation.getKey()).thenReturn(" ThisHasASpace");
		classUnderTest.validate();
		assertTrue(classUnderTest.hasActionErrors());		
	}	
	
	@Ignore
	@Test
	public void validate_keyStartingWithDot () {
		classUnderTest.setLocaleTo(Locale.FRENCH);
		when(translation.getKey()).thenReturn(".key.this");
		classUnderTest.validate();
		assertTrue(classUnderTest.hasActionErrors());		
	}
	
	@Ignore
	@Test
	public void validate_keyEndingWithDot () {
		classUnderTest.setLocaleTo(Locale.FRENCH);
		when(translation.getKey()).thenReturn("key.this.");		
		classUnderTest.validate();
		assertTrue(classUnderTest.hasActionErrors());		
	}
	
	@Ignore
	@Test
	public void validate_emptyLocaleTo () {
		classUnderTest.validate();
		assertTrue(classUnderTest.getLocaleTo().equals(Locale.FRENCH));
	}
	
	@Ignore
	@Test
	public void validate_correctKey () {
		when(translation.getKey()).thenReturn("correct.value");
		classUnderTest.validate();
		assertFalse(classUnderTest.hasActionErrors());
	}
	
	private void setProperty (String field, Object value) {
		setInternalState(classUnderTest, field, value);
	}
	
	
	private void decoupleDatabaseCallingMethods () throws Exception {
		PowerMockito.doNothing().when(classUnderTest, "updateOtherLanguagesToQuestionable");
		PowerMockito.doNothing().when(classUnderTest).run(any(SelectSQL.class));
	}
	
	private void decoupleEntityManager () throws Exception {
		autowireEMInjectedDAOs(classUnderTest);
		PowerMockito.doNothing().when(em).persist(any());
		PowerMockito.doReturn(new AppTranslation()).when(em).merge(any());
		PowerMockito.doReturn(null).when(em).createQuery(Mockito.anyString());
	}

}
