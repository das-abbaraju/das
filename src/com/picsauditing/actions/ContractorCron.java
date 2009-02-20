package com.picsauditing.actions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.FlagCalculator2;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class ContractorCron extends PicsActionSupport {

	protected FlagCalculator2 flagCalculator = null;
	protected ContractorAccountDAO contractorAccountDAO = null;

	protected long startTime = 0L;

	public ContractorCron(FlagCalculator2 fc2, ContractorAccountDAO contractorAccountDAO) {
		this.flagCalculator = fc2;
		this.contractorAccountDAO = contractorAccountDAO;
	}

	public String execute() throws Exception {

		PicsLogger.start("contractorCron");

		PicsLogger.log("Starting ContractorCron Job at: ");
		PicsLogger.log(new Date().toString());
		try {
			startTask("Running contractorCron...");

			List<Integer> conIDsList = contractorAccountDAO.findContractorsNeedingRecalculation();

			flagCalculator.runByContractors(conIDsList);

			endTask();
		} catch (Throwable t) {
			handleException(t);
		}
		PicsLogger.log("Completed ContractorCron Job at: ");
		PicsLogger.log(new Date().toString());

		PicsLogger.stop();
		return SUCCESS;
	}

	private void handleException(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		PicsLogger.log(t.getMessage());
		PicsLogger.log(sw.toString());
	}

	protected void endTask() {
		PicsLogger.log("SUCCESS..(");
		PicsLogger.log(new Long(System.currentTimeMillis() - startTime).toString());
		PicsLogger.log(" millis )");
	}

	protected void startTask(String taskName) {
		startTime = System.currentTimeMillis();
		PicsLogger.log(taskName);
	}
}
