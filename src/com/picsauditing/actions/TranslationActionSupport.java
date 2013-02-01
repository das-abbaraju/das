package com.picsauditing.actions;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.Translatable;
import com.picsauditing.util.Strings;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class TranslationActionSupport extends ActionSupport {
	@Autowired
	protected AppPropertyDAO propertyDAO;

	public static final List<KeyValue> supportedLocaleList = new ArrayList<KeyValue>() {
		{
			add(new KeyValue("en", "English"));
			add(new KeyValue("fr", "Français"));
			add(new KeyValue("es", "Español"));
			add(new KeyValue("de", "Deutsch"));
			add(new KeyValue("sv", "Svenska"));
			add(new KeyValue("fi", "Suomi"));
			add(new KeyValue("nl", "Nederlands"));
			add(new KeyValue("no", "Norsk"));
			add(new KeyValue("pt", "Português"));
		}
	};
	static final protected String i18nTracing = "i18nTracing";
	private static Locale[] supportedLocales = null;
	private final Logger logger = LoggerFactory.getLogger(TranslationActionSupport.class);
	private Set<String> usedKeys = null;
	private I18nCache i18nCache = I18nCache.getInstance();

	public static Locale[] getSupportedLocales() {
		if (supportedLocales == null) {
			supportedLocales = new Locale[supportedLocaleList.size()];
			for (int i = 0; i < supportedLocales.length; i++) {
				KeyValue kv = supportedLocaleList.get(i);
				supportedLocales[i] = new Locale(kv.getKey());
			}
		}
		return supportedLocales;
	}

	public static Locale getLocaleStatic() {
		try {
			return (Locale) ActionContext.getContext().get(ActionContext.LOCALE);
		} catch (Exception defaultToEnglish) {
			return Locale.ENGLISH;
		}
	}

	public String getTranslationName(String property) throws SecurityException {

		Map<String, Class<?>> typeMap = mapNameToType(property);
		Class<?> type = null;
		Iterator<Entry<String, Class<?>>> iter = typeMap.entrySet().iterator();
		List<Entry<String, Class<?>>> nonTranslatables = new ArrayList<Map.Entry<String, Class<?>>>();
		do {
			Entry<String, Class<?>> next = iter.next();
			if (isTranslatable(next.getValue()))
				type = next.getValue();
			else
				nonTranslatables.add(next);

		} while (iter.hasNext() && isTranslatable(type));

		if (type == null) {
			logger.warn("The field [{}] is not translatable. Please add a label for this field.", property);
		} else {
			StringBuilder result = new StringBuilder(type.getSimpleName());
			for (Entry<String, Class<?>> entry : nonTranslatables) {
				result.append(".").append(entry.getKey());
			}
			return result.toString();
		}

		return "";
	}

	public String getDefaultValueFromType(String property) throws SecurityException {
		Map<String, Class<?>> typeMap = mapNameToType(property);

		Class<?> clazz = typeMap.values().toArray(new Class<?>[0])[typeMap.size() - 1];

		if (clazz.isPrimitive() || Number.class.isAssignableFrom(clazz))
			return "0";

		return "";
	}

	public Map<String, Class<?>> mapNameToType(String property) throws SecurityException {
		Map<String, Class<?>> result = new LinkedHashMap<String, Class<?>>();
		String[] hierarchy = property.split("\\.");
		Class<?> type = getTypeFromInheritedClasses(this.getClass(), hierarchy[0]);
		result.put(hierarchy[0], type);

		for (int i = 1; i < hierarchy.length; i++) {
			type = getTypeFromInheritedClasses(type, hierarchy[i]);
			result.put(hierarchy[i], type);
		}

		return result;
	}

	public Class<?> getTypeFromInheritedClasses(Class<?> type, String field) throws SecurityException {

		Class<?> result = null;
		do {
			try {
				result = type.getDeclaredField(field).getType();
			} catch (NoSuchFieldException e) {
				type = type.getSuperclass();
			}
		} while (result == null && type != null);

		return result;
	}

	public boolean isTranslatable(Class<?> c) {
		if (c == null)
			return false;
		for (Class<?> i : c.getInterfaces()) {
			if (i.equals(Translatable.class))
				return true;
		}
		return isTranslatable(c.getSuperclass());
	}

	public String getScope() {
		return ServletActionContext.getContext().getName();
	}

	@Override
	public boolean hasKey(String key) {
		return i18nCache.hasKey(key, getLocale());
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

	@Override
	public String getText(String aTextName, String defaultValue, List<?> args, ValueStack stack) {
		useKey(aTextName);
		if (i18nCache.hasKey(aTextName, getLocaleStatic())) {
			Object[] argArray = null;
			if (args != null)
				argArray = args.toArray();
			return i18nCache.getText(aTextName, getLocaleStatic(), argArray);
		}
		return defaultValue;
	}

	public String getText(Locale locale, String aTextName, String defaultValue, List<?> args, ValueStack stack) {
		useKey(aTextName);
		if (i18nCache.hasKey(aTextName, locale)) {
			Object[] argArray = null;
			if (args != null)
				argArray = args.toArray();
			return i18nCache.getText(aTextName, locale, argArray);
		}
		return defaultValue;
	}

	@Override
	public String getText(String key, String defaultValue, String[] args) {
		return getText(key, defaultValue, args, null);
	}

	@Override
	public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
		useKey(key);
		if (i18nCache.hasKey(key, getLocale())) {
			return i18nCache.getText(key, getLocale(), (Object[]) args);
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
		Map<String, String> translationMap = i18nCache.getText(key);
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

	@SuppressWarnings("unchecked")
	public Set<String> getI18nUsedKeys() {
		if (usedKeys == null) {
			try {
				Map<String, Object> session = ActionContext.getContext().getSession();
				final String usedI18nKeys = "usedI18nKeys";
				if (session.containsKey(usedI18nKeys))
					usedKeys = (Set<String>) session.get(usedI18nKeys);
				else
					usedKeys = null;

				if (usedKeys == null) {
					usedKeys = new HashSet<String>();
					session.put(usedI18nKeys, usedKeys);
				}
			} catch (Exception doNothing) {
			}
		}
		return usedKeys;
	}

	public String getStrippedHref(String text) {
		if (text.contains("href") && text.contains("www.picsorganizer.com")) {
			text = text.replaceAll("(href\\s*=\\s*[\"']*)(https?:)?//www.picsorganizer.com/?", "$1");
		}

		return text;
	}

	public boolean isLanguageFullySupported() {
		AppProperty commaSeparatedFullySupportedLanguages = propertyDAO.find(AppProperty.FULLY_SUPPORTED_LANGUAGES);
		if (commaSeparatedFullySupportedLanguages != null && Strings.isNotEmpty(commaSeparatedFullySupportedLanguages.getValue())) {
			for (String fullySupportedLanguage : commaSeparatedFullySupportedLanguages.getValue().split(",")) {
				fullySupportedLanguage = fullySupportedLanguage.trim();
				if (fullySupportedLanguage.equals(getLocaleStatic().getLanguage())) {
					return true;
				}
			}
		} else {
			logger.error("Could not find value for app property " + AppProperty.FULLY_SUPPORTED_LANGUAGES);
			// If the app property is not set, the only fully supported language is English
			return getLocaleStatic().equals(Locale.ENGLISH);
		}

		return false;
	}

	private void useKey(String key) {
		if (key == null)
			throw new RuntimeException("i18n key cannot be NULL");
		if (key.length() == 0)
			throw new RuntimeException("i18n key cannot be empty");
		if (key.contains("'") || key.contains("\""))
			throw new RuntimeException("i18n key cannot contain quotes");
		if (key.contains(" ")) {
			throw new RuntimeException("i18n key cannot contain spaces");
		}

		try {
			Map<String, Object> session = ActionContext.getContext().getSession();
			String tracing = session.get(i18nTracing).toString();
			if (Boolean.parseBoolean(tracing)) {
				getI18nUsedKeys().add(key);
				if (getI18nUsedKeys().size() > 1000 && getActionErrors().size() == 0)
					addActionError("You have i18n Text Tracing turned on and have " + getI18nUsedKeys().size()
							+ " in your cache. You may want to turn this feature off or clear your cache.");
			}
		} catch (Exception doNothing) {
		}
	}

	private Locale convertStringToLocale(String keyStr) {
		Locale locale = null;
		String[] lanCountry = keyStr.split("_");

		// e.g. en_GB
		if (lanCountry.length > 1) {
			locale = new Locale(lanCountry[0], lanCountry[1]);
		} else {
			locale = new Locale(keyStr);
		}
		return locale;
	}

	private Map<Locale, String> sortTranslationsByLocaleDisplayNames(Map<Locale, String> newTranslationMap) {
		Comparator<Locale> displayNameComparator = new Comparator<Locale>() {
			public int compare(Locale l1, Locale l2) {
				return l1.getDisplayName().compareTo(l2.getDisplayName());
			}
		};
		Map<Locale, String> sortedTranslationMap = new TreeMap<Locale, String>(displayNameComparator);
		sortedTranslationMap.putAll(newTranslationMap);
		return sortedTranslationMap;
	}

	private static class KeyValue {
		private String key;
		private String value;

		public KeyValue(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
