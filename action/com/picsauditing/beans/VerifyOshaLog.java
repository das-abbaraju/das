package com.picsauditing.beans;

import java.util.Date;

import com.picsauditing.jpa.entities.OshaLog;

public class VerifyOshaLog extends Verifier<OshaLog, Short> implements Verifiable<Short> {

	private Short id;
		
	
	@Override
	public String edit() {
		return "edit";
	}

	
	@Override
	public String verifyLog() {
		try{
			if(getEntity().getVerifiedDate1() != null && getEntity().getVerifiedDate2() != null && getEntity().getVerifiedDate3() != null)
				getEntity().setVerifiedDate(getTodaysDate());
		}catch(Exception ex){
			
		}
		
		return null;
				
	}	
	

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}
	
	public boolean isVerifiedDate1() {
		return getEntity().getVerifiedDate1()!= null ? true : false;
		
	}
	
	public void setVerifiedDate1(boolean verified){
		
		if (verified)
			getEntity().setVerifiedDate1(new Date());
		else
			getEntity().setVerifiedDate1(null);
		
		verifyLog();
		
	}
	
	public boolean isVerifiedDate2() {
		return getEntity().getVerifiedDate2()!= null ? true : false;
	}
	public void setVerifiedDate2(boolean verified){
		if (verified)
			getEntity().setVerifiedDate2(new Date());
		else
			getEntity().setVerifiedDate2(null);
		verifyLog();
		
	}

	public boolean isVerifiedDate3() {
		return getEntity().getVerifiedDate3()!= null ? true : false;
	}
	
	public void setVerifiedDate3(boolean verified){
		if (verified)
			getEntity().setVerifiedDate3(new Date());
		else
			getEntity().setVerifiedDate3(null);
		
		verifyLog();
		
	}

			
	public boolean isNa1() {
		
		return (getEntity().getNa1() != null && getEntity().getNa1(). equals("Yes")) ? true : false;
	}

	public void setNa1(boolean na1) {
		if (na1)
			getEntity().setNa1("Yes");
		else
			getEntity().setNa1("No");
			
	}

	public boolean isNa2() {
		return (getEntity().getNa2() != null && getEntity().getNa2(). equals("Yes")) ? true : false;
		
	}

	public void setNa2(boolean na2) {
		if (na2)
			getEntity().setNa2("Yes");
		else
			getEntity().setNa2("No");
	}

	public boolean isNa3() {
		return (getEntity().getNa3() != null && getEntity().getNa3(). equals("Yes")) ? true : false;
	}

	public void setNa3(boolean na3) {
		if (na3)
			getEntity().setNa3("Yes");
		else
			getEntity().setNa3("No");
	}
	
	
}
