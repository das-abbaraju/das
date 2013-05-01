package com.picsauditing.validator;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Test;
import org.mockito.Mockito;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Country;

@UseReporter(DiffReporter.class)
public class RegistrationValidatorTest {

	@Test
	public void testValidateContractor_NoValues() throws Exception {
		String language = null;
		String dialect = null;
		ContractorAccount contractor = new ContractorAccount();
		contractor.setCountry(new Country("en_US"));
		String countrySubdivision = null;

		verifyValidation(language, dialect, contractor, countrySubdivision);
	}

	@Test
	public void testValidateContractor_Swedish() throws Exception {
		String language = "sv";
		String dialect = null;
		ContractorAccount contractor = new ContractorAccount();
		contractor.setName("Sven Nilsson");
		contractor.setAddress("Roslagsgatan 10");
		contractor.setCity("Stockholm");
		contractor.setVatId("SE999999999901");
		contractor.setCountry(new Country("SE"));
		contractor.setZip("113 51");
		String countrySubdivision = null;

		verifyValidation(language, dialect, contractor, countrySubdivision);
	}

	private void verifyValidation(String language, String dialect, ContractorAccount contractor,
			String countrySubdivision) throws Exception {
		ContractorAccountDAO contractorAccountDao = Mockito.mock(ContractorAccountDAO.class);
		when(contractorAccountDao.findByCompanyName(anyString())).thenReturn(null);

		Map<String, String> errors = RegistrationValidator.validateContractor(language, dialect, contractor,
				countrySubdivision, null, new InputValidator().setContractorAccountDao(contractorAccountDao),
				new VATValidator());
		Approvals.verify(errors);
	}
}
