package com.picsauditing.actions;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.search.Database;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class AccountActionSupportTest {
	private AccountActionSupport accountActionSupport;

	@Mock
	private Database databaseForTesting;
	@Mock
	private I18nCache i18nCache;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);

		accountActionSupport = new AccountActionSupport();

		Whitebox.setInternalState(accountActionSupport, "i18nCache", i18nCache);
	}

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Test
	public void testGetCountrySubdivisionLabelKeyFor_HasTranslationKey() throws Exception {
		String usSubdivisionLabel = "Country.US.SubdivisionLabel";
		when(i18nCache.hasKey(eq(usSubdivisionLabel), any(Locale.class))).thenReturn(true);

		assertEquals(usSubdivisionLabel, accountActionSupport.getCountrySubdivisionLabelKeyFor("US"));
	}

	@Test
	public void testGetCountrySubdivisionLabelKeyFor_DoesNotHaveTranslationKey() throws Exception {
		when(i18nCache.hasKey(anyString(), any(Locale.class))).thenReturn(false);

		assertEquals(Country.DEFAULT_COUNTRY_SUBDIVISION_LABEL, accountActionSupport.getCountrySubdivisionLabelKeyFor("US"));
	}

	@Test
	public void testGetCountrySubdivisionLabelKeyFor_NullOrEmptyCountry() throws Exception {
		assertEquals(Country.DEFAULT_COUNTRY_SUBDIVISION_LABEL, accountActionSupport.getCountrySubdivisionLabelKeyFor(""));
		assertEquals(Country.DEFAULT_COUNTRY_SUBDIVISION_LABEL,
				accountActionSupport.getCountrySubdivisionLabelKeyFor(null));
	}
}
