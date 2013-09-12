package com.picsauditing;

import static org.mockito.Mockito.when;

import java.util.Locale;

import javax.persistence.EntityManager;

import org.mockito.Mock;

import com.picsauditing.util.system.PicsEnvironment;

/**
 * Superclass for Unit Test classes that require common dependency breaking in
 * PicsOrganizer. You are not by any means required to use this class as a
 * superclass for tests.
 * 
 * @author Galen Meurer
 * 
 */
public abstract class PicsTest extends PicsTranslationTest {

	@Mock
	protected EntityManager em;
	@Mock
	protected PicsEnvironment picsEnvironment;

	private PicsTestUtil testUtil = new PicsTestUtil();

	public void setUp() throws Exception {
		super.resetTranslationService();
	}

	protected void autowireEMInjectedDAOs(Object objectToAutowire) throws InstantiationException,
			IllegalAccessException {
		testUtil.autowireEMInjectedDAOs(objectToAutowire, em);
	}

	protected void autowireDAOsFromDeclaredMocks(Object objectToAutowire, Object toTakeMockDaosFrom)
			throws InstantiationException, IllegalAccessException {
		PicsTestUtil.autowireDAOsFromDeclaredMocks(objectToAutowire, toTakeMockDaosFrom);
	}

	@SuppressWarnings("deprecation")
	protected void mockTranslationServiceForEnglishMonthNames() {
		when(translationService.hasKey("Month.Jan", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(translationService.getText("Month.Jan", Locale.ENGLISH, (Object[]) null)).thenReturn("January");
		when(translationService.hasKey("Month.Feb", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(translationService.getText("Month.Feb", Locale.ENGLISH, (Object[]) null)).thenReturn("February");
		when(translationService.hasKey("Month.Mar", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(translationService.getText("Month.Mar", Locale.ENGLISH, (Object[]) null)).thenReturn("March");
		when(translationService.hasKey("Month.Apr", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(translationService.getText("Month.Apr", Locale.ENGLISH, (Object[]) null)).thenReturn("April");
		when(translationService.hasKey("Month.May", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(translationService.getText("Month.May", Locale.ENGLISH, (Object[]) null)).thenReturn("May");
		when(translationService.hasKey("Month.Jun", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(translationService.getText("Month.Jun", Locale.ENGLISH, (Object[]) null)).thenReturn("June");
		when(translationService.hasKey("Month.Jul", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(translationService.getText("Month.Jul", Locale.ENGLISH, (Object[]) null)).thenReturn("July");
		when(translationService.hasKey("Month.Aug", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(translationService.getText("Month.Aug", Locale.ENGLISH, (Object[]) null)).thenReturn("August");
		when(translationService.hasKey("Month.Sep", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(translationService.getText("Month.Sep", Locale.ENGLISH, (Object[]) null)).thenReturn("September");
		when(translationService.hasKey("Month.Oct", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(translationService.getText("Month.Oct", Locale.ENGLISH, (Object[]) null)).thenReturn("October");
		when(translationService.hasKey("Month.Nov", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(translationService.getText("Month.Nov", Locale.ENGLISH, (Object[]) null)).thenReturn("November");
		when(translationService.hasKey("Month.Dec", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(translationService.getText("Month.Dec", Locale.ENGLISH, (Object[]) null)).thenReturn("December");
	}
}
