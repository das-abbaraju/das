package com.picsauditing.actions.rest.api;

import java.util.Map;

import com.picsauditing.jpa.entities.Report;
import org.apache.struts2.interceptor.ParameterAware;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.Api;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.report.ReportApi;
import com.picsauditing.report.ReportContext;
import com.picsauditing.report.ReportJson;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.report.data.ReportDataConverter;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class DataFeed extends ReportApi implements ParameterAware {

	private ReportDataConverter converter;

	protected String outputFormat = "";
	protected String reportIdSpecified = "";

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
        outputFormat = lookupParam(parameters,"format",JSON);
    }

    @Override
    @Api
    public String execute() {
    	Report report = null;

        try {
	        JSONObject payloadJson = getJsonFromRequestPayload();
	        ReportContext reportContext = buildReportContext(payloadJson);
	        report = reportService.createOrLoadReport(reportContext);

	        if (org.apache.commons.lang3.ArrayUtils.contains(DATAFEED_FORMATS, outputFormat)) {
		        json = reportService.buildJsonResponse(reportContext);

		        converter.convertForExtJS();
				json.put("data", converter.getReportResults().toJson());

				if (permissions.isAdmin() || permissions.getAdminID() > 0) {
					json.put("sql", debugSQL);
				}

				json.put("success", true);
			} else {
				String message = "Invalid format. Choices are: " + Strings.implodeForDB(DATAFEED_FORMATS, ", ") + ".";
				ReportJson.writeJsonErrorMessage(json, message);
				// The error message itself needs to be presented somehow...
				outputFormat = JSON;
			}
        } catch (ReportValidationException rve) {
        	ReportJson.writeJsonException(json, rve);
        } catch (Exception e) {
            if (report == null) {
                String err = "Invalid report ID: " + reportIdSpecified;
                logger.error(err);
                ReportJson.writeJsonErrorMessage(json, err);
            } else {
                logger.error("Report: {} {} SQL: {}", new Object[]{report.getId(), e.getMessage(), debugSQL});
                if (permissions.has(OpPerms.Debug) || permissions.getAdminID() > 0) {
                    ReportJson.writeJsonException(json, e);
                    json.put("sql", debugSQL);
                } else {
                	ReportJson.writeJsonErrorMessage(json, "Invalid Query");
                }
            }
        }
        logger.debug("Output Format: " + outputFormat);
        return outputFormat;
    }

}
