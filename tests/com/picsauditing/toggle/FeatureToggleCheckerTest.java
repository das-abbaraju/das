package com.picsauditing.toggle;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;

public class FeatureToggleCheckerTest {
	private FeatureToggleChecker featureToggleChecker;
	@Mock
	private AppPropertyDAO appPropertyDAO;
	@Mock
	private Logger logger;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		featureToggleChecker = new FeatureToggleChecker();
		Whitebox.setInternalState(featureToggleChecker, "appPropertyDAO", appPropertyDAO);
		Whitebox.setInternalState(featureToggleChecker, "logger", logger);
	}

	@Test
	public void testIsFeatureEnabled_AppPropertyNotFoundLogsError() {
		when(appPropertyDAO.find(anyString())).thenReturn(null);

		featureToggleChecker.isFeatureEnabled("test");

		verify(logger).warn(anyString(), anyString());
	}

	@Test
	public void testIsFeatureEnabled_AppPropertyNotFoundReturnsFalse() {
		when(appPropertyDAO.find(anyString())).thenReturn(null);

		boolean featureEnabled = featureToggleChecker.isFeatureEnabled("test");

		assertFalse(featureEnabled);
	}

	@Test
	public void testIsFeatureEnabled_EnabledAppPropertyReturnsTrue() {
		when(appPropertyDAO.find(anyString())).thenReturn(createEnabledAppProperty());

		boolean featureEnabled = featureToggleChecker.isFeatureEnabled("test");

		verify(logger, never()).error(anyString(), any(Exception.class));
		assertTrue(featureEnabled);
	}

	@Test
	public void testIsFeatureEnabled_DisabledAppPropertyReturnsFalse() {
		when(appPropertyDAO.find(anyString())).thenReturn(createDisabledAppProperty());

		boolean featureEnabled = featureToggleChecker.isFeatureEnabled("test");

		verify(logger, never()).error(anyString(), any(Exception.class));
		assertFalse(featureEnabled);
	}

	public AppProperty createEnabledAppProperty() {
		AppProperty appProperty = new AppProperty();
		appProperty.setProperty("test");
		appProperty.setValue("true");

		return appProperty;
	}

	public AppProperty createDisabledAppProperty() {
		AppProperty appProperty = new AppProperty();
		appProperty.setProperty("test");
		appProperty.setValue("false");

		return appProperty;
	}
}
