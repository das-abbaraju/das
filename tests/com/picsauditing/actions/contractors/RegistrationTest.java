package com.picsauditing.actions.contractors;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.invokeMethod;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.ArrayList;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorFee;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.User;

public class RegistrationTest extends PicsTranslationTest {

	Registration classUnderTest;

	private final static String DEMO_NAME = "^^^demo";
	private final static String REAL_NAME = "fooBar";

	@Mock
	private ContractorAccount contractor;
	@Mock
	private Country country;
	@Mock
	private CountrySubdivision countrySubdivision;
	@Mock
	private ContractorTagDAO contractorTagDAO;
	@Mock
	private CountrySubdivisionDAO countrySubdivisionDAO;
	@Mock
	private InvoiceFeeDAO feeDAO;
	@Mock
	private OperatorTagDAO operatorTagDAO;
	@Mock
	private Map<FeeClass, ContractorFee> contractorFees;
	@Mock
	private ThreadLocal<ActionContext> mockThreadLocal;
	@Mock
	private ActionContext mockContext;
	@Mock
	private User mockUser;

	@AfterClass
	public static void tearDown() {
		Whitebox.setInternalState(ActionContext.class, "actionContext", new ThreadLocal<ActionContext>());
		PicsTranslationTest.tearDownTranslationService();
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();

		setInternalState(ActionContext.class, "actionContext", mockThreadLocal);
		when(mockThreadLocal.get()).thenReturn(mockContext);
		classUnderTest = new Registration();
		setInternalState(classUnderTest, "contractorTagDAO", contractorTagDAO);
		setInternalState(classUnderTest, "countrySubdivisionDAO", countrySubdivisionDAO);
		setInternalState(classUnderTest, "invoiceFeeDAO", feeDAO);
		setInternalState(classUnderTest, "operatorTagDAO", operatorTagDAO);
		classUnderTest.setContractor(contractor);
		classUnderTest.setUser(mockUser);
		when(contractor.getFees()).thenReturn(contractorFees);
		when(contractor.getCountry()).thenReturn(country);
		when(contractor.getNaics()).thenReturn(new Naics());
		when(contractor.getStatus()).thenReturn(AccountStatus.Pending);
	}

	@Test
	public void testSetupContractorData_Demo_NoSubdivisions() {
		when(contractor.getName()).thenReturn(DEMO_NAME);
		when(country.isHasCountrySubdivisions()).thenReturn(false);
		when(contractor.getUsers()).thenReturn(new ArrayList<User>());

		classUnderTest.setupContractorData();

		verify(contractor).setName(anyString());
		verify(contractor).setStatus(AccountStatus.Demo);
		verify(contractor).setCountrySubdivision(null);
		verify(contractorFees, atLeastOnce()).put(any(FeeClass.class), any(ContractorFee.class));
		verify(countrySubdivisionDAO, never()).find(anyString());
		verify(feeDAO, atLeastOnce()).findByNumberOfOperatorsAndClass(any(FeeClass.class), anyInt());
	}

	@Ignore("Expediting fix of PICS-8975")
	@Test
	public void testSetupContractorData_noDemo_HasSubdivisions_matchedInput() {
		classUnderTest.setCountrySubdivision(countrySubdivision);
		when(contractor.getName()).thenReturn(REAL_NAME);
		when(country.isHasCountrySubdivisions()).thenReturn(true);
		when(contractor.getCountrySubdivision()).thenReturn(countrySubdivision);

		classUnderTest.setupContractorData();

		verify(contractor, never()).setName(anyString());
		verify(contractor, never()).setStatus(any(AccountStatus.class));
		verify(contractor, never()).setCountrySubdivision(any(CountrySubdivision.class));
		verify(countrySubdivisionDAO, never()).find(anyString());
		verify(feeDAO, atLeastOnce()).findByNumberOfOperatorsAndClass(any(FeeClass.class), anyInt());
	}

	@Ignore("Expediting fix of PICS-8975")
	@Test
	public void testSetupContractorData_noDemo_HasSubdivisions_unMatchedInput() {
		classUnderTest.setCountrySubdivision(new CountrySubdivision("XX"));
		when(contractor.getName()).thenReturn(REAL_NAME);
		when(country.isHasCountrySubdivisions()).thenReturn(true);
		when(contractor.getCountrySubdivision()).thenReturn(countrySubdivision);

		classUnderTest.setupContractorData();

		verify(contractor, never()).setName(anyString());
		verify(contractor, never()).setStatus(any(AccountStatus.class));
		verify(contractor).setCountrySubdivision(any(CountrySubdivision.class));
		verify(countrySubdivisionDAO).find(anyString());
		verify(feeDAO, atLeastOnce()).findByNumberOfOperatorsAndClass(any(FeeClass.class), anyInt());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testTransferRegistrationRequestTags_MultipleTags() throws Exception {
		OperatorTag operatorTag = new OperatorTag();
		operatorTag.setOperator(EntityFactory.makeOperator());

		when(operatorTagDAO.find(anyInt())).thenReturn(operatorTag);

		ContractorAccount contractor = EntityFactory.makeContractor();
		ContractorRegistrationRequest request = new ContractorRegistrationRequest();

		request.setOperatorTags("1,2");

		classUnderTest.setContractor(contractor);

		invokeMethod(classUnderTest, "transferRegistrationRequestTags", request);

		verify(contractorTagDAO, times(2)).save(any(BaseTable.class));
		verify(operatorTagDAO, times(2)).find(anyInt());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testTransferRegistrationRequestTags_NoTags() throws Exception {
		ContractorAccount contractor = EntityFactory.makeContractor();
		ContractorRegistrationRequest request = new ContractorRegistrationRequest();

		classUnderTest.setContractor(contractor);

		invokeMethod(classUnderTest, "transferRegistrationRequestTags", request);

		verify(contractorTagDAO, never()).save(any(BaseTable.class));
		verify(operatorTagDAO, never()).find(anyInt());
	}

}
