package com.picsauditing.jpa.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class BaseTableRequiringLanguages extends BaseTable implements JSONable, Serializable, Autocompleteable {

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
		if (requiredLanguages != null) {
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
		JSONArray jsonArray = new JSONArray();
		for (String language : languages)
			jsonArray.add(language);

		List<String> add = getLanguageDifferences(getLanguages(), languages);
		List<String> remove = getLanguageDifferences(languages, getLanguages());
		if (!add.isEmpty() || !remove.isEmpty())
			cascadeRequiredLanguages(add, remove);
			requiredLanguages = jsonArray.toJSONString();

		this.languages = languages;
	}

	public List<String> getLanguageDifferences(List<String> sourceLanguages, List<String> targetLanguages) {
		List<String> differences = new ArrayList<String>();
		for (String language : targetLanguages) {
			if (!sourceLanguages.contains(language))
				differences.add(language);
		}
		return differences;
	}

	public abstract void cascadeRequiredLanguages(List<String> add, List<String> remove);

	public abstract boolean hasMissingChildRequiredLanguages();

	public void addAndRemoveRequiredLanguages(List<String> add, List<String> remove) {
		List<String> newLanguages = new ArrayList<String>();
		newLanguages.addAll(getLanguages());
		newLanguages.addAll(add);
		newLanguages.removeAll(remove);
		setLanguages(newLanguages);
	}
}
