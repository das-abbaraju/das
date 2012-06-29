package com.picsauditing.actions;

import java.sql.Connection;
import java.sql.ResultSet;

import org.json.simple.JSONObject;

import com.google.common.base.Strings;
import com.picsauditing.PICS.DBBean;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.DatabaseUtil;

@SuppressWarnings("serial")
public class AuditRejectionLookup extends PicsActionSupport {

	private static final String TRANSLATION_KEY_PREFIX = "Insurance.Rejection.Reason.Code.";
	private static final String TRANSLATION_KEY_FOR_REJECTION_CODE = TRANSLATION_KEY_PREFIX + "%";

	@Override
	public String execute() throws Exception {
		SelectSQL selectSQL = new SelectSQL("app_translation");
		selectSQL.addField("msgKey");
		selectSQL.addField("msgValue");
		selectSQL.addWhere(buildWhereClause());

		Connection conn = null;
		ResultSet results = null;
		try {
			conn = DBBean.getDBConnection();
			results = conn.createStatement().executeQuery(selectSQL.toString());
		} finally {
			DatabaseUtil.closeResultSet(results);
			DatabaseUtil.closeConnection(conn);
		}

		populateJsonArray(results);

		return SUCCESS;
	}

	private String buildWhereClause() {
		return ("locale = '" + getLocaleStatic().getLanguage() + "' AND msgKey LIKE '"
				+ TRANSLATION_KEY_FOR_REJECTION_CODE + "'");
	}

	@SuppressWarnings("unchecked")
	private void populateJsonArray(ResultSet results) throws Exception {
		if (results == null)
			return;
		
		while (results.next()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", parseCode(results.getString(1)));
			jsonObject.put("value", results.getString(2));
			jsonArray.add(jsonObject);
		}
	}

	private String parseCode(String msgKey) {
		if (!Strings.isNullOrEmpty(msgKey)) {
			if (msgKey.length() > TRANSLATION_KEY_PREFIX.length()) {
				return msgKey.substring(TRANSLATION_KEY_PREFIX.length());
			}
		}

		return Strings.nullToEmpty(msgKey);
	}

}
