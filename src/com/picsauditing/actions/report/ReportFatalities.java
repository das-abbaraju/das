package com.picsauditing.actions.report;

import com.picsauditing.PICS.DateBean;

public class ReportFatalities extends ReportContractorAudits {
	protected int year;

	public String execute() throws Exception {
		sql.addJoin("INNER JOIN OSHA os ON os.conID = a.id");
		sql.addField("os.fatalities1");
		sql.addField("os.fatalities2");
		sql.addField("os.fatalities3");
		return super.execute();
	}

	public int getYear() {
		return DateBean.getCurrentYear();
    }

	public void setYear(int year) {
		this.year = year;
	}




}
