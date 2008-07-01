package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditStatus;

public class ReportPQFVerification extends ReportContractorAudits {
	protected boolean filterOshaEmr = true;
	private boolean osha1 = false;
	private boolean osha2 = false;
	private boolean osha3 = false;
	private boolean emr07 = false;
	private boolean emr06 = false;
	private boolean emr05 = false;

	public String execute() throws Exception {
		loadPermissions();
		permissions.tryPermission(OpPerms.AuditVerification);
		sql.addField("c.notes");
		sql.addWhere("ca.auditStatus IN ('" + AuditStatus.Submitted + "','" + AuditStatus.Pending + "')");
		sql.addWhere("ca.auditTypeID = 1");
		sql.addJoin("LEFT JOIN osha os ON os.conID = a.id AND os.location = 'Corporate'");
		sql.addField("os.verifiedDate1");
		sql.addField("os.verifiedDate2");
		sql.addField("os.verifiedDate3");
		sql.addPQFQuestion(1617);
		sql.addPQFQuestion(1519);
		sql.addPQFQuestion(889);
		sql.addField("q1617.dateVerified as dateVerified07");
		sql.addField("q1519.dateVerified as dateVerified06");
		sql.addField("q889.dateVerified as dateVerified05");

		if (osha1) {
			sql.addWhere("os.verifiedDate1 IS NULL");
		}
		if (osha2) {
			sql.addWhere("os.verifiedDate2 IS NULL");
		}
		if (osha3) {
			sql.addWhere("os.verifiedDate3 IS NULL");
		}
		if (emr07) {
			sql.addWhere("q1617.dateVerified IS NULL OR q1617.dateVerified='0000-00-00'");
		}
		if (emr06) {
			sql.addWhere("q1519.dateVerified IS NULL OR q1519.dateVerified='0000-00-00'");
		}
		if (emr05) {
			sql.addWhere("q889.dateVerified IS NULL OR q889.dateVerified='0000-00-00'");
		}
		
		if(filtered == null) 
			filtered = false;
				
		return super.execute();
	}

	public boolean isFilterOshaEmr() {
		return filterOshaEmr;
	}

	public void setFilterOshaEmr(boolean filterOshaEmr) {
		this.filterOshaEmr = filterOshaEmr;
	}

	public boolean isOsha1() {
		return osha1;
	}

	public void setOsha1(boolean osha1) {
		this.osha1 = osha1;
	}

	public boolean isOsha2() {
		return osha2;
	}

	public void setOsha2(boolean osha2) {
		this.osha2 = osha2;
	}

	public boolean isOsha3() {
		return osha3;
	}

	public void setOsha3(boolean osha3) {
		this.osha3 = osha3;
	}

	public boolean isEmr07() {
		return emr07;
	}

	public void setEmr07(boolean emr07) {
		this.emr07 = emr07;
	}

	public boolean isEmr06() {
		return emr06;
	}

	public void setEmr06(boolean emr06) {
		this.emr06 = emr06;
	}

	public boolean isEmr05() {
		return emr05;
	}

	public void setEmr05(boolean emr05) {
		this.emr05 = emr05;
	}

}
