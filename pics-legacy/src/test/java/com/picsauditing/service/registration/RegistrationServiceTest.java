package com.picsauditing.service.registration;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportPermissionException;
import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dr.domain.fields.QueryFilterOperator;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.report.*;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.service.PermissionService;
import com.picsauditing.service.ReportPreferencesService;
import com.picsauditing.service.ReportSearchResults;
import com.picsauditing.service.ReportService;
import com.picsauditing.service.account.AccountService;
import com.picsauditing.service.billing.RegistrationBillingBean;
import com.picsauditing.service.user.UserService;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.RowSetDynaClass;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

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

    @Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		registrationService = new RegistrationService(billingBean, accountService, supportedLanguages, userService, regReqService, appUserDAO, appUserService);
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
    }
}
