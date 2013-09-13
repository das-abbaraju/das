package com.picsauditing.toggle;

import com.picsauditing.dao.mapper.AppPropertyRowMapper;
import com.picsauditing.dao.mapper.GenericQueryMapper;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.search.Database;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"FeatureToggleCheckerGroovyITest-context.xml"})
public class FeatureToggleCheckerGroovyITest {
    private static final String FIND_ALL_TOGGLES = "SELECT * FROM app_properties WHERE property like 'Toggle.%'";

    @Autowired
    private FeatureToggle featureToggleChecker;

    @Test
    public void test() throws Exception {
        List<AppProperty> appProperties = new Database().select(FIND_ALL_TOGGLES, new AppPropertyRowMapper());
        for (AppProperty toggle : appProperties) {
            String toggleName = toggle.getProperty();
            System.out.println("Running "+ toggleName);
            featureToggleChecker.isFeatureEnabled(toggleName);
            Thread.sleep(500);
        }
        System.out.println("Take a memory dump");
        Thread.sleep(10000);
    }
}
