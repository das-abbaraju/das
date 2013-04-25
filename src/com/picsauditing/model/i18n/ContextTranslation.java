package com.picsauditing.model.i18n;

import java.util.Date;

import com.picsauditing.jpa.entities.Translatable;

public class ContextTranslation implements Translatable {

	private String actionName;
	private String methodName;
	private String frontEndControlName;
	private String key;
	private String translation;
	private String locale;
	private Date lastUsed;

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getFrontEndControlName() {
		return frontEndControlName;
	}

	public void setFrontEndControlName(String frontEndControlName) {
		this.frontEndControlName = frontEndControlName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTranslaton() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Date getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(Date lastUsed) {
		this.lastUsed = lastUsed;
	}

	@Override
	public String getI18nKey() {
		return new StringBuilder().append(actionName).append(methodName).append(frontEndControlName).append(key)
				.toString();
	}

	/**
	 * Deprecated in favor of using getI18nKey()
	 */
	@Override
	@Deprecated
	public String getI18nKey(String property) {
		return getI18nKey() + property;
	}

}
