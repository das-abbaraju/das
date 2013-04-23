package com.picsauditing.jpa.entities;

import com.picsauditing.util.i18n.RequiredLanguageTransformer;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class BaseHistoryRequiringLanguages extends BaseHistory implements RequiresLanguages {
	private RequiredLanguageTransformer transformer = new RequiredLanguageTransformer();

	protected String requiredLanguages = null;
	protected List<String> languages = new ArrayList<String>();

	@Override
	public String getRequiredLanguages() {
		return requiredLanguages;
	}

	@Override
	public void setRequiredLanguages(String requiredLanguages) {
		this.requiredLanguages = requiredLanguages;
	}

	@Override
	@Transient
	public List<String> getLanguages() {
		this.languages = transformer.getListFromJSON(requiredLanguages);
		return languages;
	}

	@Override
	@Transient
	public void setLanguages(List<String> languages) {
		this.languages = languages;

		requiredLanguages = transformer.getJSONStringFrom(languages);
	}

	@Override
	public abstract boolean hasMissingChildRequiredLanguages();

	@Override
	public void addAndRemoveRequiredLanguages(List<String> add, List<String> remove) {
		transformer.updateRequiredLanguages(this, add, remove);
	}
}