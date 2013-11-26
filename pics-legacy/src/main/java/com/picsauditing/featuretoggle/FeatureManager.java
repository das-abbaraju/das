package com.picsauditing.featuretoggle;

import com.picsauditing.PICS.DBBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.togglz.core.Feature;
import org.togglz.core.manager.FeatureManagerBuilder;
import org.togglz.core.metadata.FeatureMetaData;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.jdbc.JDBCStateRepository;
import org.togglz.core.repository.mem.InMemoryStateRepository;
import org.togglz.core.user.FeatureUser;
import org.togglz.core.user.UserProvider;

import javax.naming.NamingException;
import java.util.Set;

public class FeatureManager implements org.togglz.core.manager.FeatureManager {
    private final Logger logger = LoggerFactory.getLogger(FeatureManager.class);
    private static org.togglz.core.manager.FeatureManager featureManager;

    public FeatureManager() {
        if (featureManager == null) {
            featureManager = new FeatureManagerBuilder()
                    .featureEnum(Features.class)
                    .stateRepository(stateRepository())
                    .userProvider(userProvider())
                    .build();
        }
    }

    public StateRepository stateRepository() {
        try {
            return new JDBCStateRepository(DBBean.getJdbcPics());
        } catch (NamingException e) {
            logger.error("we were unable to get a jdbc connection from JNDI resources: {}", e);
            logger.error("Returning an InMemoryStateRepository");
            return new InMemoryStateRepository();
        }
        //  return new CachingStateRepository(someOtherRepository, 10000);
    }

    private UserProvider userProvider() {
        return new UserProvider() {
            @Override
            public FeatureUser getCurrentUser() {
                return new ServletFeatureUser();
            }
        };
    }

    @Override
    public String getName() {
        return featureManager.getName();
    }

    @Override
    public Set<Feature> getFeatures() {
        return featureManager.getFeatures();
    }

    @Override
    public FeatureMetaData getMetaData(Feature feature) {
        return featureManager.getMetaData(feature);
    }

    @Override
    public boolean isActive(Feature feature) {
        return featureManager.isActive(feature);
    }

    @Override
    public FeatureUser getCurrentFeatureUser() {
        return featureManager.getCurrentFeatureUser();
    }

    @Override
    public FeatureState getFeatureState(Feature feature) {
        return featureManager.getFeatureState(feature);
    }

    @Override
    public void setFeatureState(FeatureState state) {
        featureManager.setFeatureState(state);
    }
}