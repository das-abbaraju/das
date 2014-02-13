package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountEmployeeGuard;
import com.picsauditing.jpa.entities.Account;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class AccountEmployeeGuardDAOTest {

    private AccountEmployeeGuardDAO accountEmployeeGuardDAO;

    @Mock
    private EntityManager em;
    @Mock
    private TypedQuery typedQuery;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        accountEmployeeGuardDAO = new AccountEmployeeGuardDAO();

        Whitebox.setInternalState(accountEmployeeGuardDAO, "em", em);
    }

    @Test
    public void testFind_NoResultThrowsException() throws Exception {
        when(em.createQuery(anyString(), eq(AccountEmployeeGuard.class))).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenThrow(new NoResultException());

        AccountEmployeeGuard result = accountEmployeeGuardDAO.find(Account.PicsID);

        assertNull(result);
    }

    @Test
    public void testFind_WithResult() throws Exception {
        AccountEmployeeGuard expectedResult = new AccountEmployeeGuard();
        when(em.createQuery(anyString(), eq(AccountEmployeeGuard.class))).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(expectedResult);

        AccountEmployeeGuard result = accountEmployeeGuardDAO.find(Account.PicsID);

        assertEquals(expectedResult, result);
    }
}
