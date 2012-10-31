package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OperatorAccountTest {

//	public OperatorAccount getInheritedDiscountPercentOperator() {
//		OperatorAccount parent = getParent();
//		while (parent != null) {
//			if (parent.isHasDiscount())
//				return parent;
//
//			parent = parent.getParent();
//		}
//
//		// check corporate associations
//		for (Facility f : getCorporateFacilities())
//			if (f.getCorporate().isHasDiscount())
//				return f.getCorporate();
//
//		return null;
//	}
	OperatorAccount classUnderTest;
	
	@Mock 
	OperatorAccount parentOperator;

	@Before
	public void setUp() throws Exception {
		classUnderTest = new OperatorAccount();
		
		MockitoAnnotations.initMocks(this);
	}

	@Test(timeout = 3000)
	public void testGetInheritedDiscountPercentOperator() {
		int id = 1;
		classUnderTest.setId(id);
		classUnderTest.setParent(parentOperator);
		when(parentOperator.getId()).thenReturn(id);
		when(parentOperator.isHasDiscount()).thenReturn(false);
		when(parentOperator.getParent()).thenReturn(parentOperator);
		
		classUnderTest.getInheritedDiscountPercentOperator();
		assertTrue(true);
	}
	
	@Test
	public void test_isAcceptsList_isCEDA_CANADA () {
		classUnderTest.setId(OperatorAccount.CEDA_CANADA);
		assertTrue(classUnderTest.isAcceptsList());
	}

	@Test
	public void test_isAcceptsList_descendsFromCEDA_CANADA () {
		classUnderTest.setId(5);
		classUnderTest.setParent(parentOperator);
		when(parentOperator.getId()).thenReturn(OperatorAccount.CEDA_CANADA);
		assertTrue(classUnderTest.isAcceptsList());
	}

	@Test
	public void test_isAcceptsList_descendsFromCEDA_CANADA2 () {
		classUnderTest.setId(5);
		classUnderTest.setParent(parentOperator);
		when(parentOperator.getId()).thenReturn(10);
		when(parentOperator.isDescendantOf(OperatorAccount.CEDA_CANADA)).thenReturn(true);
		assertTrue(classUnderTest.isAcceptsList());
	}

	@Test
	public void test_isAcceptsList_isCEDA_USA () {
		classUnderTest.setId(OperatorAccount.CEDA_USA);
		assertTrue(classUnderTest.isAcceptsList());
	}

	@Test
	public void test_isAcceptsList_descendsFromCEDA_USA () {
		classUnderTest.setId(5);
		classUnderTest.setParent(parentOperator);
		when(parentOperator.getId()).thenReturn(OperatorAccount.CEDA_USA);
		assertTrue(classUnderTest.isAcceptsList());
	}

	@Test
	public void test_isAcceptsList_descendsFromCEDA_USA2 () {
		classUnderTest.setId(5);
		classUnderTest.setParent(parentOperator);
		when(parentOperator.getId()).thenReturn(10);
		when(parentOperator.isDescendantOf(OperatorAccount.CEDA_USA)).thenReturn(true);
		assertTrue(classUnderTest.isAcceptsList());
	}
}
