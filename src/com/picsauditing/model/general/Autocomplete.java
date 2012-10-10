package com.picsauditing.model.general;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AutocompleteDAO;
import com.picsauditing.util.Strings;

/**
 * Any custom autocomplete logic outside the simple key-value pairs will be placed here.
 */
public class Autocomplete {
	
	@Autowired
	private AutocompleteDAO autocompleteDAO;
	
	public JSONObject sharingAutocomplete(int reportId, String searchTerm, Permissions permissions) throws SQLException {
		List<BasicDynaBean> searchResults = autocompleteDAO.findPeopleToShareWith(reportId, searchTerm, permissions);
		return mapResultsToJSON(searchResults);
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject mapResultsToJSON(List<BasicDynaBean> searchResults) {
		JSONObject result = new JSONObject();
		JSONArray resultRecords = new JSONArray();
		for (BasicDynaBean bean : searchResults) {
			JSONObject record = new JSONObject();
			record.put("result_type", bean.get("resultCategory"));
			record.put("result_name", bean.get("name"));
			record.put("result_id", bean.get("id"));
			record.put("search_type", bean.get("type"));
			record.put("result_at", Strings.nullToBlank(bean.get("location").toString()));
			resultRecords.add(record);
		}
		
		result.put("results", resultRecords);
		
		return result;
	}

}
