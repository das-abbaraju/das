package com.picsauditing.featuretoggle;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.togglz.core.Feature;
import org.togglz.core.activation.Parameter;
import org.togglz.core.activation.UsernameActivationStrategy;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.user.FeatureUser;
import org.togglz.core.user.SimpleFeatureUser;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class UserIdActivationStrategyTest {

    @Mock
    private ServletFeatureUser servletFeatureUser;

    private final UserIdActivationStrategy userIdActivationStrategy = new UserIdActivationStrategy();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsActive_emptyUserList() {

        FeatureUser user = new ServletFeatureUser();
        FeatureState state = new FeatureState(MyFeature.FEATURE)
                .enable()
                .setStrategyId(UserIdActivationStrategy.ID);

        boolean active = userIdActivationStrategy.isActive(state, user);

        assertEquals(false, active);
    }

    @Test
    public void testIsActive_nonServletFeatureUser() {

        FeatureUser user = new SimpleFeatureUser("1234", false);
        FeatureState state = new FeatureState(MyFeature.FEATURE)
                .enable()
                .setStrategyId(UsernameActivationStrategy.ID)
                .setParameter(UserIdActivationStrategy.PARAM_USER_IDS, "1234, 3456, 34333");

        boolean active = userIdActivationStrategy.isActive(state, user);

        assertEquals(false, active);
    }

    @Test
    public void testIsActive_differentUserId() {

        FeatureState state = new FeatureState(MyFeature.FEATURE)
                .enable()
                .setStrategyId(UsernameActivationStrategy.ID)
                .setParameter(UsernameActivationStrategy.PARAM_USERS, "1234, 3456, 34333");

        when(servletFeatureUser.getId()).thenReturn("2323");

        boolean active = userIdActivationStrategy.isActive(state, servletFeatureUser);

        assertEquals(false, active);
    }

    @Test
    public void testIsActive_correctUserId() {

        FeatureState state = new FeatureState(MyFeature.FEATURE)
                .enable()
                .setStrategyId(UsernameActivationStrategy.ID)
                .setParameter(UsernameActivationStrategy.PARAM_USERS, "1234, 3456, 34333");

        when(servletFeatureUser.getId()).thenReturn("34333");

        boolean active = userIdActivationStrategy.isActive(state, servletFeatureUser);

        assertEquals(true, active);
    }

    @Test
    public void testGetParameters() {

        Parameter[] parameters = userIdActivationStrategy.getParameters();

        assertThat(parameters, notNullValue());
        assertThat(parameters.length, is(1));

        Parameter userParam = parameters[0];

        assertThat(userParam, notNullValue());
        assertThat(userParam.getName(), is(UsernameActivationStrategy.PARAM_USERS));
    }
    private enum MyFeature implements Feature {
        FEATURE
    }
}