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
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;

public class FacilitiesEditModelTest extends PicsTest {

	private int NON_ZERO_OPERATOR_ID = 123;

	private FacilitiesEditModel facilitiesEditModel;
	private User user;
	private Country country;
	private List<Facility> corporateFacilities;
	private HashMap<String, OperatorAccount> picsConsortiumMap;

	@Mock
	private OperatorAccount operator;
	@Mock
	private Permissions permissions;
	@Mock
	private BasicDAO dao;

	private List<OperatorAccount> listOfPicsConsortiumAccounts;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);
		user = EntityFactory.makeUser();
		when(permissions.getUserId()).thenReturn(user.getId());
		when(operator.getId()).thenReturn(NON_ZERO_OPERATOR_ID);
		facilitiesEditModel = new FacilitiesEditModel();
		autowireEMInjectedDAOs(facilitiesEditModel);
		PicsTestUtil.forceSetPrivateField(facilitiesEditModel, "dao", dao);
		country = new Country();
		when(operator.getCountry()).thenReturn(country);
		corporateFacilities = new ArrayList<Facility>();
		when(operator.getCorporateFacilities()).thenReturn(corporateFacilities);

		picsConsortiumMap = new HashMap<String, OperatorAccount>();
		listOfPicsConsortiumAccounts = new ArrayList<OperatorAccount>();

		createConsortium("US");
		createConsortium("CA");
		createConsortium("FR");
		createConsortium("GB");
		createConsortium("AE");
		createConsortium("DE");

		when(dao.findWhere(OperatorAccount.class, "inPicsConsortium=1")).thenReturn(listOfPicsConsortiumAccounts);
	}

	private void createConsortium(String code) {
		OperatorAccount consortium = EntityFactory.makeOperator();
		consortium.setCountry(new Country(code));
		consortium.setInPicsConsortium(true);
		listOfPicsConsortiumAccounts.add(consortium);
		picsConsortiumMap.put(code, consortium);
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
	public void testAddPicsCountry_Us() throws Exception {


		testAddPicsCountry("US");
	}

	@Test
	public void testAddPicsCountry_Canada() throws Exception {
		testAddPicsCountry("CA");
	}

	@Test
	public void testAddPicsCountry_Uae() throws Exception {
		testAddPicsCountry("AE");
	}

	@Test
	public void testAddPicsCountry_Uk() throws Exception {
		testAddPicsCountry("GB");
	}

	@Ignore
	@Test
	public void testAddPicsCountry_France() throws Exception {
		testAddPicsCountry("FR");
	}

	@Test
	public void testAddPicsCountry_Germanny() throws Exception {
		testAddPicsCountry("DE");
	}

	@Test
	public void testAddPicsCountry_Us_SwitchCountry() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_CANADA, "CA");
		testAddPicsCountry("US");
	}

	@Test
	public void testAddPicsCountry_Canada_SwitchCountry() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_US, "US");
		testAddPicsCountry("CA");
	}

	@Test
	public void testAddPicsCountry_Uae_SwitchCountry() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_US, "US");
		testAddPicsCountry("AE");
	}

	@Test
	public void testAddPicsCountry_Uk_SwitchCountry() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_US, "US");
		testAddPicsCountry("GB");
	}

	@Test
	public void testAddPicsCountry_France_SwitchCountry() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_US, "US");
		testAddPicsCountry("FR");
	}

	@Test
	public void testAddPicsCountry_Germanny_SwitchCountry() throws Exception {
		addCountryFacility(FacilitiesEditModel.PICS_US, "US");
		testAddPicsCountry("DE");
	}

	@Test
	public void testCountryNotWithNoCorrespondingAccount() throws Exception {
		String currentIsoCode = "UX";
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
		addCountryFacility(FacilitiesEditModel.PICS_US, "US");

		String currentIsoCode = "UX";
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

		int picsCountryIdThatShouldBeSet = picsConsortiumMap.get(currentIsoCode).getId();
		facilitiesEditModel.addPicsCountry(operator, permissions);
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
