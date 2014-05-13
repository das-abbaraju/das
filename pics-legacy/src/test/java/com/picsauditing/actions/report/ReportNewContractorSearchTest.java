package com.picsauditing.actions.report;

import com.picsauditing.PICS.FlagCalculatorFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.flagcalculator.FlagCalculator;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.messaging.MessagePublisherService;
import com.picsauditing.search.SelectAccount;
import org.apache.commons.beanutils.BasicDynaBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class ReportNewContractorSearchTest extends PicsTest {
	ReportNewContractorSearch reportNewContractorSearch;

	@Mock
	private Permissions permissions;
    @Mock
    private ContractorAccountDAO contractorAccountDAO;
	@Mock
	private BasicDynaBean basicDynaBean;
    @Mock
    private ContractorAccount contractorAccount;
    @Mock
    private OperatorAccount operatorAccount;
    @Mock
    private FlagCriteriaContractor flagCriteriaContractor;
    @Mock
    private FlagCriteria flagCriteria;
    @Mock
    private FlagCriteriaOperator flagCriteriaOperator;
    @Mock
    private FlagCalculatorFactory flagCalculatorFactory;
    @Mock
    private FlagCalculator flagCalculator;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.setUp();
		reportNewContractorSearch = new ReportNewContractorSearch();
		autowireEMInjectedDAOs(reportNewContractorSearch);

        Whitebox.setInternalState(reportNewContractorSearch, "permissions", permissions);
        Whitebox.setInternalState(reportNewContractorSearch, "flagCalculatorFactory", flagCalculatorFactory);

		when(translationService.hasKey("global.CompanyName", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(translationService.getText("global.CompanyName", Locale.ENGLISH)).thenReturn("Company Name");
	}

    @Test
    public void testBuildQuery_ifShowPostalIsFalse_dontAddWhereClause() {
        reportNewContractorSearch.getFilter().setShowPostalCode(false);
        when(permissions.getAccountStatus()).thenReturn(AccountStatus.Active);

        reportNewContractorSearch.buildQuery();

        SelectAccount sql = reportNewContractorSearch.sql;
        assertFalse(sql.toString().contains("a.zip = "));
    }

    @Test
    public void testBuildQuery_ifShowPostalIsTrue_addWhereClause() {
        reportNewContractorSearch.getFilter().setShowPostalCode(true);
        reportNewContractorSearch.getFilter().setZip("99999");
        when(permissions.getAccountStatus()).thenReturn(AccountStatus.Active);

        reportNewContractorSearch.buildQuery();

        SelectAccount sql = reportNewContractorSearch.sql;
        assertTrue(sql.toString().contains("a.zip = "));
    }

    @Test
    public void testCalculateOverallFlags_notOperator() throws Exception {
        Whitebox.invokeMethod(reportNewContractorSearch, "calculateOverallFlags");
    }

    @Test
    public void testCalculateOverallFlags_byConIds() throws Exception {
        when(permissions.isOperator()).thenReturn(true);

        Map<Integer, FlagColor> byConID = new HashMap<>();
        byConID.put(12345, FlagColor.Red);
        Whitebox.setInternalState(reportNewContractorSearch, "byConID", byConID);

        Whitebox.invokeMethod(reportNewContractorSearch, "calculateOverallFlags");
    }

    @Test
    public void testCalculateOverallFlags_dataEmpty() throws Exception {
        when(permissions.isOperator()).thenReturn(true);

        Whitebox.setInternalState(reportNewContractorSearch, "data", new ArrayList<>());

        Whitebox.invokeMethod(reportNewContractorSearch, "calculateOverallFlags");
    }

    @Test
    public void testCalculateOverallFlags_NoFlagCriteria() throws Exception {
        when(permissions.isOperator()).thenReturn(true);

        ArrayList<BasicDynaBean> data = new ArrayList<>();
        data.add(basicDynaBean);

        when(basicDynaBean.get("id")).thenReturn(12345);
        Set<Integer> conIDs = new HashSet<>();
        conIDs.add(12345);

        List<ContractorAccount> contractorAccounts = new ArrayList<>();
        contractorAccounts.add(contractorAccount);

        when(contractorAccountDAO.findByContractorIds(conIDs)).thenReturn(contractorAccounts);

        Whitebox.setInternalState(reportNewContractorSearch, "contractorAccountDAO", contractorAccountDAO);
        Whitebox.setInternalState(reportNewContractorSearch, "data", data);

        Whitebox.invokeMethod(reportNewContractorSearch, "calculateOverallFlags");
    }

    @Test
    public void testCalculateOverallFlags() throws Exception {
        when(permissions.isOperator()).thenReturn(true);

        ArrayList<BasicDynaBean> data = new ArrayList<>();
        data.add(basicDynaBean);

        when(basicDynaBean.get("id")).thenReturn(12345);
        when(basicDynaBean.get("opID")).thenReturn(12344);
        Set<Integer> conIDs = new HashSet<>();
        conIDs.add(12345);

        List<ContractorAccount> contractorAccounts = new ArrayList<>();
        contractorAccounts.add(contractorAccount);

        when(contractorAccountDAO.findByContractorIds(conIDs)).thenReturn(contractorAccounts);

        Set<FlagCriteriaContractor> flagCriteriaContractors = new HashSet<>();
        flagCriteriaContractors.add(flagCriteriaContractor);

        when(contractorAccount.getFlagCriteria()).thenReturn(flagCriteriaContractors);

        when(contractorAccount.getId()).thenReturn(12345);
        when(flagCalculatorFactory.flagCalculator(any(ContractorOperator.class), any(MessagePublisherService.class))).thenReturn(flagCalculator);

        Whitebox.setInternalState(reportNewContractorSearch, "contractorAccountDAO", contractorAccountDAO);
        Whitebox.setInternalState(reportNewContractorSearch, "data", data);

        Whitebox.invokeMethod(reportNewContractorSearch, "calculateOverallFlags");
        Mockito.verify(flagCalculator).calculate();
    }

    @Test
    public void testGetWorstFlagColor_NoColors() throws Exception {
        List<com.picsauditing.flagcalculator.FlagData> flagData = null;

        FlagColor flag = Whitebox.invokeMethod(reportNewContractorSearch, "getWorstColor", flagData);
        assertNull(flag);
    }

    @Test
    public void testGetWorstFlagColor_MultipleColors() throws Exception {
        List<com.picsauditing.flagcalculator.FlagData> flagData = new ArrayList<>();
        FlagData flagData1 = new FlagData();
        flagData1.setFlag(FlagColor.Green);
        FlagData flagData2 = new FlagData();
        flagData2.setFlag(FlagColor.Green);
        FlagData flagData3 = new FlagData();
        flagData3.setFlag(FlagColor.Amber);
        FlagData flagData4 = new FlagData();
        flagData4.setFlag(FlagColor.Amber);
        FlagData flagData5 = new FlagData();
        flagData5.setFlag(FlagColor.Red);
        FlagData flagData6 = new FlagData();
        flagData6.setFlag(FlagColor.Red);
        flagData.add(flagData1);
        flagData.add(flagData2);
        flagData.add(flagData3);
        flagData.add(flagData4);
        flagData.add(flagData5);
        flagData.add(flagData6);

        FlagColor flag = Whitebox.invokeMethod(reportNewContractorSearch, "getWorstColor", flagData);
        assertEquals(FlagColor.Red, flag);
    }
}
