package com.picsauditing.service.contractor;

import com.picsauditing.dao.contractor.ContractorCertificateDao;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.model.contractor.CertificateType;
import com.picsauditing.model.contractor.ContractorCertificate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.Mock;

public class ContractorCertificateServiceTest {
    @Mock
    private ContractorCertificateDao contractorCertificateDao;
    @Mock
    private ContractorAccount contractor;

    private ContractorCertificateService contractorCertificateService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        contractorCertificateService = new ContractorCertificateService();

        Whitebox.setInternalState(contractorCertificateService, "contractorCertificateDao", contractorCertificateDao);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetSsipCertificate_cdmScopesShouldBeFormattedCorrectly() throws Exception {
        ContractorCertificate contractorCertificate = ContractorCertificate.builder()
                .cdmScope("PrincipalContractor,CDMCoordinator,Designer,Contractor")
                .build();
        when(contractorCertificateDao.findMostRecentByContractor(contractor, CertificateType.SSIP)).thenReturn(contractorCertificate);

        ContractorCertificate ssipCertificate = contractorCertificateService.getSsipCertificate(contractor);

        assertEquals("CDM Scope: Principal Contractor / CDM Coordinator / Designer / Contractor", ssipCertificate.getFormattedCdmScope());
    }

    @Test
    public void testGetSsipCertificate_cdmScopesShouldBeFormattedCorrectly_noCdmScopes() throws Exception {
        ContractorCertificate contractorCertificate = ContractorCertificate.builder()
                .cdmScope(null)
                .build();
        when(contractorCertificateDao.findMostRecentByContractor(contractor, CertificateType.SSIP)).thenReturn(contractorCertificate);

        ContractorCertificate ssipCertificate = contractorCertificateService.getSsipCertificate(contractor);

        assertEquals("", ssipCertificate.getFormattedCdmScope());
    }


}
