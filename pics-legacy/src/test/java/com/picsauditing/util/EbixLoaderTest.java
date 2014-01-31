package com.picsauditing.util;

import com.picsauditing.EntityFactory;
import com.picsauditing.actions.audits.ConAuditMaintain;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.*;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class EbixLoaderTest {
    private EbixLoader loader;

    @Mock
    private FTPClient ftp;
    @Mock
    private AppPropertyDAO appPropDao;
    @Mock
    private ContractorAuditDAO contractorAuditDAO;
    @Mock
    private ContractorAccountDAO contractorAccountDAO;
    @Mock
    private ContractorAccount contractor;

    private InputStream retrieveFileStream;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        loader = new EbixLoader();
        Whitebox.setInternalState(loader, "contractorAuditDAO", contractorAuditDAO);
        Whitebox.setInternalState(loader, "contractorAccountDAO", contractorAccountDAO);
    }

    @Test
    public void testProcessFile_DeactivatedContractor() throws Exception {
        String filename = "deactive";
        String data="1,Y";

        retrieveFileStream = new ByteArrayInputStream(data.getBytes("UTF-8"));

        when(ftp.retrieveFileStream(filename)).thenReturn(retrieveFileStream);
        when(contractor.getStatus()).thenReturn(AccountStatus.Deactivated);
        when(contractorAccountDAO.find(anyInt())).thenReturn(contractor);
        when(contractor.getId()).thenReturn(1);

        Whitebox.invokeMethod(loader, "processFile", ftp, filename);
        verify(contractorAuditDAO, times(0)).findWhere(900, "auditType.id = "
                + AuditType.HUNTSMAN_EBIX + " and contractorAccount.id = 1", "");

    }

    @Test
    public void testProcessFile_ActiveContractor() throws Exception {
        String filename = "activate";
        String data="1,Y";

        retrieveFileStream = new ByteArrayInputStream(data.getBytes("UTF-8"));
        ContractorAudit audit = mock(ContractorAudit.class);
        ContractorAuditOperator cao = mock (ContractorAuditOperator.class);

        List<ContractorAudit> audits = new ArrayList<>();
        audits.add(audit);
        List<ContractorAuditOperator> operators = new ArrayList<>();
        operators.add(cao);

        when(contractorAuditDAO.findWhere(900, "auditType.id = "
                + AuditType.HUNTSMAN_EBIX + " and contractorAccount.id = 1", "")).thenReturn(audits);
        when(audit.getOperators()).thenReturn(operators);
        when(cao.getStatus()).thenReturn(AuditStatus.Pending);

        when(ftp.retrieveFileStream(filename)).thenReturn(retrieveFileStream);
        when(contractor.getStatus()).thenReturn(AccountStatus.Active);
        when(contractorAccountDAO.find(anyInt())).thenReturn(contractor);
        when(contractor.getId()).thenReturn(1);

        Whitebox.invokeMethod(loader, "processFile", ftp, filename);
        verify(contractorAuditDAO, times(1)).save(audit);
    }
}
