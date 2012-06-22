package com.picsauditing;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Locale;

import javax.persistence.EntityManager;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.picsauditing.PICS.I18nCache;

/**
 * Superclass for Unit Test classes that require common dependency breaking in PicsOrganizer. You are not by any
 * means required to use this class as a superclass for tests. Only use it if you need it as it uses PowerMockRunner
 * which will slow your tests down.
 * 
 * @author Galen Meurer
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(I18nCache.class)
@PowerMockIgnore({"javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*"})
public abstract class PicsTest {
	
	@Mock protected EntityManager em;
	@Mock protected I18nCache i18nCache;

	private PicsTestUtil testUtil = new PicsTestUtil();
	
	public void setUp() throws Exception {
		// I am pretty sure we want to do this in the subclass and not here. Not sure
		// of the effects of calling it twice. -GAM
		// MockitoAnnotations.initMocks(this);

		mockStatic(I18nCache.class);
		Mockito.when(I18nCache.getInstance()).thenReturn(i18nCache);
	}

	protected void autowireEMInjectedDAOs(Object objectToAutowire) 
			throws InstantiationException,IllegalAccessException {
		testUtil.autowireEMInjectedDAOs(objectToAutowire, em);
	}

	protected void autowireDAOsFromDeclaredMocks(Object objectToAutowire, Object toTakeMockDaosFrom) 
			throws InstantiationException,IllegalAccessException {
		PicsTestUtil.autowireDAOsFromDeclaredMocks(objectToAutowire, toTakeMockDaosFrom);
	}

	protected void mockI18nCacheForEnglishMonthNames() {
		when(i18nCache.hasKey("Month.Jan", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(i18nCache.getText("Month.Jan", Locale.ENGLISH, (Object[])null)).thenReturn("January");
		when(i18nCache.hasKey("Month.Feb", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(i18nCache.getText("Month.Feb", Locale.ENGLISH, (Object[])null)).thenReturn("February");
		when(i18nCache.hasKey("Month.Mar", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(i18nCache.getText("Month.Mar", Locale.ENGLISH, (Object[])null)).thenReturn("March");
		when(i18nCache.hasKey("Month.Apr", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(i18nCache.getText("Month.Apr", Locale.ENGLISH, (Object[])null)).thenReturn("April");
		when(i18nCache.hasKey("Month.May", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(i18nCache.getText("Month.May", Locale.ENGLISH, (Object[])null)).thenReturn("May");
		when(i18nCache.hasKey("Month.Jun", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(i18nCache.getText("Month.Jun", Locale.ENGLISH, (Object[])null)).thenReturn("June");
		when(i18nCache.hasKey("Month.Jul", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(i18nCache.getText("Month.Jul", Locale.ENGLISH, (Object[])null)).thenReturn("July");
		when(i18nCache.hasKey("Month.Aug", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(i18nCache.getText("Month.Aug", Locale.ENGLISH, (Object[])null)).thenReturn("August");
		when(i18nCache.hasKey("Month.Sep", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(i18nCache.getText("Month.Sep", Locale.ENGLISH, (Object[])null)).thenReturn("September");
		when(i18nCache.hasKey("Month.Oct", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(i18nCache.getText("Month.Oct", Locale.ENGLISH, (Object[])null)).thenReturn("October");
		when(i18nCache.hasKey("Month.Nov", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(i18nCache.getText("Month.Nov", Locale.ENGLISH, (Object[])null)).thenReturn("November");
		when(i18nCache.hasKey("Month.Dec", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(i18nCache.getText("Month.Dec", Locale.ENGLISH, (Object[])null)).thenReturn("December");
	}
}
