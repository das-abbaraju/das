package com.picsauditing.model.general;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import com.picsauditing.PICS.I18nCache;
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

	private static I18nCache i18nCache = I18nCache.getInstance();

	public JSONArray buildSharingAutocomplete(int reportId, String searchTerm, Permissions permissions) throws SQLException {
		List<BasicDynaBean> searchResults = autocompleteDAO.findPeopleToShareWith(reportId, searchTerm, permissions);
		return mapResultsToJSON(searchResults, permissions);
	}

	@SuppressWarnings("unchecked")
	private JSONArray mapResultsToJSON(List<BasicDynaBean> searchResults, Permissions permissions) {
		JSONArray jsonResults = new JSONArray();

		for (BasicDynaBean bean : searchResults) {
			JSONObject jsonResult = new JSONObject();

			jsonResult.put("id", bean.get("id"));
			jsonResult.put("name", bean.get("name"));
			jsonResult.put("type", bean.get("resultCategory"));
			jsonResult.put("location", buildLocationString(bean, permissions.getLocale()));
			jsonResult.put("access_type", bean.get("type"));

			jsonResults.add(jsonResult);
		}

		return jsonResults;
	}

	private String buildLocationString(BasicDynaBean bean, Locale locale) {
		String location = "";

		String city = (String) bean.get("city");
		String cityLocation = "";
		if (StringUtils.isNotEmpty(city)) {
			cityLocation = city + ", ";
		}

		String countrySubdivision = (String) bean.get("countrySubdivision");
		if (StringUtils.isNotEmpty(countrySubdivision)) {
			String key = "CountrySubdivision." + countrySubdivision;
			String translatedCountrySubdivision = i18nCache.getText(key, locale);

			if (key != translatedCountrySubdivision) {
				location = cityLocation + translatedCountrySubdivision;
			}
		}

		String userLocation = (String) bean.get("userLocation");
		if (StringUtils.isNotEmpty(userLocation)) {
			location = userLocation;
		}

		return location;
	}

}
