package com.picsauditing.service.contractor;

import com.picsauditing.dao.contractor.ContractorCertificateDao;
import com.picsauditing.model.contractor.ContractorCertificate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.mockito.MockitoAnnotations.Mock;

public class ContractorCertificateServiceTest {
    @Mock
    private ContractorCertificateDao contractorCertificateDao;

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
    public void testIssueCertificate() throws Exception {
        ContractorCertificate contractorCertificate = ContractorCertificate.builder().build();
        contractorCertificateService.issueCertificate(contractorCertificate);
    }
}
