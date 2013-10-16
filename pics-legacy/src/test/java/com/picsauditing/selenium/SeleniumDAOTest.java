
package com.picsauditing.selenium;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.DynaBean;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;

public class SeleniumDAOTest {
	
	private SeleniumDeletable Doc, Dopey, Bashful, Sneezy, Sleepy, Grumpy, Happy;
	private List<SeleniumDeletable> theDwarves;
	
	@Mock AccountDeleter accountDeleter;
	@Mock UserDeleter userDeleter;
	@Mock EmployeeDeleter employeeDeleter;
    @Mock AuditCategoryRuleDeleter auditCategoryRuleDeleter;
    @Mock AuditTypeRuleDeleter auditTypeRuleDeleter;
	
	@Spy SeleniumDAO classUnderTest;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp () {
		Doc = mock(SeleniumWrapper.class);
		when(Doc.getID()).thenReturn(1);
		when(Doc.isAnAccount()).thenReturn(true);
        when(Doc.isClientSite()).thenReturn(true);
        when(Doc.isContractor()).thenReturn(false);
		when(Doc.isAnEmployee()).thenReturn(false);
		when(Doc.isUser()).thenReturn(false);
		
		Dopey = mock(SeleniumWrapper.class);
		when(Dopey.getID()).thenReturn(2);
		when(Dopey.isAnAccount()).thenReturn(false);
        when(Dopey.isClientSite()).thenReturn(false);
        when(Dopey.isContractor()).thenReturn(false);
		when(Dopey.isAnEmployee()).thenReturn(true);
		when(Dopey.isUser()).thenReturn(false);
		
		Bashful = mock(SeleniumWrapper.class);
		when(Bashful.getID()).thenReturn(3);
		when(Bashful.isAnAccount()).thenReturn(false);
        when(Bashful.isClientSite()).thenReturn(false);
        when(Bashful.isContractor()).thenReturn(false);
		when(Bashful.isAnEmployee()).thenReturn(false);
		when(Bashful.isUser()).thenReturn(true);
		
		Sleepy = mock(SeleniumWrapper.class);
		when(Sleepy.getID()).thenReturn(1);
		when(Sleepy.isAnAccount()).thenReturn(false);
        when(Sleepy.isClientSite()).thenReturn(false);
        when(Sleepy.isContractor()).thenReturn(false);
        when(Sleepy.isAnEmployee()).thenReturn(true);
		when(Sleepy.isUser()).thenReturn(false);
		
		Sneezy = mock(SeleniumWrapper.class);
		when(Sneezy.getID()).thenReturn(2);
		when(Sneezy.isAnAccount()).thenReturn(false);
        when(Sneezy.isClientSite()).thenReturn(false);
        when(Sneezy.isContractor()).thenReturn(false);
        when(Sneezy.isAnEmployee()).thenReturn(false);
		when(Sneezy.isUser()).thenReturn(true);
		
		Grumpy = mock(SeleniumWrapper.class);
		when(Grumpy.getID()).thenReturn(3);
		when(Grumpy.isAnAccount()).thenReturn(true);
        when(Grumpy.isClientSite()).thenReturn(false);
        when(Grumpy.isContractor()).thenReturn(true);
        when(Grumpy.isAnEmployee()).thenReturn(false);
		when(Grumpy.isUser()).thenReturn(false);
		
		Happy = mock(SeleniumWrapper.class);
		when(Happy.getID()).thenReturn(4);
		when(Happy.isAnAccount()).thenReturn(true);
        when(Happy.isClientSite()).thenReturn(false);
        when(Happy.isContractor()).thenReturn(true);
        when(Happy.isAnEmployee()).thenReturn(true);
		when(Happy.isUser()).thenReturn(true);
		
		theDwarves = new ArrayList<>();
		theDwarves.add(Dopey);
		theDwarves.add(Sleepy);
		theDwarves.add(Sneezy);
		theDwarves.add(Bashful);
		theDwarves.add(Doc);
		theDwarves.add(Grumpy);
		theDwarves.add(Happy);
		
		MockitoAnnotations.initMocks(this);
		setInternalState(classUnderTest, "employeeDeleter", employeeDeleter);
		setInternalState(classUnderTest, "userDeleter", userDeleter);
		setInternalState(classUnderTest, "accountDeleter", accountDeleter);
        setInternalState(classUnderTest, "auditCategoryRuleDeleter", auditCategoryRuleDeleter);
        setInternalState(classUnderTest, "auditTypeRuleDeleter", auditTypeRuleDeleter);
		when(accountDeleter.setIds((List<Integer>) any())).thenReturn(accountDeleter);
		when(userDeleter.setIds((List<Integer>) any())).thenReturn(userDeleter);
		when(employeeDeleter.setIds((List<Integer>) any())).thenReturn(employeeDeleter);
        when(auditCategoryRuleDeleter.setIds((List<Integer>) any())).thenReturn(auditCategoryRuleDeleter);
        when(auditTypeRuleDeleter.setIds((List<Integer>) any())).thenReturn(auditTypeRuleDeleter);

	}
	
