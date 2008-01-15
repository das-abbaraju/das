package com.picsauditing.beans;

import java.io.Serializable;

public interface Verifiable<ID extends Serializable> {
	
	public ID getId();
	public void setId(ID id);
	public String verifyLog();
	public String edit();
	

}
