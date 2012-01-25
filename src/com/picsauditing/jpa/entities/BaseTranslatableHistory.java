package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

@SuppressWarnings("serial")
@Entity
@MappedSuperclass
public abstract class BaseTranslatableHistory extends BaseHistory {

	protected String requiredLanguages = null;
	
	private List<String> languages = new ArrayList<String>();

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
		if (!jsonArray.isEmpty())
			requiredLanguages = jsonArray.toJSONString();
	}
}
