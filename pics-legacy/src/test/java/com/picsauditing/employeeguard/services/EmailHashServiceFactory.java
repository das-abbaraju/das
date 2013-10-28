package com.picsauditing.employeeguard.services;

import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;
import com.picsauditing.jpa.entities.Account;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class EmailHashServiceFactory {
	public static final String VALID_HASH = "valid hash";
	public static final String FIRST_NAME = "First";
	public static final String LAST_NAME = "Last";
	public static final String EMAIL = "tester@picsauditing.com";

	private static EmailHashService emailHashService = Mockito.mock(EmailHashService.class);

	public static EmailHashService getEmailHashService() throws Exception {
		Mockito.reset(emailHashService);

		EmailHash emailHash = new EmailHash();
		SoftDeletedEmployee employee = new SoftDeletedEmployee();
		employee.setAccountId(Account.PicsID);
		employee.setId(Identifiable.SYSTEM);
		employee.setFirstName(FIRST_NAME);
		employee.setLastName(LAST_NAME);
		employee.setEmail(EMAIL);

		emailHash.setEmployee(employee);
		emailHash.setEmailAddress(employee.getEmail());

		when(emailHashService.createNewHash(any(Employee.class))).thenReturn(emailHash);
		when(emailHashService.hashIsValid(VALID_HASH)).thenReturn(true);
		when(emailHashService.findByHash(anyString())).thenReturn(emailHash);

		return emailHashService;
	}
}
