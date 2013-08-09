package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.*;

public class ContractorFlagCriteriaList {

	private List<FlagCriteriaCategory> categories = new ArrayList<>();
	private Map<FlagCriteriaCategory, List<FlagCriteria>> categoryMap = new HashMap<>();
	private Map<FlagCriteria, List<FlagData>> criteriaMap = new HashMap<>();

	public ContractorFlagCriteriaList(List<FlagData> list) {
		for (FlagData flagData : list) {
            FlagCriteriaCategory category = flagData.getCriteria().getCategory();
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

	public List<FlagCriteriaCategory> getCategories() {
		return categories;
	}

	public List<FlagCriteria> getCriteria(FlagCriteriaCategory category) {
		return categoryMap.get(category);
	}

	public FlagColor getWorstColor(FlagCriteria criteria) {
		FlagColor worst = null;
		for (FlagData flagData : getFlags(criteria)) {
			worst = FlagColor.getWorseColor(worst, flagData.getFlag());
			if (worst.isRed())
				return worst;
		}
		return worst;
	}

	public String getWorstFlagOperators(FlagCriteria criteria) {
		String results = "";
		FlagData worstFlagData = null;
		
		for (FlagData flagData : getFlags(criteria)) {
			if (worstFlagData == null) {
				worstFlagData = flagData;
			} else {
				if (flagData.getFlag().ordinal() > worstFlagData.getFlag().ordinal()) {
					worstFlagData = flagData;
				}
			}
			
			if (worstFlagData.getFlag().isRed())
				break;
		}

		for (FlagData flagData : getFlags(criteria)) {
			if (flagData.getFlag().ordinal() == worstFlagData.getFlag().ordinal()) {
				if (results.length() > 0)
					results +="\n";
				results += flagData.getOperator().getName();
			}
		}

		return results;
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
