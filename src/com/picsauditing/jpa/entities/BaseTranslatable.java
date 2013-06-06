package com.picsauditing.jpa.entities;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseTranslatable implements Translatable {

	// @Transient
	// private List<Field> getTranslatableFields() {
	// List<Field> result = new ArrayList<Field>();
	// for (Field field : this.getClass().getDeclaredFields()) {
	// if (field.getType().equals(TranslatableString.class)) {
	// result.add(field);
	// }
	// }
	//
	// return result;
	// }
	//
	// @SuppressWarnings("unchecked")
	// @Transient
	// private List<String> getRequiredLanguagesForEntity() {
	// try {
	// Method getRequiredLanguages = this.getClass().getMethod("getLanguages");
	// return (List<String>) getRequiredLanguages.invoke(this);
	// } catch (Exception e) {
	// return Collections.emptyList();
	// }
	// }
	//
	// @PostLoad
	// public void postLoad() throws Exception {
	// for (Field field : getTranslatableFields()) {
	// I18nCache i18nCache = I18nCache.getInstance();
	// TranslatableString translatable = new TranslatableString();
	// translatable.setKey(this.getI18nKey(field.getName()));
	// Map<String, String> translationCache =
	// i18nCache.getText(getI18nKey(field.getName()));
	// for (String key : translationCache.keySet()) {
	// translatable.putTranslation(key, translationCache.get(key), false);
	// }
	//
	// StringBuilder sb = new StringBuilder();
	// sb.append("set").append(field.getName().substring(0,
	// 1).toUpperCase()).append(field.getName().substring(1));
	//
	// Method declaredMethod = this.getClass().getDeclaredMethod(sb.toString(),
	// TranslatableString.class);
	// declaredMethod.invoke(this, translatable);
	// }
	// }
	//
	// @PostUpdate
	// @PostPersist
	// public void postSave() throws Exception {
	// I18nCache i18nCache = I18nCache.getInstance();
	//
	// for (Field field : getTranslatableFields()) {
	// Method getField = this.getClass().getDeclaredMethod("get" +
	// WordUtils.capitalize(field.getName()));
	// String key = this.getI18nKey(field.getName());
	// TranslatableString value = (TranslatableString) getField.invoke(this);
	// i18nCache.saveTranslatableString(key, value,
	// getRequiredLanguagesForEntity());
	// }
	// }
	//
	// @PreRemove
	// public void preRemove() throws Exception {
	// I18nCache i18nCache = I18nCache.getInstance();
	// List<String> keys = new ArrayList<String>();
	// String key = this.getI18nKey();
	// keys.add(key);
	// i18nCache.removeTranslatableStrings(keys);
	// }
}
