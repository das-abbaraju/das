package com.picsauditing.strutsutil.actionmapper;

import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.picsauditing.strutsutil.actionmapper.delegate.ActionMapperDelegate;
import com.picsauditing.strutsutil.url.StrutsUrlUtil;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.MapUtils;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.rest.RestActionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public final class PicsRestfulActionMapper extends RestActionMapper {

    private static final Logger logger = LoggerFactory.getLogger(PicsRestfulActionMapper.class);

    private String indexMethodName = "index";
    private String getMethodName = "show";
    private String postMethodName = "insert";
    private String editMethodName = "edit";
    private String newMethodName = "create";
    private String deleteMethodName = "delete";
    private String putMethodName = "update";

    private RestMethodMapping restMethodMapping = new RestMethodMapping();
    private ConfigLocator configLocator = new ConfigLocator();
    private ActionMapperDelegate actionMapperDelegate = new ActionMapperDelegate();

    /**
     * Get the index method name from the struts.xml configuration file.
     */
    @Inject(value = "struts.mapper.indexMethodName")
    public void setIndexMethodName(String indexMethodName) {
        this.indexMethodName = indexMethodName;
    }

    /**
     * Get the get method name from the struts.xml configuration file.
     */
    @Inject(value = "struts.mapper.getMethodName")
    public void setGetMethodName(String getMethodName) {
        this.getMethodName = getMethodName;
    }

    /**
     * Get the post method name from the struts.xml configuration file.
     */
    @Inject(value = "struts.mapper.postMethodName")
    public void setPostMethodName(String postMethodName) {
        this.postMethodName = postMethodName;
    }

    /**
     * Get the edit method name from the struts.xml configuration file.
     */
    @Inject(value = "struts.mapper.editMethodName")
    public void setEditMethodName(String editMethodName) {
        this.editMethodName = editMethodName;
    }

    /**
     * Get the new method name from the struts.xml configuration file.
     */
    @Inject(value = "struts.mapper.newMethodName")
    public void setNewMethodName(String newMethodName) {
        this.newMethodName = newMethodName;
    }

    /**
     * Get the delete method name from the struts.xml configuration file.
     */
    @Inject(value = "struts.mapper.deleteMethodName")
    public void setDeleteMethodName(String deleteMethodName) {
        this.deleteMethodName = deleteMethodName;
    }

    /**
     * Get the put method name from the struts.xml configuration file.
     */
    @Inject(value = "struts.mapper.putMethodName")
    public void setPutMethodName(String putMethodName) {
        this.putMethodName = putMethodName;
    }

    /**
     * Builds the appropriate Struts mapping for REST-style actions. The
     * expected REST-style URL is
     * /namespace/action/id/method?paramName=paramValue
     */
    @Override
    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
        overrideMapperProperties();

        if (!isSlashesInActionNames()) {
            throw new IllegalStateException(
                    "This action mapper requires the setting 'slashesInActionNames' to be set to 'true'");
        }

        ActionMapping mapping = getDefaultActionMapping(request, configManager);

        String preprocessedActionName = mapping.getName();
        mapping = processActionNameAndMethodMapping(configManager, mapping);

        if (mapping == null) {
            return null;
        }

        ParsedUrlWrapper parsedUrlWrapper = getParsedUrlWrapper(mapping, preprocessedActionName);
        buildRestUrlMapping(mapping, request, parsedUrlWrapper);

        mapping.getParams().putAll(buildParameterMap(parsedUrlWrapper));

        return mapping;
    }

    private void overrideMapperProperties() {
        this.setExtensions(Strings.EMPTY_STRING);
    }

    private ActionMapping processActionNameAndMethodMapping(ConfigurationManager configManager, ActionMapping mapping) {
        PackageConfig packageConfig = configLocator.findPackageConfigForNamespace(configManager, mapping);
        ActionConfig actionConfig = configLocator.findActionConfig(packageConfig, mapping);
        if (actionConfig != null) {
            mapping.setName(actionConfig.getName());

            if (Strings.isNotEmpty(actionConfig.getMethodName())) {
                mapping.setMethod(actionConfig.getMethodName());
            }
        }

        return mapping;
    }

    private ActionMapping getDefaultActionMapping(HttpServletRequest request, ConfigurationManager configManager) {
        ActionMapping mapping = new ActionMapping();
        String uri = getUri(request);

        int indexOfSemicolon = uri.indexOf(";");
        uri = (indexOfSemicolon > -1) ? uri.substring(0, indexOfSemicolon) : uri;

        uri = dropExtension(uri, mapping);
        if (uri == null) {
            return null;
        }

        actionMapperDelegate.parseNameAndNamespace(uri, mapping, configManager);

        handleSpecialParameters(request, mapping);

        if (mapping.getName() == null) {
            return null;
        }

        parseActionName(mapping);

        return mapping;
    }

    private void buildRestUrlMapping(ActionMapping mapping, HttpServletRequest request,
                                     ParsedUrlWrapper parsedUrlWrapper) {
        if (mapping.getName() == null) {
            return;
        }

        String idParameterValue = setIdParameter(mapping, parsedUrlWrapper);
        if (Strings.isEmpty(mapping.getMethod())) {
            mapping.setMethod(restMethodMapping.getMethodName(request, idParameterValue, parsedUrlWrapper,
                    getRestMethodMapperConfig()));
        }
    }

    private RestMethodMapperConfig getRestMethodMapperConfig() {
        return new RestMethodMapperConfig.Builder().deleteMethodName(deleteMethodName).editMethodName(editMethodName)
                .getMethodName(getMethodName).indexMethodName(indexMethodName).newMethodName(newMethodName)
                .postMethodName(postMethodName).putMethodName(putMethodName).idParameterName(getIdParameterName())
                .build();
    }

    private String setIdParameter(ActionMapping mapping, ParsedUrlWrapper parsedUrlWrapper) {
        try {
            if (CollectionUtils.isEmpty(mapping.getParams())) {
                mapping.setParams(new HashMap<String, Object>());
            }

            String key = URLDecoder.decode(getIdParameterName(), "UTF-8");
            String firstValueAndRemove = getFirstValueAndRemove(parsedUrlWrapper);
            if (firstValueAndRemove == null) {
                firstValueAndRemove = "";
            }
            String value = URLDecoder.decode(firstValueAndRemove, "UTF-8");

            if (!mapping.getParams().containsKey(getIdParameterName())) {
                mapping.getParams().put(key, value);
            }

            return value;
        } catch (Exception e) {
            logger.error("Error while parsing uri {}", parsedUrlWrapper, e);
        }

        return null;
    }

    private ParsedUrlWrapper getParsedUrlWrapper(ActionMapping mapping, String uri) {
        if (Strings.isNotEmpty(mapping.getName()) && !mapping.getName().contains("{")) {
            uri = StrutsUrlUtil.truncateLeadingSlash(uri.replaceFirst(mapping.getName(), Strings.EMPTY_STRING));
        } else if (mapping.getName().endsWith("{id}")) {
            String actionName = removeIdVariableParameterInActionName(mapping.getName());
            actionName = replaceVariableParameterNamesWithParams(actionName, mapping.getParams());
            uri = StrutsUrlUtil.truncateLeadingSlash(uri.replaceFirst( "\\Q" + actionName + "\\E", Strings.EMPTY_STRING));
        } else if (mapping.getName().contains("{")) {
            String actionName = replaceVariableParameterNamesWithParams(mapping.getName(), mapping.getParams());
            uri = StrutsUrlUtil.truncateLeadingSlash(uri.replaceFirst( "\\Q" + actionName + "\\E", Strings.EMPTY_STRING));
        }

        ParsedUrlWrapper parsedUrlWrapper = new ParsedUrlWrapper(uri);
        parsedUrlWrapper.parse();
        return parsedUrlWrapper;
    }

    private String removeIdVariableParameterInActionName(String actionName) {
        return actionName.replace("{id}", Strings.EMPTY_STRING);
    }

    private String getFirstValueAndRemove(ParsedUrlWrapper parsedUrlWrapper) {
        if (parsedUrlWrapper.isEmpty()) {
            return null;
        }

        Iterator<String> iterator = parsedUrlWrapper.iterator();
        String value = iterator.next();
        iterator.remove();
        return value;
    }

    private Map<String, Object> buildParameterMap(ParsedUrlWrapper parsedUrlWrapper) {
        if (parsedUrlWrapper.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Object> parameters = new HashMap<>();
        try {
            String parameterName = URLDecoder.decode(getIdParameterName(), "UTF-8");
            boolean setParameterValue = false;
            Iterator<String> iterator = parsedUrlWrapper.iterator();

            while (iterator.hasNext()) {
                if (setParameterValue) {
                    parameters.put(parameterName, URLDecoder.decode(iterator.next(), "UTF-8"));
                    setParameterValue = false;
                } else {
                    parameterName = URLDecoder.decode(iterator.next(), "UTF-8");
                    setParameterValue = true;
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.warn("Unable to determine parameters from the url {}", parsedUrlWrapper, e);
        }

        return parameters;
    }

    /**
     * This method is called by &lt;s:url&gt; to build the URL from the mapping
     * built by this class.
     */
    @Override
    public String getUriFromActionMapping(final ActionMapping mapping) {
        StringBuilder uri = new StringBuilder();

        Map<String, Object> params = new HashMap<>();
        if (MapUtils.isNotEmpty(mapping.getParams())) {
            params.putAll(mapping.getParams());
        }

        ActionMapping actionMapping = new ActionMapping(mapping.getName(), mapping.getNamespace(), mapping.getMethod(),
                params);

        uri = appendNamespace(actionMapping, uri);
        uri = appendActionName(actionMapping, uri);
        uri = appendIdValues(actionMapping, uri);
        uri = appendMethodName(actionMapping, uri);

        return uri.toString();
    }

    private StringBuilder appendNamespace(ActionMapping mapping, StringBuilder uri) {
        if (mapping.getNamespace() != null) {
            uri.append(mapping.getNamespace());
            if (!"/".equals(mapping.getNamespace())) {
                uri.append("/");
            }
        }

        return uri;
    }

    private StringBuilder appendActionName(ActionMapping mapping, StringBuilder uri) {
        String name = mapping.getName();
        if (name.indexOf('?') != -1) {
            name = name.substring(0, name.indexOf('?'));
        }

        if (mapping.getName().contains("{")) {
            name = replaceVariableParameterNamesWithParams(name, mapping.getParams());
        }

        uri.append(name);
        return uri;
    }

    private String replaceVariableParameterNamesWithParams(String actionName, Map<String, Object> params) {
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (actionName.contains(getNamedVariableParameter(param))) {
                actionName = actionName.replace(getNamedVariableParameter(param), param.getValue().toString());
            }
        }

        return actionName;
    }

    private String getNamedVariableParameter(Map.Entry<String, Object> param) {
        return "{" + param.getKey() + "}";
    }

    private StringBuilder appendIdValues(ActionMapping mapping, StringBuilder uri) {
        Map<String, Object> params = mapping.getParams();
        if (MapUtils.isEmpty(params) || !params.containsKey(getIdParameterName()) || mapping.getName().contains("{")) {
            return uri;
        }

        StrutsUrlUtil.appendTrailingSlash(uri);
        uri.append(mapping.getParams().get(getIdParameterName()));
        return uri;
    }

    private StringBuilder appendMethodName(ActionMapping mapping, StringBuilder uri) {
        String method = mapping.getMethod();
        if (Strings.isEmpty(method)) {
            return uri;
        }

        if (!newMethodName.equals(method) && MapUtils.isEmpty(mapping.getParams())) {
            return uri;
        }

        StrutsUrlUtil.appendTrailingSlash(uri);
        uri.append(method);
        return uri;
    }
}