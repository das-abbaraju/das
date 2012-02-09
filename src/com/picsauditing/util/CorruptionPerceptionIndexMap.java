package com.picsauditing.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Country;
/**
 * This class wraps a map of ISO codes to Corruption Perception Indices
 * @author Lucas
 *
 */
@SuppressWarnings("serial")
public class CorruptionPerceptionIndexMap extends PicsActionSupport {

	@Autowired
	protected CountryDAO countryDAO;
	/*testable*/ Map<String, Double> map = null;
	

	public List<Double> findCorruptionPerceptionIndices(String unparsedJsonCountries) {
		initialize();

	
		List<String> isoCodesList = convertJsonToIsoCodes(unparsedJsonCountries);
		List<Double> corruptionPerceptionIndices = new ArrayList<Double>();
		for (String isoCode: isoCodesList) {
			corruptionPerceptionIndices.add(map.get(isoCode.trim()));
		}
		return corruptionPerceptionIndices;
	}
	
	private List<String> convertJsonToIsoCodes(String unprasedJsonCountries) {
		List<String> isoCodes = new ArrayList<String>();
		JSONArray countriesArray = (JSONArray) JSONValue.parse(unprasedJsonCountries);
	
		for (Object country: countriesArray.toArray()) {
			JSONObject object = (JSONObject) country;
			isoCodes.add(object.get("id").toString());
		}
		
		return isoCodes;
	}
	
	public Double findCorruptionPerceptionIndex(String isoCode) {
		initialize();
		return map.get(isoCode.trim());
	}


	private void initialize() {
		if (map == null) {
			map = new HashMap<String, Double>();
		
			for (Country country: countryDAO.findAll()) {
				map.put(country.getIsoCode(), country.getCorruptionPerceptionIndex());
			}
		}
	}

}
