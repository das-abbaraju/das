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

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getReportIdSpecified() {
        return reportIdSpecified;
    }

    public void setReportIdSpecified(String reportIdSpecified) {
        this.reportIdSpecified = reportIdSpecified;
    }

    protected String outputFormat = "";
    protected String reportIdSpecified = "";

    @Override
    public void setParameters(Map<String, String[]> parameters) {
        super.setParameters(parameters);
        /*
           * While we are here, let's memorize the report ID specified in the
           * parameters, just in case the JpaEntityConverter cannot find the report
           * in question, so that we can report on the requested ID number in the
           * error message (above).
           */
        reportIdSpecified = lookupParam(parameters, "report");
        outputFormat = lookupParam(parameters, "format");
    }

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
        logger.debug("Output Format: " + outputFormat);
        return outputFormat;
    }
}
