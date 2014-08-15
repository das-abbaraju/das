package com.picsauditing.featuretoggle;

import org.togglz.core.activation.Parameter;
import org.togglz.core.activation.ParameterBuilder;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.spi.ActivationStrategy;
import org.togglz.core.user.FeatureUser;
import org.togglz.core.util.Strings;

import java.util.List;

/**
 * Created by kishon on 8/13/14.
 */
public class UserIdActivationStrategy implements ActivationStrategy {

    public static final String ID = "userid";

    public static final String PARAM_USER_IDS = "users";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Users by App User Id";
    }

    @Override
    public boolean isActive(FeatureState state, FeatureUser user) {

        if (!(user instanceof ServletFeatureUser)) return false;

        String userIdsAsString = state.getParameter(PARAM_USER_IDS);

        if (Strings.isNotBlank(userIdsAsString)) {

            String userId = ((ServletFeatureUser) user).getId();

            List<String> userIdsStored = Strings.splitAndTrim(userIdsAsString, ",");

            if (user != null && Strings.isNotBlank(userId)) {
                for (String userIdStored : userIdsStored) {
                    if (userIdStored.equals(userId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Parameter[] getParameters() {
        return new Parameter[]{
                ParameterBuilder.create(PARAM_USER_IDS).label("Users").largeText()
                        .description("A list of users for which the feature is active.")
        };
    }
}