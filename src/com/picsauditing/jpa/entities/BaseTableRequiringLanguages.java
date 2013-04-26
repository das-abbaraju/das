package com.picsauditing.jpa.entities;

import com.picsauditing.util.i18n.RequiredLanguageTransformer;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class BaseTableRequiringLanguages extends BaseTable implements JSONable, Serializable, Autocompleteable, RequiresLanguages {
	private RequiredLanguageTransformer transformer = new RequiredLanguageTransformer();

	protected String requiredLanguages = null;
	protected List<String> languages = new ArrayList<>();

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
		List<String> add = transformer.getLanguagesToAdd(getLanguages(), languages);
		List<String> remove = transformer.getLanguagesToRemove(getLanguages(), languages);

		if (!add.isEmpty() || !remove.isEmpty()) {
			cascadeRequiredLanguages(add, remove);
		}

		Collections.sort(languages);

		requiredLanguages = transformer.getJSONStringFrom(languages);
		this.languages = languages;
	}

	public abstract void cascadeRequiredLanguages(List<String> add, List<String> remove);

	@Override
	public void addAndRemoveRequiredLanguages(List<String> add, List<String> remove) {
		transformer.updateRequiredLanguages(this, add, remove);
	}
}
