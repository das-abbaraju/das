package com.picsauditing.validator;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.model.i18n.LanguageModel;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@UseReporter(DiffReporter.class)
public class RegistrationValidatorTest {

    private VATValidator vatValidator;

    private RegistrationValidator registrationValidator;

    @Mock
    private VATWebValidator vatWebValidator;
    @Mock
    private TaxIdValidatorFactory taxIdValidatorFactory;
    @Mock
    private ContractorAccountDAO contractorAccountDao;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        registrationValidator = new RegistrationValidator();
        when(taxIdValidatorFactory.buildTaxIdValidator(Matchers.<Country>anyObject())).thenReturn(new MockTaxIdValidator(true));
        Whitebox.setInternalState(registrationValidator, "taxIdValidatorFactory", taxIdValidatorFactory);
        Whitebox.setInternalState(registrationValidator, "inputValidator",
                new InputValidator().setContractorAccountDao(contractorAccountDao));

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

        verifyValidation(language, dialect, contractor, countrySubdivision, Arrays.asList(new Country[]{new Country("US")}));
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

    @Test
    public void testValidateContractor_ValidBrazil_ErrorsWillBeEmpty() throws Exception {
        when(taxIdValidatorFactory.buildTaxIdValidator(Matchers.<Country>anyObject())).thenReturn(new CnpjTaxIdValidator());
        String language = "pt";
        String dialect = "br";
        ContractorAccount contractor = new ContractorAccount();
        contractor.setName("Yumi Marina");
        contractor.setAddress("55 Luiza Way");
        contractor.setCity("Rio de Janeiro");
        contractor.setVatId("18.791.925/0001-63");
        Country brazil = new Country(Country.BRAZIL_ISO_CODE);
        contractor.setCountry(brazil);
        contractor.setZip("113 51");
        contractor.setTimezone(TimeZone.getDefault());
        String countrySubdivision = null;

        verifyValidation(language, dialect, contractor, countrySubdivision, Arrays.asList(new Country[]{brazil}));
    }

    @Test
    public void testValidateContractor_InvalidBrazil_InvalidCnpj() throws Exception {
        when(taxIdValidatorFactory.buildTaxIdValidator(Matchers.<Country>anyObject())).thenReturn(new CnpjTaxIdValidator());

        String language = "pt";
        String dialect = "br";
        ContractorAccount contractor = new ContractorAccount();
        contractor.setName("Yumi Marina");
        contractor.setAddress("55 Luiza Way");
        contractor.setCity("Rio de Janeiro");
        contractor.setVatId("1891.925/0001-63");
        Country brazil = new Country(Country.BRAZIL_ISO_CODE);
        contractor.setCountry(brazil);
        contractor.setZip("113 51");
        contractor.setTimezone(TimeZone.getDefault());
        String countrySubdivision = null;

        verifyValidation(language, dialect, contractor, countrySubdivision, Arrays.asList(new Country[]{brazil}));
    }

    private void verifyValidation(String language, String dialect, ContractorAccount contractor,
                                  String countrySubdivision, List<Country> dialects) throws Exception {
        when(contractorAccountDao.findByCompanyName(anyString())).thenReturn(null);
        LanguageModel languageModel = Mockito.mock(LanguageModel.class);
        when(languageModel.getDialectCountriesBasedOn(anyString())).thenReturn(dialects);

        Map<String, String> errors = registrationValidator.validateContractor(language, dialect, contractor,
                countrySubdivision, languageModel);
        Approvals.verify(errors);
    }
}
