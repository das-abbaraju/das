package com.picsauditing.actions.report;

@SuppressWarnings("serial")
public class ReportLifetimeMembership extends ReportAccount {
	
	@Override
	public void buildQuery(){
		super.buildQuery();
		
		sql.addField("a.active");
		sql.addField("c.payingFacilities");
		
		sql.addWhere("c.mustPay = 'No'");
	}
}
