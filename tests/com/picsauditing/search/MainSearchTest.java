package com.picsauditing.search;

import com.picsauditing.PicsActionTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.net.URLEncoder;

import static org.junit.Assert.assertTrue;

public class MainSearchTest extends PicsActionTest {
    private MainSearch mainSearch;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        mainSearch = new MainSearch();
        super.setUp(mainSearch);
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
