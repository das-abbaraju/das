package com.picsauditing.actions.report;

import java.util.Date;

import com.picsauditing.PICS.DateBean;

public class ReportContractorLicenses extends ReportContractorAudits {
	// private String[] state;
	protected boolean filterConLicense = true;
	protected boolean filterExpiredLicense = true;
	protected boolean conExpiredLic = false;
	protected String validLicense = "Valid";

	public String execute() throws Exception {
		sql.addWhere("ca.auditTypeID = 1"); // PQF
		sql.addWhere("a.active = 'Y'");
		sql.addPQFQuestion(401);
		sql.addPQFQuestion(755);
		sql.addField("q401.verifiedAnswer AS verifiedAnswer401");
		sql.addField("q401.isCorrect AS isCorrect401");
		sql.addField("q755.verifiedAnswer AS verifiedAnswer755");
		sql.addField("q755.isCorrect AS isCorrect755");
		sql.addField("q401.comment AS comment401");
		sql.addField("q755.comment AS comment755");
		setOrderBy("a.name");
		if (conExpiredLic == true) {
			sql.addWhere("q755.verifiedAnswer < '" + DateBean.format(new Date(), "yyyy-MM-dd") + "'");
		}
		if (validLicense.equals("Valid"))
			sql.addWhere("q401.isCorrect = 'Yes'");
		if (validLicense.equals("UnValid"))
			sql.addWhere("q401.isCorrect <> 'Yes' OR q401.isCorrect IS NULL");
		if (validLicense.equals("All"))
			sql.addWhere("1");

		if (filtered == null)
			filtered = false;

		this.filterAuditType = false;

		return super.execute();
	}

	public boolean isFilterConLicense() {
		return filterConLicense;
	}

	public void setFilterConLicense(boolean filterConLicense) {
		this.filterConLicense = filterConLicense;
	}

	public boolean isFilterExpiredLicense() {
		return filterExpiredLicense;
	}

	public void setFilterExpiredLicense(boolean filterExpiredLicense) {
		this.filterExpiredLicense = filterExpiredLicense;
	}

	public boolean isConExpiredLic() {
		return conExpiredLic;
	}

	public void setConExpiredLic(boolean conExpiredLic) {
		this.conExpiredLic = conExpiredLic;
	}

	public String getValidLicense() {
		return validLicense;
	}

	public void setValidLicense(String validLicense) {
		this.validLicense = validLicense;
	}

}
