package com.picsauditing.beans;

import com.picsauditing.jpa.entities.PqfLog;

public class VerifyEMRLog extends Verifier<PqfLog, Short> implements Verifiable<Short> {
	
	private Short id;
	
	
	

	@Override
	public String edit() {
		return "edit";
	}

	
	@Override
	public String verifyLog() {	
		return null;
	}

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}
	
	

	
	

}
