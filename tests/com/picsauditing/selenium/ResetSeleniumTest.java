package com.picsauditing.selenium;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.opensymphony.xwork2.Action;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.Account;

import static com.picsauditing.actions.PicsActionSupport.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ResetSelenium.class, I18nCache.class})
@PowerMockIgnore({"javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*"})
public class ResetSeleniumTest {
	
	ResetSelenium classUnderTest;
	List<SeleniumDeletable> testList;
	SeleniumDAO SeleniumDAO;
	
	@SuppressWarnings({ "serial", "unchecked" })
	@Before
	public void setup() throws Exception {
		PowerMockito.mockStatic(I18nCache.class);
		MockitoAnnotations.initMocks(this);
		
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
		//Be careful here: If inject this list, modify it, and then call a function
		//that returns the list again, your changes will persist!
		
		//PowerMockito.mockStatic(SeleniumDAO.class);
		//PowerMockito.doNothing().when(SeleniumDAO.class, "delete", any());
		//PowerMockito.doReturn(testList).when(SeleniumDAO.class, "AvailableTestingAccounts");
		classUnderTest = PowerMockito.spy(new ResetSelenium());
		SeleniumDAO = mock(SeleniumDAO.class);
		setInternalState(classUnderTest, "SD", SeleniumDAO);
		doReturn("blank").when(classUnderTest).setUrlForRedirect(anyString());
		doNothing().when(SeleniumDAO).delete((List<SeleniumDeletable>) any());
		when(SeleniumDAO.availableTestingReferences()).thenReturn(testList);
	}
	
	@Test
	public void execute() {
		String result = classUnderTest.execute();
		assertEquals(Action.SUCCESS, result);
		assertFalse(classUnderTest.getDBAccounts().isEmpty());
	}
	
	@Test
	public void execute_preInitializedDBAccounts () {
		setInternalState(classUnderTest, "accountsInDB", new ArrayList<Account>());
		String result = classUnderTest.execute();
		assertEquals(Action.SUCCESS, result);
		assertFalse(classUnderTest.getDBAccounts().isEmpty());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void delete_singleWrongInput() throws Exception {
		setInternalState(classUnderTest, "userSpecifiedAccount", "foo");
		String result = classUnderTest.delete();
		
		assertEquals(BLANK, result);
		PowerMockito.verifyPrivate(classUnderTest).invoke("deleteSingleAccount", anyString());
		PowerMockito.verifyPrivate(classUnderTest, never()).invoke("performMultipleDeletion");
		verify(SeleniumDAO, never()).delete((List<SeleniumDeletable>) any());
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void delete_singleCorrectInput() throws Exception {
		classUnderTest.setDeleteAccount("Huey");
		String result = classUnderTest.delete();
		
		assertEquals(BLANK, result);
		PowerMockito.verifyPrivate(classUnderTest).invoke("deleteSingleAccount", anyString());
		PowerMockito.verifyPrivate(classUnderTest, never()).invoke("performMultipleDeletion");
		
		verify(SeleniumDAO).delete((List<SeleniumDeletable>) any());
		//PowerMockito.verifyStatic();
		//SeleniumDAO.delete((List<SeleniumDeletable>)any());
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
		
		assertEquals(BLANK, result);
		PowerMockito.verifyPrivate(classUnderTest).invoke("performMultipleDeletion");
		PowerMockito.verifyPrivate(classUnderTest, never()).invoke("deleteSingleAccount", anyString());
		
		verify(SeleniumDAO, never()).delete((List<SeleniumDeletable>) any());
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
		
		assertEquals(BLANK, result);
		PowerMockito.verifyPrivate(classUnderTest).invoke("performMultipleDeletion");
		PowerMockito.verifyPrivate(classUnderTest, never()).invoke("deleteSingleAccount", anyString());
		
		verify(SeleniumDAO).delete((List<SeleniumDeletable>) any());
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
		
		assertEquals(BLANK, result);
		PowerMockito.verifyPrivate(classUnderTest).invoke("performMultipleDeletion");
		PowerMockito.verifyPrivate(classUnderTest, never()).invoke("deleteSingleAccount", anyString());
		
		verify(SeleniumDAO).delete((List<SeleniumDeletable>) any());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void delete_nullInput () throws Exception {
		String result = classUnderTest.delete();
		
		assertEquals(BLANK, result);
		PowerMockito.verifyPrivate(classUnderTest).invoke("performMultipleDeletion");
		verify(SeleniumDAO, never()).delete((List<SeleniumDeletable>) any());
		PowerMockito.verifyPrivate(classUnderTest, never()).invoke("deleteSingleAccount", anyString());
	}	
	
	@SuppressWarnings("unchecked")
	@Test
	public void deleteAll () throws Exception {
		String result = classUnderTest.deleteAll();
		
		assertEquals(BLANK, result);
		//PowerMockito.verifyPrivate(classUnderTest).invoke("performMultipleDeletion");
		//PowerMockito.verifyPrivate(classUnderTest, never()).invoke("deleteSingleAccount", anyString());
		verify(SeleniumDAO).availableTestingReferences();
		verify(SeleniumDAO).delete((List<SeleniumDeletable>) any());
	}

}
