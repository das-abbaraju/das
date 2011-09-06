package com.picsauditing.jpa.entities;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PreRemove;
import javax.persistence.Transient;

import com.picsauditing.PICS.I18nCache;

import freemarker.template.utility.StringUtil;

@MappedSuperclass
public abstract class BaseTranslatable implements Translatable {

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
			Map<String, String> translationCache = i18nCache.getText(getI18nKey(field.getName()));
			for (String key : translationCache.keySet()) {
				translatable.putTranslation(key, translationCache.get(key), false);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("set").append(field.getName().substring(0, 1).toUpperCase()).append(field.getName().substring(1));

			Method declaredMethod = this.getClass().getDeclaredMethod(sb.toString(), TranslatableString.class);
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
}
