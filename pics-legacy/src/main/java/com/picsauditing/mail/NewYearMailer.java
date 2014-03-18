package com.picsauditing.mail;

import com.picsauditing.messaging.MessagePublisherService;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;

@SuppressWarnings("serial")
public class NewYearMailer extends PicsActionSupport {

	@Autowired
	private EmailReportRunner runner;
    @Autowired
    private MessagePublisherService messageService;

	private final int ANNUAL_UPDATE_REPORT_ID = 163;
	private final int ANNUAL_UPDATE_TEMPLATE_ID = 37;

	@RequiredPermission(value = OpPerms.DevelopmentEnvironment)
	public String execute() throws Exception {
		runner.runReport(ANNUAL_UPDATE_REPORT_ID, permissions);
		return SUCCESS;
	}

	public String queueEmails() throws Exception {
		execute();
		queueReportRequest();
		return SUCCESS;
	}

	private void queueReportRequest() {
		EmailRequestDTO request = runner.buildEmailRequest();
		request.templateID = ANNUAL_UPDATE_TEMPLATE_ID;
		request.userTypes.add(OpPerms.ContractorAdmin);
		try {
            messageService.getEmailRequestPublisher().publish(request);
			addActionMessage("Successfully added " + request.contractorIDs.size() + " contractors to the email queue");
		} catch (Exception e) {
			addActionError("Failed to add to message queue");
		}
	}

	public EmailReportRunner getRunner() {
		return runner;
	}
}
