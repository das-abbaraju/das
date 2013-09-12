package com.picsauditing.mail;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.ListType;

@SuppressWarnings("serial")
public class EmailWizard extends PicsActionSupport {
	private ListType type = null;
	private int listSize = 0;
	
	public String execute() {
		if (!forceLogin())
			return LOGIN;

		if (type == null) {
			WizardSession wizardSession = new WizardSession(ActionContext.getContext().getSession());
			type = wizardSession.getListType();
			if (wizardSession.getIds() != null)
				listSize = wizardSession.getIds().size();
		}
		
		return SUCCESS;
	}

	public ListType getType() {
		return type;
	}

	public void setType(ListType type) {
		this.type = type;
	}
	
	public int getListSize() {
		return listSize;
	}
	
}
