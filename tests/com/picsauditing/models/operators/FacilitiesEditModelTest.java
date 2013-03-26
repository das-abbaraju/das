package com.picsauditing.models.operators;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.dao.FacilitiesDAO;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;

public class FacilitiesEditModelTest {

	private static final String UNEXPECTED_COUNTRY_ISO_CODE = "UX";
	private int NON_ZERO_OPERATOR_ID = 123;

	private FacilitiesEditModel facilitiesEditModel;

	@Mock
	private OperatorAccount operator;
	@Mock
	private Permissions permissions;
	@Mock
	private BasicDAO dao;
	@Mock
	private FacilitiesDAO facilitiesDAO;

	private User user;
	private Country country;
	private List<Facility> corporateFacilities;
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
		corporateFacilities = new ArrayList<Facility>();
		when(operator.getCorporateFacilities()).thenReturn(corporateFacilities);

		picsConsortiumMap = new HashMap<String, OperatorAccount>();
		listOfPicsConsortiumAccounts = new ArrayList<OperatorAccount>();

		createConsortium(100, Country.US_ISO_CODE);
		createConsortium(101, Country.CANADA_ISO_CODE);
		createConsortium(102, Country.FRANCE_ISO_CODE);
		createConsortium(103, Country.UK_ISO_CODE);
		createConsortium(104, Country.UAE_ISO_CODE);
		createConsortium(105, Country.GERMANY_ISO_CODE);

		when(dao.findWhere(OperatorAccount.class, "inPicsConsortium=1")).thenReturn(listOfPicsConsortiumAccounts);

		Whitebox.setInternalState(facilitiesEditModel, "dao", dao);
		Whitebox.setInternalState(facilitiesEditModel, "facilitiesDAO", facilitiesDAO);
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
