package com.picsauditing.actions;

import org.json.simple.JSONObject;

import com.picsauditing.access.Anonymous;

@SuppressWarnings("serial")
public class TranslateJS extends PicsActionSupport {

	private JSONObject translations = new JSONObject();

	@SuppressWarnings("unchecked")
	@Anonymous
	public String execute() throws Exception {
		translations.put("ContractorAccount.name", getText("ContractorAccount.name"));
		translations.put("Kyle", "Activated on {0} by {1}");
		translations.put("Trevor", "It's a \"great\" day");
		
		return SUCCESS;
	}
	
	
	@Override
	public String getText(String aTextName) {
		// TODO Auto-generated method stub
		return super.getText(aTextName);
	}

	public JSONObject getTranslations() {
		return translations;
	}
}
