package com.picsauditing.jpa.entities;

import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "app_language")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class Language extends BaseTranslatable {
	
	private Locale locale;
	private String language;
	private String country;
	private LanguageStatus status;

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public LanguageStatus getStatus() {
		return status;
	}

	public void setStatus(LanguageStatus status) {
		this.status = status;
	}

	@Override
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + this.locale;
	}
	
	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}

}
