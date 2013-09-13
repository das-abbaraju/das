package com.picsauditing.model.general;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import com.picsauditing.report.ReportUtil;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AutocompleteDAO;

/**
 * Any custom autocomplete logic outside the simple key-value pairs will be placed here.
 */
public class AutocompleteSharing {

	@Autowired
	private AutocompleteDAO autocompleteDAO;

	public JSONArray buildSharingAutocomplete(int reportId, String searchTerm, Permissions permissions) throws SQLException {
		List<BasicDynaBean> searchResults = autocompleteDAO.findPeopleToShareWith(reportId, searchTerm, permissions);
		return mapResultsToJSON(searchResults, permissions.getLocale());
	}

	@SuppressWarnings("unchecked")
	private JSONArray mapResultsToJSON(List<BasicDynaBean> searchResults, Locale locale) {
		JSONArray jsonResults = new JSONArray();

		for (BasicDynaBean bean : searchResults) {
			JSONObject jsonResult = new JSONObject();

			jsonResult.put("id", bean.get("id"));
			jsonResult.put("name", bean.get("name"));
			jsonResult.put("type", bean.get("resultCategory"));

			String city = (String) bean.get("city");
			String countrySubdivision = (String) bean.get("countrySubdivision");
			String userLocation = (String) bean.get("userLocation");
			String location = buildLocationString(city, countrySubdivision, userLocation, locale);
			jsonResult.put("location", location);

			jsonResult.put("access_type", bean.get("type"));

			jsonResults.add(jsonResult);
		}

		return jsonResults;
	}

	private String buildLocationString(String city, String countrySubdivision, String userLocation, Locale locale) {
		String location = ReportUtil.buildLocationString(city, countrySubdivision, locale);

		if (StringUtils.isNotEmpty(userLocation)) {
			location = userLocation;
		}

		return location;
	}

}
