package com.picsauditing.actions;

import java.sql.Connection;
import java.sql.ResultSet;

import org.json.simple.JSONObject;

import com.google.common.base.Strings;
import com.picsauditing.PICS.DBBean;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class AuditRejectionLookup extends PicsActionSupport { 
	
	private static final String TRANSLATION_KEY_FOR_REJECTION_CODE = "Insurance.Rejection.Reason.Code.%";
	
	@Override
	public String execute() throws Exception {
		SelectSQL selectSQL = new SelectSQL("app_translation");
		selectSQL.addField("msgKey");
		selectSQL.addField("msgValue");
		selectSQL.addWhere(buildWhereClause());
		
		Connection conn =  DBBean.getDBConnection();
		ResultSet results = conn.createStatement().executeQuery(selectSQL.toString());
		
		populateJsonArray(results);
		
		return SUCCESS;
	}
	
	private String buildWhereClause() {
		return ("locale = '" + getLocaleStatic().getLanguage() 
				+  "' AND msgKey LIKE '" + TRANSLATION_KEY_FOR_REJECTION_CODE + "'");
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
