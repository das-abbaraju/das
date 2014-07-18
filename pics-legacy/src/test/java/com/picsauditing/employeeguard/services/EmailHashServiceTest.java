package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.authentication.dao.EmailHashDAO;
import com.picsauditing.employeeguard.daos.softdeleted.SoftDeletedEmployeeDAO;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.builders.EmailHashBuilder;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.entities.builders.SoftDeletedEmployeeBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.picsauditing.employeeguard.EGTestDataUtil.*;

public class EmailHashServiceTest {

	public static final String INVALID_HASH = "MY_HASH";
	public static final String VALID_HASH = "VALID_HASH";
	public static final int EMPLOYEE_ID = 45;
	public static final String EMPLOYEE_EMAIL = "test@test.com";

	// Class under test
	private EmailHashService emailHashService;

	@Mock
	private EmailHashDAO emailHashDAO;
	@Mock
	private SoftDeletedEmployeeDAO softDeletedEmployeeDAO;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		emailHashService = new EmailHashService();

		Whitebox.setInternalState(emailHashService, "emailHashDAO", emailHashDAO);
		Whitebox.setInternalState(emailHashService, "softDeletedEmployeeDAO", softDeletedEmployeeDAO);
	}

	@Test
	public void testIsUserRegistered() {
		Boolean result = emailHashService.isUserRegistered(EXISTING_PROFILE_EMAIL_HASH);

		assertTrue(result);
	}

	@Test
	public void testIsUserRegistered_NotRegistered() {
		Boolean result = emailHashService.isUserRegistered(VALID_EMAIL_HASH);

		assertFalse(result);
	}

	@Test
	public void testHashIsValid() {
		Boolean result = emailHashService.isValid(VALID_EMAIL_HASH);

		assertTrue(result);
	}

	@Test
	public void testHashIsNotValid_EmptyHash() {
		Boolean result = emailHashService.invalidHash(null);

		assertTrue(result);
	}

	@Test
	public void testHashIsNotValid_ExpiredHash() {
		Boolean result = emailHashService.invalidHash(INVALID_EMAIL_HASH);

		assertTrue(result);
	}

	@Test
	public void testCreateNewHash() throws Exception {
		Employee fakeEmployee = setupTestCreateNewHash();
		when(softDeletedEmployeeDAO.find(anyInt())).thenReturn(new SoftDeletedEmployeeBuilder().id(EMPLOYEE_ID)
				.email(EMPLOYEE_EMAIL).build());

		EmailHash result = emailHashService.createNewHash(fakeEmployee);

		verifyTestCreateNewHash(fakeEmployee, result);
	}

	private Employee setupTestCreateNewHash() {
		Employee fakeEmployee = new EmployeeBuilder().id(EMPLOYEE_ID).email(EMPLOYEE_EMAIL).build();

		when(emailHashDAO.save(any(EmailHash.class))).thenAnswer(new Answer<EmailHash>() {

			@Override
			public EmailHash answer(InvocationOnMock invocation) throws Throwable {
				return (EmailHash) invocation.getArguments()[0];
			}
		});

		return fakeEmployee;
	}

	private void verifyTestCreateNewHash(Employee fakeEmployee, EmailHash result) {
		verify(emailHashDAO).save(any(EmailHash.class));

		assertEquals(fakeEmployee.getId(), result.getEmployee().getId());
		assertEquals(fakeEmployee.getEmail(), result.getEmailAddress());
		assertNotNull(result.getExpirationDate());
	}
}
