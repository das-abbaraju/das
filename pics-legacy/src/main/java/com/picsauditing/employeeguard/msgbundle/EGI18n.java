package com.picsauditing.employeeguard.msgbundle;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.model.i18n.ThreadLocalLocale;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class EGI18n implements TextProvider, LocaleProvider {
	private static Logger log = LoggerFactory.getLogger(EGI18n.class);

	private transient TextProvider textProvider;
	private Container container;

	public static String getBreadCrumbResourceBundle(String breadcrumbLabel) {
		if(!breadcrumbLabel.contains("{")) {
			String valueFromResourceBundle = EGI18n.getTextFromResourceBundle("BREADCRUMB.LABEL." + breadcrumbLabel);
			if (StringUtils.isNotEmpty(valueFromResourceBundle)) {
				breadcrumbLabel = valueFromResourceBundle;
			}
		}

		return breadcrumbLabel;
	}

	public static String getTextFromResourceBundle(String aTextName){
		ActionContext actionContext = ActionContext.getContext();
		ActionInvocation actionInvocation = actionContext.getActionInvocation();
		ActionSupport actionSupport = (ActionSupport)actionInvocation.getAction();
		ValueStack valueStack = actionContext.getValueStack();
		return actionSupport.getText(aTextName,aTextName,new ArrayList<Object>(),valueStack);
	}

	@Override
	public boolean hasKey(String key) {
		return getTextProvider().hasKey(key);
	}

	@Override
	public String getText(String aTextName) {
		return convertToUTF8(getTextProvider().getText(aTextName));
	}

	@Override
	public String getText(String aTextName, String defaultValue) {
		return convertToUTF8(getTextProvider().getText(aTextName, defaultValue));
	}

	@Override
	public String getText(String aTextName, String defaultValue, String obj) {
		return convertToUTF8(getTextProvider().getText(aTextName, defaultValue, obj));
	}

	@Override
	public String getText(String aTextName, List<?> args) {
		return convertToUTF8(getTextProvider().getText(aTextName, args));
	}

	@Override
	public String getText(String key, String[] args) {
		return convertToUTF8(getTextProvider().getText(key, args));
	}

	@Override
	public String getText(String aTextName, String defaultValue, List<?> args) {
		return convertToUTF8(getTextProvider().getText(aTextName, defaultValue, args));
	}

	@Override
	public String getText(String key, String defaultValue, String[] args) {
		return convertToUTF8(getTextProvider().getText(key, defaultValue, args));
	}

	@Override
	public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
		return convertToUTF8(getTextProvider().getText(key, defaultValue, args, stack));
	}

	@Override
	public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
		return convertToUTF8(getTextProvider().getText(key, defaultValue, args, stack));
	}

	@Override
	public ResourceBundle getTexts() {
		return getTextProvider().getTexts();
	}

	@Override
	public ResourceBundle getTexts(String aBundleName) {
		return getTextProvider().getTexts(aBundleName);
	}

	@Override
	public Locale getLocale() {
		try {
			return ThreadLocalLocale.INSTANCE.load();
		} catch (Exception e) {
			// ignore
		}

		return Locale.ENGLISH;
	}

	/**
	 * If called first time it will create {@link com.opensymphony.xwork2.TextProviderFactory},
	 * inject dependency (if {@link com.opensymphony.xwork2.inject.Container} is accesible) into in,
	 * then will create new {@link com.opensymphony.xwork2.TextProvider} and store it in a field
	 * for future references and at the returns reference to that field
	 *
	 * @return reference to field with TextProvider
	 */
	private TextProvider getTextProvider() {
		if (textProvider == null) {
			TextProviderFactory tpf = new TextProviderFactory();
			if (container == null) {
				container = ActionContext.getContext().getContainer();
			}
			container.inject(tpf);
			textProvider = tpf.createInstance(getClass(), this);
		}
		return textProvider;
	}

	private static String convertToUTF8(String val){
		try {
			String utf8Value= new String(val.getBytes("ISO-8859-1"), "UTF-8");
			return utf8Value;
		} catch (UnsupportedEncodingException e) {
			log.warn(String.format("Failed to convert [%s] to UTF-8 for translations", val));
		}

		return val;
	}
}
