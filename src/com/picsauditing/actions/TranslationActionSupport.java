package com.picsauditing.actions;

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

@SuppressWarnings("serial")
public class TranslationActionSupport extends ActionSupport {

	private Set<String> usedKeys = null;
	private I18nCache i18nCache = I18nCache.getInstance();
	static final protected String i18nTracing = "i18nTracing";

	public static Locale getLocaleStatic() {
		Locale locale = Locale.ENGLISH;
		try {
			locale = (Locale) ActionContext.getContext().get(ActionContext.LOCALE);
		} catch (Exception defaultToEnglish) {
		}

		return locale;
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
	 * This is for a paramater-based getText
	 */
	public String getText(String aTextName, Object... args ) {
		return getText(aTextName, null, args);
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return super.getTexts();
	}

	@Override
	public ResourceBundle getTexts(String aBundleName) {
		// TODO Auto-generated method stub
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
