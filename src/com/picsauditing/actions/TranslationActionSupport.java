package com.picsauditing.actions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.Translatable;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class TranslationActionSupport extends ActionSupport {

	private Set<String> usedKeys = null;
	private I18nCache i18nCache = I18nCache.getInstance();
	static final protected String i18nTracing = "i18nTracing";

	public static Locale getLocaleStatic() {
		try {
			return (Locale) ActionContext.getContext().get(ActionContext.LOCALE);
		} catch (Exception defaultToEnglish) {
			return Locale.ENGLISH;
		}
	}

	@SuppressWarnings("rawtypes")
	public String getTranslationName(String property) throws SecurityException, NoSuchFieldException {
		String result = "";

		List<String> hierarchy = Arrays.asList(property.split("\\."));
		Class type = this.getClass().getDeclaredField(hierarchy.get(0)).getType();
		int i;
		for (i = 1; i < hierarchy.size(); i++) {
			try {
				Class childFieldType = getTypeFromInheritedClasses(type,hierarchy.get(i));
				if (isTranslatable(childFieldType))
					type = childFieldType;
				else
					break;
			} catch (NoSuchFieldException fieldMissing) {
				break;
			}
		}

		result = type.getSimpleName();
		if (i < hierarchy.size()) {
			result += "." + Strings.implode(hierarchy.subList(i, hierarchy.size()), ".");
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public Class getTypeFromInheritedClasses(Class type, String field) throws NoSuchFieldException {
		if (type == null)
			return null;

		Class result;
		try {
			result = type.getDeclaredField(field).getType();
		} catch (NoSuchFieldException e) {
			return getTypeFromInheritedClasses(type.getSuperclass(), field);
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public boolean isTranslatable(Class c) {
		if (c == null)
			return false;
		for (Class i : c.getInterfaces()) {
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

	/**
	 * This is for a parameter-based getText
	 */
	public String getText(String aTextName, Object[] args) {
		return getText(aTextName, null, Arrays.asList(args));
	}

	@Override
	public String getText(String aTextName, String defaultValue) {
		return getText(aTextName, defaultValue, (List<Object>) null);
	}

	@Override
	public String getText(String aTextName, String defaultValue, List<Object> args) {
		return getText(aTextName, defaultValue, args, null);
	}

	@Override
	public String getText(String aTextName, String defaultValue, String obj) {
		return super.getText(aTextName, defaultValue, obj);
	}

	@Override
	public String getText(String aTextName, String defaultValue, List<Object> args, ValueStack stack) {
		useKey(aTextName);
		if (i18nCache.hasKey(aTextName, getLocale())) {
			Object[] argArray = null;
			if (args != null)
				argArray = args.toArray();
			return i18nCache.getText(aTextName, getLocale(), argArray);
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
	public String getText(String aTextName, List<Object> args) {
		return getText(aTextName, null, args);
	}

	@Override
	public String getText(String key, String[] args) {
		return getText(key, null, args);
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

	private void useKey(String key) {
		if (key == null)
			throw new RuntimeException("i18n key cannot be NULL");
		if (key.length() == 0)
			throw new RuntimeException("i18n key cannot be empty");
		if (key.contains("'") || key.contains("\""))
			throw new RuntimeException("i18n key cannot contain quotes");
		if (key.contains(" "))
			throw new RuntimeException("i18n key cannot contain spaces");

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
}
