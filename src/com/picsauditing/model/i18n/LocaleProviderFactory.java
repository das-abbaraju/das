package com.picsauditing.model.i18n;

public class LocaleProviderFactory {

	public enum LocaleProviderType { Default, StrutsFramework }

	public static LocaleProvider localeProviderFactoryMethod(LocaleProviderType localeProviderType) {
		switch (localeProviderType) {
			case StrutsFramework:
				return new StrutsLocaleProvider();

			case Default:
				return new DefaultLocaleProvider();

			default:
				throw new IllegalArgumentException("Invalid LocaleProviderType.");
		}
	}

}
