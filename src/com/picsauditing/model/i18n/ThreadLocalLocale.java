package com.picsauditing.model.i18n;

import java.util.Locale;

import com.spun.util.persistence.Loader;

public final class ThreadLocalLocale implements Loader<Locale> {

	public static final ThreadLocalLocale INSTANCE = new ThreadLocalLocale();

	private final ThreadLocal<Locale> locales = new ThreadLocal<>();

	@Override
	final public Locale load() throws Exception {
		return locales.get();
	}

	public final Locale set(Locale locale) {
		this.locales.set(locale);
		return locale;
	}

}
