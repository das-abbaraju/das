package com.picsauditing.mail;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.ListType;

public class EmailWizard extends PicsActionSupport {
	private ListType type = null;
	
	public String execute() {
		if (!forceLogin())
			return LOGIN;

		if (type == null) {
			WizardSession wizardSession = new WizardSession(ActionContext.getContext().getSession());
			type = wizardSession.getListType();
		}
		
		return SUCCESS;
	}

	public ListType getType() {
		return type;
	}

	public void setType(ListType type) {
		this.type = type;
	}
	
	
	
}
