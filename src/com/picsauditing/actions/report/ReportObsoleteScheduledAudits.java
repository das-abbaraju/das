package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.DynaBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSenderSpring;

@SuppressWarnings("serial")
public class ReportObsoleteScheduledAudits extends ReportContractorAudits {
	@Autowired
	private EmailSenderSpring emailSender;

	private static final int TEMPLATE_ID = 162;

	public void prepare() throws Exception {
		super.prepare();

		getFilter().setAllowMailReport(true);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
		setReportAddresses("mmina@picsauditing.com, sjones@picsauditing.com");
	}

	public void buildQuery() {
		super.buildQuery();

		sql.addWhere("a.status = 'Active'");
		sql.addWhere("scheduledDate IS NOT NULL");
		sql
				.addWhere("NOT EXISTS (SELECT 'x' FROM contractor_audit_operator cao WHERE ca.id = cao.auditID AND cao.visible = 1)");

		orderByDefault = "a.name DESC";
		filteredDefault = true;
	}

	@Override
	protected String returnResult() throws IOException {
		if (mailReport) {
			List<DynaBean> obsoleteAudits = new ArrayList<DynaBean>();

			for (DynaBean bean : data) {
				obsoleteAudits.add(bean);
			}

			// Send Report as Email to auditors
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(TEMPLATE_ID);
			emailBuilder.setFromAddress("PICS Mailer <info@picsauditing.com>");
			emailBuilder.setToAddresses(reportAddresses);

			if (obsoleteAudits.size() > 0) {
				emailBuilder.addToken("obsoleteAudits", obsoleteAudits);
				emailBuilder.addToken("i18nCache", I18nCache.getInstance());
			}

			EmailQueue emailQueue = emailBuilder.build();
			emailQueue.setPriority(90);
			emailSender.send(emailQueue);
			addActionMessage("Report Sent to Auditors");
			return BLANK;
		}

		return super.returnResult();
	}
}