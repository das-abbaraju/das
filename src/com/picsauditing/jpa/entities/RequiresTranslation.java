package com.picsauditing.jpa.entities;

import java.util.List;

public interface RequiresTranslation {

	public String getRequiredLanguages();

	public void setRequiredLanguages(String requiredLanguages);

	public List<String> getLanguages();

	public void setLanguages(List<String> requiredTranslations);
}