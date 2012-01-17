package com.picsauditing.jpa.entities;

import java.util.List;


public interface RequiresTranslation {

	public void createRequiredTranslationsFromJSON(String requiredLanguages);

	public void createRequiredLanguagesToJSON(List<String> requiredTranslations);

	public String getRequiredLanguages();

	public void setRequiredLanguages(String requiredLanguages);

	public List<String> getRequiredTranslations();

	public void setRequiredTranslations(List<String> requiredTranslations);
}