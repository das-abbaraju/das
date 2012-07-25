package com.picsauditing.actions;

import static junit.framework.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.search.Database;

public class PicsStyleGuideTest {
	private PicsStyleGuide picsStyleGuide;

	@Mock private Database databaseForTesting;
	
	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database)null);
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);
		
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
