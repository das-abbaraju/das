package com.picsauditing.actions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.model.i18n.ObjectPropertyTranslator;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.TranslationUtil;

@SuppressWarnings("serial")
public class TranslationActionSupport extends ActionSupport {

	@Autowired
	protected LanguageModel supportedLanguages;

	protected static final String i18nTracing = "i18nTracing";

	private Set<String> usedKeys = null;
	private TranslationService translationService = TranslationServiceFactory.getTranslationService();

	public static Locale getLocaleStatic() {
		try {
			return (Locale) ActionContext.getContext().get(ActionContext.LOCALE);
		} catch (Exception defaultToEnglish) {
			return Locale.ENGLISH;
		}
	}

	public String getTranslationName(String property) throws SecurityException {
		return ObjectPropertyTranslator.getTranslationName(this.getClass(), property);
	}

	public String getDefaultValueFromType(String property) throws SecurityException {
		return ObjectPropertyTranslator.getDefaultValueFromType(this.getClass(), property);
	}

	public Map<String, Class<?>> mapNameToType(String property) throws SecurityException {
		return ObjectPropertyTranslator.mapNameToType(this.getClass(), property);
	}

	public Class<?> getTypeFromInheritedClasses(Class<?> type, String field) throws SecurityException {
		return ObjectPropertyTranslator.getTypeFromInheritedClasses(type, field);
	}

	public boolean isTranslatable(Class<?> c) {
		return ObjectPropertyTranslator.isTranslatable(c);
	}

	public String getScope() {
		return ServletActionContext.getContext().getName();
	}

	@Override
	public boolean hasKey(String key) {
		return translationService.hasKey(key, getLocale());
	}

	@Override
	public String getText(String aTextName) {
		return getText(aTextName, (String) null);
	}

	public String getText(Locale locale, String aTextName) {
		return getText(locale, aTextName, (String) null);
	}

	/**
	 * This is for a parameter-based getText
	 */
	public String getTextParameterized(String aTextName, Object... args) {
		return getText(aTextName, null, Arrays.asList(args));
	}

	public String getTextParameterized(Locale locale, String aTextName, Object... args) {
		return getText(locale, aTextName, null, Arrays.asList(args));
	}

	public String getTextNullSafe(String aTextName) {
		return getText(aTextName, aTextName);
	}

	public String getTextNullSafeParameterized(String aTextName, Object... args) {
		return getText(aTextName, aTextName, Arrays.asList(args));
	}

	public String getText(String aTextName, Object[] args) {
		return getText(aTextName, null, Arrays.asList(args));
	}

	@Override
	public String getText(String aTextName, String defaultValue) {
		return getText(aTextName, defaultValue, (List<Object>) null);
	}

	public String getText(Locale locale, String aTextName, String defaultValue) {
		return getText(locale, aTextName, defaultValue, (List<Object>) null);
	}

	@Override
	public String getText(String aTextName, String defaultValue, List<?> args) {
		return getText(aTextName, defaultValue, args, null);
	}

	public String getText(Locale locale, String aTextName, String defaultValue, List<?> args) {
		return getText(locale, aTextName, defaultValue, args, null);
	}

	@Override
	public String getText(String aTextName, String defaultValue, String obj) {
		return super.getText(aTextName, defaultValue, obj);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getText(String aTextName, String defaultValue, List<?> args, ValueStack stack) {
		useKey(aTextName);
		if (translationService.hasKey(aTextName, getLocaleStatic())) {
			Object[] argArray = null;
			if (args != null) {
				argArray = args.toArray();
			}

			return translationService.getText(aTextName, getLocaleStatic(), argArray);
		}

		return defaultValue;
	}

	@SuppressWarnings("deprecation")
	public String getText(Locale locale, String aTextName, String defaultValue, List<?> args, ValueStack stack) {
		useKey(aTextName);
		if (translationService.hasKey(aTextName, locale)) {
			Object[] argArray = null;
			if (args != null) {
				argArray = args.toArray();
			}
			return translationService.getText(aTextName, locale, argArray);
		}

		return defaultValue;
	}

	@Override
	public String getText(String key, String defaultValue, String[] args) {
		return getText(key, defaultValue, args, null);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
		useKey(key);
		if (translationService.hasKey(key, getLocale())) {
			return translationService.getText(key, getLocale(), (Object[]) args);
		}

		return defaultValue;
	}

	@Override
	public String getText(String aTextName, List<?> args) {
		return getText(aTextName, null, args);
	}

	@Override
	public String getText(String key, String[] args) {
		return getText(key, null, args);
	}

	public Map<Locale, String> findAllTranslations(String key) {
		return findAllTranslations(key, true);
	}

	public Map<Locale, String> findAllTranslations(String key, Boolean includeLocaleStatic) {
		Map<String, String> translationMap = translationService.getText(key);
		Map<Locale, String> newTranslationMap = new HashMap<Locale, String>();

		for (Map.Entry<String, String> entry : translationMap.entrySet()) {
			String keyStr = entry.getKey();

			newTranslationMap.put(convertStringToLocale(keyStr), entry.getValue());
		}

		if (!includeLocaleStatic) {
			newTranslationMap.remove(getLocaleStatic());
		}

		return sortTranslationsByLocaleDisplayNames(newTranslationMap);
	}

	@Override
	public ResourceBundle getTexts() {
		return super.getTexts();
	}

	@Override
	public ResourceBundle getTexts(String aBundleName) {
		return super.getTexts(aBundleName);
	}

	public Set<String> getI18nUsedKeys() {
		return getTranslationKeysFromTheSession();
	}

	@SuppressWarnings("unchecked")
	private Set<String> getTranslationKeysFromTheSession() {
		if (usedKeys == null) {
			try {
				Map<String, Object> session = ActionContext.getContext().getSession();
				final String usedI18nKeys = "usedI18nKeys";
				if (session.containsKey(usedI18nKeys)) {
					usedKeys = (Set<String>) session.get(usedI18nKeys);
				} else {
					usedKeys = null;
				}

				if (usedKeys == null) {
					usedKeys = new HashSet<String>();
					session.put(usedI18nKeys, usedKeys);
				}
			} catch (Exception doNothing) {
			}
		}

		return usedKeys;
	}

	@Deprecated
	public String getStrippedHref(String text) {
		return TranslationUtil.getStrippedHref(text);
	}

	public LanguageModel getSupportedLanguages() {
		return this.supportedLanguages;
	}

	private void useKey(String key) {
		TranslationUtil.validateTranslationKey(key);
		addKeyToSessionForTracing(key);
	}

	private void addKeyToSessionForTracing(String key) {
		try {
			Map<String, Object> session = ActionContext.getContext().getSession();
			String tracing = session.get(i18nTracing).toString();
			if (Boolean.parseBoolean(tracing)) {
				getI18nUsedKeys().add(key);
				if (getI18nUsedKeys().size() > 1000 && getActionErrors().size() == 0) {
					addActionError("You have i18n Text Tracing turned on and have " + getI18nUsedKeys().size()
							+ " in your cache. You may want to turn this feature off or clear your cache.");
				}
			}
		} catch (Exception doNothing) {
		}
	}

	private Locale convertStringToLocale(String keyStr) {
		return TranslationUtil.convertStringToLocale(keyStr);
	}

	private Map<Locale, String> sortTranslationsByLocaleDisplayNames(Map<Locale, String> newTranslationMap) {
		return TranslationUtil.sortTranslationsByLocaleDisplayNames(newTranslationMap);
	}
}
