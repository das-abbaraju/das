package com.picsauditing.actions.forms;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ManageResourcesTest extends PicsActionTest {
    private static final int OPERATOR_ID = 456;
    private static final int CORP_FACILITY_ID = 789;
    private static final int PARENT_OPERATOR_ID = 987;
    private ManageResources manageResources;
    private List<Facility> corporateFacilities;
    private List<OperatorAccount> parentOperators;

    @Mock
    protected com.picsauditing.dao.OperatorAccountDAO operatorDao;
    @Mock
    private com.picsauditing.dao.OperatorFormDAO operatorFormDAO;
    @Mock
    private com.picsauditing.dao.AccountDAO accountDAO;
    @Mock
    protected BasicDAO dao;
    @Mock
    private OperatorAccount operatorAccount;
    @Mock
    private Facility facility;
    @Mock
    private OperatorAccount corporateFacility;
    @Mock
    private OperatorAccount parentOperator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        manageResources = new ManageResources();
        super.setUp(manageResources);
        PicsTestUtil.autowireDAOsFromDeclaredMocks(manageResources, this);
        corporateFacilities = new ArrayList<>();
        parentOperators = new ArrayList<>();

        Whitebox.setInternalState(manageResources, "operator", operatorAccount);

        when(operatorAccount.getId()).thenReturn(OPERATOR_ID);
        when(operatorAccount.getCorporateFacilities()).thenReturn(corporateFacilities);
        when(corporateFacility.getId()).thenReturn(CORP_FACILITY_ID);
        when(facility.getCorporate()).thenReturn(corporateFacility);
        corporateFacilities.add(facility);

        when(parentOperator.getId()).thenReturn(PARENT_OPERATOR_ID);
        when(operatorAccount.getParentOperators()).thenReturn(parentOperators);
        parentOperators.add(parentOperator);
    }

    // covers PICS-10328
    @Test
    public void testFacilityIdsToCheck_TestCorpFaciltyAddsCorpId() throws Exception {
        when(permissions.isAdmin()).thenReturn(false);

        List<Integer> ids = Whitebox.invokeMethod(manageResources, "facilityIdsToCheck");

        assertTrue(ids.contains(CORP_FACILITY_ID));
        assertTrue(ids.contains(OPERATOR_ID));
    }

    @Test
    public void testFacilityIdsToCheck_ifUserIsAdminPicsIdIsAdded() throws Exception {
        when(permissions.isAdmin()).thenReturn(true);

        List<Integer> ids = Whitebox.invokeMethod(manageResources, "facilityIdsToCheck");

        assertTrue(ids.contains(Account.PicsID));
        assertTrue(ids.contains(OPERATOR_ID));
    }

    @Test
    public void testFacilityIdsToCheck_ParentOperatorAddedForAdmin() throws Exception {
        when(permissions.isAdmin()).thenReturn(true);

        List<Integer> ids = Whitebox.invokeMethod(manageResources, "facilityIdsToCheck");

        assertTrue(ids.contains(PARENT_OPERATOR_ID));
        assertTrue(ids.contains(OPERATOR_ID));
    }


    @Test
    public void testFacilityIdsToCheck_ParentOperatorAddedForAdminEvenIfPicsCorp() throws Exception {
        Integer picsCorporateId = randomPicsCorpId();
        when(parentOperator.getId()).thenReturn(picsCorporateId);
        when(permissions.isAdmin()).thenReturn(true);

        List<Integer> ids = Whitebox.invokeMethod(manageResources, "facilityIdsToCheck");

        assertTrue(ids.contains(picsCorporateId));
        assertTrue(ids.contains(OPERATOR_ID));
    }

    @Test
    public void testFacilityIdsToCheck_ParentOperatorNotAddedForNonAdminIfPicsCorp() throws Exception {
        Integer picsCorporateId = randomPicsCorpId();
        when(parentOperator.getId()).thenReturn(picsCorporateId);
        when(permissions.isAdmin()).thenReturn(false);

        List<Integer> ids = Whitebox.invokeMethod(manageResources, "facilityIdsToCheck");

        assertFalse(ids.contains(picsCorporateId));
        assertTrue(ids.contains(OPERATOR_ID));
    }

    private Integer randomPicsCorpId() {
        return Account.PICS_CORPORATE.get((int) (Math.random() * (Account.PICS_CORPORATE.size() + 1)));
    }
}
