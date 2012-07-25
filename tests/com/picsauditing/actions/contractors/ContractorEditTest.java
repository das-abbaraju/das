package com.picsauditing.actions.contractors;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.State;


public class ContractorEditTest extends PicsTest{
	ContractorEdit contractorEdit;
	
	@Mock private ContractorAccount contractor;
	@Mock private Country country;
	@Mock private State state;
	@Mock private CountrySubdivision countrySubdivision;
	@Mock private CountrySubdivisionDAO countrySubdivisionDAO;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);

		contractorEdit = new ContractorEdit();
		autowireEMInjectedDAOs(contractorEdit);
		
		PicsTestUtil.forceSetPrivateField(contractorEdit, "countrySubdivisionDAO", countrySubdivisionDAO);		
		contractorEdit.setContractor(contractor);
	}
	
	@Test
	public void testUpdateStateAndCountrySubdivision_countryHasState() throws Exception{
		state = new State("CA");
		state.setCountry(new Country("US"));		
		
		countrySubdivision = new CountrySubdivision("US-CA");
		Whitebox.setInternalState(contractorEdit, "countrySubdivision", countrySubdivision);
		when(contractor.getState()).thenReturn(state);				
		when(contractor.getCountry()).thenReturn(new Country("US"));				
		when(countrySubdivisionDAO.find(anyString())).thenReturn(countrySubdivision);
		
		Whitebox.invokeMethod(contractorEdit, "updateStateAndCountrySubdivision");		
		verify(contractor).setCountrySubdivision(countrySubdivision);
	}
	
	@Test
	public void testUpdateStateAndCountrySubdivision_countryHasWrongState() throws Exception{
		state = new State("CA");
		country = new Country("US");
		state.setCountry(new Country("CA"));
		countrySubdivision = new CountrySubdivision("CA-CA");
		when(contractor.getState()).thenReturn(state);		
		when(contractor.getCountry()).thenReturn(country);		
		when(countrySubdivisionDAO.find(anyString())).thenReturn(countrySubdivision);
		Whitebox.invokeMethod(contractorEdit, "updateStateAndCountrySubdivision");		
		verify(contractor).setState(null);
		verify(contractor).setCountrySubdivision(null);	
	}
}
