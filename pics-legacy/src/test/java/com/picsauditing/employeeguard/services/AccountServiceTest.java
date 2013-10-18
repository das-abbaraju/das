package com.picsauditing.employeeguard.services;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.jpa.entities.Account;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

public class AccountServiceTest {

    private AccountService accountService;

    @Mock
    private AccountDAO accountDAO;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        accountService = new AccountService();

        Whitebox.setInternalState(accountService, "accountDAO", accountDAO);
    }

    @Test
    public void testGetAccountById() {
        when(accountDAO.find(23)).thenReturn(buildAccount(45, "The Account", "Operator"));

        AccountModel result = accountService.getAccountById(23);

        assertEquals(45, result.getId());
    }

    public Account buildAccount(int id, String name, String type) {
        Account account = new Account();
        account.setId(id);
        account.setName(name);
        account.setType(type);
        return account;
    }
}
