package com.picsauditing.actions.contractors;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailQueueDAO;

@SuppressWarnings("serial")
public class ContractorCronStatistics extends PicsActionSupport implements Preparable {
	private ContractorAccountDAO contractorAccountDAO;
	private EmailQueueDAO emailQueueDAO;

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

	public ContractorCronStatistics(ContractorAccountDAO contractorAccountDAO, EmailQueueDAO emailQueueDAO) {
		this.contractorAccountDAO = contractorAccountDAO;
		this.emailQueueDAO = emailQueueDAO;
	}

	@Override
	public void prepare() throws Exception {
		// Wanting to initialize in prepare so database isn't re-queried as
		// often
		contractorsProcessed = getNumberOfContractorsProcessed(60);
		contractorsWaiting = getNumberOfContractorsWaiting();
		emailsSentInHour = getNumberOfEmailsSent(60);
		emailsPending = getNumberOfEmailsPending();
		emailsSentInLastFiveMinutes = getNumberOfEmailsSent(5);
		emailsPendingAndCreatedMoreThanFiveMinutesAgo = getNumberOfEmailsPendingBeforeTime(5);
		emailsWithErrorsInLastWeek = getNumberOfEmailsWithErrorsInTimePeriod(60 * 24 * 7);

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
	}

	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		return SUCCESS;
	}

	public long getNumberOfContractorsProcessed(int timePeriodInMinutes) {
		return contractorAccountDAO.findNumberOfContractorsProcessed(timePeriodInMinutes);
	}

	public long getNumberOfContractorsWaiting() {
		return contractorAccountDAO.findNumberOfContractorsNeedingRecalculation();
	}

	public long getNumberOfEmailsSent(int timePeriodInMinutes) {
		return emailQueueDAO.findNumberOfEmailsSent(timePeriodInMinutes);
	}

	public long getNumberOfEmailsPending() {
		return emailQueueDAO.findNumberOfEmailsWithStatus("Pending");
	}

	public long getNumberOfEmailsPendingBeforeTime(int creationTimeInMinutes) {
		return emailQueueDAO.findNumberOfEmailsWithStatusBeforeTime("Pending", creationTimeInMinutes);
	}

	public long getNumberOfEmailsWithErrorsInTimePeriod(int creationTimeInMinutes) {
		return emailQueueDAO.findNumberOfEmailsWithStatusInTime("Error", creationTimeInMinutes);
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
