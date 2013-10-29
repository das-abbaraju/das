package com.picsauditing.validator;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.model.i18n.LanguageModel;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Country;

@UseReporter(DiffReporter.class)
public class RegistrationValidatorTest {

    private VATValidator vatValidator;

    @Mock
    private VATWebValidator vatWebValidator;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        vatValidator = new VATValidator();
        PicsTestUtil.forceSetPrivateField(vatValidator, "webValidator", vatWebValidator);
    }

	@Test
	public void testValidateContractor_NoValues_PopulatesErrors() throws Exception {
		String language = null;
		String dialect = null;
		ContractorAccount contractor = new ContractorAccount();
		contractor.setCountry(new Country("en_US"));
		String countrySubdivision = null;

        when(vatWebValidator.execute()).thenReturn(Boolean.FALSE);

		verifyValidation(language, dialect, contractor, countrySubdivision, Arrays.asList(new Country[] { new Country("US") }));
	}

	@Test
	public void testValidateContractor_ValidSwedish_ErrorsWillBeEmpty() throws Exception {
		String language = "sv";
		String dialect = null;
		ContractorAccount contractor = new ContractorAccount();
		contractor.setName("Sven Nilsson");
		contractor.setAddress("Roslagsgatan 10");
		contractor.setCity("Stockholm");
		contractor.setVatId("SE989898999901");
		contractor.setCountry(new Country("SE"));
		contractor.setZip("113 51");
		contractor.setTimezone(TimeZone.getDefault());
		String countrySubdivision = null;

        when(vatWebValidator.execute()).thenReturn(Boolean.TRUE);

		verifyValidation(language, dialect, contractor, countrySubdivision, Collections.<Country>emptyList());
	}

	private void verifyValidation(String language, String dialect, ContractorAccount contractor,
								  String countrySubdivision, List<Country> dialects) throws Exception {
		ContractorAccountDAO contractorAccountDao = Mockito.mock(ContractorAccountDAO.class);
		when(contractorAccountDao.findByCompanyName(anyString())).thenReturn(null);
		LanguageModel languageModel = Mockito.mock(LanguageModel.class);
		when(languageModel.getDialectCountriesBasedOn(anyString())).thenReturn(dialects);

		Map<String, String> errors = RegistrationValidator.validateContractor(language, dialect, contractor,
				countrySubdivision, languageModel, new InputValidator().setContractorAccountDao(contractorAccountDao),
				vatValidator);
		Approvals.verify(errors);
	}
}
