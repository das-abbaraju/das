package com.picsauditing.actions.report;


public class ReportContractorLicenses extends ReportContractorAudits {
	//private String[] state;
	
	public String execute() throws Exception {
		sql.addWhere("ca.auditTypeID = 1"); // PQF
		sql.addWhere("a.active = 'Y'");
		sql.addPQFQuestion(401);
		sql.addPQFQuestion(755);
		sql.addField("q401.verifiedAnswer AS verifiedAnswer401");
		sql.addField("q401.isCorrect AS isCorrect401");
		sql.addField("q755.verifiedAnswer AS verifiedAnswer755");
		sql.addField("q755.isCorrect AS isCorrect755");
		setOrderBy("a.name");
		
		if(filtered == null) 
			filtered = false;
		
		return super.execute();
	}
}
