package com.picsauditing.actions;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.jpa.entities.Country;

public class AccountActionSupportTest extends PicsTranslationTest {

	private AccountActionSupport accountActionSupport;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();
		accountActionSupport = new AccountActionSupport();
	}

	@Test
	public void testGetCountrySubdivisionLabelKeyFor_HasTranslationKey() throws Exception {
		String usSubdivisionLabel = "Country.US.SubdivisionLabel";
		when(translationService.hasKey(eq(usSubdivisionLabel), any(Locale.class))).thenReturn(true);

		assertEquals(usSubdivisionLabel, accountActionSupport.getCountrySubdivisionLabelKeyFor("US"));
	}

	@Test
	public void testGetCountrySubdivisionLabelKeyFor_DoesNotHaveTranslationKey() throws Exception {
		when(translationService.hasKey(anyString(), any(Locale.class))).thenReturn(false);

		assertEquals(Country.DEFAULT_COUNTRY_SUBDIVISION_LABEL,
				accountActionSupport.getCountrySubdivisionLabelKeyFor("US"));
	}

	@Test
	public void testGetCountrySubdivisionLabelKeyFor_NullOrEmptyCountry() throws Exception {
		assertEquals(Country.DEFAULT_COUNTRY_SUBDIVISION_LABEL,
				accountActionSupport.getCountrySubdivisionLabelKeyFor(""));
		assertEquals(Country.DEFAULT_COUNTRY_SUBDIVISION_LABEL,
				accountActionSupport.getCountrySubdivisionLabelKeyFor(null));
	}
}
