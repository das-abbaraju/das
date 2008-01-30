package com.picsauditing.beans;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import com.picsauditing.PICS.PermissionsBean;
import com.picsauditing.access.OpPerms;

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
	
	public String login(){
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession session = (HttpSession)externalContext.getSession(false);
		PermissionsBean pBean = (PermissionsBean)session.getAttribute("pBean");
		
		if (pBean.isAdmin())
			return "hasAccess";
		
		return "noAccess";
		
	}
}
