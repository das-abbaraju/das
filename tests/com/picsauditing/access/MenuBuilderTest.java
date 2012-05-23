package com.picsauditing.access;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.User;

public class MenuBuilderTest {

	User user;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		user = EntityFactory.makeUser();
	}

	@Ignore("Need to find a solution for I18NCache")
	@Test
	public void testUserNotLoggedIn() throws Exception {
		MenuComponent menu = new MenuComponent();
		// This hits the database, change it
		MenuBuilder.buildNotLoggedInMenubar(menu);
		assertTrue(menu.getChildren().size() == 1);
	}

	@Ignore("Need to find a solution for I18NCache")
	@Test
	public void testBuildNotLoggedInMenu() {
		// Create a user that's not logged in
		user.getPermissions().clear();
		// Build a not logged in menu
		MenuComponent menu = new MenuComponent();
		MenuBuilder.buildNotLoggedInMenubar(menu);

		//dumpMenuData(menu);
	}

	@Test public void testBuildAssessmentMenu() {
	}

	@Test public void testBuildContractorMenu() {
		// Create a user that's a contractor
		// Build a contractor menu
	}

	@Test public void testBuildGenericMenu() {
	}

	@Test
	public void testBuildAllMenuTypes() {
		// Create a user that's an assessment
		// Build an assessment menu

		// Create a user with all permissions
		// Build a generic menu
	}
}
