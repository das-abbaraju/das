package com.picsauditing.dao;

import com.picsauditing.jpa.entities.*;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


import static org.junit.Assert.assertEquals;

public class IdpUserDAOTest {
    @Mock
    private EntityManager entityManager;
    @Mock
    private Query fakeQuery;

    private IdpUserDAO idpUserDAO = new IdpUserDAO();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(entityManager.createQuery(anyString())).thenReturn(fakeQuery);
        idpUserDAO.setEntityManager(entityManager);
    }

    @Test
    public void testFindBy() {
        IdpUser idpUser = new IdpUser();
        when(fakeQuery.getResultList()).thenReturn(Arrays.asList(new IdpUser[]{idpUser}));

        assertTrue(idpUserDAO.findBy("test", "idp")!=null);
    }

    @Test
    public void testFind() {
        IdpUser idpUser = new IdpUser();
        when(entityManager.find(IdpUser.class, 1)).thenReturn(idpUser);

        assertTrue(idpUserDAO.find(1) != null);
    }
}
