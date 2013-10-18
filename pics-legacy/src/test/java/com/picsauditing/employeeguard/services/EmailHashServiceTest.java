package com.picsauditing.employeeguard.services;

import com.picsauditing.authentication.dao.EmailHashDAO;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Employee;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailHashServiceTest {

    public static final String INVALID_HASH = "MY_HASH";
    public static final String VALID_HASH = "VALID_HASH";
    private EmailHashService emailHashService;

    @Mock
    private EmailHashDAO emailHashDAO;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        emailHashService = new EmailHashService();

        Whitebox.setInternalState(emailHashService, "emailHashDAO", emailHashDAO);
    }

    @Test
    public void testHashIsValid() {
        when(emailHashDAO.hashExists(VALID_HASH)).thenReturn(true);
        when(emailHashDAO.hashIsExpired(VALID_HASH)).thenReturn(false);

        Boolean result = emailHashService.hashIsValid(VALID_HASH);

        assertTrue(result);
    }

    @Test
    public void testHashIsNotValid_EmptyHash() {
        Boolean result = emailHashService.hashIsValid(null);

        assertFalse(result);
    }

    @Test
    public void testHashIsNotValid_ExistingHash() {
        when(emailHashDAO.hashExists(INVALID_HASH)).thenReturn(false);

        Boolean result = emailHashService.hashIsValid(INVALID_HASH);

        assertFalse(result);
    }

    @Test
    public void testHashIsNotValid_ExpiredHash() {
        when(emailHashDAO.hashIsExpired(INVALID_HASH)).thenReturn(true);

        Boolean result = emailHashService.hashIsValid(INVALID_HASH);

        assertFalse(result);
    }

    @Test
    public void testCreateNewHash() throws Exception {
        Employee employee = buildEmployee();

        EmailHash result = emailHashService.createNewHash(employee);

        verifyCreateNewHash(employee, result);
    }

    private void verifyCreateNewHash(Employee employee, EmailHash result) {
        assertEquals(employee, result.getEmployee());
        assertEquals(employee.getEmail(), result.getEmailAddress());
        assertNotNull(result.getHash());
        assertNotNull(result.getExpirationDate());
        assertNotNull(result.getCreationDate());
        verify(emailHashDAO).save(any(EmailHash.class));
    }

    private Employee buildEmployee() {
        Employee employee = new Employee();
        employee.setEmail("employee_email@test.com");
        return employee;
    }
}
