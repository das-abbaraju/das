package com.picsauditing.dao.jdbc;

import com.picsauditing.PICS.DBBean;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class JdbcAppPropertyProviderTest {
    private static final String TEST_APP_PROPERTY_NAME = "TestAppProperty";
    private static final String TEST_APP_PROPERTY_VALUE = "TestAppPropertyValue";
    private static final String cacheName = "app_properties";

    private JdbcAppPropertyProvider jdbcAppPropertyProvider;

    @Mock
    private CacheManager cacheManager;
    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        jdbcAppPropertyProvider = new JdbcAppPropertyProvider();

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString("value")).thenReturn(TEST_APP_PROPERTY_VALUE);
        when(resultSet.getString("value")).thenReturn(TEST_APP_PROPERTY_VALUE);
        // when(resultSet.getDate("ticklerDate")).thenReturn(null);

        Whitebox.setInternalState(DBBean.class, "staticDataSource", dataSource);
        Whitebox.setInternalState(jdbcAppPropertyProvider, "cacheManager", cacheManager);
    }

    @After
    public void tearDown() throws Exception {
        Whitebox.setInternalState(DBBean.class, "staticDataSource", (DataSource) null);

        CacheManager.getInstance().getCache(cacheName).removeAll();
    }

    @Test
    public void testFindAppProperty_ReturnedFromCache_NeverCallsDatabase() throws Exception {
        CacheManager.getInstance().getCache(cacheName).removeAll();
        // set this to null so we use a real cache. ehcache.Cache is not mockable b/c of final methods
        Whitebox.setInternalState(jdbcAppPropertyProvider, "cacheManager", (CacheManager)null);
        Cache cache = CacheManager.getInstance().getCache(cacheName);
        cache.put(new Element(TEST_APP_PROPERTY_NAME, TEST_APP_PROPERTY_VALUE));

        jdbcAppPropertyProvider.getPropertyString(TEST_APP_PROPERTY_NAME);

        verify(dataSource, never()).getConnection();
    }

    @Test
    public void testFindAppProperty_ReturnedFromDatabase_AddedToCache() throws Exception {
        CacheManager.getInstance().getCache(cacheName).removeAll();
        // set this to null so we use a real cache. ehcache.Cache is not mockable b/c of final methods
        Whitebox.setInternalState(jdbcAppPropertyProvider, "cacheManager", (CacheManager)null);

        jdbcAppPropertyProvider.getPropertyString(TEST_APP_PROPERTY_NAME);

        String result = (String)(CacheManager.getInstance().getCache(cacheName).get(TEST_APP_PROPERTY_NAME).getObjectValue());

        assertEquals(TEST_APP_PROPERTY_VALUE, result);
    }

    @Test
    public void testFindAppProperty_NothingReturnedFromDatabase_NullProperty() throws Exception {
        when(resultSet.next()).thenReturn(false);
        when(cacheManager.getCache(cacheName)).thenReturn(null);

        String result = jdbcAppPropertyProvider.getPropertyString(TEST_APP_PROPERTY_NAME);

        assertNull(result);
    }

    @Test
    public void testFindAppProperty_ReturnedFromDatabase_CorrectProperty() throws Exception {
        when(cacheManager.getCache(cacheName)).thenReturn(null);

        String result = jdbcAppPropertyProvider.getPropertyString(TEST_APP_PROPERTY_NAME);

        assertEquals(TEST_APP_PROPERTY_VALUE, result);
    }

}
