package com.picsauditing.model.i18n;

import com.picsauditing.jpa.entities.Language;
import com.picsauditing.jpa.entities.LanguageStatus;

import java.util.List;
import java.util.Locale;

public interface LanguageProvider {
	public Language find(String id);
	public Language find(Locale locale);
	public List<Language> findAll();
	public List<Language> findByStatus(LanguageStatus status);
	public List<Language> findByStatuses(LanguageStatus[] statuses);
	public List<Language> findWhere(String where);
}
