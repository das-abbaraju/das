package com.picsauditing.actions;

import java.util.List;
import java.util.ResourceBundle;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.PICS.I18nCache;

@SuppressWarnings("serial")
public class TranslationActionSupport extends ActionSupport {

	private I18nCache i18nCache = I18nCache.getInstance();

	@Override
	public boolean hasKey(String key) {
		return i18nCache.hasKey(key, getLocale());
	}

	@Override
	public String getText(String aTextName) {
		return getText(aTextName, (String) null);
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
		if (i18nCache.hasKey(aTextName, getLocale())) {
			String key = stack.findValue(aTextName) == null ? aTextName : stack.findValue(aTextName).toString();
			return i18nCache.getText(key, getLocale(), args.toArray());
		}
		return defaultValue;
	}

	@Override
	public String getText(String key, String defaultValue, String[] args) {
		return getText(key, defaultValue, args, null);
	}

	@Override
	public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
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
}
