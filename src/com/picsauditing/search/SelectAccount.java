package com.picsauditing.search;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.Permissions;
import com.picsauditing.util.PermissionQueryBuilder;

/**
 * SELECT a.id, a.name, a.active
 * FROM accounts a
 * @author Trevor
 *
 */
public class SelectAccount extends SelectSQL {
	private Type type = null;

	public static enum Type {
		Operator, Contractor
	}

	private String startsWith = "";

	public SelectAccount() {
		super();
		this.setFromTable("accounts a");
		this.addField("a.id");
		this.addField("a.name");
		this.addField("a.active");
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
		join = join + "JOIN pqfdata q" + questionID + " on q" + questionID + ".conID = a.id AND q" + questionID
				+ ".questionID = " + questionID;
		this.addJoin(join);
		this.addField("q" + questionID + ".answer AS " + columnName);
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

	public void addPQFQuestion(String questionID) {
		int temp = Integer.parseInt(questionID);
		this.addPQFQuestion(temp);
	}

	public void addAudit(int auditTypeID) {
		String join = "LEFT JOIN contractor_audit ca" + auditTypeID + " on ca" + auditTypeID + ".conID = a.id AND ca"
				+ auditTypeID + ".auditStatus IN ('Active','Exempt') AND ca" + auditTypeID + ".auditTypeID = "
				+ auditTypeID;
		this.addJoin(join);
		this.addField("ca" + auditTypeID + ".auditID AS ca" + auditTypeID + "_auditID");
		this.addField("ca" + auditTypeID + ".auditStatus AS ca" + auditTypeID + "_auditStatus");
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
			this.addWhere("a.name LIKE '" + Utilities.escapeQuotes(startsWith) + "%'");
		}
	}

	/**
	 * Limit contractor search to the accounts I can see based on my perms If
	 * I'm an operator join to flags.flag and gc.workStatus too
	 * 
	 * @param permissions
	 */
	public void setPermissions(Permissions permissions) {
		if (permissions.isOperator()) {
			// Anytime we query contractor accounts as an operator,
			// get the flag color/status at the same time
			this.addJoin("LEFT JOIN flags ON flags.conID = a.id AND flags.opID = " + permissions.getAccountId());
			this.addField("flags.flag");
			this.addField("lower(flags.flag) AS lflag");
			this.addJoin("JOIN generalcontractors gc ON gc.subID = a.id AND gc.genID = " + permissions.getAccountId());
			this.addField("gc.workStatus");
		}
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);

		this.addWhere("1 " + permQuery.toString());
	}

}
