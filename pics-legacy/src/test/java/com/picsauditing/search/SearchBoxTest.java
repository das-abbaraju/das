package com.picsauditing.search;

import com.picsauditing.PicsTest;
import com.picsauditing.access.Permissions;
import org.apache.commons.beanutils.BasicDynaBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class SearchBoxTest extends PicsTest {
    private SearchBox searchBox;

    @Mock
    private SearchEngine searchEngine;
    @Mock
    private Database db;
    @Mock
    private Permissions permissions;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        searchBox = new SearchBox();

        Whitebox.setInternalState(searchBox, "db", db);
        Whitebox.setInternalState(searchBox, "permissions", permissions);
    }

    @Test
    public void testButtonAutocomplete() throws Exception {
        String searchTerm = "term1 term2";
        Whitebox.setInternalState(searchBox, "searchTerm", searchTerm);

        List<String> terms = Collections.unmodifiableList(Arrays.asList("term1", "term2"));
        when(searchEngine.buildTerm(searchTerm, true, true)).thenReturn(terms);

        String query1 = "test1";
        String query2 = "test2";
        when(searchEngine.buildQuery(permissions, terms, "i1.indexType NOT IN ('T','G')", 0, 10,
                false, false)).thenReturn(query1);
        when(db.select(query1, true)).thenReturn(new ArrayList<BasicDynaBean>());
        when(db.select(query2, true)).thenReturn(new ArrayList<BasicDynaBean>());
        when(db.getAllRows()).thenReturn(100);
        when(searchEngine.buildAccountSearch(permissions, terms)).thenReturn(query2);

        Whitebox.invokeMethod(searchBox, "buttonAutocomplete", searchEngine);
        verify(db, times(2)).getAllRows();

    }
}
