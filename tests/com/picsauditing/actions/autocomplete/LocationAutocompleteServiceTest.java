package com.picsauditing.actions.autocomplete;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.jpa.entities.Autocompleteable;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocationAutocompleteServiceTest {
	private LocationAutocompleteService locationAutocompleteService;

	@Mock
	private Autocompleteable autocompleteable;
	@Mock
	private CountryDAO countryDAO;
	@Mock
	private CountrySubdivisionDAO countrySubdivisionDAO;
	@Mock
	private Permissions permissions;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		locationAutocompleteService = new LocationAutocompleteService();

		Whitebox.setInternalState(locationAutocompleteService, "countryDAO", countryDAO);
		Whitebox.setInternalState(locationAutocompleteService, "countrySubdivisionDAO", countrySubdivisionDAO);
	}

	@Test
	public void testGetItemsForSearch_EmptyOrNullSearchReturnsEmptyCollection() throws Exception {
		Collection<Autocompleteable> results =
				locationAutocompleteService.getItemsForSearch("", permissions);

		assertNotNull(results);
		assertTrue(results.isEmpty());

		results = locationAutocompleteService.getItemsForSearch(null, permissions);
		assertNotNull(results);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testGetItemsForSearch_SearchIsIsocodeLength() throws Exception {
		locationAutocompleteService.getItemsForSearch("US", permissions);

		ArgumentCaptor<String> isocodeLookupCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> translationLookupCaptor = ArgumentCaptor.forClass(String.class);

		verify(countryDAO).findWhere(isocodeLookupCaptor.capture());
		verify(countryDAO).findByTranslatableField(eq(Country.class), translationLookupCaptor.capture());
		verify(countrySubdivisionDAO).findWhere(isocodeLookupCaptor.capture());
		verify(countrySubdivisionDAO).findByTranslatableField(eq(CountrySubdivision.class),
				translationLookupCaptor.capture());

		for (String capturedValue : isocodeLookupCaptor.getAllValues()) {
			assertTrue(capturedValue.toLowerCase().equals("isocode like '%us'"));
		}

		for (String capturedValue : translationLookupCaptor.getAllValues()) {
			assertEquals("%US%", capturedValue);
		}
	}

	@Test
	public void testGetItemsForSearch_SearchIsNotIsocodeLength() throws Exception {
		locationAutocompleteService.getItemsForSearch("United States", permissions);

		ArgumentCaptor<String> translationLookupCaptor = ArgumentCaptor.forClass(String.class);

		verify(countryDAO, never()).findWhere(anyString());
		verify(countryDAO).findByTranslatableField(eq(Country.class), translationLookupCaptor.capture());
		verify(countrySubdivisionDAO, never()).findWhere(anyString());
		verify(countrySubdivisionDAO).findByTranslatableField(eq(CountrySubdivision.class),
				translationLookupCaptor.capture());

		for (String capturedValue : translationLookupCaptor.getAllValues()) {
			assertEquals("%United States%", capturedValue);
		}
	}

	@Test
	public void testGetKey() throws Exception {
		when(autocompleteable.getAutocompleteItem()).thenReturn("Value");

		assertEquals("Value", locationAutocompleteService.getKey(autocompleteable));
	}

	@Test
	public void testGetValue() throws Exception {
		when(autocompleteable.getAutocompleteValue()).thenReturn("Value");

		assertEquals("Value", locationAutocompleteService.getValue(autocompleteable, permissions));
	}
}
