package com.picsauditing.model.operators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.dao.FacilitiesDAO;

public class FacilitiesEditModelTest {

	private static final String UNEXPECTED_COUNTRY_ISO_CODE = "UX";
	private int NON_ZERO_OPERATOR_ID = 123;

	private FacilitiesEditModel facilitiesEditModel;

	@Mock
	private OperatorAccount operator;
    @Mock
    private OperatorAccount facilityOperator;
    @Mock
	private Permissions permissions;
	@Mock
	private BasicDAO dao;
	@Mock
	private FacilitiesDAO facilitiesDAO;
    @Mock
    private OperatorAccountDAO operatorDAO;
    @Mock
    private AccountUser accountUser;
    @Mock
    private User accountUserUser;
    @Mock
    private Facility facility;

	private User user;
	private Country country;
	private List<Facility> corporateFacilities;
	private List<Facility> operatorFacilities;
	private HashMap<String, OperatorAccount> picsConsortiumMap;
	private List<OperatorAccount> listOfPicsConsortiumAccounts;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		user = EntityFactory.makeUser();

		when(permissions.getUserId()).thenReturn(user.getId());
		when(operator.getId()).thenReturn(NON_ZERO_OPERATOR_ID);
		facilitiesEditModel = new FacilitiesEditModel();

		country = new Country();
		when(operator.getCountry()).thenReturn(country);
		corporateFacilities = new ArrayList<>();
        operatorFacilities = new ArrayList<>();
        operatorFacilities.add(facility);
		when(operator.getCorporateFacilities()).thenReturn(corporateFacilities);
		when(operator.getOperatorFacilities()).thenReturn(operatorFacilities);

		picsConsortiumMap = new HashMap<>();
		listOfPicsConsortiumAccounts = new ArrayList<>();

		createConsortium(100, Country.US_ISO_CODE);
		createConsortium(101, Country.CANADA_ISO_CODE);
		createConsortium(102, Country.FRANCE_ISO_CODE);
		createConsortium(103, Country.UK_ISO_CODE);
		createConsortium(104, Country.UAE_ISO_CODE);
		createConsortium(105, Country.GERMANY_ISO_CODE);

		when(dao.findWhere(OperatorAccount.class, "inPicsConsortium=1")).thenReturn(listOfPicsConsortiumAccounts);
        when(accountUser.getUser()).thenReturn(accountUserUser);
        when(facility.getOperator()).thenReturn(facilityOperator);

