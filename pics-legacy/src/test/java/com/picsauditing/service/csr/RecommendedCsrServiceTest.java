package com.picsauditing.service.csr;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

public class RecommendedCsrServiceTest {
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

        contractorList = new ArrayList<>();
        for (Integer id : CONTRACTOR_IDS_LIST) {
            ContractorAccount contractor = mock(ContractorAccount.class);
            when(contractor.getId()).thenReturn(id);
            when(contractor.getRecommendedCsr()).thenReturn(recommendedCsr);
            when(contractor.getCurrentCsr()).thenReturn(currentCsr);
            contractorList.add(contractor);
            when(contractorAccountDAO.find(id)).thenReturn(contractor);
        }
        when(contractorAccountDAO.rejectRecommendedAssignmentForList(Strings.implode(CONTRACTOR_IDS_LIST, ","))).thenReturn(CONTRACTOR_IDS_LIST.size());

        PicsTestUtil.autowireDAOsFromDeclaredMocks(recommendedCsrService, this);
    }

    @Test
    public void testAcceptRecommendedCsrs_ProxiesLogicToDao() throws Exception {
        recommendedCsrService.acceptRecommendedCsrs(Strings.implode(CONTRACTOR_IDS_LIST, ","), User.SYSTEM);

            verify(contractorAccountDAO).expireCurrentCsrForContractors(StringUtils.join(CONTRACTOR_IDS_LIST, ","), 1);
            verify(contractorAccountDAO).acceptRecommendedCsrForList(StringUtils.join(CONTRACTOR_IDS_LIST, ","), 1);
    }

    @Test
    public void testAcceptRecommendedCsrs_ReturnsNumberAccepted() throws Exception {
        recommendedCsrService.acceptRecommendedCsrs(Strings.implode(CONTRACTOR_IDS_LIST, ","), User.SYSTEM);

        assertThat(CONTRACTOR_IDS_LIST.size(), is(equalTo(contractorList.size())));
    }

    @Test
    public void testRejectRecommendedCsrs_ProxiesLogicToDao() throws Exception {
        recommendedCsrService.rejectRecommendedCsrs(Strings.implode(CONTRACTOR_IDS_LIST, ","));

        verify(contractorAccountDAO).rejectRecommendedAssignmentForList(Strings.implode(CONTRACTOR_IDS_LIST, ","));
    }

    @Test
    public void testRejectRecommendedCsrs_ReturnsNumberRejected() throws Exception {
        int numRejected = recommendedCsrService.rejectRecommendedCsrs(Strings.implode(CONTRACTOR_IDS_LIST, ","));

        assertThat(numRejected, is(equalTo(contractorList.size())));
    }

}
