package com.picsauditing.actions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import com.picsauditing.PICS.FlagCalculator2;
import com.picsauditing.cron.CronMetricsAggregator;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class ContractorCron extends PicsActionSupport {

	protected FlagCalculator2 flagCalculator = null;
	protected ContractorAccountDAO contractorAccountDAO = null;
	protected CronMetricsAggregator cronMetrics = null;

	protected long startTime = 0L;

	private int[] conID = null;
	private int[] opID = null;

	public ContractorCron(FlagCalculator2 fc2, ContractorAccountDAO contractorAccountDAO,
			CronMetricsAggregator cronMetrics) {
		this.flagCalculator = fc2;
		this.contractorAccountDAO = contractorAccountDAO;
		this.cronMetrics = cronMetrics;
	}

	public String execute() throws Exception {

		if (cronMetrics.startJob()) {
			flagCalculator.setCronMetrics(cronMetrics);

			PicsLogger.start("ContractorCron");

			try {
				if (conID != null) {
					if (opID != null)
						flagCalculator.run(conID, opID);
					else
						flagCalculator.run(conID, null);

					addActionMessage("ContractorCron processed contractor " + conID);
				} else {
					List<Integer> conIDsList = contractorAccountDAO.findContractorsNeedingRecalculation();

					if (conIDsList != null && conIDsList.size() > 0) {
						flagCalculator.runByContractors(conIDsList);
					}
					addActionMessage("ContractorCron processed " + conIDsList.size() + " record(s)");
				}

				PicsLogger.log("Cron completed successfully");
			} catch (Throwable t) {
				addActionError("ContractorCron failed: " + t.getMessage());
				handleException(t);
			}

			PicsLogger.stop();
			cronMetrics.stopJob();
		} else {
			addActionError("ContractorCron was already running");
		}
		return SUCCESS;
	}

	private void handleException(Throwable t) {
		t.printStackTrace();
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		PicsLogger.log(t.getMessage());
		PicsLogger.log(sw.toString());
	}

	protected void startTask(String taskName) {
		PicsLogger.log(taskName);
	}

	public int[] getConID() {
		return conID;
	}

	public void setConID(int[] conID) {
		this.conID = conID;
	}

	public int[] getOpID() {
		return opID;
	}

	public void setOpID(int[] opID) {
		this.opID = opID;
	}

}
