package com.picsauditing.jpa.entities;

import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "app_language")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class Language implements Comparable<Language>, Translatable {
	private Locale locale;
	private String language;
	private String country;
	private LanguageStatus status;

	@Id
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
	@Transient
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + this.locale;
	}

	@Override
	@Transient
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}

	@Override
	public int compareTo(Language otherLocale) {
		if (locale != null && otherLocale.getLocale() != null) {
			return this.locale.toString().compareTo(otherLocale.toString());
		}

		return this.language.compareTo(otherLocale.getLanguage());
	}

	@Override
	public String toString() {
		return this.locale.toString();
	}
}
