package com.picsauditing.actions.autocomplete;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Country;

public class CountryAutocompleteServiceTest {

	private CountryAutocompleteService service;
	
	@Mock
	protected CountryDAO countryDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new CountryAutocompleteService();
		PicsTestUtil.autowireDAOsFromDeclaredMocks(service, this);
	}

	@Test
	public void testGetItems_EmptyQuery() throws Exception {
		Collection<Country> results = service.getItems(null);
		
		assertThat(results, notNullValue());
		assertTrue(results.isEmpty());
	}

	@Test
	public void testGetItems_HasParsedIsCodesButQueryFindsNone() throws Exception {
		List<Country> emptyResult = new ArrayList<Country>();
		when(countryDAO.findWhere(anyString())).thenReturn(emptyResult);
		
		Collection<Country> results = service.getItems("non-empty query");
		
		assertThat(results, notNullValue());
		assertTrue(results.isEmpty());
	}

	@Test
	public void testQueryContainsIsoCodes_NullOrEmptyQuery() throws Exception {
		Boolean result = Whitebox.invokeMethod(service, "queryContainsIsoCodes", "");
		assertFalse(result);

		result = Whitebox.invokeMethod(service, "queryContainsIsoCodes", (String) null);
		assertFalse(result);
	}

	@Test
	public void testQueryContainsIsoCodes_NotIsoCodeQuery_ThreeCharacterQuery() throws Exception {
		Boolean result = Whitebox.invokeMethod(service, "queryContainsIsoCodes", "123");
		assertFalse(result);
	}

	@Test
	public void testQueryContainsIsoCodes_NotIsoCodeQuery_OneCharacterQuery() throws Exception {
		Boolean result = Whitebox.invokeMethod(service, "queryContainsIsoCodes", "1");
		assertFalse(result);
	}

	@Test
	public void testQueryContainsIsoCodes_NotIsoCodeQuery_IncorrectNumberOfCharactersInCommaSeparatedString() throws Exception {
		Boolean result = Whitebox.invokeMethod(service, "queryContainsIsoCodes", "12,34,567");
		assertFalse(result);

		result = Whitebox.invokeMethod(service, "queryContainsIsoCodes", "1,1");
		assertFalse(result);
	}

	@Test
	public void testQueryContainsIsoCodes_NotIsoCodeQuery_CorrectNumberOfCharacters() throws Exception {
		Boolean result = Whitebox.invokeMethod(service, "queryContainsIsoCodes", "12");
		assertTrue(result);
	}

	@Test
	public void testQueryContainsIsoCodes_NotIsoCodeQuery_CorrectNumberOfCharactersInCommaSeparatedString() throws Exception {
		Boolean result = Whitebox.invokeMethod(service, "queryContainsIsoCodes", "12,34");
		assertTrue(result);

		result = Whitebox.invokeMethod(service, "queryContainsIsoCodes", "12,34,56");
		assertTrue(result);
	}
	
}
