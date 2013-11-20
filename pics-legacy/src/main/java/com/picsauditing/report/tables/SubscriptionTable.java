package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.EmailSubscription;

public class SubscriptionTable extends AbstractTable {

    public static final String User = "User";
    public static final String Report = "Report";

    public SubscriptionTable() {
        super("email_subscription");
        addFields(EmailSubscription.class);
    }

    protected void addJoins() {
        addRequiredKey(new ReportForeignKey(User, new UserTable(), new ReportOnClause("userID", "id")));
        addOptionalKey(new ReportForeignKey(Report, new ReportTable(), new ReportOnClause("reportID", "id")));
    }
}