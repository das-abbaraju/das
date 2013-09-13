package com.picsauditing.search;

public class SelectContractorAudit extends SelectAccount {

	public SelectContractorAudit() {
		super();
		this.setType(Type.Contractor);

		this.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		this.addJoin("JOIN audit_type atype ON atype.id = ca.auditTypeID");

		this.addField("ca.id auditID");
		this.addField("ca.auditTypeID");
		this.addField("CONCAT('AuditType.',atype.id,'.name') `atype.name`");

	}

	public void setAuditTypeID(int auditTypeID) {
		this.addWhere("ca.auditTypeID=" + auditTypeID);
	}

	/**
	 * JOIN to pqfdata for this contractor and add q123.answer to the field list
	 * 
	 * @param questionID
	 *            PQF question ID must be > 0
	 * @param require
	 *            if true, do an INNER JOIN, else LEFT JOIN
	 */
	@Override
	public void addPQFQuestion(int questionID, boolean require, String columnName) {
		String join = "";
		if (!require)
			join = "LEFT ";
		join = join + "JOIN pqfdata q" + questionID + " on q" + questionID + ".auditid = ca.id AND q" + questionID
				+ ".questionID = " + questionID;
		this.addJoin(join);
		this.addField("q" + questionID + ".answer AS " + columnName);
	}
}
