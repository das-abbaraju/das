package com.picsauditing.actions.rest.api;

import com.picsauditing.access.Api;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.actions.report.ReportData;
import org.apache.struts2.interceptor.ParameterAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@SuppressWarnings("serial")
public class DataFeed extends ReportData implements ParameterAware {

    private static final Logger logger = LoggerFactory.getLogger(DataFeed.class);
    private String reportIdSpecified = "";

    @Override
    @Api
    public String execute() {
        try {
            initialize();
            runQuery();

            converter.convertForExtJS();
            json.put("data", converter.getReportResults().toJson());

            if (permissions.isAdmin() || permissions.getAdminID() > 0) {
                json.put("sql", debugSQL);
            }
            json.put("success", true);
        } catch (ReportValidationException error) {
            writeJsonError(error.getMessage());
        } catch (Exception e) {
            if (report == null) {
                String err = "Invalid report ID: " + reportIdSpecified;
                logger.error(err);
                writeJsonError(err);
            } else {
                logger.error("Report: {} {} SQL: {}", new Object[]{report.getId(), e.getMessage(), debugSQL});
                if (permissions.has(OpPerms.Debug) || permissions.getAdminID() > 0) {
                    writeJsonError(e);
                    json.put("sql", debugSQL);
                } else {
                    writeJsonError("Invalid Query");
                }
            }
        }
        logger.warn("Output Format: " + outputFormat);
        return outputFormat;
    }

    /**
     * We need to set the apiKey parameter early (earlier than when the
     * JpaEntityInterceptor/JpaEntityConverter code normally kicks in to set the
     * parameters), so that the apiKey will be available to the
     * SecurityInterceptor.
     */
    @Override
    public void setParameters(Map<String, String[]> parameters) {
        logger.debug("Setting parameters");
        super.setParameters(parameters);
        setApiKey(lookupParam(parameters, "apiKey"));
        /*
       * While we are here, let's memorize the report ID specified in the
       * paramers, just in case the JpaEntityConverter cannot find the report
       * in question, so that we can report on the requested ID number in the
       * error message (above).
       */
        reportIdSpecified = lookupParam(parameters, "report");
    }

    private String lookupParam(Map<String, String[]> parameters, String key) {
        String paramValue = null;
        String[] param = parameters.get(key);
        if (param != null && param.length > 0) {
            paramValue = param[0];
        }
        return paramValue;
    }
}
