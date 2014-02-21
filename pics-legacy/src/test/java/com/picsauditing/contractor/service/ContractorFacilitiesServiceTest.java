package com.picsauditing.contractor.service;

import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.service.contractor.TopLevelOperatorFinder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ContractorFacilitiesServiceTest {
    private ContractorFacilitiesService contractorFacilitiesService;

    @Mock
    private TopLevelOperatorFinder topLevelOperatorFinder;
    @Mock
    private FacilityChanger facilityChanger;
    @Mock
    private ContractorAccount contractor;
    @Mock
    private OperatorAccount operator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        contractorFacilitiesService = new ContractorFacilitiesService();

        Whitebox.setInternalState(contractorFacilitiesService, "topLevelOperatorFinder", topLevelOperatorFinder);
        when(facilityChanger.getContractor()).thenReturn(contractor);
        when(facilityChanger.getOperator()).thenReturn(operator);
    }

    @Test
    public void testAddContractorFacilityForQualifiedContractor_suncorSingleOperatorContractor() throws Exception {
        when(contractor.getLogoForSingleOperatorContractor()).thenReturn(null);
        OperatorAccount suncor = mock(OperatorAccount.class);
        when(topLevelOperatorFinder.findLogoForSingleOperatorContractor(contractor)).thenReturn(suncor);

        contractorFacilitiesService.addContractorFacilityForQualifiedContractor(contractor, operator, facilityChanger);

        verify(topLevelOperatorFinder).findLogoForSingleOperatorContractor(contractor);
        verify(contractor).setLogoForSingleOperatorContractor(suncor);
    }

    @Test
    public void testAddContractorFacilityForQualifiedContractor_notSingleOperatorContractor() throws Exception {
        when(contractor.getLogoForSingleOperatorContractor()).thenReturn(new OperatorAccount());

        contractorFacilitiesService.addContractorFacilityForQualifiedContractor(contractor, operator, facilityChanger);

        verify(topLevelOperatorFinder, never()).findLogoForSingleOperatorContractor(contractor);
        verify(contractor, never()).setLogoForSingleOperatorContractor(any(OperatorAccount.class));
    }

}
