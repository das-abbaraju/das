package com.picsauditing.actions;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Locale;

import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PicsTranslationTest;
import org.powermock.reflect.Whitebox;

public class AccountActionSupportTest extends PicsTranslationTest {

    @Mock
    protected NoteDAO noteDao;

	private AccountActionSupport accountActionSupport;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		accountActionSupport = new AccountActionSupport();
        Whitebox.setInternalState(accountActionSupport, "noteDao", noteDao);
    }

    @Test
    public void testAddNote_EmailBody() {
        Note note = accountActionSupport.addNote(new Account(), "Summary", "Body", NoteCategory.Registration, LowMedHigh.High, true, 1);
        assertEquals("Summary", note.getSummary());
        assertEquals("Body", note.getBody());
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
