package com.picsauditing.access;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MenuBuilderTest {
	
	@Test
	public void testUserNotLoggedIn() throws Exception {
		MenuComponent menu = new MenuComponent();
		// This hits the database, change it
		MenuBuilder.buildNotLoggedInMenubar(menu);
		assertTrue(menu.getChildren().size() == 1);
	}

	@Test
	public void generateAllMenuTypes() {
	}
}
