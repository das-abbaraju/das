package com.picsauditing.auditbuilder.util;

public class CorruptionPerceptionIndexMap {
//
//	@Autowired
//	protected CountryDAO countryDAO;
//	private Map<String, Double> map = null;
//
//
//	public List<Double> findCorruptionPerceptionIndices(String unparsedJsonCountries) {
//		initialize();
//
//		List<Double> corruptionPerceptionIndices = new ArrayList<Double>();
//
//		if (!Strings.isEmpty(unparsedJsonCountries)) {
//			List<String> isoCodesList = convertJsonToIsoCodes(unparsedJsonCountries);
//
//			for (String isoCode: isoCodesList) {
//				corruptionPerceptionIndices.add(map.get(isoCode.trim()));
//			}
//		}
//		return corruptionPerceptionIndices;
//	}
//
//	private List<String> convertJsonToIsoCodes(String unparsedJsonCountries) {
//		List<String> isoCodes = new ArrayList<String>();
//		JSONArray countriesArray = (JSONArray) JSONValue.parse(unparsedJsonCountries);
//
//		if (countriesArray != null) {
//			for (Object country: countriesArray.toArray()) {
//				JSONObject object = (JSONObject) country;
//				isoCodes.add(object.get("id").toString());
//			}
//		}
//
//		return isoCodes;
//	}
//
//	public Double findCorruptionPerceptionIndex(String isoCode) {
//		initialize();
//		return map.get(isoCode.trim());
//	}
//
//
//	private void initialize() {
//		if (map == null) {
//			map = new HashMap<String, Double>();
//
//			for (Country country: countryDAO.findAll()) {
//				map.put(country.getIsoCode(), country.getCorruptionPerceptionIndex());
//			}
//		}
//	}
//
}
