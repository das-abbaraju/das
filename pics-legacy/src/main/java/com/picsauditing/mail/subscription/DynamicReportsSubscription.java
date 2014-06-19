package com.picsauditing.mail.subscription;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.actions.report.ReportApi;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.data.HtmlWriter;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class DynamicReportsSubscription extends SubscriptionBuilder {
    @Autowired
    ReportApi reportApi;

	@Override
	public Map<String, Object> process(EmailSubscription subscription) {
		Map<String, Object> tokens = new HashMap<>();

        Report report = subscription.getReport();

        reportApi.setIncludeData(true);
        reportApi.setReportId(report.getId());

        int totalRowCount = 0;
        try {
            reportApi.setUser(subscription.getUser());

            reportApi.execute();
            JSONObject json = reportApi.getJson();
            JSONObject results = (JSONObject) json.get("results");
            totalRowCount = Integer.parseInt(results.get("total").toString());
        }
        catch (Exception e) {

        }

        if (totalRowCount == 0) {
            return tokens;
        }

        String output = reportApi.html(report.getId());

        boolean reportExceedsColumnMax = HtmlWriter.MAX_COLUMNS < report.getColumns().size();
        boolean reportExceedsRowMax = HtmlWriter.MAX_ROWS < totalRowCount;
        boolean isNotFullReport = reportExceedsColumnMax || reportExceedsRowMax;

        tokens.put("notFullReport", isNotFullReport);
        tokens.put("subscriptionFrequency", subscription.getTimePeriod().toString().toLowerCase());
        tokens.put("report", report);
        tokens.put("reportData", output);
        tokens.put("reportLink", "https://www.picsorganizer.com/Report.action?report=" + report.getId());
        tokens.put("linkToSubscriptionPage", "https://www.picsorganizer.com/ProfileEdit.action?goEmailSub=true");

		return tokens;
	}
}
