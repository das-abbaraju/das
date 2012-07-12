package com.picsauditing.actions;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.ActionSupport;

public class PicsStyleGuideTest {
	private PicsStyleGuide picsStyleGuide;

	@Before
	public void setUp() {
		picsStyleGuide = new PicsStyleGuide();
	}

	@Test
	public void testExecute() throws Exception {
		assertEquals(ActionSupport.SUCCESS, picsStyleGuide.execute());
	}

	@Test
	public void testButtons() throws Exception {
		assertEquals("buttons", picsStyleGuide.buttons());
	}

	@Test
	public void testForms() throws Exception {
		assertEquals("forms", picsStyleGuide.forms());
	}

	@Test
	public void testPills() throws Exception {
		assertEquals("pills", picsStyleGuide.pills());
	}
}
