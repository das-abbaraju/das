package com.picsauditing.dao;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class WidgetUserDAOTest {
    private static final int CURRENT_USER_ID = 5678;
    private WidgetUserDAO widgetUserDAO;

    @Mock
    private Permissions permissions;
    @Mock
    private EntityManager em;
    @Mock
    private Query query;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        widgetUserDAO = new WidgetUserDAO();

        when(em.createQuery(anyString())).thenReturn(query);
        when(permissions.getUserId()).thenReturn(CURRENT_USER_ID);

        Whitebox.setInternalState(widgetUserDAO, "em", em);
    }

    @Test
    public void testFindByUser_PicsEmployeeNotCSR() throws Exception {
        when(permissions.isPicsEmployee()).thenReturn(true);

        widgetUserDAO.findByUser(permissions);

        verify(query).setParameter(2, CURRENT_USER_ID);
        verify(query).setParameter(1, WidgetUserDAO.PICS_EMPLOYEE_WIDGETS_TO_INHERIT);
    }

    @Test
    public void testFindByUser_PicsEmployeeIsCSR() throws Exception {
        when(permissions.isPicsEmployee()).thenReturn(true);
        when(permissions.hasDirectlyRelatedGroup(User.GROUP_CSR)).thenReturn(true);

        widgetUserDAO.findByUser(permissions);

        verify(query).setParameter(2, CURRENT_USER_ID);
        verify(query).setParameter(1, User.GROUP_CSR);
    }

    @Test
    public void testFindByUser_PicsEmployeeIsDeveloper() throws Exception {
        when(permissions.isPicsEmployee()).thenReturn(true);
        when(permissions.hasDirectlyRelatedGroup(User.GROUP_DEVELOPER)).thenReturn(true);

        widgetUserDAO.findByUser(permissions);

        verify(query).setParameter(2, CURRENT_USER_ID);
        verify(query).setParameter(1, WidgetUserDAO.PICS_EMPLOYEE_WIDGETS_TO_INHERIT);
    }

    @Test
    public void testFindByUser_NotPicsEmployeeIsOnlyAuditor() throws Exception {
        when(permissions.isPicsEmployee()).thenReturn(false);
        when(permissions.isOnlyAuditor()).thenReturn(true);

        widgetUserDAO.findByUser(permissions);

        verify(query).setParameter(2, CURRENT_USER_ID);
        verify(query).setParameter(1, WidgetUserDAO.ONLY_AUDITOR_WIDGETS_TO_INHERIT);
    }

    @Test
    public void testFindByUser_NotPicsEmployeeIsOperator() throws Exception {
        when(permissions.isPicsEmployee()).thenReturn(false);
        when(permissions.isOperator()).thenReturn(true);

        widgetUserDAO.findByUser(permissions);

        verify(query).setParameter(2, CURRENT_USER_ID);
        verify(query).setParameter(1, WidgetUserDAO.OPERATOR_WIDGETS_TO_INHERIT);
    }

    @Test
    public void testFindByUser_NotPicsEmployeeIsCorporate() throws Exception {
        when(permissions.isPicsEmployee()).thenReturn(false);
        // corporate will be operator, so these are order dependent in CUT
        when(permissions.isOperator()).thenReturn(true);
        when(permissions.isCorporate()).thenReturn(true);

        widgetUserDAO.findByUser(permissions);

        verify(query).setParameter(2, CURRENT_USER_ID);
        verify(query).setParameter(1, WidgetUserDAO.CORPORATE_WIDGETS_TO_INHERIT);
    }

}
