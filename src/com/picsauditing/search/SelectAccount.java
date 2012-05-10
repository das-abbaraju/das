package com.picsauditing.search;

import com.picsauditing.access.Permissions;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.Strings;

/**
 * SELECT a.id, a.name, a.active FROM accounts a
 * 
 * @author Trevor
 * 
 */
public class SelectAccount extends SelectSQL {
	private Type type = null;

	public static enum Type {
		Operator,
		Contractor
	}

	private String startsWith = "";

	public SelectAccount() {
		super();
		this.setFromTable("accounts a");
		this.addField("a.id");
		this.addField("a.name");
		this.addField("a.status");
		this.addField("a.type");

	}

	public Type getType() {
		return type;
	}

	/**
	 * Can only set the type of account search once Contractor joins to
	 * contractor_info c Operator joins to operators o
	 * 
	 * @param type
	 */
	public void setType(Type type) {
		if (type == null)
			return;
		if (this.type != null)
			return;

		this.type = type;
		if (type == Type.Contractor) {
			this.addJoin("JOIN contractor_info c ON a.id = c.id");
			this.addWhere("a.type='Contractor'");
		}
		if (type == Type.Operator) {
			this.addJoin("JOIN operators o ON a.id = o.id");
		}
	}

	/**
	 * JOIN to pqfdata for this contractor and add q123.answer to the field list
	 * 
	 * @param questionID
	 *            PQF question ID must be > 0
	 * @param require
	 *            if true, do an INNER JOIN, else LEFT JOIN
	 */
	public void addPQFQuestion(int questionID, boolean require, String columnName) {
		String join = "";
		if (!require)
			join = "LEFT ";
		join = join + "JOIN pqfdata q" + questionID + " on q" + questionID + ".auditID = pqf.id AND q" + questionID
				+ ".questionID = " + questionID;
		this.addJoin(join);
		this.addField("q" + questionID + ".answer AS " + columnName);
	}

	public void addAnnualQuestion(int questionID, boolean require, String columnName, String name) {
		String join = "";
		if (!require)
			join = "LEFT ";
		join = join + "JOIN pqfdata q" + name + " on q" + name + ".auditID = " + name + ".id AND q" + name
				+ ".questionID = " + questionID;
		this.addJoin(join);
		this.addField("q" + name + ".answer AS " + columnName);
	}

	public void addAuditQuestion(int questionID, int auditTypeID, boolean require) {
		String name = "ca" + questionID;
		this.addJoin("LEFT JOIN contractor_audit " + name + " ON " + name + ".conID = a.id AND " + name
				+ ".auditTypeID = " + auditTypeID + " AND (" + name
				+ ".expiresDate IS NULL OR " + name
				+ ".expiresDate > NOW())");

		String join = "";
		if (!require)
			join = "LEFT ";
		join = join + "JOIN pqfdata q" + questionID + " on q" + questionID + ".auditID = " + name + ".id AND q"
				+ questionID + ".questionID = " + questionID;
		this.addJoin(join);
		this.addField("q" + questionID + ".answer AS answer" + questionID);
	}

	/**
	 * LEFT JOIN to pqfdata for this contractor and add q123.answer to the field
	 * list
	 * 
	 * @param questionID
	 *            PQF question ID must be > 0
	 * @param require
	 *            if true, do an INNER JOIN, else LEFT JOIN
	 */
	public void addPQFQuestion(int questionID) {
		this.addPQFQuestion(questionID, false, "answer" + questionID);
	}

	public void addAudit(int auditTypeID) {
		String join = "LEFT JOIN contractor_audit ca" + auditTypeID + " on ca" + auditTypeID + ".conID = a.id " +
				"AND ca" + auditTypeID + ".auditTypeID = "+ auditTypeID;
		join += " LEFT JOIN contractor_audit_operator cao" + auditTypeID + " ON cao" + auditTypeID + ".auditID = ca"+auditTypeID+".id " +
				"AND cao"+auditTypeID + ".visible = 1 AND cao"+auditTypeID+".status IN ('Complete')";
		this.addJoin(join);
		this.addField("ca" + auditTypeID + ".id AS ca" + auditTypeID + "_auditID");
	}

	public String getStartsWith() {
		return startsWith;
	}

	/**
	 * Support search by first character in the name
	 * 
	 * @param startsWith
	 */
	public void setStartsWith(String startsWith) {
		if (startsWith != null && startsWith.length() > 0) {
			this.startsWith = startsWith;
			this.addWhere("a.name LIKE '" + Strings.escapeQuotes(startsWith) + "%'");
		}
	}

	/**
	 * Limit contractor search to the accounts I can see based on my perms If
	 * I'm an operator join to gc.flag and gc.workStatus too
	 * 
	 * @param permissions
	 */
	public void setPermissions(Permissions permissions) {
		if (permissions.isOperatorCorporate()) {
			// Anytime we query contractor accounts as an operator,
			// get the flag color/status at the same time
			if (!this.hasJoin("generalcontractors gc"))
				this.addJoin("JOIN generalcontractors gc ON gc.subID = a.id AND gc.genID = "
						+ permissions.getAccountId());
			this.addField("gc.workStatus");
			this.addField("gc.flag");
			this.addField("lower(gc.flag) AS lflag");
			this.addField("gc.forceEnd");
			this.addWhere("gc.genID = " + permissions.getAccountId());
			
			this.addJoin("LEFT JOIN flag_data_override fdo on fdo.conID=a.id and fdo.forceEnd > NOW() and fdo.opID="
					+ permissions.getAccountId());
			this.addField("fdo.forceEnd as 'dataForceEnd'");
		}
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);

		this.addWhere("1 " + permQuery.toString());
	}

}
