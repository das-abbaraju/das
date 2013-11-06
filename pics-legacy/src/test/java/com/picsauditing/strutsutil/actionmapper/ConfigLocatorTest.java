package com.picsauditing.strutsutil.actionmapper;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.picsauditing.util.Strings;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ConfigLocatorTest {

    public static final String TEST_NAMESPACE = "employee-guard/operator";
    public static final String NOT_FOUND_NAMESPACE = "not-found";

    public static final String DASHBOARD_ACTION_NAME = "dashboard";
    public static final String OPERATOR_ROLE_ACTION_NAME = "dashboard/{operatorId}/role/{roleId}";
    public static final String ACTION_NAME_FOR_NOT_FOUND_TEST = "not_found_test";

    public static final String OPERATOR_DASHBOARD_ACTION_CLASS = "com.picsauditing.employeeguard.controllers.OperatorDashboardAction";
    public static final String OPERATOR_ROLE_ACTION_CLASS = "com.picsauditing.employeeguard.controllers.OperatorRoleAction";
    public static final String OPERATOR_EDIT_ACTION_CLASS = "com.picsauditing.employeeguard.controllers.EditOperatorAction";

    private ConfigLocator configLocator;

    @Mock
    private Configuration configuration;
    @Mock
    private ConfigurationManager configurationManager;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        configLocator = new ConfigLocator();
    }

    @Test
    public void testFindPackageConfigForNamespace_NoPackageConfigs() {
        ConfigurationManager configurationManager = getConfigurationManager_NoPackageConfigs();
        ActionMapping actionMapping = getActionMapping();

        PackageConfig result = configLocator.findPackageConfigForNamespace(configurationManager, actionMapping);

        assertNull(result);
    }

    private ConfigurationManager getConfigurationManager_NoPackageConfigs() {
        when(configurationManager.getConfiguration()).thenReturn(configuration);

        return configurationManager;
    }

    @Test
    public void testFindPackageConfigForNamespace_NoMatchingPackageConfigs() {
        ConfigurationManager configurationManager = getConfigurationManager_NoMatchingPackageConfigFound();
        ActionMapping actionMapping = getActionMapping();

        PackageConfig result = configLocator.findPackageConfigForNamespace(configurationManager, actionMapping);

        assertNull(result);
    }

    private ConfigurationManager getConfigurationManager_NoMatchingPackageConfigFound() {
        when(configuration.getPackageConfigs()).thenReturn(getPackageConfigs());
        when(configurationManager.getConfiguration()).thenReturn(configuration);

        return configurationManager;
    }

    private Map<String, PackageConfig> getPackageConfigs() {
        Map<String, PackageConfig> packageConfigs = new HashMap<>();
        packageConfigs.put("Config 1", getFakePackageConfig(NOT_FOUND_NAMESPACE));
        packageConfigs.put("Config 2", getFakePackageConfig(NOT_FOUND_NAMESPACE));
        return packageConfigs;
    }

    @Test
    public void testFindPackageConfigForNamespace_FoundMatchingPackageConfig() {
        ConfigurationManager configurationManager = getConfigurationManager_MatchingPackageConfigFound();
        ActionMapping actionMapping = getActionMapping();

        PackageConfig result = configLocator.findPackageConfigForNamespace(configurationManager, actionMapping);

        assertEquals(TEST_NAMESPACE, result.getNamespace());
    }

    private ConfigurationManager getConfigurationManager_MatchingPackageConfigFound() {
        when(configuration.getPackageConfigs()).thenReturn(getPackageConfigs_HasMatchingConfig());
        when(configurationManager.getConfiguration()).thenReturn(configuration);

        return configurationManager;
    }

    private Map<String, PackageConfig> getPackageConfigs_HasMatchingConfig() {
        Map<String, PackageConfig> packageConfigs = new HashMap<>();
        packageConfigs.put("Config 1", getFakePackageConfig(NOT_FOUND_NAMESPACE));
        packageConfigs.put("Config 2", getFakePackageConfig(TEST_NAMESPACE));
        return packageConfigs;
    }

    private PackageConfig getFakePackageConfig(String namespace) {
        PackageConfigToFake packageConfig = new PackageConfigToFake();
        packageConfig.setNamespace(namespace);
        return packageConfig;
    }

    private ActionMapping getActionMapping() {
        return getActionMapping(DASHBOARD_ACTION_NAME, TEST_NAMESPACE);
    }

    private ActionMapping getActionMapping(String name, String namespace) {
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName(name);
        actionMapping.setNamespace(namespace);
        return actionMapping;
    }

    @Test
    public void testFindActionConfig_NoActionConfigs() {
        PackageConfig packageConfig = getPackageConfig_NoActionConfigs();
        ActionMapping actionMapping = getActionMapping();

        ActionConfig result = configLocator.findActionConfig(packageConfig, actionMapping);

        assertNull(result);
    }

    @Test
    public void testFindActionConfig_LiteralActionName() {
        PackageConfig packageConfig = getPackageConfig_MultipleActionConfigs();
        ActionMapping actionMapping = getActionMapping();

        ActionConfig result = configLocator.findActionConfig(packageConfig, actionMapping);

        assertEquals(OPERATOR_DASHBOARD_ACTION_CLASS, result.getClassName());
    }

    @Test
    public void testFindActionConfig_NamedVariableParameterActionName() {
        PackageConfig packageConfig = getPackageConfig_MultipleActionConfigs();
        ActionMapping actionMapping = getActionMapping("dashboard/123/role/456", TEST_NAMESPACE);

        ActionConfig result = configLocator.findActionConfig(packageConfig, actionMapping);

        assertEquals(OPERATOR_ROLE_ACTION_CLASS, result.getClassName());
    }

    @Test
    public void testFindActionConfig_NoMatchFound() {
        PackageConfig packageConfig = getPackageConfig_MultipleActionConfigs();
        ActionMapping actionMapping = getActionMapping("does_not_exist", TEST_NAMESPACE);

        ActionConfig result = configLocator.findActionConfig(packageConfig, actionMapping);

        assertNull(result);
    }

    private PackageConfig getPackageConfig_NoActionConfigs() {
        PackageConfigToFake packageConfig = new PackageConfigToFake();
        packageConfig.setActionConfigs(null);
        return packageConfig;
    }

    private PackageConfig getPackageConfig_MultipleActionConfigs() {
        PackageConfigToFake packageConfig = new PackageConfigToFake();
        packageConfig.setActionConfigs(getActionConfigs());
        return packageConfig;
    }

    private Map<String, ActionConfig> getActionConfigs() {
        Map<String, ActionConfig> actionConfigs = new HashMap<>();
        actionConfigs.put(DASHBOARD_ACTION_NAME, new ActionConfigToFake(TEST_NAMESPACE, DASHBOARD_ACTION_NAME,
                OPERATOR_DASHBOARD_ACTION_CLASS));
        actionConfigs.put(OPERATOR_ROLE_ACTION_NAME, new ActionConfigToFake(TEST_NAMESPACE, OPERATOR_ROLE_ACTION_NAME,
                OPERATOR_ROLE_ACTION_CLASS));
        actionConfigs.put(ACTION_NAME_FOR_NOT_FOUND_TEST, new ActionConfigToFake(TEST_NAMESPACE, ACTION_NAME_FOR_NOT_FOUND_TEST,
                OPERATOR_EDIT_ACTION_CLASS));

        return actionConfigs;
    }
}
