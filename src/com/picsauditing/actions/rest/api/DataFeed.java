package com.picsauditing.actions.rest.api;

import com.picsauditing.access.Api;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.actions.report.ReportData;
import org.apache.struts2.interceptor.ParameterAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class DataFeed extends ReportData implements ParameterAware {

    private static final Logger logger = LoggerFactory.getLogger(DataFeed.class);

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
