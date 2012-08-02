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

public class ReportNewReqConImportTest extends PicsTest{
	ReportNewReqConImport reportNewReqConImport;
	
	@Mock private ContractorRegistrationRequest crr;
	@Mock private Country country;
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
}
