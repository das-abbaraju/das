package com.picsauditing.actions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.FlagCalculator2;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;

@SuppressWarnings("serial")
public class ContractorCron extends PicsActionSupport {

	protected FlagCalculator2 flagCalculator = null;
	protected ContractorAccountDAO contractorAccountDAO = null;

	protected long startTime = 0L;
	StringBuffer report = null;

	public ContractorCron(FlagCalculator2 fc2, ContractorAccountDAO contractorAccountDAO) {
		this.flagCalculator = fc2;
		this.contractorAccountDAO = contractorAccountDAO;
	}

	public String execute() throws Exception {

		report = new StringBuffer();

		report.append("Starting ContractorCron Job at: ");
		report.append(new Date().toString());
		report.append("\n\n");
		try {
			startTask("\nRunning contractorCron...");

			List<Integer> conIDsList = contractorAccountDAO.findContractorsNeedingRecalculation();

			for (Integer conID : conIDsList) {
				flagCalculator.runByContractor(conID);
			}

			endTask();
		} catch (Throwable t) {
			handleException(t);
		}
		report.append("\n\n\nCompleted ContractorCron Job at: ");
		report.append(new Date().toString());

		return SUCCESS;
	}

	private void handleException(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		report.append(t.getMessage());
		report.append(sw.toString());
		report.append("\n\n\n");
	}

	protected void endTask() {
		report.append("SUCCESS..(");
		report.append(new Long(System.currentTimeMillis() - startTime).toString());
		report.append(" millis )");
	}

	protected void startTask(String taskName) {
		startTime = System.currentTimeMillis();
		report.append(taskName);
	}
}
