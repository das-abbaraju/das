package com.picsauditing.jpa.entities;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

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
	OperatorAccount operator;
	
	@Mock 
	OperatorAccount parentOperator;

	@Before
	public void setUp() throws Exception {
		operator = new OperatorAccount();
		
		MockitoAnnotations.initMocks(this);
	}

	@Test(timeout = 3000)
	public void testGetInheritedDiscountPercentOperator() {
		int id = 1;
		operator.setId(id);
		operator.setParent(parentOperator);
		when(parentOperator.getId()).thenReturn(id);
		when(parentOperator.isHasDiscount()).thenReturn(false);
		when(parentOperator.getParent()).thenReturn(parentOperator);
		
		operator.getInheritedDiscountPercentOperator();
		assertTrue(true);
	}

}
