package com.picsauditing.actions;

import org.apache.struts2.interceptor.ParameterAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PicsApiSupport extends PicsActionSupport implements ParameterAware {

    private static final Logger logger = LoggerFactory.getLogger(PicsApiSupport.class);

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(final String apiKey) {
        this.apiKey = apiKey;
    }

    public void setParameters(Map<String, String[]> parameters) {
        logger.debug("Setting parameters");
        setApiKey(lookupParam(parameters, "apiKey"));
    }

    protected String lookupParam(Map<String, String[]> parameters, String key) {
        String paramValue = null;
        String[] param = parameters.get(key);
        if (param != null && param.length > 0) {
            paramValue = param[0];
        }
        return paramValue;
    }
}
