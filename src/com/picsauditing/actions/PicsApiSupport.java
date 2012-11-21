package com.picsauditing.actions;

import org.apache.struts2.interceptor.ParameterAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: pschlesinger
 * Date: 11/21/12
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class PicsApiSupport extends PicsActionSupport implements ParameterAware {

    private static final Logger logger = LoggerFactory.getLogger(PicsApiSupport.class);
    protected String outputFormat = "";
    protected String reportIdSpecified = "";

    public void setParameters(Map<String, String[]> parameters) {
        logger.warn("Setting parameters");
        setApiKey(lookupParam(parameters, "apiKey"));
        /*
       * While we are here, let's memorize the report ID specified in the
       * parameters, just in case the JpaEntityConverter cannot find the report
       * in question, so that we can report on the requested ID number in the
       * error message (above).
       */
        reportIdSpecified = lookupParam(parameters, "report");
        outputFormat = lookupParam(parameters, "format");
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
