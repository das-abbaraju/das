package com.picsauditing.actions.users;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;

public class UsersManageTest extends PicsActionTest {
	private UsersManage usersManage;
	@Mock
	private User user;	
	@Mock
	private UserDAO userDAO;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		usersManage = new UsersManage();
		super.setUp(usersManage);

		Whitebox.setInternalState(usersManage, "userDAO", userDAO);		
	}
	@Test
	public void testSetUserResetHash() throws Exception {
		user = new User();
		Whitebox.setInternalState(usersManage, "user", user);		
		Whitebox.invokeMethod(usersManage, "setUserResetHash");
		assertNotNull(user.getResetHash());
		verify(userDAO).save(user);
	}
}
