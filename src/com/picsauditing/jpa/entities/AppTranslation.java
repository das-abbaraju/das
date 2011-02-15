package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@Table(name = "app_translation")
public class AppTranslation extends BaseTable implements java.io.Serializable {

	private String key;
	private String locale;
	private String value;
	private Date lastUsed;

	private static List<Locale> locales = null;

	@Column(name = "msgKey", nullable = false)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Column(nullable = false)
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@Column(name = "msgValue", nullable = false)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Temporal(TemporalType.DATE)
	public Date getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(Date lastUsed) {
		this.lastUsed = lastUsed;
	}

	public static List<Locale> getLocales() {
		if (locales == null) {
			locales = new ArrayList<Locale>();
			locales.add(new Locale("en"));
			locales.add(new Locale("en", "CA"));
			locales.add(new Locale("en", "US"));
			locales.add(new Locale("fr"));
			locales.add(new Locale("fr", "CA"));
			locales.add(new Locale("fr", "FR"));
			locales.add(new Locale("es"));
		}
		return locales;
	}
}
