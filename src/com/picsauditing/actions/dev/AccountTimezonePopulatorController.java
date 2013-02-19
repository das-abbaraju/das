package com.picsauditing.actions.dev;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.model.general.AccountTimezonePopulator;

public class AccountTimezonePopulatorController extends PicsActionSupport {
	private static final long serialVersionUID = -1458581708515317445L;

	@Autowired
	private AccountTimezonePopulator accountAndUserTimezonePopulator;
	@Autowired
	private TaskExecutor taskExecutor;

	@Override
	public String execute() throws Exception {
		checkPermissions();
		return SUCCESS;
	}

	public String startConversion() throws Exception {
		checkPermissions();
		if (conversionIsNotRunning() && accountAndUserTimezonePopulator.getTotalAccountsWillRun() > 0) {
			taskExecutor.execute(accountAndUserTimezonePopulator);
		}
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String progressOnConversion() {
		json.put("totalAccounts", accountAndUserTimezonePopulator.getTotalAccounts());
		json.put("accountsConverted", accountAndUserTimezonePopulator.getAccountsConverted());
		json.put("populatorIsRunning", accountAndUserTimezonePopulator.isPopulatorRunning());
		json.put("totalAccountsWillRun", accountAndUserTimezonePopulator.getTotalAccountsWillRun());
		json.put("info", accountAndUserTimezonePopulator.getInfoMessage());
		return JSON;
	}

	private boolean conversionIsNotRunning() {
		return !accountAndUserTimezonePopulator.isPopulatorRunning();
	}

	public int getTotalAccountsWillRun() {
		return accountAndUserTimezonePopulator.getTotalAccountsWillRun();
	}

	public void setTotalAccountsWillRun(int totalAccountsWillRun) {
		accountAndUserTimezonePopulator.setTotalAccountsWillRun(totalAccountsWillRun);
	}

	private void checkPermissions() throws NoRightsException {
		loadPermissions();

		if (!permissions.isAdmin()) {
			throw new NoRightsException("Admin");
		}
	}

}
