package com.picsauditing.PICS;

public class SearchAccounts extends SearchRaw {

	private Type type;
	public enum Type {Operator, Contractor, Admin, Auditor}

	public SearchAccounts() {
		super();
		this.sql.setFromTable("accounts a");
		this.sql.addField("a.id");
		this.sql.addField("a.name");
		this.sql.addField("a.active");
	}
	
	public Type getType() {
		return type;
	}

	/**
	 * Can only set the type of account search once
	 * Contractor joins to contractor_info c
	 * Operator joins to operators o
	 * @param type
	 */
	public void setType(Type type) {
		if (type == null) return;
		if (this.type != null) return;
		
		this.type = type;
		this.sql.addWhere("a.type = '"+this.type.toString()+"'");
		if (type == Type.Contractor) {
			this.sql.addJoin("JOIN contractor_info c ON a.id = c.id");
		}
		if (type == Type.Operator) {
			this.sql.addJoin("JOIN operators o ON a.id = o.id");
		}
	}
	
	public void addPQFQuestion() {
		
	}
}
