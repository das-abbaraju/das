package com.picsauditing.actions.report;

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
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.State;


public class ReportNewReqConImportTest extends PicsTest{
	ReportNewReqConImport reportNewReqConImport;
	
	@Mock private ContractorRegistrationRequest crr;
	@Mock private Country country;
	@Mock private State state;
	@Mock private CountrySubdivision countrySubdivision;
	@Mock private CountrySubdivisionDAO countrySubdivisionDAO;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);

		reportNewReqConImport = new ReportNewReqConImport();
		autowireEMInjectedDAOs(reportNewReqConImport);
		
		PicsTestUtil.forceSetPrivateField(reportNewReqConImport, "countrySubdivisionDAO", countrySubdivisionDAO);				
	}

	@Test
	public void testUpdateStateAndCountrySubdivision_countryHasState() throws Exception{
		state = new State("CA");
		state.setCountry(new Country("US"));
		countrySubdivision = new CountrySubdivision("US-CA");
		Whitebox.setInternalState(reportNewReqConImport, "countrySubdivision", countrySubdivision);
		when(crr.getState()).thenReturn(state);		
		when(crr.getCountry()).thenReturn(new Country("US"));		
		when(countrySubdivisionDAO.find(anyString())).thenReturn(countrySubdivision);
		
		Whitebox.invokeMethod(reportNewReqConImport, "updateStateAndCountrySubdivision", crr);		
		verify(crr).setCountrySubdivision(countrySubdivision);
	}
	
	@Test
	public void testUpdateStateAndCountrySubdivision_countryHasWrongState() throws Exception{
		state = new State("CA");
		state.setCountry(new Country("CA"));
		countrySubdivision = new CountrySubdivision("CA-CA");
		when(crr.getState()).thenReturn(state);		
		when(crr.getCountry()).thenReturn(new Country("US"));		
		when(countrySubdivisionDAO.find(anyString())).thenReturn(countrySubdivision);
		Whitebox.invokeMethod(reportNewReqConImport, "updateStateAndCountrySubdivision", crr);		
		verify(crr).setState(null);
		verify(crr).setCountrySubdivision(null);	
	}
}
