package com.picsauditing.actions.report;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;

@SuppressWarnings("serial")
public class ReportFatalities extends ReportAnnualAddendum {
	protected int year;

	public String execute() throws Exception {
		if(!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.FatalitiesReport);
		sql.addJoin("JOIN OSHA os ON os.conID = a.id");
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
