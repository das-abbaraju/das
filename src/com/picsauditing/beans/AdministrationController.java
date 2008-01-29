package com.picsauditing.beans;

import javax.faces.event.ActionEvent;

public class AdministrationController {
	
	private boolean showVerification = false;
	
	
	public boolean isShowVerification() {
		return showVerification;
	}

	public void setShowVerification(boolean showVerification) {
		this.showVerification = showVerification;
	}

	public String osha(){
		return "osha";
	}
	
	public void gotoVerification(ActionEvent event){
		showVerification = true;
	}
}
