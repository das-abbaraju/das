package com.picsauditing.interceptors;

import com.picsauditing.actions.Home;
import com.picsauditing.actions.audits.ContractorAuditController;
import com.picsauditing.actions.users.ProfileEdit;
import com.picsauditing.actions.users.UsersManage;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class MenuContextInterceptorTest {
    private static final String CONTRACTOR_PAGE_METHOD_NAME = "contractorPage";

    private MenuContextInterceptor menuContextInterceptor;

    @Before
    public void setup() {
        menuContextInterceptor = new MenuContextInterceptor();
    }

    @Test
    public void testContractorPage_ContractorActionsSupportPage_NoContractorSet() throws Exception {
        ContractorAuditController contractorPage = Mockito.mock(ContractorAuditController.class);
        when(contractorPage.getContractor()).thenReturn(null);

        Whitebox.invokeMethod(menuContextInterceptor, CONTRACTOR_PAGE_METHOD_NAME, contractorPage);
        verify(contractorPage, times(1)).isShowContractorSubmenu();
    }

    @Test
    public void testContractorPage_ContractorActionsSupportPage_ContractorHasNoId() throws Exception {
        ContractorAuditController contractorPage = Mockito.mock(ContractorAuditController.class);
        when(contractorPage.getContractor()).thenReturn(ContractorAccount.builder().id(0).build());

        assertFalse((Boolean) Whitebox.invokeMethod(menuContextInterceptor, CONTRACTOR_PAGE_METHOD_NAME, contractorPage));
        verify(contractorPage, never()).isShowContractorSubmenu();
    }

    @Test
    public void testContractorPage_ContractorActionsSupportPage() throws Exception {
        ContractorAuditController contractorPage = Mockito.mock(ContractorAuditController.class);
        when(contractorPage.getContractor()).thenReturn(ContractorAccount.builder().id(1).build());

        Whitebox.invokeMethod(menuContextInterceptor, CONTRACTOR_PAGE_METHOD_NAME, contractorPage);
        verify(contractorPage, times(1)).isShowContractorSubmenu();
    }

    @Test
    public void testContractorPage_UsersManage_ContractorSet() throws Exception {
        UsersManage userManagePage = Mockito.mock(UsersManage.class);
        when(userManagePage.getAccount()).thenReturn(ContractorAccount.builder().build());

        assertTrue((Boolean) Whitebox.invokeMethod(menuContextInterceptor, CONTRACTOR_PAGE_METHOD_NAME, userManagePage));
    }

    @Test
    public void testContractorPage_UsersManage_OperatorSet() throws Exception {
        UsersManage userManagePage = Mockito.mock(UsersManage.class);
        when(userManagePage.getAccount()).thenReturn(OperatorAccount.builder().build());

        assertFalse((Boolean) Whitebox.invokeMethod(menuContextInterceptor, CONTRACTOR_PAGE_METHOD_NAME, userManagePage));
    }

    @Test
    public void testContractorPage_UsersManage_AccountNull() throws Exception {
        UsersManage userManagePage = Mockito.mock(UsersManage.class);
        when(userManagePage.getAccount()).thenReturn(null);

        assertFalse((Boolean) Whitebox.invokeMethod(menuContextInterceptor, CONTRACTOR_PAGE_METHOD_NAME, userManagePage));
    }

    @Test
    public void testContractorPage_Home_ContractorSet() throws Exception {
        Home homePage = Mockito.mock(Home.class);
        when(homePage.getAccount()).thenReturn(ContractorAccount.builder().build());

        assertTrue((Boolean) Whitebox.invokeMethod(menuContextInterceptor, CONTRACTOR_PAGE_METHOD_NAME, homePage));
    }

    @Test
    public void testContractorPage_Home_OperatorSet() throws Exception {
        Home homePage = Mockito.mock(Home.class);
        when(homePage.getAccount()).thenReturn(OperatorAccount.builder().build());

        assertFalse((Boolean) Whitebox.invokeMethod(menuContextInterceptor, CONTRACTOR_PAGE_METHOD_NAME, homePage));
    }

    @Test
    public void testContractorPage_Home_AccountNull() throws Exception {
        Home homePage = Mockito.mock(Home.class);
        when(homePage.getAccount()).thenReturn(null);

        assertFalse((Boolean) Whitebox.invokeMethod(menuContextInterceptor, CONTRACTOR_PAGE_METHOD_NAME, homePage));
    }

    @Test
    public void testContractorPage_ProfileEdit() throws Exception {
        ProfileEdit profileEdit = Mockito.mock(ProfileEdit.class);

        when(profileEdit.getAccount()).thenReturn(ContractorAccount.builder().build());

        assertFalse((Boolean) Whitebox.invokeMethod(menuContextInterceptor, CONTRACTOR_PAGE_METHOD_NAME, profileEdit));
    }
}
