package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagData;

public class ContractorFlagCriteriaList {

	private List<String> categories = new ArrayList<String>();
	private Map<String, List<FlagCriteria>> categoryMap = new HashMap<String, List<FlagCriteria>>();
	private Map<FlagCriteria, List<FlagData>> criteriaMap = new HashMap<FlagCriteria, List<FlagData>>();

	public ContractorFlagCriteriaList(List<FlagData> list) {
		for (FlagData flagData : list) {
			String category = flagData.getCriteria().getCategory();
			if (!categoryMap.containsKey(category)) {
				categoryMap.put(category, new ArrayList<FlagCriteria>());
				categories.add(category);
			}
			FlagCriteria criteria = flagData.getCriteria();
			if (!criteriaMap.containsKey(criteria)) {
				criteriaMap.put(criteria, new ArrayList<FlagData>());
				categoryMap.get(category).add(criteria);
			}

			criteriaMap.get(criteria).add(flagData);
		}
	}

	public List<String> getCategories() {
		return categories;
	}

	public List<FlagCriteria> getCriteria(String category) {
		return categoryMap.get(category);
	}

	public List<FlagData> getFlags(FlagCriteria criteria) {
		return criteriaMap.get(criteria);
	}

	public int getFlagCount(FlagCriteria criteria, FlagColor color) {
		int count = 0;
		for (FlagData flagData : getFlags(criteria)) {
			if (flagData.getFlag().equals(color))
				count++;
		}
		return count;
	}
}
