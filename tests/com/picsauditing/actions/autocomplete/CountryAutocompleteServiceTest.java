package com.picsauditing.actions.autocomplete;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CountryAutocompleteService.class)
public class CountryAutocompleteServiceTest {

	CountryAutocompleteService service;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new CountryAutocompleteService();
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
	public void testQueryContainsIsoCodes_NotIsoCodeQuery_CorrectNumberOfCharactersInCommaSeparatedString() throws Exception {
		Boolean result = Whitebox.invokeMethod(service, "queryContainsIsoCodes", "12");
		assertTrue(result);
		
		result = Whitebox.invokeMethod(service, "queryContainsIsoCodes", "12,34");
		assertTrue(result);
		
		result = Whitebox.invokeMethod(service, "queryContainsIsoCodes", "12,34,56");
		assertTrue(result);
	}
	
}
