package com.picsauditing.dao;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"UserDAOITest-context.xml"})
public class UserDaoITest {
    @Mock
    private Database databaseForTesting;
    @Autowired
    private UserDAO userDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);
    }


    @Test
    public void testFindName() throws Exception {
        User user = userDAO.findName("gmeurer");
        assertThat(user, is(notNullValue()));
    }
}
