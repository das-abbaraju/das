package com.picsauditing.models.operators;

import static org.junit.Assert.fail;

import static org.mockito.Mockito.when;

import java.util.ArrayList;

import java.util.List;

import org.junit.Before;

import org.junit.Test;

import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;

import com.picsauditing.PicsTest;

import com.picsauditing.access.Permissions;

import com.picsauditing.jpa.entities.Country;

import com.picsauditing.jpa.entities.Facility;

import com.picsauditing.jpa.entities.OperatorAccount;

import com.picsauditing.jpa.entities.User;

public class FacilitiesEditModelTest extends PicsTest {

	private int NON_ZERO_OPERATOR_ID = 123;

	FacilitiesEditModel facilitiesEditModel;
	User user;
	Country country;
	List<Facility> corporateFacilities;
	
	@Mock
	OperatorAccount operator;
	@Mock
	private Permissions permissions;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		user = EntityFactory.makeUser();
		when(permissions.getUserId()).thenReturn(user.getId());
		when(operator.getId()).thenReturn(NON_ZERO_OPERATOR_ID);
		facilitiesEditModel = new FacilitiesEditModel();
		autowireEMInjectedDAOs(facilitiesEditModel);
		country = new Country();
		when(operator.getCountry()).thenReturn(country);
		corporateFacilities = new ArrayList<Facility>();
		when(operator.getCorporateFacilities()).thenReturn(corporateFacilities);
	}

	@Test
	public void testAddPicsGlobal() throws Exception {
		Whitebox.invokeMethod(facilitiesEditModel, "addPicsGlobal", operator,
				permissions);
		for (Facility facility : corporateFacilities) {
			if (facility.getCorporate().getId() == OperatorAccount.PicsConsortium) {
				return;
			}
		}
		fail("PICS Global was not found.");
	}

	@Test
	public void testAddPicsCountry_Us() throws Exception {
		testAddPicsCountry("US", FacilitiesEditModel.PICS_US);
	}

	@Test
	public void testAddPicsCountry_Canada() throws Exception {
		testAddPicsCountry("CA", FacilitiesEditModel.PICS_CANADA);
	}

	@Test
	public void testAddPicsCountry_Uae() throws Exception {
		testAddPicsCountry("AE", FacilitiesEditModel.PICS_UAE);
	}

	@Test
	public void testAddPicsCountry_Uk() throws Exception {
		testAddPicsCountry("GB", FacilitiesEditModel.PICS_UK);
	}

	@Test
	public void testAddPicsCountry_France() throws Exception {
		testAddPicsCountry("FR", FacilitiesEditModel.PICS_FRANCE);
	}

	@Test
	public void testAddPicsCountry_Germanny() throws Exception {
		testAddPicsCountry("DE", FacilitiesEditModel.PICS_GERMANY);
	}

	@Test
	public void testAddPicsCountry_Us_SwitchCountry() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_CANADA, "CA");
		testAddPicsCountry("US", FacilitiesEditModel.PICS_US);
	}

	@Test
	public void testAddPicsCountry_Canada_SwitchCountry() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_US, "US");
		testAddPicsCountry("CA", FacilitiesEditModel.PICS_CANADA);
	}

	@Test
	public void testAddPicsCountry_Uae_SwitchCountry() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_US, "US");
		testAddPicsCountry("AE", FacilitiesEditModel.PICS_UAE);
	}

	@Test
	public void testAddPicsCountry_Uk_SwitchCountry() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_US, "US");
		testAddPicsCountry("GB", FacilitiesEditModel.PICS_UK);
	}

	@Test
	public void testAddPicsCountry_France_SwitchCountry() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_US, "US");
		testAddPicsCountry("FR", FacilitiesEditModel.PICS_FRANCE);
	}

	@Test
	public void testAddPicsCountry_Germanny_SwitchCountry() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_US, "US");
		testAddPicsCountry("DE", FacilitiesEditModel.PICS_GERMANY);
	}
	
	@Test
	public void testCountryNotWithNoCorrespondingAccount() throws Exception {
		String currentIsoCode = "UX";
		country.setIsoCode(currentIsoCode);

		Whitebox.invokeMethod(facilitiesEditModel, "addPicsCountry", operator,
				permissions);

		for (Facility facility : operator.getCorporateFacilities()) {
			OperatorAccount corporate = facility.getCorporate();
			if (corporate.isPicsCorporate()
					&& corporate.getId() != OperatorAccount.PicsConsortium) {
					fail("Found unexpected PICS country " + corporate);
			}
		}
	}

	@Test
	public void testCountryNotWithNoCorrespondingAccount_usExisiting() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_US, "US");
		
		String currentIsoCode = "UX";
		country.setIsoCode(currentIsoCode);

		Whitebox.invokeMethod(facilitiesEditModel, "addPicsCountry", operator,
				permissions);

		for (Facility facility : operator.getCorporateFacilities()) {
			OperatorAccount corporate = facility.getCorporate();
			if (corporate.isPicsCorporate()
					&& corporate.getId() != OperatorAccount.PicsConsortium) {
					fail("Found unexpected PICS country " + corporate);
			}
		}
	}
	
	public void testAddPicsCountry(String currentIsoCode,
			int picsCountryIdThatShouldBeSet) throws Exception {

		country.setIsoCode(currentIsoCode);

		Whitebox.invokeMethod(facilitiesEditModel, "addPicsCountry", operator,
				permissions);
		boolean foundExpectedCountry = false;

		for (Facility facility : operator.getCorporateFacilities()) {
			OperatorAccount corporate = facility.getCorporate();
			if (corporate.isPicsCorporate()
					&& corporate.getId() != OperatorAccount.PicsConsortium) {
				if (facility.getCorporate().getId() == picsCountryIdThatShouldBeSet) {
					foundExpectedCountry = true;
				}
				else {
					fail("Found unexpected PICS country " + corporate);
				}
			}
		}
		if (!foundExpectedCountry) {
			fail("PICS " + currentIsoCode + "US was not found.");
		}
	}

	private void addCountryFacility(int picsCountryId, String iso) {
		OperatorAccount corporate = new OperatorAccount();
		corporate.setId(picsCountryId);
		Country country = new Country();
		country.setIsoCode(iso);
		corporate.setCountry(country);
		Facility facility = new Facility();
		facility.setCorporate(corporate);
		facility.setOperator(operator);
		facility.setAuditColumns(permissions);
		corporateFacilities.add(facility);
	}

}