        PicsTestUtil.autowireDAOsFromDeclaredMocks(facilitiesEditModel, this);
	}

	private void createConsortium(int id, String isoCode) {
		OperatorAccount consortium = EntityFactory.makeOperator();
		consortium.setId(id);
		consortium.setCountry(new Country(isoCode));
		consortium.setInPicsConsortium(true);
		listOfPicsConsortiumAccounts.add(consortium);
		picsConsortiumMap.put(isoCode, consortium);
	}

    @Test
    public void testCopySingleCurrentAccountUserToChildAccounts_Happy() throws Exception {
        when(facilityOperator.isActiveOrDemo()).thenReturn(true);
        when(accountUser.getRole()).thenReturn(UserAccountRole.PICSAccountRep);

        facilitiesEditModel.copySingleCurrentAccountUserToChildAccounts(permissions, operator, accountUser);

        verify(facilityOperator).setCurrentAccountRepresentative(accountUserUser, permissions.getUserId());
        verify(operatorDAO).save(any(OperatorAccount.class));
    }


    @Test
    public void testCopySingleCurrentAccountUserToChildAccounts_RoleNotAccountRepNoCopy() throws Exception {
        when(facilityOperator.isActiveOrDemo()).thenReturn(true);
        when(accountUser.getRole()).thenReturn(UserAccountRole.PICSSalesRep);

        facilitiesEditModel.copySingleCurrentAccountUserToChildAccounts(permissions, operator, accountUser);

        verify(operatorDAO, never()).save(any(OperatorAccount.class));
    }

    @Test
    public void testCopySingleCurrentAccountUserToChildAccounts_NoActiveChildFacilitiesNoCopy() throws Exception {
        when(facilityOperator.isActiveOrDemo()).thenReturn(false);

        facilitiesEditModel.copySingleCurrentAccountUserToChildAccounts(permissions, operator, accountUser);

        verify(operatorDAO, never()).save(any(OperatorAccount.class));
    }

    @Test
    public void testCopySingleCurrentAccountUserToChildAccounts_NoChildFacilitiesNoCopy() throws Exception {
        facilitiesEditModel.copySingleCurrentAccountUserToChildAccounts(permissions, operator, accountUser);
        verify(operatorDAO, never()).save(any(OperatorAccount.class));
    }


    @Test
    public void testManageSingleCurrentAccountUser_WeOnlyDealWithAccountRepresentativesRightNow() throws Exception {
        when(accountUser.getRole()).thenReturn(UserAccountRole.PICSCustomerServiceRep);

        FacilitiesEditStatus status = facilitiesEditModel.manageSingleCurrentAccountUser(permissions, operator, accountUser);

        assertTrue(status.isOk);
        verify(operatorDAO, never()).save(operator);
    }

    @Test
    public void testManageSingleCurrentAccountUser_ZeroIdMeansRemove_NotActiveOrDemoOperatorIsOkToRemove() throws Exception {
        when(accountUser.getRole()).thenReturn(UserAccountRole.PICSAccountRep);
        when(accountUserUser.getId()).thenReturn(0);
        when(operator.isActiveOrDemo()).thenReturn(false);

        facilitiesEditModel.manageSingleCurrentAccountUser(permissions, operator, accountUser);

        verify(operator).setCurrentAccountRepresentative(null, permissions.getUserId());
        verify(operatorDAO).save(operator);
    }

    @Test
    public void testManageSingleCurrentAccountUser_ZeroIdMeansRemove_ActiveOperatorNotIsOkToRemove() throws Exception {
        when(accountUser.getRole()).thenReturn(UserAccountRole.PICSAccountRep);
        when(accountUserUser.getId()).thenReturn(0);
        when(operator.isActiveOrDemo()).thenReturn(true);

        FacilitiesEditStatus status = facilitiesEditModel.manageSingleCurrentAccountUser(permissions, operator, accountUser);

        assertFalse(status.isOk);
        verify(operator, never()).setCurrentAccountRepresentative(null, permissions.getUserId());
        verify(operatorDAO, never()).save(operator);
    }

    @Test
    public void testManageSingleCurrentAccountUser_SettingToSameOneIsIgnored() throws Exception {
        when(accountUser.getRole()).thenReturn(UserAccountRole.PICSAccountRep);
        when(accountUserUser.getId()).thenReturn(123);
        when(operator.getCurrentAccountRepresentative()).thenReturn(accountUserUser);

        facilitiesEditModel.manageSingleCurrentAccountUser(permissions, operator, accountUser);

        verify(operator, never()).setCurrentAccountRepresentative(null, permissions.getUserId());
        verify(operatorDAO, never()).save(operator);
    }

    @Test
    public void testManageSingleCurrentAccountUser_NoCurrentAccountRep_WillSetNewOne() throws Exception {
        when(accountUser.getRole()).thenReturn(UserAccountRole.PICSAccountRep);
        when(accountUserUser.getId()).thenReturn(123);
        when(operator.getCurrentAccountRepresentative()).thenReturn(null);

        facilitiesEditModel.manageSingleCurrentAccountUser(permissions, operator, accountUser);

        verify(operator).setCurrentAccountRepresentative(accountUserUser, permissions.getUserId());
        verify(operatorDAO).save(operator);
    }

    @Test
    public void testIsOperatorAllowedToHaveNoAccountUserOfThisRole_AllowedToHaveNoSalesRep() throws Exception {
        FacilitiesEditStatus status = facilitiesEditModel.isOperatorAllowedToHaveNoAccountUserOfThisRole(operator, UserAccountRole.PICSSalesRep);
        assertTrue(status.isOk);
    }

    @Test
    public void testIsOperatorAllowedToHaveNoAccountUserOfThisRole_ActiveNotAllowedToHaveNoAccountManager() throws Exception {
        when(operator.isActiveOrDemo()).thenReturn(true);
        when(operator.isActive()).thenReturn(true);

        FacilitiesEditStatus status = facilitiesEditModel.isOperatorAllowedToHaveNoAccountUserOfThisRole(operator, UserAccountRole.PICSAccountRep);

        assertFalse(status.isOk);
        assertTrue(status.notOkErrorMessage.startsWith("Active"));
    }

    @Test
    public void testIsOperatorAllowedToHaveNoAccountUserOfThisRole_DemoNotAllowedToHaveNoAccountManager() throws Exception {
        when(operator.isActiveOrDemo()).thenReturn(true);
        when(operator.isActive()).thenReturn(false);

        FacilitiesEditStatus status = facilitiesEditModel.isOperatorAllowedToHaveNoAccountUserOfThisRole(operator, UserAccountRole.PICSAccountRep);

        assertFalse(status.isOk);
        assertTrue(status.notOkErrorMessage.startsWith("Demo"));
    }

    @Test
	public void testAddPicsGlobal() throws Exception {
		Whitebox.invokeMethod(facilitiesEditModel, "addPicsGlobal", operator, permissions);
		for (Facility facility : corporateFacilities) {
			if (facility.getCorporate().getId() == OperatorAccount.PicsConsortium) {
				return;
			}
		}

		fail("PICS Global was not found.");
	}

	@Test
	public void testAddPicsCountry_US() throws Exception {
		testAddPicsCountry(Country.US_ISO_CODE);
	}

	@Test
	public void testAddPicsCountry_Canada() throws Exception {
		testAddPicsCountry(Country.CANADA_ISO_CODE);
	}

	@Test
	public void testAddPicsCountry_UAE() throws Exception {
		testAddPicsCountry(Country.UAE_ISO_CODE);
	}

	@Test
	public void testAddPicsCountry_UK() throws Exception {
		testAddPicsCountry(Country.UK_ISO_CODE);
	}

	@Test
	public void testAddPicsCountry_France() throws Exception {
		testAddPicsCountry(Country.FRANCE_ISO_CODE);
	}

	@Test
	public void testAddPicsCountry_Germany() throws Exception {
		testAddPicsCountry(Country.GERMANY_ISO_CODE);
	}

	@Test
	public void testAddPicsCountry_US_SwitchCountry() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_CANADA, Country.CANADA_ISO_CODE);
		testAddPicsCountry(Country.US_ISO_CODE);
	}

	@Test
	public void testAddPicsCountry_Canada_SwitchCountry() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_US, Country.US_ISO_CODE);
		testAddPicsCountry(Country.CANADA_ISO_CODE);
	}

	@Test
	public void testAddPicsCountry_UAE_SwitchCountry() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_US, Country.US_ISO_CODE);
		testAddPicsCountry(Country.UAE_ISO_CODE);
	}

	@Test
	public void testAddPicsCountry_UK_SwitchCountry() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_US, Country.US_ISO_CODE);
		testAddPicsCountry(Country.UK_ISO_CODE);
	}

	@Test
	public void testAddPicsCountry_France_SwitchCountry() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_US, Country.US_ISO_CODE);
		testAddPicsCountry(Country.FRANCE_ISO_CODE);
	}

	@Test
	public void testAddPicsCountry_Germany_SwitchCountry() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_US, Country.US_ISO_CODE);
		testAddPicsCountry(Country.GERMANY_ISO_CODE);
	}

	@Test
	public void testCountryNotWithNoCorrespondingAccount() throws Exception {
		String currentIsoCode = UNEXPECTED_COUNTRY_ISO_CODE;
		country.setIsoCode(currentIsoCode);

		facilitiesEditModel.addPicsCountry(operator, permissions);

		for (Facility facility : operator.getCorporateFacilities()) {
			OperatorAccount corporate = facility.getCorporate();
			if (corporate.isInPicsConsortium() && corporate.getId() != OperatorAccount.PicsConsortium) {
				fail("Found unexpected PICS country " + corporate);
			}
		}
	}

	@Test
	public void testCountryNotWithNoCorrespondingAccount_usExisiting() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_US, Country.US_ISO_CODE);

		String currentIsoCode = UNEXPECTED_COUNTRY_ISO_CODE;
		country.setIsoCode(currentIsoCode);

		facilitiesEditModel.addPicsCountry(operator, permissions);

		for (Facility facility : operator.getCorporateFacilities()) {
			OperatorAccount corporate = facility.getCorporate();
			if (corporate.isInPicsConsortium() && corporate.getId() != OperatorAccount.PicsConsortium) {
				fail("Found unexpected PICS country " + corporate);
			}
		}
	}

	public void testAddPicsCountry(String currentIsoCode) throws Exception {
		country.setIsoCode(currentIsoCode);

		facilitiesEditModel.addPicsCountry(operator, permissions);

		verifyAddedCountry(currentIsoCode);
	}

	private void verifyAddedCountry(String currentIsoCode) {
		int picsCountryIdThatShouldBeSet = picsConsortiumMap.get(currentIsoCode).getId();

		boolean foundExpectedCountry = false;
		for (Facility facility : operator.getCorporateFacilities()) {
			OperatorAccount corporate = facility.getCorporate();
			if (corporate.isInPicsConsortium() && corporate.getId() != OperatorAccount.PicsConsortium) {
				if (facility.getCorporate().getId() == picsCountryIdThatShouldBeSet) {
					foundExpectedCountry = true;
				} else {
					fail("Found unexpected PICS country " + corporate);
				}
			}
		}

		if (!foundExpectedCountry) {
			fail("PICS " + currentIsoCode + " was not found.");
		}
	}

	private void addCountryFacility(int picsCountryId, String isoCode) {
		OperatorAccount corporate = buildFakeCorporateAccount(picsCountryId, isoCode);
		Facility facility = buildFakeFacility(corporate);
		corporateFacilities.add(facility);
	}

	private OperatorAccount buildFakeCorporateAccount(int picsCountryId, String isoCode) {
		OperatorAccount corporateAccount = new OperatorAccount();
		corporateAccount.setId(picsCountryId);

		Country country = new Country();
		country.setIsoCode(isoCode);
		corporateAccount.setCountry(country);

		return corporateAccount;
	}

	private Facility buildFakeFacility(OperatorAccount corporate) {
		Facility facility = new Facility();
		facility.setCorporate(corporate);
		facility.setOperator(operator);
		facility.setAuditColumns(permissions);

		return facility;
	}

}
