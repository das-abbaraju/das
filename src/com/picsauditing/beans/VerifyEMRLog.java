package com.picsauditing.beans;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.picsauditing.jpa.entities.PqfLog;

public class VerifyEMRLog extends Verifier<PqfLog, Short> implements Verifiable<Short> {
	
	private Short id;
	
	
	

	@Override
	public String edit() {
		return "edit";
	}

	
	@Override
	public String verifyLog() {	
		try{
			if(getEntity().getIsCorrect().equals("Yes")){
				Calendar cal = Calendar.getInstance();
				getEntity().setDateVerified(cal.getTime());
			}else{
				getEntity().setDateVerified(null);
			}
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
	
	
	public boolean isCorrect() {
		return getEntity().getIsCorrect().equals("Yes")? true : false;
	}
	
	public void setCorrect(boolean isCorrect){
		if (isCorrect)
			getEntity().setIsCorrect("Yes");
		else
			getEntity().setIsCorrect("No");
		
		verifyLog();
		
	}

	
	

}
