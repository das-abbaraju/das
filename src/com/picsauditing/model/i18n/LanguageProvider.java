package com.picsauditing.model.i18n;

import com.picsauditing.jpa.entities.Language;
import com.picsauditing.jpa.entities.LanguageStatus;

import java.util.List;
import java.util.Locale;

public interface LanguageProvider {
	Language find(String id);
	Language find(Locale locale);
	List<Language> findAll();
	List<Language> findByStatus(LanguageStatus status);
	List<Language> findByStatuses(LanguageStatus[] statuses);
	List<Language> findWhere(String where);
	List<Language> findDialectsByLanguage(String language);
}
