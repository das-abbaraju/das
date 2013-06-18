package com.picsauditing.validator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import com.picsauditing.PicsTestUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.Currency;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

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
    @Mock
    VATWebValidator webValidator;

    VATValidator classUnderTest;

    @BeforeClass
    public static void config() {
        GREAT_BRITAIN.setCurrency(Currency.GBP);
        IRELAND.setCurrency(Currency.EUR);
        GERMANY.setCurrency(Currency.EUR);
        GREECE.setCurrency(Currency.EUR);
    }

    @Before
    public void setup() throws ValidationException {
        MockitoAnnotations.initMocks(this);
        classUnderTest = new VATValidator();

        PicsTestUtil.forceSetPrivateField(classUnderTest, "webValidator", webValidator);

        when(mockData.getAudit()).thenReturn(mockAudit);
        when(mockAudit.getContractorAccount()).thenReturn(mockContractor);
    }

    @Test
    public void testValidate() throws ValidationException {
        when(mockContractor.getCountry()).thenReturn(GREAT_BRITAIN);
        String testVAT = "990 0134 03"; // A valid GB VAT
        when(mockData.getAnswer()).thenReturn(testVAT);
        assertEquals(GREAT_BRITAIN_ISO + testVAT, classUnderTest.validatedVATfromAudit(mockData));
    }

    @Test
    public void testValidate_alreadyPrefixed() throws ValidationException {
        when(mockContractor.getCountry()).thenReturn(GERMANY);
        String testVat = "DE012345678"; // A valid German VAT
        when(mockData.getAnswer()).thenReturn(testVat);
        assertEquals(testVat, classUnderTest.validatedVATfromAudit(mockData));
    }

    @Test(expected = ValidationException.class)
    public void testValidate_badAnswer() throws ValidationException {
        when(mockContractor.getCountry()).thenReturn(IRELAND);
        String testVat = "00000"; // A bogus VAT
        when(mockData.getAnswer()).thenReturn(testVat);
        doThrow(new ValidationException()).when(webValidator).webValidate(anyString());
        classUnderTest.validatedVATfromAudit(mockData); // Should throw an error.
    }

    @Test(expected = ValidationException.class)
    public void testValidate_singleDigitAnswer() throws ValidationException {
        when(mockContractor.getCountry()).thenReturn(GREAT_BRITAIN);
        String testVat = "0";
        when(mockData.getAnswer()).thenReturn(testVat);
        doThrow(new ValidationException()).when(webValidator).webValidate(anyString());
        classUnderTest.validatedVATfromAudit(mockData);
    }

    @Test(expected = ValidationException.class)
    public void testValidate_dualDigitBogusAnswer() throws ValidationException {
        when(mockContractor.getCountry()).thenReturn(GERMANY);
        String testVat = "11";
        when(mockData.getAnswer()).thenReturn(testVat);
        doThrow(new ValidationException()).when(webValidator).webValidate(anyString());
        classUnderTest.validatedVATfromAudit(mockData);
    }

    @Test
    public void testValidate_dualDigitRealVAT() throws ValidationException {
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

    @Test(expected = ValidationException.class)
    public void testValidate_emptyAnswer() throws Exception {
        when(mockContractor.getCountry()).thenReturn(GERMANY);
        String testVat = "";
        when(mockData.getAnswer()).thenReturn(testVat);
        doThrow(new ValidationException()).when(webValidator).webValidate(anyString());
        classUnderTest.validatedVATfromAudit(mockData);
    }

    @Test(expected = ValidationException.class)
    public void testValidate_yesAnswer() throws ValidationException {
        when(mockContractor.getCountry()).thenReturn(GREAT_BRITAIN);
        String testVat = "yes";
        when(mockData.getAnswer()).thenReturn(testVat);
        doThrow(new ValidationException()).when(webValidator).webValidate(anyString());
        assertEquals(GREAT_BRITAIN_ISO + testVat.toUpperCase(), classUnderTest.validatedVATfromAudit(mockData));
    }

    @Test(expected = ValidationException.class)
    public void testValidate_obfuscated() throws ValidationException {
        when(mockContractor.getCountry()).thenReturn(GREAT_BRITAIN);
        String testVat = "xxxxxx983";
        when(mockData.getAnswer()).thenReturn(testVat);
        doThrow(new ValidationException()).when(webValidator).webValidate(anyString());
        assertEquals(GREAT_BRITAIN_ISO + testVat.toUpperCase(), classUnderTest.validatedVATfromAudit(mockData));
    }

    @Test(expected = ValidationException.class)
    public void testValidate_noAnswer() throws ValidationException {
        when(mockContractor.getCountry()).thenReturn(GREAT_BRITAIN);
        String testVat = "no";
        when(mockData.getAnswer()).thenReturn(testVat);
        doThrow(new ValidationException()).when(webValidator).webValidate(anyString());
        assertEquals(GREAT_BRITAIN_ISO + testVat.toUpperCase(), classUnderTest.validatedVATfromAudit(mockData));
    }

    @Test(expected = ValidationException.class)
    public void testValidate_naAnswer() throws ValidationException {
        when(mockContractor.getCountry()).thenReturn(GREAT_BRITAIN);
        String testVat = "n/a";
        when(mockData.getAnswer()).thenReturn(testVat);
        doThrow(new ValidationException()).when(webValidator).webValidate(anyString());
        assertEquals(GREAT_BRITAIN_ISO + testVat.toUpperCase(), classUnderTest.validatedVATfromAudit(mockData));
    }

    @Test
    public void testValidate_demoNumber() throws ValidationException {
        when(mockContractor.getCountry()).thenReturn(GERMANY);
        String testVat = "DE999999999"; // demo VAT
        when(mockData.getAnswer()).thenReturn(testVat);
        assertEquals(testVat, classUnderTest.validatedVATfromAudit(mockData));
    }

}
