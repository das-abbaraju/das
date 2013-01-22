package com.picsauditing.validator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.Currency;

public class VATValidatorTest {
    private static final String GREAT_BRITAIN_ISO = "GB";
    private static final String IRELAND_ISO = "IE";
    private static final String GERMANY_ISO = "DE";
    private static final String GREECE_ISO = "GR";
    private static final String GREECE_PREFIX = "EL";

    private static final Country GREAT_BRITAIN = new Country(GREAT_BRITAIN_ISO);
    private static final Country IRELAND = new Country(IRELAND_ISO);
    private static final Country GERMANY = new Country(GERMANY_ISO);
    private static final Country GREECE = new Country(GREECE_ISO);

    @Mock
    AuditData mockData;
    @Mock
    ContractorAudit mockAudit;
    @Mock
    ContractorAccount mockContractor;

    VATValidator classUnderTest;

    @BeforeClass
    public static void config() {
        GREAT_BRITAIN.setCurrency(Currency.GBP);
        IRELAND.setCurrency(Currency.EUR);
        GERMANY.setCurrency(Currency.EUR);
        GREECE.setCurrency(Currency.EUR);
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        classUnderTest = new VATValidator();

        when(mockData.getAudit()).thenReturn(mockAudit);
        when(mockAudit.getContractorAccount()).thenReturn(mockContractor);
    }

    @Test
    public void testValidate() throws VATValidator.ValidationException {
        when(mockContractor.getCountry()).thenReturn(GREAT_BRITAIN);
        String testVAT = "990 0134 03"; // A valid GB VAT
        when(mockData.getAnswer()).thenReturn(testVAT);
        assertEquals(GREAT_BRITAIN_ISO + testVAT, classUnderTest.validatedVATfromAudit(mockData));
    }

    @Test
    public void testValidate_alreadyPrefixed() throws VATValidator.ValidationException {
        when(mockContractor.getCountry()).thenReturn(GERMANY);
        String testVat = "DE012345678"; // A valid German VAT
        when(mockData.getAnswer()).thenReturn(testVat);
        assertEquals(testVat, classUnderTest.validatedVATfromAudit(mockData));
    }

    @Test(expected = VATValidator.ValidationException.class)
    public void testValidate_badAnswer() throws VATValidator.ValidationException {
        when(mockContractor.getCountry()).thenReturn(IRELAND);
        String testVat = "00000"; // A bogus VAT
        when(mockData.getAnswer()).thenReturn(testVat);
        classUnderTest.validatedVATfromAudit(mockData); // Should throw an error.
    }

    @Test(expected = VATValidator.ValidationException.class)
    public void testValidate_singleDigitAnswer() throws VATValidator.ValidationException {
        when(mockContractor.getCountry()).thenReturn(GREAT_BRITAIN);
        String testVat = "0";
        when(mockData.getAnswer()).thenReturn(testVat);
        classUnderTest.validatedVATfromAudit(mockData);
    }

    @Test(expected = VATValidator.ValidationException.class)
    public void testValidate_dualDigitBogusAnswer() throws VATValidator.ValidationException {
        when(mockContractor.getCountry()).thenReturn(GERMANY);
        String testVat = "11";
        when(mockData.getAnswer()).thenReturn(testVat);
        classUnderTest.validatedVATfromAudit(mockData);
    }

    @Test
    public void testValidate_dualDigitRealVAT() throws VATValidator.ValidationException {
        when(mockContractor.getCountry()).thenReturn(IRELAND);
        String testVat = "00235";
        when(mockData.getAnswer()).thenReturn(testVat);
        assertEquals(IRELAND_ISO + testVat, classUnderTest.validatedVATfromAudit(mockData));
    }

    @Test
    public void testValidate_GreeceSwap() throws Exception {
        when(mockContractor.getCountry()).thenReturn(GREECE);
        String testVat = "EL 225 110";
        when(mockData.getAnswer()).thenReturn(testVat);
        assertEquals(testVat, classUnderTest.validatedVATfromAudit(mockData));
    }

    @Test(expected = VATValidator.ValidationException.class)
    public void testValidate_emptyAnswer() throws Exception {
        when(mockContractor.getCountry()).thenReturn(GERMANY);
        String testVat = "";
        when(mockData.getAnswer()).thenReturn(testVat);
        classUnderTest.validatedVATfromAudit(mockData);
    }

    @Test(expected = VATValidator.ValidationException.class)
    public void testValidate_yesAnswer() throws VATValidator.ValidationException {
        when(mockContractor.getCountry()).thenReturn(GREAT_BRITAIN);
        String testVat = "yes";
        when(mockData.getAnswer()).thenReturn(testVat);
        assertEquals(GREAT_BRITAIN_ISO + testVat.toUpperCase(), classUnderTest.validatedVATfromAudit(mockData));
    }

    @Test(expected = VATValidator.ValidationException.class)
    public void testValidate_obfuscated() throws VATValidator.ValidationException {
        when(mockContractor.getCountry()).thenReturn(GREAT_BRITAIN);
        String testVat = "xxxxxx983";
        when(mockData.getAnswer()).thenReturn(testVat);
        assertEquals(GREAT_BRITAIN_ISO + testVat.toUpperCase(), classUnderTest.validatedVATfromAudit(mockData));
    }

    @Test(expected = VATValidator.ValidationException.class)
    public void testValidate_noAnswer() throws VATValidator.ValidationException {
        when(mockContractor.getCountry()).thenReturn(GREAT_BRITAIN);
        String testVat = "no";
        when(mockData.getAnswer()).thenReturn(testVat);
        assertEquals(GREAT_BRITAIN_ISO + testVat.toUpperCase(), classUnderTest.validatedVATfromAudit(mockData));
    }

    @Test(expected = VATValidator.ValidationException.class)
    public void testValidate_naAnswer() throws VATValidator.ValidationException {
        when(mockContractor.getCountry()).thenReturn(GREAT_BRITAIN);
        String testVat = "n/a";
        when(mockData.getAnswer()).thenReturn(testVat);
        assertEquals(GREAT_BRITAIN_ISO + testVat.toUpperCase(), classUnderTest.validatedVATfromAudit(mockData));
    }

}
