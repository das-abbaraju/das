package com.picsauditing.actions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONObject;

import com.google.common.base.Strings;
import com.picsauditing.PICS.DBBean;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class AuditRejectionLookup extends PicsActionSupport { 
	
	@Override
	public String execute() throws Exception {
		// FIXME: restrict this query to the user's preferred language
		SelectSQL selectSQL = new SelectSQL("app_translation");
		selectSQL.addField("msgKey");
		selectSQL.addField("msgValue");
		selectSQL.addWhere("msgKey like 'Insurance.Rejection.Reason.Code.%'");
		
		Connection conn =  DBBean.getDBConnection();
		// TODO: move this out of the controller and into the DAO Layer without using Dyna Beans
		ResultSet results = conn.createStatement().executeQuery(selectSQL.toString());
		
		populateJsonArray(results);
		
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	private void populateJsonArray(ResultSet results) throws Exception {
		while (results.next()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", parseCode(results.getString(1)));
			jsonObject.put("value", results.getString(2));
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
