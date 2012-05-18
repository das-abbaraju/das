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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.opensymphony.xwork2.Action;
import static com.picsauditing.actions.PicsActionSupport.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ResetSelenium.class, SeleniumDAO.class})
public class ResetSeleniumTest {
	
	ResetSelenium classUnderTest;
	List<SeleniumTestingAccount> testList;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		testList = new ArrayList<SeleniumTestingAccount>();
		testList.add(new SeleniumTestingAccount("Huey", "fffff", "Duck"));
		testList.add(new SeleniumTestingAccount("Dewey", "ggggg", "Duck"));
		testList.add(new SeleniumTestingAccount("Lewey", "hhhhh", "Duck"));
		//Be careful here: If inject this list, modify it, and then call a function
		//that returns the list again, your changes will persist!
		
		PowerMockito.mockStatic(SeleniumDAO.class);
		classUnderTest = PowerMockito.spy(new ResetSelenium());
		doReturn("blank").when(classUnderTest).redirect(anyString());
		PowerMockito.doNothing().when(SeleniumDAO.class, "delete", any());
		PowerMockito.doReturn(testList).when(SeleniumDAO.class, "AvailableTestingAccounts");
	}
	
	@Test
	public void execute() {
		String result = classUnderTest.execute();
		assertEquals(Action.SUCCESS, result);
		assertFalse(classUnderTest.getDBAccounts().isEmpty());
	}
	
	@Test
	public void execute_preInitializedDBAccounts () {
		setInternalState(classUnderTest, "accountsInDB", new ArrayList<SeleniumTestingAccount>());
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
		
		PowerMockito.verifyStatic(never());
		SeleniumDAO.delete((List<SeleniumTestingAccount>)any());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void delete_singleCorrectInput() throws Exception {
		classUnderTest.setDeleteAccount("Huey");
		String result = classUnderTest.delete();
		
		assertEquals(BLANK, result);
		PowerMockito.verifyPrivate(classUnderTest).invoke("deleteSingleAccount", anyString());
		PowerMockito.verifyPrivate(classUnderTest, never()).invoke("performMultipleDeletion");
		
		PowerMockito.verifyStatic();
		SeleniumDAO.delete((List<SeleniumTestingAccount>)any());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void delete_multiplesWrongInput () throws Exception {
		@SuppressWarnings("serial")
		List<String> testIDList = new ArrayList<String>() {{ 
			add("uuuuu");
			add("iiiii");
			add("ooooo");
		}};
		classUnderTest.setDBAccounts(testIDList);
		
		String result = classUnderTest.delete();
		
		assertEquals(BLANK, result);
		PowerMockito.verifyPrivate(classUnderTest).invoke("performMultipleDeletion");
		PowerMockito.verifyPrivate(classUnderTest, never()).invoke("deleteSingleAccount", anyString());
		
		PowerMockito.verifyStatic();
		SeleniumDAO.delete((List<SeleniumTestingAccount>)any());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void delete_multiplesCorrectInput() throws Exception {
		@SuppressWarnings("serial")
		List<String> testIDList = new ArrayList<String>() {{ 
			add("fffff");
			add("hhhhh");
		}};
		classUnderTest.setDBAccounts(testIDList);
		
		String result = classUnderTest.delete();
		
		assertEquals(BLANK, result);
		PowerMockito.verifyPrivate(classUnderTest).invoke("performMultipleDeletion");
		PowerMockito.verifyPrivate(classUnderTest, never()).invoke("deleteSingleAccount", anyString());
		
		PowerMockito.verifyStatic();
		SeleniumDAO.delete((List<SeleniumTestingAccount>)any());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void delete_multiplesMixedInput() throws Exception {
		@SuppressWarnings("serial")
		List<String> testIDList = new ArrayList<String>() {{ 
			add("fffff");
			add("aaaaa");
			add("bbbbb");
		}};
		classUnderTest.setDBAccounts(testIDList);
		
		String result = classUnderTest.delete();
		
		assertEquals(BLANK, result);
		PowerMockito.verifyPrivate(classUnderTest).invoke("performMultipleDeletion");
		PowerMockito.verifyPrivate(classUnderTest, never()).invoke("deleteSingleAccount", anyString());
		
		PowerMockito.verifyStatic();
		SeleniumDAO.delete((List<SeleniumTestingAccount>)any());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void delete_nullInput () throws Exception {
		String result = classUnderTest.delete();
		
		assertEquals(BLANK, result);
		PowerMockito.verifyPrivate(classUnderTest, never()).invoke("performMultipleDeletion");
		PowerMockito.verifyPrivate(classUnderTest, never()).invoke("deleteSingleAccount", anyString());
		
		PowerMockito.verifyStatic(never());
		SeleniumDAO.delete((List<SeleniumTestingAccount>)any());
	}	
	
	@SuppressWarnings("unchecked")
	@Test
	public void deleteAll () throws Exception {
		String result = classUnderTest.deleteAll();
		
		assertEquals(BLANK, result);
		PowerMockito.verifyPrivate(classUnderTest).invoke("performMultipleDeletion");
		PowerMockito.verifyPrivate(classUnderTest, never()).invoke("deleteSingleAccount", anyString());
		
		PowerMockito.verifyStatic();
		SeleniumDAO.delete((List<SeleniumTestingAccount>)any());
	}

}
