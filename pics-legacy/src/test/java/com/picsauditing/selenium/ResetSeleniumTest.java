package com.picsauditing.selenium;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.Action;
import com.picsauditing.PicsActionTest;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.selenium.SeleniumDeletable;

import static com.picsauditing.actions.PicsActionSupport.*;

public class ResetSeleniumTest extends PicsActionTest {
	
	private ResetSelenium classUnderTest;
	private List<SeleniumDeletable> testList;

	@Mock
	private SeleniumDAO seleniumDAO;
	
	@SuppressWarnings({ "serial", "unchecked" })
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ResetSelenium();
		super.setUp(classUnderTest);
		
		testList = new ArrayList<SeleniumDeletable>();
		
		testList.add(new SeleniumWrapper(new Account() {{
			setId(00000);
			setName("Huey");
			setType("Duck");
		}}));
		
		testList.add(new SeleniumWrapper(new Account(){{
			setId(11111);
			setName("Dewey");
			setType("Duck");
		}}));
		
		testList.add(new SeleniumWrapper(new Account(){{
			setId(22222);
			setName("Lewey");
			setType("Duck");
		}}));
		Whitebox.setInternalState(classUnderTest, "seleniumDao", seleniumDAO);
		when(seleniumDAO.availableTestingReferences()).thenReturn(testList);
	}
	
//	@Test
//	public void execute() {
//		String result = classUnderTest.execute();
//		assertEquals(Action.SUCCESS, result);
//		assertFalse(classUnderTest.getDBAccounts().isEmpty());
//	}
//
//	@Test
//	public void execute_preInitializedDBAccounts () {
//		setInternalState(classUnderTest, "accountsInDB", new ArrayList<Account>());
//		String result = classUnderTest.execute();
//		assertEquals(Action.SUCCESS, result);
//		assertFalse(classUnderTest.getDBAccounts().isEmpty());
//	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void delete_singleWrongInput() throws Exception {
		classUnderTest.setDeleteAccount("foo");

		String result = classUnderTest.delete();
		
		assertEquals(REDIRECT, result);
		verify(seleniumDAO, never()).delete((List<SeleniumDeletable>) any());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void delete_singleCorrectInput() throws Exception {
		classUnderTest.setDeleteAccount("Huey");
		String result = classUnderTest.delete();
		
		assertEquals(REDIRECT, result);
		
		ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
		verify(seleniumDAO).delete(argument.capture());
		List accounts = (List) argument.getValue();
		boolean found = false;
		for (Object account : accounts) {
			if ("Huey".equals(((SeleniumDeletable) account).getName())) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void delete_multiplesWrongInput () throws Exception {
		@SuppressWarnings("serial")
		List<Integer> testIDList = new ArrayList<Integer>() {{ 
			add(99999);
			add(88888);
			add(77777);
		}};
		classUnderTest.setDBAccounts(testIDList);
		
		String result = classUnderTest.delete();
		
		assertEquals(REDIRECT, result);
		
		verify(seleniumDAO, never()).delete((List<SeleniumDeletable>) any());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void delete_multiplesCorrectInput() throws Exception {
		@SuppressWarnings("serial")
		List<Integer> testIDList = new ArrayList<Integer>() {{ 
			add(00000);
			add(11111);
		}};
		classUnderTest.setDBAccounts(testIDList);
		
		String result = classUnderTest.delete();
		
		assertEquals(REDIRECT, result);
		
		List<Integer> accountIDs = populateDeletedAccounts();
		assertThat(accountIDs, hasItem(0));
		assertThat(accountIDs, hasItem(11111));
	}

	private List<Integer> populateDeletedAccounts() throws Exception {
		ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
		verify(seleniumDAO).delete(argument.capture());
		List<Integer> accountIDs = new ArrayList<Integer>();
		for (Object account : (List) argument.getValue()) {
			accountIDs.add(((SeleniumDeletable) account).getID());
		}
		return accountIDs;
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void delete_multiplesMixedInput() throws Exception {
		@SuppressWarnings("serial")
		List<Integer> testIDList = new ArrayList<Integer>() {{ 
			add(00000);
			add(99999);
			add(22222);
		}};
		classUnderTest.setDBAccounts(testIDList);
		
		String result = classUnderTest.delete();
		
		assertEquals(REDIRECT, result);
		List<Integer> accountIDs = populateDeletedAccounts();
		assertThat(accountIDs, hasItem(0));
		assertThat(accountIDs, hasItem(22222));
		assertThat(accountIDs, not(hasItem(99999)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void delete_nullInput () throws Exception {
		String result = classUnderTest.delete();
		
		assertEquals(REDIRECT, result);
		verify(seleniumDAO, never()).delete((List<SeleniumDeletable>) any());
	}	
	
	@SuppressWarnings("unchecked")
	@Test
	public void deleteAll() throws Exception {
		String result = classUnderTest.deleteAll();
		
		assertEquals(REDIRECT, result);
		verify(seleniumDAO).availableTestingReferences();
		verify(seleniumDAO).delete((List<SeleniumDeletable>) any());
	}

}
