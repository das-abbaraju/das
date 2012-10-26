package com.picsauditing.toggle;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import groovy.lang.Script;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.OperatorAccount;

public class FeatureToggleCheckerGroovyTest {
	private FeatureToggleCheckerGroovy featureToggleCheckerGroovy;
	private AppProperty appPropertyFeature;
	private String toggleName = "Toggle.Test";
	private String cacheName;

	@Mock
	private CacheManager cacheManager;
	@Mock
	private Cache cache;
	@Mock
	private Script script;
	@Mock
	private FeatureToggleProvider featureToggleProvider;
	@Mock
	private AppPropertyDAO appPropertyDAO;
	@Mock
	private Permissions permissions;
	@Mock
	private Logger logger;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		featureToggleCheckerGroovy = new FeatureToggleCheckerGroovy(permissions);

		cacheName = featureToggleCheckerGroovy.getCacheName();

		featureToggleCheckerGroovy.setPermissions(permissions);

		Whitebox.setInternalState(featureToggleCheckerGroovy, "featureToggleProvider", featureToggleProvider);
		Whitebox.setInternalState(featureToggleCheckerGroovy, "appPropertyDAO", appPropertyDAO);
		Whitebox.setInternalState(featureToggleCheckerGroovy, "logger", logger);

		when(appPropertyDAO.find(toggleName)).thenReturn(appPropertyFeature);

