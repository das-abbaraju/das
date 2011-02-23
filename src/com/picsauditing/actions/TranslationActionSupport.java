package com.picsauditing.actions;

import java.util.HashSet;
import java.util.List;
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

	private Set<String> usedKeys = new HashSet<String>();
	private I18nCache i18nCache = I18nCache.getInstance();

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

	private void useKey(String key) {
		usedKeys.add(key);

		Map<String, Object> session = ActionContext.getContext().getSession();
		if (session == null) {
			System.out.println("Failed to get Session");
		} else
			session.put("usedI18nKeys", usedKeys);
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

}
