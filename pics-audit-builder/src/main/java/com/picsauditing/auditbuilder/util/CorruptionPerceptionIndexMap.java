package com.picsauditing.auditbuilder.util;

import com.picsauditing.auditbuilder.dao.CountryDAO;
import com.picsauditing.auditbuilder.entities.Country;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CorruptionPerceptionIndexMap {

	@Autowired
	protected CountryDAO countryDAO;
	private Map<String, Double> map = null;

	public List<Double> findCorruptionPerceptionIndices(String unparsedJsonCountries) {
		initialize();

		List<Double> corruptionPerceptionIndices = new ArrayList<>();

		if (!Strings.isEmpty(unparsedJsonCountries)) {
			List<String> isoCodesList = convertJsonToIsoCodes(unparsedJsonCountries);

			for (String isoCode: isoCodesList) {
				corruptionPerceptionIndices.add(map.get(isoCode.trim()));
			}
		}
		return corruptionPerceptionIndices;
	}

	private List<String> convertJsonToIsoCodes(String unparsedJsonCountries) {
		List<String> isoCodes = new ArrayList<>();
		JSONArray countriesArray = (JSONArray) JSONValue.parse(unparsedJsonCountries);

		if (countriesArray != null) {
			for (Object country: countriesArray.toArray()) {
				JSONObject object = (JSONObject) country;
				isoCodes.add(object.get("id").toString());
			}
		}

		return isoCodes;
	}

	private void initialize() {
		if (map == null) {
			map = new HashMap<>();

			for (Country country: countryDAO.findAll()) {
				map.put(country.getIsoCode(), country.getCorruptionPerceptionIndex());
			}
		}
	}
}