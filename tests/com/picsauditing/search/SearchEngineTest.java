package com.picsauditing.search;

import com.picsauditing.access.Permissions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class SearchEngineTest {
    private SearchEngine searchEngine;

    @Mock
    private Permissions permissions;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        searchEngine = new SearchEngine(permissions);
    }

    @Test
    public void testBuildTerm_EmptyTermReturnsEmptyCollection() throws Exception {
        assertTrue(searchEngine.buildTerm("", true, true) != null);
        assertTrue(searchEngine.buildTerm("", true, true).size() == 0);
        assertTrue(searchEngine.buildTerm(null, true, true) != null);
        assertTrue(searchEngine.buildTerm(null, true, true).size() == 0);
    }

    @Test
    public void testBuildTerm_LatinCharsNoSortNoDeDupe() throws Exception {
        String latin = "This is regular text";
        List<String> terms = stringToListOfWords(latin);

        List<String> searchTerms = searchEngine.buildTerm(latin, false, false);

        for(String term : terms) {
            assertTrue(searchTerms.contains(term.toUpperCase()));
        }
    }

    @Test
    public void testBuildTerm_NonLatinCharsNoSortNoDeDupe() throws Exception {
        String nonlatin = "曲忠信éç Fun With Languages Województwo łódzkie";
        List<String> terms = stringToListOfWords(nonlatin);

        List<String> searchTerms = searchEngine.buildTerm(nonlatin, false, false);

        for(String term : terms) {
            assertTrue(searchTerms.contains(term.toUpperCase()));
        }
    }

    @Test
    public void testBuildTerm_LatinCharsRemovesNonAlphaNumeric() throws Exception {
        String search = "^This is &regular t*ext 123";
        // existing behavior is to split on non-word chars
        String latin = "This is regular t ext 123";
        List<String> terms = stringToListOfWords(latin);

        List<String> searchTerms = searchEngine.buildTerm(search, false, false);

        for(String term : terms) {
            assertTrue(searchTerms.contains(term.toUpperCase()));
        }
    }

    @Test
    public void testBuildTerm_LatinCharsNoSortDeDupe() throws Exception {
        String latin = "This is regular regular text";
        String latinDedupe = "This is regular text";
        List<String> terms = stringToListOfWords(latinDedupe);

        List<String> searchTerms = searchEngine.buildTerm(latin, false, true);

        for(String term : terms) {
            assertTrue(searchTerms.contains(term.toUpperCase()));
        }
    }

    @Test
    public void testBuildTerm_LatinCharsSortDeDupe() throws Exception {
        String latin = "This is regular text";
        List<String> terms = stringToSortedListOfCapitalizedWords(latin);

        List<String> searchTerms = searchEngine.buildTerm(latin, false, true);

        assertArrayEquals(terms.toArray(), searchTerms.toArray());
    }

    private List<String> stringToSortedListOfCapitalizedWords(String latin) {
        List<String> terms = new ArrayList();
        for (String term : latin.split(" ")) {
            terms.add(term.toUpperCase());
        }
        Collections.sort(terms);
        return terms;
    }

    private List<String> stringToListOfWords(String latin) {
        List<String> terms = new ArrayList();
        for (String term : latin.split(" ")) {
            terms.add(term);
        }
        return terms;
    }
}
