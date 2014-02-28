package com.picsauditing.search;

import com.picsauditing.PicsActionTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.jpa.entities.AbstractIndexableTable;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.apache.commons.beanutils.BasicDynaBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainSearchTest extends PicsActionTest {
    private MainSearch mainSearch;

    @Mock
    private AccountDAO accountDAO;
    @Mock
    private SearchEngine searchEngine;
    @Mock
    private Permissions permissions;
    @Mock
    private Database db;
    @Mock
    private BasicDynaBean bdb1;
    @Mock
    private BasicDynaBean bdb2;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        mainSearch = new MainSearch();
        Whitebox.setInternalState(mainSearch, "permissions", permissions);
        Whitebox.setInternalState(mainSearch, "db", db);
        Whitebox.setInternalState(mainSearch, "accountDAO", accountDAO);
        super.setUp(mainSearch);
    }

    @Test
    public void testExecute_AllResults() throws Exception {
        String searchTerm = "term1 term2";
        String query1 = "query1";
        List<String> terms = Collections.unmodifiableList(Arrays.asList("term1", "term2"));

        when(searchEngine.buildTerm(searchTerm, true, true)).thenReturn(terms);
        when(searchEngine.buildAccountSearch(permissions, terms)).thenReturn(query1);
        when(db.getAllRows()).thenReturn(100);

        Whitebox.setInternalState(mainSearch, "searchEngine", searchEngine);

        mainSearch.setButton("search");
        mainSearch.setSearchTerm(searchTerm);
        mainSearch.execute();
        verify(db).getAllRows();
    }

    @Test
    public void testExecute_duplicateResults() throws Exception {
        String searchTerm = "term1 term2";
        String query1 = "query1";
        List<String> terms = new ArrayList<>();
        terms.add("term1");
        terms.add("term2");

        when(searchEngine.buildTerm(searchTerm, true, true)).thenReturn(terms);
        when(searchEngine.buildAccountSearch((Permissions)any(), (List<String>)any())).thenReturn(query1);
        ArrayList<BasicDynaBean> queryList = new ArrayList<>();
        queryList.add(bdb1);
        queryList.add(bdb2);
        when(db.getAllRows()).thenReturn(100);
        when(db.selectReadOnly(query1, true)).thenReturn(queryList);
        when(bdb1.get("indexType")).thenReturn("CO");
        when(bdb1.get("foreignKey")).thenReturn("1100");
        when(bdb2.get("indexType")).thenReturn("CO");
        when(bdb2.get("foreignKey")).thenReturn("1100");
        when(bdb2.get("foreignKey")).thenReturn("1100");
        List<Account> accounts = new ArrayList<>();
        OperatorAccount operatorAccount = new OperatorAccount();
        accounts.add(operatorAccount);
        when(accountDAO.findWhere((Class)any(), anyString(), anyInt())).thenReturn(accounts);

        Whitebox.setInternalState(mainSearch, "searchEngine", searchEngine);

        mainSearch.setButton("search");
        mainSearch.setSearchTerm(searchTerm);
        mainSearch.execute();
        verify(db).getAllRows();
        assertEquals(mainSearch.totalRows,1);
    }

    @Test
    public void testPrepareLatin() throws Exception {
        String[] basicAscii = {URLEncoder.encode("This is regular text", "UTF-8")};
        parameters.put("q", basicAscii);

        mainSearch.prepare();
        String searchTerm = mainSearch.getSearchTerm();

        assertTrue(basicAscii[0].equals(searchTerm));
    }

    @Test
    public void testPrepareInternational() throws Exception {
        String[] internationalChars = {URLEncoder.encode("曲忠信éç Fun With Languages Województwo łódzkie", "UTF-8")};
        parameters.put("q", internationalChars);

        mainSearch.prepare();
        String searchTerm = mainSearch.getSearchTerm();

        assertTrue(internationalChars[0].equals(searchTerm));
    }

}
