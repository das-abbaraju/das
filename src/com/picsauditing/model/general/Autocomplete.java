package com.picsauditing.model.general;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AutocompleteDAO;

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
			record.put("result_type", bean.get("result_type"));
			record.put("result_name", bean.get("result_name"));
			record.put("result_id", bean.get("result_id"));
			record.put("search_type", bean.get("search_type"));
			record.put("result_at", bean.get("result_at"));
			resultRecords.add(record);
		}
		
		result.put("results", resultRecords);
		
		return result;
	}

}
