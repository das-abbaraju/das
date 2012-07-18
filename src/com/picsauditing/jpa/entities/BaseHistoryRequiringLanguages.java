package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class BaseHistoryRequiringLanguages extends BaseHistory {

	protected String requiredLanguages = null;
	
	protected List<String> languages = new ArrayList<String>();

	public String getRequiredLanguages() {
		return requiredLanguages;
	}

	public void setRequiredLanguages(String requiredLanguages) {
		this.requiredLanguages = requiredLanguages;
	}

	@Transient
	public List<String> getLanguages() {
		if (requiredLanguages != null)
		{
			JSONArray JSONLanguages = (JSONArray) JSONValue.parse(requiredLanguages);
			languages.clear();
			for (Object obj : JSONLanguages) {
				String language = (String) obj;
				languages.add(language);
			}
		}
		return languages;
	}

	@Transient
	public void setLanguages(List<String> languages) {
		this.languages = languages;
		JSONArray jsonArray = new JSONArray();
		for (String language : languages)
			jsonArray.add(language);
		requiredLanguages = jsonArray.toJSONString();
	}
	
	public abstract boolean hasMissingChildRequiredLanguages();

	public void addAndRemoveRequiredLanguages(List<String> add, List<String> remove) {
		List<String> newLanguages = new ArrayList<String>();
		newLanguages.addAll(getLanguages());
		newLanguages.addAll(add);
		newLanguages.removeAll(remove);
		setLanguages(newLanguages);
	}
}
