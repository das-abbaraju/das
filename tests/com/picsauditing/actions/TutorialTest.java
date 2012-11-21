
package com.picsauditing.actions;


import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;

public class TutorialTest extends PicsActionTest {

	Tutorial tutorial;
	
	@Mock
	private Permissions permissions;
	@Mock
	private UserDAO userDAO;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		tutorial = new Tutorial();
		super.setUp(tutorial);

		tutorial.permissions = permissions;
		
		PicsTestUtil.autowireDAOsFromDeclaredMocks(tutorial, this);
	}
	
	@Test
	public void testExecute_UpdateFirstTimeUser() throws Exception {
		when(permissions.isUsingDynamicReports()).thenReturn(true);
		when(permissions.getUsingDynamicReportsDate()).thenReturn(null);
		when(userDAO.find(anyInt())).thenReturn(new User());
		
		String result = tutorial.execute();
		
		verify(permissions, times(1)).setUsingDynamicReportsDate(any(Date.class));
		verify(userDAO, times(1)).save(any(User.class));
		assertEquals("success", result);
	}
	
	@Test
	public void testExecute_NotUpdateFirstTimeUser() throws Exception {
		when(permissions.isUsingDynamicReports()).thenReturn(true);
		when(permissions.getUsingDynamicReportsDate()).thenReturn(new Date());
		
		String result = tutorial.execute();
		
		verifyZeroInteractions(userDAO);
		verify(permissions, never()).setUsingDynamicReportsDate(any(Date.class));
		assertEquals("success", result);
	}

}
