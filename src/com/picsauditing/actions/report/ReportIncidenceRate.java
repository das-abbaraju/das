package com.picsauditing.actions.report;

import com.picsauditing.PICS.DateBean;

public class ReportIncidenceRate extends ReportAccount {
	protected int year;
	protected double incidenceRate;
	protected boolean searchYear1 = false;
	protected boolean searchYear2 = false;
	protected boolean searchYear3 = false;
	public String execute() throws Exception {
		sql.addJoin("INNER JOIN OSHA os ON os.conID = a.id");
		if (searchYear1 == true)
			sql.addWhere("(os.recordableTotal1*200000/os.manHours1 > "
					+ incidenceRate + ")");
		if (searchYear2 == true)
			sql.addWhere("(os.recordableTotal2*200000/os.manHours2 > "
					+ incidenceRate + ")");
		if (searchYear3 == true)
			sql.addWhere("(os.recordableTotal3*200000/os.manHours3 > "
					+ incidenceRate + ")");
		
		sql.addField("os.location");
		sql.addField("os.description");
		sql.addField("os.SHAType");
		sql.addField("os.recordableTotal1");
		sql.addField("os.manHours1");
		sql.addField("os.recordableTotal2");
		sql.addField("os.manHours2");
		sql.addField("os.recordableTotal3");
		sql.addField("os.manHours3");
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

	public double getIncidenceRate() {
		return incidenceRate;
	}

	public void setIncidenceRate(double incidenceRate) {
		this.incidenceRate = incidenceRate;
	}

	public boolean isSearchYear1() {
		return searchYear1;
	}

	public void setSearchYear1(boolean searchYear1) {
		this.searchYear1 = searchYear1;
	}

	public boolean isSearchYear2() {
		return searchYear2;
	}

	public void setSearchYear2(boolean searchYear2) {
		this.searchYear2 = searchYear2;
	}

	public boolean isSearchYear3() {
		return searchYear3;
	}

	public void setSearchYear3(boolean searchYear3) {
		this.searchYear3 = searchYear3;
	}
}
