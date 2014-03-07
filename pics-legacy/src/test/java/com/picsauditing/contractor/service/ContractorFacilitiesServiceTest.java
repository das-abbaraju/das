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
    private FacilityChanger facilityChanger;
    @Mock
    private ContractorAccount contractor;
    @Mock
    private OperatorAccount operator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        contractorFacilitiesService = new ContractorFacilitiesService();

        when(facilityChanger.getContractor()).thenReturn(contractor);
        when(facilityChanger.getOperator()).thenReturn(operator);
    }

    @Test
    public void testAddContractorFacilityForQualifiedContractor_suncorSingleOperatorContractor() throws Exception {
        contractorFacilitiesService.addContractorFacilityForQualifiedContractor(contractor, operator, facilityChanger);
        verify(facilityChanger, times(1)).add();
    }

}