	@Test
	public void getAccountIDNumbersFromTheDwarves () {
		List<Integer> results = SeleniumDAO.getAccountIDNumbersFrom(theDwarves);
		verify(Doc).getID();
		verify(Grumpy).getID();
		verify(Happy).getID();
		assertTrue(results.contains(Doc.getID()));
		assertTrue(results.contains(Grumpy.getID()));
		assertTrue(results.contains(Happy.getID()));
		verify(Sleepy, never()).getID();
		verify(Bashful, never()).getID();
		verify(Dopey, never()).getID();
		verify(Sneezy, never()).getID();
	}
	
	@Test
	public void getUserIDNumbersFromTheDwarves () {
		List<Integer> results = SeleniumDAO.getUserIDNumbersFrom(theDwarves);
		verify(Bashful).getID();
		verify(Sneezy).getID();
		verify(Happy).getID();
		assertTrue(results.contains(Bashful.getID()));
		assertTrue(results.contains(Sneezy.getID()));
		assertTrue(results.contains(Happy.getID()));
		verify(Doc, never()).getID();
		verify(Dopey, never()).getID();
		verify(Grumpy, never()).getID();
		verify(Sleepy, never()).getID();
	}
	
	@Test
	public void getEmployeeIDNumbersFromTheDwarves () {
		List<Integer> results = SeleniumDAO.getEmployeeIDNumbersFrom(theDwarves);
		verify(Dopey).getID();
		verify(Sleepy).getID();
		verify(Happy).getID();
		assertTrue(results.contains(Dopey.getID()));
		assertTrue(results.contains(Sleepy.getID()));
		assertTrue(results.contains(Happy.getID()));
		verify(Doc, never()).getID();
		verify(Grumpy, never()).getID();
		verify(Sneezy, never()).getID();
		verify(Bashful, never()).getID();
	}
	
	@Test
	public void delete_emptyList () throws Exception {
		classUnderTest.delete(new ArrayList<SeleniumDeletable>());
		verify(accountDeleter, never()).execute();
		verify(employeeDeleter, never()).execute();
		verify(userDeleter, never()).execute();
	}
	
	@Test
	public void delete_testList() throws Exception {
		classUnderTest.delete(theDwarves);
		verify(accountDeleter, atMost(2)).execute();
//		verify(accountDeleter, atMost(1)).execute();
		verify(employeeDeleter).execute();
		verify(employeeDeleter, atMost(1)).execute();
		verify(userDeleter).execute();
		verify(userDeleter, atMost(1)).execute();
	}
	
	@Ignore
	@Test
	public void availableDBEntries() throws Exception {
		DynaBean one = mock(DynaBean.class);
		when(one.get("id")).thenReturn(1);
		when(one.get("name")).thenReturn("One");
		when(one.get("firstname")).thenReturn("One");
		when(one.get("lastname")).thenReturn("");
		when(one.get("type")).thenReturn("Corporate");
		
		DynaBean two = mock(DynaBean.class);
		when(two.get("id")).thenReturn(2);
		when(two.get("name")).thenReturn("Two");
		when(two.get("firstname")).thenReturn("Two");
		when(two.get("lastname")).thenReturn("");
		when(two.get("type")).thenReturn("Contractor");
		
		List<DynaBean> theTestList = new ArrayList<>();
		theTestList.add(one);
		theTestList.add(two);
		
		//PowerMockito.when(classUnderTest, "basicInformationFetch").thenReturn(theTestList);
		PowerMockito.doReturn(theTestList).when(classUnderTest, "basicInformationFetch");
		//System.out.println(classUnderTest.availableTestingReferences());
	}
	
}
