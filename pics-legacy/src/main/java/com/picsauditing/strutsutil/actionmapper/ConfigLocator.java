package com.picsauditing.strutsutil.actionmapper;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.util.NamedVariablePatternMatcher;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.MapUtils;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public final class ConfigLocator {

    private NamedVariablePatternMatcher namedVariablePatternMatcher = new NamedVariablePatternMatcher();

    public PackageConfig findPackageConfigForNamespace(ConfigurationManager configManager, ActionMapping mapping) {
        Configuration configuration = configManager.getConfiguration();

        Map<String, PackageConfig> packageConfigs = configuration.getPackageConfigs();
        if (MapUtils.isEmpty(packageConfigs)) {
            return null;
        }

        for (PackageConfig packageConfig : packageConfigs.values()) {
            if (foundPackageConfig(mapping, packageConfig)) {
                return packageConfig;
            }
        }

        return null;
    }

    private boolean foundPackageConfig(ActionMapping mapping, PackageConfig packageConfig) {
        return packageConfig != null && mapping != null && Strings.isNotEmpty(mapping.getNamespace())
                && mapping.getNamespace().equals(packageConfig.getNamespace());
    }

    public ActionConfig findActionConfig(PackageConfig packageConfig, ActionMapping mapping) {
        if (packageConfig == null || mapping == null) {
            return null;
        }

        Map<String, ActionConfig> actionConfigs = packageConfig.getActionConfigs();
        if (MapUtils.isEmpty(actionConfigs)) {
            return null;
        }

        String actionName = mapping.getName();
        if (Strings.isNotEmpty(actionName) && actionConfigs.containsKey(actionName)) {
            return actionConfigs.get(actionName);
        }

        ActionConfig patternMatchedActionConfig = getPatternMatchedActionConfig(actionConfigs, mapping, actionName);
        if (patternMatchedActionConfig == null) {
            return getActionConfigByName(actionConfigs, actionName);
        }

        return patternMatchedActionConfig;
    }

    private ActionConfig getPatternMatchedActionConfig(Map<String, ActionConfig> actionConfigs, ActionMapping actionMapping, String actionName) {
        Map<String, ActionConfig> nonLiteralActionConfigs = getNonLiteralActionConfigs(actionConfigs);
        for (String name : nonLiteralActionConfigs.keySet()) {
            NamedVariablePatternMatcher.CompiledPattern compiledPattern = namedVariablePatternMatcher.compilePattern(name);

            if (foundMatchingConfiguration(actionMapping, compiledPattern, actionName)) {
                return actionConfigs.get(name);
            }
        }

        return null;
    }

    private boolean foundMatchingConfiguration(ActionMapping actionMapping, NamedVariablePatternMatcher.CompiledPattern compiledPattern, String actionName) {
        while (Strings.isNotEmpty(actionName)) {
            Map<String, String> variableParameters = new HashMap<>();
            if (namedVariablePatternMatcher.match(variableParameters, actionName, compiledPattern)) {
                addParamsToActionMapping(actionMapping, variableParameters);
                return true;
            }

            actionName = truncateActionName(actionName);
        }

        return false;
    }

    private void addParamsToActionMapping(ActionMapping actionMapping, Map<String, String> variableParameters) {
        if (actionMapping.getParams() == null) {
            actionMapping.setParams(new HashMap<String, Object>());
        }

        actionMapping.getParams().putAll(variableParameters);
    }

    private Map<String, ActionConfig> getNonLiteralActionConfigs(Map<String, ActionConfig> actionConfigs) {
        Map<String, ActionConfig> nonLiteralActionConfigs = new LinkedHashMap<>();
        for (String actionName : actionConfigs.keySet()) {
            if (!namedVariablePatternMatcher.isLiteral(actionName)) {
                nonLiteralActionConfigs.put(actionName, actionConfigs.get(actionName));
            }
        }

        return nonLiteralActionConfigs;
    }

    private ActionConfig getActionConfigByName(Map<String, ActionConfig> actionConfigs, String actionName) {
        while (Strings.isNotEmpty(actionName)) {
            if (actionConfigs.containsKey(actionName)) {
                return actionConfigs.get(actionName);
            } else {
                actionName = truncateActionName(actionName);
            }
        }

        return null;
    }

    private String truncateActionName(String actionName) {
        int index = actionName.lastIndexOf('/');
        if (index == -1) {
            return Strings.EMPTY_STRING;
        }

        return actionName.substring(0, index);
    }
}
