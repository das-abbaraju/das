package com.picsauditing.search;

import com.picsauditing.PicsActionTest;
import com.picsauditing.access.Permissions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainSearchTest extends PicsActionTest {
    private MainSearch mainSearch;

    @Mock
    private SearchEngine searchEngine;
    @Mock
    private Permissions permissions;
    @Mock
    private Database db;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        mainSearch = new MainSearch();
        Whitebox.setInternalState(mainSearch, "permissions", permissions);
        Whitebox.setInternalState(mainSearch, "db", db);
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
        verify(db, times(2)).getAllRows();
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
