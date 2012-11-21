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