package com.picsauditing.actions.contractors;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailQueueDAO;

@SuppressWarnings("serial")
public class ContractorCronStatistics extends PicsActionSupport {

	private long contractorsProcessed;
	private long contractorsWaiting;
	private long emailsSentInHour;
	private long emailsPending;
	private long emailsSentInLastFiveMinutes;
	private long emailsPendingAndCreatedMoreThanFiveMinutesAgo;
	private long emailsWithErrorsInLastWeek;

	private boolean contractorCronError = false;
	private boolean contractorCronWarning = false;
	private boolean emailCronError = false;
	private boolean emailCronWarning = false;

	private ContractorAccountDAO contractorAccountDAO;
	private EmailQueueDAO emailQueueDAO;

	public ContractorCronStatistics(ContractorAccountDAO contractorAccountDAO, EmailQueueDAO emailQueueDAO) {
		this.contractorAccountDAO = contractorAccountDAO;
		this.emailQueueDAO = emailQueueDAO;
	}

	public String execute() throws Exception {
		contractorsProcessed = contractorAccountDAO.findNumberOfContractorsProcessed(60);
		contractorsWaiting = contractorAccountDAO.findNumberOfContractorsNeedingRecalculation();
		emailsSentInHour = emailQueueDAO.findNumberOfEmailsSent(60);
		emailsPending = emailQueueDAO.findNumberOfEmailsWithStatus("Pending");
		emailsSentInLastFiveMinutes = emailQueueDAO.findNumberOfEmailsSent(5);
		emailsPendingAndCreatedMoreThanFiveMinutesAgo = emailQueueDAO.findNumberOfEmailsWithStatusBeforeTime("Pending",
				5);
		emailsWithErrorsInLastWeek = emailQueueDAO.findNumberOfEmailsWithStatusInTime("Error", 60 * 24 * 7);

		if (contractorsWaiting == 0) {
			// leave default values
		} else if (contractorsProcessed > 100) {
			contractorCronWarning = true;
		} else {
			contractorCronError = true;
		}

		if (emailsPendingAndCreatedMoreThanFiveMinutesAgo == 0) {
			// leave default values
		} else if (emailsSentInLastFiveMinutes < 20) {
			emailCronError = true;
		} else if (emailsWithErrorsInLastWeek > 10) {
			emailCronError = true;
		} else {
			emailCronWarning = true;
		}

		return SUCCESS;
	}

	public long getContractorsProcessed() {
		return contractorsProcessed;
	}

	public long getContractorsWaiting() {
		return contractorsWaiting;
	}

	public long getEmailsSentInHour() {
		return emailsSentInHour;
	}

	public long getEmailsPending() {
		return emailsPending;
	}

	public long getEmailsSentInLastFiveMinutes() {
		return emailsSentInLastFiveMinutes;
	}

	public long getEmailsPendingAndCreatedMoreThanFiveMinutesAgo() {
		return emailsPendingAndCreatedMoreThanFiveMinutesAgo;
	}

	public long getEmailsWithErrorsInLastWeek() {
		return emailsWithErrorsInLastWeek;
	}

	public boolean isContractorCronError() {
		return contractorCronError;
	}

	public boolean isEmailCronError() {
		return emailCronError;
	}

	public boolean isContractorCronWarning() {
		return contractorCronWarning;
	}

	public boolean isEmailCronWarning() {
		return emailCronWarning;
	}
}