		CacheManager cacheManager = CacheManager.getInstance();
		Cache cache = cacheManager.getCache(cacheName);
		cache.removeAll();
	}

	@Ignore("too slow to run every time")
	@Test
	public void testScriptWithDynamicVariable_True() throws Exception {
		boolean result = dynamicContractorScript(1, 22107);
		assertTrue(result);
		result = dynamicContractorScript(22107, 222);
		assertTrue(result);
		result = dynamicContractorScript(1, 222);
		assertTrue(result);
	}

	@Ignore("too slow to run every time")
	@Test
	public void testScriptWithDynamicVariable_False() throws Exception {
		boolean result = dynamicContractorScript(1, 2);
		assertFalse(result);
	}

	private boolean dynamicContractorScript(int opId1, int opId2) {
		String scriptBody =
				"return contractor.operatorAccounts.find {it.id in [22107, 222] } != null";

		ContractorAccount contractor = mock(ContractorAccount.class);
		List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
		// OperatorAccount op1 = mock(OperatorAccount.class);
		// when(op1.getId()).thenReturn(opId1);
		OperatorAccount op1 = new OperatorAccount();
		op1.setId(opId1);
		operators.add(op1);
		// OperatorAccount op2 = mock(OperatorAccount.class);
		// when(op2.getId()).thenReturn(opId2);
		OperatorAccount op2 = new OperatorAccount();
		op2.setId(opId2);
		operators.add(op2);

		when(contractor.getOperatorAccounts()).thenReturn(operators);
		when(featureToggleProvider.findFeatureToggle(toggleName)).thenReturn(scriptBody);

		featureToggleCheckerGroovy.addToggleVariable("contractor", contractor);
		return featureToggleCheckerGroovy.isFeatureEnabled(toggleName);
	}

	@Test
	public void testIsFeatureEnabled_Happy() throws Exception {
		when(featureToggleProvider.findFeatureToggle(toggleName)).thenReturn("true");
		
		assertTrue(featureToggleCheckerGroovy.isFeatureEnabled(toggleName));
	}

	@Test
	public void testIsFeatureEnabled_NullScriptResultsInFalseToggle() throws Exception {
		when(featureToggleProvider.findFeatureToggle(toggleName)).thenReturn(null);

		assertFalse(featureToggleCheckerGroovy.isFeatureEnabled(toggleName));
		verify(logger).error(anyString(), eq(toggleName));
	}

	@Test
	public void testIsFeatureEnabled_UnparsableScriptResultsInFalseToggle() throws Exception {
		String unparseableScript = "$soy!..==sl";
		when(featureToggleProvider.findFeatureToggle(toggleName)).thenReturn(unparseableScript);

		assertFalse(featureToggleCheckerGroovy.isFeatureEnabled(toggleName));
		verify(logger).error(anyString(), eq(toggleName), anyString());
	}

	@Test
	public void testRunScript_NullPermissionsResultsInFalseToggle() throws Exception {
		featureToggleCheckerGroovy.setPermissions(null);

		Script script = Whitebox.invokeMethod(featureToggleCheckerGroovy, "createScript",
				"permissions.username.equals('foo')");
		Boolean result = Whitebox.invokeMethod(featureToggleCheckerGroovy, "runScript", toggleName, script);
		assertFalse(result);
	}

	@Test
	public void testRunScript_NonBooleanScriptResultsInFalseToggle() throws Exception {
		featureToggleCheckerGroovy.setPermissions(null);

		Script script = Whitebox.invokeMethod(featureToggleCheckerGroovy, "createScript", "2");
		Boolean result = Whitebox.invokeMethod(featureToggleCheckerGroovy, "runScript", toggleName, script);
		assertFalse(result);
	}

	@Test
	public void testGroovyScript_UserLocale() throws Exception {
		when(permissions.getLocale()).thenReturn(new Locale("en", "CA"));

		String scriptBody = "permissions.locale.toString() == 'en_CA'";
		trueScript(scriptBody);

		scriptBody = "permissions.locale.toString() in ['en_CA', 'en_US']";
		trueScript(scriptBody);
	}

	@Test
	public void testGroovyScript_SimpleTrueBoolean() throws Exception {
		String scriptBody = "true";
		trueScript(scriptBody);
	}

	@Test
	public void testGroovyScript_SimpleFalseBoolean() throws Exception {
		String scriptBody = "false";
		falseScript(scriptBody);
	}

	@Test
	public void testGroovyScript_VersionOfPicsOrg() throws Exception {
		String scriptBody = "versionOf('PICSORG') > 4.2";
		trueScript(scriptBody);
	}

	@Test
	public void testGroovyScript_VersionOfBackProcs() throws Exception {
		when(appPropertyDAO.getProperty("VERSION.BPROC")).thenReturn("1.0");
		String scriptBody = "versionOf('BPROC') > 0.9";
		trueScript(scriptBody);
	}

	@Test
	public void testGroovyScript_releaseToUserAudienceLevel() throws Exception {
		when(permissions.hasGroup(User.GROUP_DEVELOPER)).thenReturn(true);
		trueScript("releaseToUserAudienceLevel(2)");
		trueScript("releaseToUserAudienceLevel(com.picsauditing.access.BetaPool.Stakeholder)");

		when(permissions.hasGroup(User.GROUP_DEVELOPER)).thenReturn(false);
		when(permissions.hasGroup(User.GROUP_STAKEHOLDER)).thenReturn(true);
		falseScript("releaseToUserAudienceLevel(1)");
		falseScript("import com.picsauditing.access.BetaPool\n releaseToUserAudienceLevel(BetaPool.Developer)");
	}

	@Test
	public void testGroovyScript_UserIsMemberOfAny() throws Exception {
		when(permissions.getGroupNames()).thenReturn(new ArrayList<String>() {
			{
				add("test1");
				add("test2");
			}
		});
		when(permissions.getGroupIds()).thenReturn(new HashSet<Integer>() {
			{
				add(1);
				add(2);
			}
		});
		trueScript("userIsMemberOfAny(['test2'])");
		trueScript("userIsMemberOfAny(['test2', 'notMember'])");
	}

	@Test
	public void testScriptFromCache_NullCacheReturnsNullScript() throws Exception {
		Whitebox.setInternalState(featureToggleCheckerGroovy, "cacheManager", cacheManager);
		when(cacheManager.getCache(cacheName)).thenReturn(null);

		assertThat(Whitebox.invokeMethod(featureToggleCheckerGroovy, "scriptFromCache", toggleName), is(equalTo(null)));
	}

	@Test(expected = FeatureToggleException.class)
	public void testCacheScript_throwsWhenCacheNotFound() throws Exception {
		Whitebox.setInternalState(featureToggleCheckerGroovy, "cacheManager", cacheManager);
		when(cacheManager.getCache(cacheName)).thenReturn(null);

		Whitebox.invokeMethod(featureToggleCheckerGroovy, "cacheScript", toggleName, script);
	}

	@Test
	public void testScriptFromCache_NonScriptElementReturnsNull() throws Exception {
		CacheManager cacheManager = CacheManager.getInstance();
		Cache cache = cacheManager.getCache(cacheName);
		cache.put(new Element(toggleName, new Integer(1)));

		assertThat(Whitebox.invokeMethod(featureToggleCheckerGroovy, "scriptFromCache", toggleName), is(equalTo(null)));
	}

	// Cache.put is final - I don't want to use PowerMock so I'm using a real
	// Cache
	@Test
	public void testCacheScript_HappyPath() throws Exception {
		Whitebox.invokeMethod(featureToggleCheckerGroovy, "cacheScript", toggleName, script);
		
		CacheManager cacheManager = CacheManager.getInstance();
		Cache cache = cacheManager.getCache(cacheName);
		Element element = cache.get(toggleName);
		assertThat((Script) element.getObjectValue(), is(equalTo(script)));
	}

	private void trueScript(String scriptBody) throws Exception {
		Script script = Whitebox.invokeMethod(featureToggleCheckerGroovy, "createScript", scriptBody);
		Object result = script.run();
		assertTrue((Boolean) result);
	}

	private void falseScript(String scriptBody) throws Exception {
		Script script = Whitebox.invokeMethod(featureToggleCheckerGroovy, "createScript", scriptBody);
		Object result = script.run();
		assertFalse((Boolean) result);
	}

}
