package com.picsauditing.jpa.entities;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.simple.JSONObject;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.util.Strings;

import freemarker.template.utility.StringUtil;

@Entity
@Table(name = "ref_country")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "daily")
public class Country implements Comparable<Country>, Serializable, Autocompleteable, Translatable, IsoCode {
	private static final long serialVersionUID = 6312208192653925848L;

	protected String isoCode;
	protected TranslatableString name;
	protected String english;
	protected String spanish;
	protected String french;

	protected User csr;

	public Country() {
	}

	public Country(String isoCode) {
		this.isoCode = isoCode;
	}

	public Country(String isoCode, String english) {
		this.isoCode = isoCode;
		this.english = english;
	}

	@Transient
	private List<Field> getTranslatableFields() {
		List<Field> result = new ArrayList<Field>();
		for (Field field : this.getClass().getDeclaredFields()) {
			if (field.getType().equals(TranslatableString.class)) {
				result.add(field);
			}
		}

		return result;
	}

	@PostLoad
	public void postLoad() throws Exception {
		for (Field field : getTranslatableFields()) {
			I18nCache i18nCache = I18nCache.getInstance();
			TranslatableString translatable = new TranslatableString();
			Map<String, String> translationCache = i18nCache.getText(getI18nKey());
			for (String key : translationCache.keySet()) {
				translatable.putTranslation(key, translationCache.get(key), false);
			}

			Method declaredMethod = this.getClass().getDeclaredMethod("set" + StringUtil.capitalize(field.getName()),
					TranslatableString.class);
			declaredMethod.invoke(this, translatable);
		}
	}

	@PostUpdate
	@PostPersist
	public void postSave() throws Exception {
		for (Field field : getTranslatableFields()) {
			I18nCache i18nCache = I18nCache.getInstance();
			Method getField = this.getClass().getDeclaredMethod("get" + StringUtil.capitalize(field.getName()));
			String key = this.getI18nKey();
			TranslatableString value = (TranslatableString) getField.invoke(this);
			i18nCache.saveTranslatableString(key, value);
		}
	}

	@PreRemove
	public void preRemove() throws Exception {
		I18nCache i18nCache = I18nCache.getInstance();
		List<String> keys = new ArrayList<String>();
		String key = this.getI18nKey();
		keys.add(key);
		i18nCache.removeTranslatableStrings(keys);
	}

	@Id
	@Column(nullable = false, length = 2)
	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

	@Transient
	public String getName() {
		return name.toString();
	}

	public void setName(TranslatableString name) {
		this.name = name;
	}

	@Transient
	public String getName(Locale locale) {
		if (locale.getLanguage().equals("es"))
			return spanish;
		if (locale.getLanguage().equals("fr"))
			return french;
		return english;
	}

	public String getEnglish() {
		return english;
	}

	public void setEnglish(String english) {
		this.english = english;
	}

	public String getSpanish() {
		return spanish;
	}

	public void setSpanish(String spanish) {
		this.spanish = spanish;
	}

	public String getFrench() {
		return french;
	}

	public void setFrench(String french) {
		this.french = french;
	}

	public static String convertToCode(String tempCountry) {
		if (Strings.isEmpty(tempCountry))
			return null;

		tempCountry = tempCountry.trim();
		if (tempCountry.length() == 2)
			return tempCountry;
		if (tempCountry.equals("Canada"))
			return "CA";

		return "US";
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "csrID")
	public User getCsr() {
		return csr;
	}

	public void setCsr(User csr) {
		this.csr = csr;
	}

	@Transient
	public boolean isHasStates() {
		return isUS() || isCanada();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Country && this.isoCode.equals(((Country) obj).getIsoCode());
	}

	@Override
	public String toString() {
		return english;
	}

	@Transient
	public String getAutocompleteResult() {
		return isoCode + "_C";
	}

	@Transient
	public String getAutocompleteItem() {
		return isoCode;
	}

	@Transient
	public String getAutocompleteValue() {
		return getName();
	}

	@Transient
	public JSONObject toJSON() {
		return toJSON(false);
	}

	@Transient
	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject obj = new JSONObject();
		obj.put("isoCode", isoCode);

		if (full) {
			obj.put("english", english);
			obj.put("french", french);
			obj.put("spanish", spanish);
			obj.put("CSR", csr == null ? null : csr.toJSON());
		}
		return obj;
	}

	@Transient
	public String getI18nKey() {
		return getClass().getSimpleName() + "." + isoCode;
	}

	@Transient
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}

	@Override
	public int compareTo(Country o) {
		return this.getI18nKey().compareTo(o.getI18nKey());
	}

	@Transient
	public boolean isUS() {
		return "US".equals(isoCode);
	}

	@Transient
	public boolean isCanada() {
		return "CA".equals(isoCode);
	}
	
	@Transient
	public boolean isUAE() {
		return "AE".equals(isoCode);
	}
}