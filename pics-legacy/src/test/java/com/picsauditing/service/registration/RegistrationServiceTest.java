package com.picsauditing.service.registration;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.service.account.AccountService;
import com.picsauditing.service.authentication.AuthenticationService;
import com.picsauditing.service.billing.RegistrationBillingBean;
import com.picsauditing.service.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class RegistrationServiceTest extends PicsTranslationTest {

	private RegistrationService registrationService;

	@Mock
	private RegistrationBillingBean billingBean;
	@Mock
	private AccountService accountService;
	@Mock
	private AppUserService appUserService;
	@Mock
	private AppUserDAO appUserDAO;
	@Mock
	private LanguageModel supportedLanguages;
	@Mock
	private UserService userService;
	@Mock
	private RegistrationRequestService regReqService;
	@Mock
	private AuthenticationService authenticationService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		registrationService = new RegistrationService(billingBean, accountService, supportedLanguages, userService,
				regReqService, authenticationService, appUserService);
	}

	@Test
	public void testCreateContractorAccountFrom() throws Exception {
		RegistrationSubmission form = new RegistrationSubmission(registrationService);
		form.setContractorName("ContractorName");
		form.setUserName("UserFirstName UserLastName");
		form.setPassword("Password");
		form.setEmail("tester@picsauditing.com");
		form.setUserFirstName("UserFirstName");
		form.setUserLastName("UserLastName");
		form.setAddress("Address");
		form.setAddress2("Address2");
		form.setZip("98765");
		form.setCity("City");
		form.setCountryISO("AL");
		form.setPhoneNumber("9876543210");
		form.setPhoneNumber("9876543210");

		ContractorAccount contractorAccount = Whitebox.invokeMethod(registrationService, "createContractorAccountFrom", form);
		assertNull(contractorAccount.getCountrySubdivision());
		assertNotNull(contractorAccount.getCity());
	}
}
