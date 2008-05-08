package com.picsauditing.actions.report;

import com.picsauditing.PICS.DateBean;

public class ReportFatalities extends ReportAccount {
	protected int year;

	public String execute() throws Exception {
		sql.addJoin("INNER JOIN OSHA os ON os.conID = a.id");
		sql.addWhere("os.fatalities1 >0 OR os.fatalities2 >0 OR os.fatalities3 >0");
		sql.addField("os.fatalities1");
		sql.addField("os.fatalities2");
		sql.addField("os.fatalities3");
		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		sql.addField("ca.auditID");
		sql.addWhere("ca.auditTypeID = 1");
		
		return super.execute();
	}

	public int getYear() {
		return DateBean.getCurrentYear();
    }

	public void setYear(int year) {
		this.year = year;
	}




}
