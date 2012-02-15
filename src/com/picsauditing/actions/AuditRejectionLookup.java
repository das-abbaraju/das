package com.picsauditing.actions;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONObject;

import com.google.common.base.Strings;
import com.picsauditing.search.Database;

@SuppressWarnings("serial")
public class AuditRejectionLookup extends PicsActionSupport { 
	
	@Override
	public String execute() throws Exception {
		Database database = new Database();
		// TODO: move this out of the controller and into the DAO Layer without using Dyna Beans
		List<BasicDynaBean> results = database.select("Select msgKey, msgValue from app_translation where msgKey like 'Insurance.Rejection.Reason.Code.%'", false);
		populateJsonArray(results);
		
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	private void populateJsonArray(List<BasicDynaBean> results) {
		for (BasicDynaBean value : results) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", parseCode((String) value.get("msgKey")));
			jsonObject.put("value", value.get("msgValue"));
			jsonArray.add(jsonObject);
		}
	}
	
	private String parseCode(String msgKey) {
		if (!Strings.isNullOrEmpty(msgKey)) {
			int index = msgKey.lastIndexOf(".");
			if (index > -1 && index != (msgKey.length() - 1)) {
				return msgKey.substring(index + 1);
			}
		}
		
		return Strings.nullToEmpty(msgKey);
	}
	
}
