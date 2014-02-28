package com.picsauditing.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.List;
import java.util.ArrayList;

public class RequestNewContractorServiceTest extends PicsTest {
    @Mock
    private com.picsauditing.dao.ContractorAccountDAO contractorAccountDAO;
    @Mock
    private com.picsauditing.dao.ContractorOperatorDAO contractorOperatorDAO;
    @Mock
    protected com.picsauditing.dao.UserDAO userDAO;
    @Mock
    private com.picsauditing.model.user.UserManagementService userManagementService;
    @Mock
    private com.picsauditing.dao.ContractorTagDAO contractorTagDAO;
    @Mock
    private Permissions permissions;


    private RequestNewContractorService service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        service = new RequestNewContractorService();
        service.setPermissions(permissions);

        Whitebox.setInternalState(service, "contractorAccountDAO", contractorAccountDAO);
        Whitebox.setInternalState(service, "contractorOperatorDAO", contractorOperatorDAO);
        Whitebox.setInternalState(service, "userDAO", userDAO);
        Whitebox.setInternalState(service, "userManagementService", userManagementService);
        Whitebox.setInternalState(service, "contractorTagDAO", contractorTagDAO);
    }

    @Test
    public void testSaveRelationship() throws Exception {
        ContractorAccount contractor = EntityFactory.makeContractor();
        OperatorAccount operator = EntityFactory.makeOperator();
        ContractorOperator relationship = new ContractorOperator();
        relationship.setOperatorAccount(operator);

        service.saveRelationship(relationship);
        assertEquals(contractor, relationship.getContractorAccount());
    }

    @Test
    public void testSavePrimaryContact() throws Exception {
        ContractorAccount contractor = EntityFactory.makeContractor();
        User primaryContact = EntityFactory.makeUser();
        primaryContact.setId(0);

        service.savePrimaryContact(primaryContact);
        assertEquals(contractor, primaryContact.getAccount());
    }

    @Test
    public void testSaveRequestingContractor() throws Exception {
        ContractorAccount contractor = EntityFactory.makeContractor();
        contractor.setId(0);
        OperatorAccount operator = EntityFactory.makeOperator();
        when(contractorAccountDAO.save(contractor)).thenReturn(contractor);
        service.saveRequestedContractor(contractor);
        assertEquals(operator, contractor.getRequestedBy());
        assertTrue(contractor.getRegistrationHash() != null);
        verify(contractorAccountDAO, times(1)).save(any(BaseTable.class));
    }

    @Test
    public void testAddTagsToContractor() throws Exception {
        List<OperatorTag> opTags = new ArrayList<>();
        OperatorTag opTag = new OperatorTag();
        opTags.add(opTag);

        ContractorAccount contractor = EntityFactory.makeContractor();

        service.addTagsToContractor(contractor, opTags);
        assertEquals(1, contractor.getOperatorTags().size());
        assertEquals(opTag, contractor.getOperatorTags().get(0).getTag());
    }
}
