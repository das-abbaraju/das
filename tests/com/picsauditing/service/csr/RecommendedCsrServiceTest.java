package com.picsauditing.service.csr;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

public class RecommendedCsrServiceTest {
    private static final String CONTRACTOR_IDS = "1,2,3,4,5";
    private static final List<Integer> CONTRACTOR_IDS_LIST = new ArrayList<Integer>() {{
        add(1); add(2); add(3); add(4); add(5);
    }};

    private RecommendedCsrService recommendedCsrService;
    private List<ContractorAccount> contractorList;

    @Mock
    private ContractorAccountDAO contractorAccountDAO;
    @Mock
    private User recommendedCsr;
    @Mock
    private User currentCsr;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        recommendedCsrService = new RecommendedCsrService();

        contractorList = new ArrayList<ContractorAccount>();
        for (int i = 1; i <= 5; i++) {
            ContractorAccount contractor = mock(ContractorAccount.class);
            when(contractor.getRecommendedCsr()).thenReturn(recommendedCsr);
            when(contractor.getCurrentCsr()).thenReturn(currentCsr);
            contractorList.add(contractor);
        }
        when(contractorAccountDAO.findByIDs(ContractorAccount.class, CONTRACTOR_IDS_LIST)).thenReturn(contractorList);

        PicsTestUtil.autowireDAOsFromDeclaredMocks(recommendedCsrService, this);
    }

    @Test
    public void testAcceptRecommendedCsrs() throws Exception {
        int numAccepted = recommendedCsrService.acceptRecommendedCsrs(CONTRACTOR_IDS, User.SYSTEM);

        for (ContractorAccount contractor : contractorList) {
            verify(contractor).setCurrentCsr(recommendedCsr, User.SYSTEM);
        }
        commonVerifyForAcceptReject(numAccepted);
    }

    @Test
    public void testRejectRecommendedCsrs() throws Exception {
        int numAccepted = recommendedCsrService.rejectRecommendedCsrs(CONTRACTOR_IDS);

        for (ContractorAccount contractor : contractorList) {
            verify(contractor).setRecommendedCsr(null);
        }
        commonVerifyForAcceptReject(numAccepted);

    }

    private void commonVerifyForAcceptReject(int numAccepted) {
        verify(contractorAccountDAO, times(5)).save(Mockito.any(ContractorAccount.class));
        assertThat(numAccepted, is(equalTo(contractorList.size())));
    }
}
