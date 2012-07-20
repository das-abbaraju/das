package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
@Entity
@Table(name = "app_translation")
public class AppTranslation extends BaseTable implements java.io.Serializable {

	private String key;
	private String locale;
	private String value;
	private Date lastUsed;
	private TranslationQualityRating qualityRating;
	private boolean applicable;
	private String sourceLanguage;
	private boolean contentDriven;

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

	@Enumerated(EnumType.ORDINAL)
	public TranslationQualityRating getQualityRating() {
		return qualityRating;
	}

	public void setQualityRating(TranslationQualityRating qualityRating) {
		this.qualityRating = qualityRating;
	}

	public boolean isApplicable() {
		return applicable;
	}

	public void setApplicable(boolean applicable) {
		this.applicable = applicable;
	}

	public String getSourceLanguage() {
		return sourceLanguage;
	}

	public void setSourceLanguage(String sourceLanguage) {
		this.sourceLanguage = sourceLanguage;
	}

	public static List<Locale> getLocales() {
		if (locales == null) {
			locales = new ArrayList<Locale>();
			// locales.add(new Locale("ar"));
			locales.add(new Locale("zh"));
			locales.add(new Locale("zh", "CN"));
			locales.add(new Locale("zh", "TW"));
			locales.add(new Locale("nl"));
			locales.add(new Locale("en"));
			locales.add(new Locale("en", "AU"));
			locales.add(new Locale("en", "CA"));
			locales.add(new Locale("en", "GB"));
			locales.add(new Locale("en", "US"));
			locales.add(new Locale("en", "ZA"));
			locales.add(new Locale("fr"));
			locales.add(new Locale("fr", "CA"));
			locales.add(new Locale("fr", "FR"));
			locales.add(new Locale("de"));
			// locales.add(new Locale("ja"));
			locales.add(new Locale("pt"));
			locales.add(new Locale("es"));
			locales.add(new Locale("es", "MX"));
			locales.add(new Locale("es", "ES"));
			locales.add(new Locale("fi"));
			locales.add(new Locale("sv"));
			locales.add(new Locale("no"));
			// locales.add(new Locale("ru"));
		}
		return locales;
	}

	public static Set<String> getLocaleLanguages() {
		Set<String> languages = new TreeSet<String>();
		if (locales == null)
			getLocales();
		for (Locale locale : locales) {
			languages.add(locale.getLanguage());
		}

		return languages;
	}

	@Transient
	public boolean isKeyContentDriven() {
		return StringUtils.startsWith(key, "AuditType") || StringUtils.startsWith(key, "AuditCategory")
				|| StringUtils.startsWith(key, "AuditQuestion") || StringUtils.startsWith(key, "AuditOptionGroup")
				|| StringUtils.startsWith(key, "EmailTemplate") || StringUtils.startsWith(key, "FlagCriteria");
	}

	public boolean isContentDriven() {
		return contentDriven;
	}

	public void setContentDriven(boolean contentDriven) {
		this.contentDriven = contentDriven;
	}
}
