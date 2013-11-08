package com.picsauditing.dao.jdbc;

import com.picsauditing.PICS.DBBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TranslationsDAOTest {
    private static final String TEST_KEY = "Test.Key";
    private static final String TEST_LOCALE = "en";
    private static final String TEST_PAGENAME = "TestPage";
    private static final String TEST_ENVIRONMENWT = "TestEnvironment";
    private static final int TEST_KEY_ID = 123;
    private static final int TEST_LOCALE_ID = 321;

    private TranslationsDAO translationsDAO;
    private Map<String, Object> ids;

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        translationsDAO = new TranslationsDAO();

        Whitebox.setInternalState(translationsDAO, "namedParameterJdbcTemplate", namedParameterJdbcTemplate);
        ids = new HashMap();
        ids.put("keyID", TEST_KEY_ID);
        ids.put("localeID", TEST_LOCALE_ID);
    }

    @Test(expected = RuntimeException.class)
    public void testUpdateTranslationLastUsed_BadQueryForIdsThrows() throws Exception {
        doThrow(DataAccessException.class).when(namedParameterJdbcTemplate.queryForMap(anyString(), any(MapSqlParameterSource.class)));

        translationsDAO.updateTranslationLastUsed(TEST_KEY, TEST_LOCALE, TEST_PAGENAME, TEST_ENVIRONMENWT);
    }

    @Test
    public void testUpdateTranslationLastUsed_() throws Exception {
        when(namedParameterJdbcTemplate.queryForMap(anyString(), any(MapSqlParameterSource.class))).thenReturn(ids);

        translationsDAO.updateTranslationLastUsed(TEST_KEY, TEST_LOCALE, TEST_PAGENAME, TEST_ENVIRONMENWT);

        verify(namedParameterJdbcTemplate).update(anyString(), any(Map.class));
    }
}
