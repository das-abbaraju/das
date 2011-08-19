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

import freemarker.template.utility.StringUtil;

@Entity
@Table(name = "ref_state")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "daily")
public class State implements Comparable<State>, Serializable, Autocompleteable, Translatable, IsoCode {
	private static final long serialVersionUID = -7010252482295453919L;

	protected String isoCode;
	protected TranslatableString name;
	protected String english;
	protected String french;

	protected Country country;
	protected User csr;

	public State() {
	}

	public State(String isoCode) {
		this.isoCode = isoCode;
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

	public String getEnglish() {
		return english;
	}

	public void setEnglish(String english) {
		this.english = english;
	}

	public String getFrench() {
		return french;
	}

	public void setFrench(String french) {
		this.french = french;
	}

	@ManyToOne
	@JoinColumn(name = "countryCode")
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
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
	public String getName() {
		return name.toString() + ", " + country.getName();
	}

	public void setName(TranslatableString name) {
		this.name = name;
	}

	@Transient
	public String getName(Locale locale) {
		if (locale.getLanguage().equals("fr"))
			return french;
		return english;
	}

	@Override
	public String toString() {
		return isoCode;
	}

	@Transient
	public String getAutocompleteResult() {
		return isoCode;
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
			obj.put("country", country == null ? null : country.getIsoCode());
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
	public int compareTo(State o) {
		return this.getI18nKey().compareTo(o.getI18nKey());
	}
}