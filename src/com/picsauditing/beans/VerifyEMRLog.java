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
	
	
	public String getCorrect() {
		if(getEntity().getIsCorrect() == null)
			return "No";
		
		return getEntity().getIsCorrect().equals("Yes")? "Yes" : "No";
	}
	
	public void setCorrect(String isCorrect){
		if (isCorrect.equals("Yes"))
			getEntity().setIsCorrect("Yes");
		else
			getEntity().setIsCorrect("No");
		
		verifyLog();
		
	}
	
	public String getVerifiedAnswer(){
		if(getEntity().getIsCorrect() == null || getEntity().getIsCorrect().equals("No"))
			return getEntity().getVerifiedAnswer();		
		else
			return getEntity().getAnswer();
	}
	
	public void setVerifiedAnswer(String answer){
		if(getEntity().getIsCorrect() == null || getEntity().getIsCorrect().equals("No"))
			getEntity().setVerifiedAnswer(answer);
		else
			getEntity().setVerifiedAnswer(getEntity().getAnswer());
			
	}

	
	

}
