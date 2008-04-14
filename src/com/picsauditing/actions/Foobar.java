package com.picsauditing.actions;

import java.util.Date;

import com.picsauditing.jpa.entities.YesNo;

public class Foobar extends PicsActionSupport {
	private YesNo varEnum;
	private String varString;
	private boolean varBool;
	private Date varDate;
	private int varInt;
	
	public String execute() {
		message = "";
		if (varEnum != null)
			message += "<br>Enum: "+varEnum.toString();
		if (varString != null)
			message += "<br>String: "+varString;
		if (varBool)
			message += "<br>Bool: "+varBool;
		if (varDate != null)
			message += "<br>Date: "+varDate.toString();
		if (varInt > 0)
			message += "<br>Int: "+varInt;
		
		return SUCCESS;
	}

	public YesNo getVarEnum() {
		return varEnum;
	}

	public void setVarEnum(YesNo varEnum) {
		this.varEnum = varEnum;
	}

	public String getVarString() {
		return varString;
	}

	public void setVarString(String varString) {
		this.varString = varString;
	}

	public boolean isVarBool() {
		return varBool;
	}

	public void setVarBool(boolean varBool) {
		this.varBool = varBool;
	}

	public Date getVarDate() {
		return varDate;
	}

	public void setVarDate(Date varDate) {
		this.varDate = varDate;
	}

	public int getVarInt() {
		return varInt;
	}

	public void setVarInt(int varInt) {
		this.varInt = varInt;
	}

}
