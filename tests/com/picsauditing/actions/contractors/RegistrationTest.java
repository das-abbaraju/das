package com.picsauditing.actions.contractors;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;


public class RegistrationTest extends PicsTest{
	Registration registration;
	
	@Mock private ContractorAccount contractor;
	@Mock private Country country;
	@Mock private CountrySubdivision countrySubdivision;
	@Mock private CountrySubdivisionDAO countrySubdivisionDAO;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);

		registration = new Registration();
		autowireEMInjectedDAOs(registration);
		
		PicsTestUtil.forceSetPrivateField(registration, "countrySubdivisionDAO", countrySubdivisionDAO);		
		registration.setContractor(contractor);
	}
}
