package com.picsauditing.service.contractor;

import com.picsauditing.dao.InvoiceFeeCountryDAO;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceFeeCountry;
import org.approvaltests.Approvals;
import org.approvaltests.legacycode.LegacyApprovals;
import org.approvaltests.legacycode.Range;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.MockitoAnnotations.Mock;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UseReporter(DiffReporter.class)
public class PricingTiersBuilderTest {
    @Mock
    private InvoiceFeeCountryDAO invoiceFeeCountryDAO;

    private PricingTiersBuilder pricingTiersBuilder;
    private List<InvoiceFeeCountry> countryFees;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        pricingTiersBuilder = new PricingTiersBuilder();
        Whitebox.setInternalState(pricingTiersBuilder, "invoiceFeeCountryDAO", invoiceFeeCountryDAO);
    }



    @Test
    public void testBuildPricingTiersForCountry() throws Exception {
        buildAllInvoiceFees();
        when(invoiceFeeCountryDAO.findVisibleByCountryAndFeeClassList(any(Country.class), anySetOf(FeeClass.class))).thenReturn(countryFees);

        Country anyCountry = new Country();
        List<FeeClass> applicable = Arrays.asList(FeeClass.DocuGUARD, FeeClass.InsureGUARD, FeeClass.AuditGUARD, FeeClass.EmployeeGUARD);

        List<PricingTier> pricingTiers = pricingTiersBuilder.buildPricingTiersForCountry(anyCountry, applicable, 23);

        verifyPricingTiers(pricingTiers);
    }

    @Test
    public void testFindRawInvoiceFees_Fallback() throws Exception {
        Country unsupportedCountry = new Country("ZE");
        Country unitedStates = new Country("US");
        countryFees = new ArrayList<>();
        countryFees.add(new InvoiceFeeCountry());
        when(invoiceFeeCountryDAO.findVisibleByCountryAndFeeClassList(eq(unsupportedCountry), anySetOf(FeeClass.class)))
                .thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFeeCountryDAO.findVisibleByCountryAndFeeClassList(eq(unitedStates), anySetOf(FeeClass.class))).thenReturn(countryFees);

        List<InvoiceFeeCountry> result = Whitebox.invokeMethod(pricingTiersBuilder, "findRawInvoiceFees",
                unsupportedCountry, FeeClass.getContractorPriceTableFeeTypes());
        verify(invoiceFeeCountryDAO, times(1)).findVisibleByCountryAndFeeClassList(eq(unitedStates), anySetOf(FeeClass.class));
        assertEquals(1, result.size());
    }

    private void verifyPricingTiers(List<PricingTier> pricingTiers) throws Exception {
        String actualResult = "";
        for (PricingTier pricingTier: pricingTiers) {
            actualResult += ("Pricing Tier: " + pricingTier.getLevel());
            actualResult += ("\n");
            for (PricingAmount pricingAmount: pricingTier.getPricingAmounts()) {
                actualResult += ("Pricing Amount: " + pricingAmount.getFeeClass() + " " + pricingAmount.getFeeAmount()
                        + " "  + pricingAmount.isApplies());
                actualResult += ("\n");
            }
        }


        Approvals.verify(actualResult);
    }

    // Admittedly, this test might be a bit heavy handed.  But hey, it's kind of cool.  Feel free to remove this if it causes problems.
    @Test
    public void testBuildPricingTiersForCountry_LockDown() throws Exception {
        buildAllInvoiceFees();
        when(invoiceFeeCountryDAO.findVisibleByCountryAndFeeClassList(any(Country.class), anySetOf(FeeClass.class))).thenReturn(countryFees);

        Integer[] numberOfClientSites = {0, 1, 2, 4, 5, 8, 9, 12, 13, 19, 20, 49, 50, 51};

        Country[] country = {new Country("US")};

        FeeClass[] applicableFeeClass = {FeeClass.DocuGUARD, FeeClass.InsureGUARD, FeeClass.AuditGUARD, FeeClass.EmployeeGUARD, FeeClass.Activation};

        LegacyApprovals.LockDown(this, "buildPricingTiers", country, applicableFeeClass, applicableFeeClass,
                applicableFeeClass, applicableFeeClass, numberOfClientSites);
    }

    public Object buildPricingTiers(Country country, FeeClass applicableFeeClass1, FeeClass applicableFeeClass2,
                                    FeeClass applicableFeeClass3, FeeClass applicableFeeClass4 , Integer numberOfFacilities){
        Set<FeeClass> applicableFeeClasses = new HashSet<>();
        applicableFeeClasses.add(applicableFeeClass1);
        applicableFeeClasses.add(applicableFeeClass2);
        applicableFeeClasses.add(applicableFeeClass3);
        applicableFeeClasses.add(applicableFeeClass4);
        return pricingTiersBuilder.buildPricingTiersForCountry(country, new ArrayList<>(applicableFeeClasses), numberOfFacilities);
    }

    // I wish I had tuples =(
    private static final PricingTierLevel[] OLD_PRICING_TIER_LEVELS = {
            new PricingTierLevel(1, 1),
            new PricingTierLevel(2, 4),
            new PricingTierLevel(5, 8),
            new PricingTierLevel(9, 12),
            new PricingTierLevel(13, 19),
            new PricingTierLevel(20, 49),
            new PricingTierLevel(50, 1000)
    };

    private static final Map<Integer, BigDecimal> DOCUGUARD_PRICING_MAP = new HashMap<>();
    private static final Map<Integer, BigDecimal> INSUREGUARD_PRICING_MAP = new HashMap<>();
    private static final Map<Integer, BigDecimal> AUDITGUARD_PRICING_MAP = new HashMap<>();
    private static final Map<Integer, BigDecimal> EMPLOYEEGUARD_PRICING_MAP = new HashMap<>();
    static {
        DOCUGUARD_PRICING_MAP.put(1, new BigDecimal(199));
        DOCUGUARD_PRICING_MAP.put(2, new BigDecimal(199));
        DOCUGUARD_PRICING_MAP.put(5, new BigDecimal(199));
        DOCUGUARD_PRICING_MAP.put(9, new BigDecimal(199));
        DOCUGUARD_PRICING_MAP.put(13, new BigDecimal(199));
        DOCUGUARD_PRICING_MAP.put(20, new BigDecimal(199));
        DOCUGUARD_PRICING_MAP.put(50, new BigDecimal(199));

        INSUREGUARD_PRICING_MAP.put(1, new BigDecimal(99.00));
        INSUREGUARD_PRICING_MAP.put(2, new BigDecimal(199.00));
        INSUREGUARD_PRICING_MAP.put(5, new BigDecimal(399.00));
        INSUREGUARD_PRICING_MAP.put(9, new BigDecimal(399.00));
        INSUREGUARD_PRICING_MAP.put(13, new BigDecimal(399.00));
        INSUREGUARD_PRICING_MAP.put(20, new BigDecimal(799.00));
        INSUREGUARD_PRICING_MAP.put(50, new BigDecimal(799.00));

        AUDITGUARD_PRICING_MAP.put(1, new BigDecimal(499.00));
        AUDITGUARD_PRICING_MAP.put(2, new BigDecimal(999.00));
        AUDITGUARD_PRICING_MAP.put(5, new BigDecimal(1999.00));
        AUDITGUARD_PRICING_MAP.put(9, new BigDecimal(1999.00));
        AUDITGUARD_PRICING_MAP.put(13, new BigDecimal(1999.00));
        AUDITGUARD_PRICING_MAP.put(20, new BigDecimal(3999.00));
        AUDITGUARD_PRICING_MAP.put(50, new BigDecimal(3999.00));

        EMPLOYEEGUARD_PRICING_MAP.put(1, new BigDecimal(199.00));
        EMPLOYEEGUARD_PRICING_MAP.put(2, new BigDecimal(399.00));
        EMPLOYEEGUARD_PRICING_MAP.put(5, new BigDecimal(799.00));
        EMPLOYEEGUARD_PRICING_MAP.put(9, new BigDecimal(799.00));
        EMPLOYEEGUARD_PRICING_MAP.put(13, new BigDecimal(799.00));
        EMPLOYEEGUARD_PRICING_MAP.put(20, new BigDecimal(1599.00));
        EMPLOYEEGUARD_PRICING_MAP.put(50, new BigDecimal(1599.00));
    }

    private List<InvoiceFeeCountry> buildAllInvoiceFees() {
        countryFees = new ArrayList<>();
        for (FeeClass feeClass : FeeClass.getContractorPriceTableFeeTypes()) {
            for (PricingTierLevel pricingTierLevel : OLD_PRICING_TIER_LEVELS) {
                InvoiceFeeCountry invoiceFeeCountry = new InvoiceFeeCountry();
                InvoiceFee invoiceFee = new InvoiceFee();
                invoiceFee.setFeeClass(feeClass);
                invoiceFee.setMinFacilities(pricingTierLevel.getMinFacilities());
                invoiceFee.setMaxFacilities(pricingTierLevel.getMaxFacilities());
                invoiceFeeCountry.setInvoiceFee(invoiceFee);

                if (feeClass == FeeClass.DocuGUARD) {
                    invoiceFeeCountry.setAmount(DOCUGUARD_PRICING_MAP.get(pricingTierLevel.getMinFacilities()));
                } else if (feeClass == FeeClass.InsureGUARD) {
                    invoiceFeeCountry.setAmount(INSUREGUARD_PRICING_MAP.get(pricingTierLevel.getMinFacilities()));
                } else if (feeClass == FeeClass.AuditGUARD) {
                    invoiceFeeCountry.setAmount(AUDITGUARD_PRICING_MAP.get(pricingTierLevel.getMinFacilities()));
                } else if (feeClass == FeeClass.EmployeeGUARD) {
                    invoiceFeeCountry.setAmount(EMPLOYEEGUARD_PRICING_MAP.get(pricingTierLevel.getMinFacilities()));
                }

                countryFees.add(invoiceFeeCountry);
            }
        }
        return countryFees;
    }

    private static class PricingTierLevel {
        private int minFacilities;
        private int maxFacilities;

        public PricingTierLevel(int minFacilities, int maxFacilities) {
            this.minFacilities = minFacilities;
            this.maxFacilities = maxFacilities;
        }

        private int getMinFacilities() {
            return minFacilities;
        }

        private void setMinFacilities(int minFacilities) {
            this.minFacilities = minFacilities;
        }

        private int getMaxFacilities() {
            return maxFacilities;
        }

        private void setMaxFacilities(int maxFacilities) {
            this.maxFacilities = maxFacilities;
        }
    }
}
