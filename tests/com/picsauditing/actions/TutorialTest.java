package com.picsauditing.actions;


import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

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
        when(permissions.isUsingVersion7Menus()).thenReturn(true);
        when(permissions.getUsingVersion7MenusDate()).thenReturn(null);
        when(userDAO.find(anyInt())).thenReturn(new User());

        String result = tutorial.navigationMenu();

        verify(permissions, times(1)).setUsingVersion7MenusDate(any(Date.class));
        verify(userDAO, times(1)).save(any(User.class));
        assertEquals("navigation-menu", result);
    }

    @Test
    public void testExecute_NotUpdateFirstTimeUser() throws Exception {
        when(permissions.isUsingVersion7Menus()).thenReturn(true);
        when(permissions.getUsingVersion7MenusDate()).thenReturn(new Date());

        String result = tutorial.navigationMenu();

        verifyZeroInteractions(userDAO);
        verify(permissions, never()).setUsingVersion7MenusDate(any(Date.class));
        assertEquals("navigation-menu", result);
    }

}
