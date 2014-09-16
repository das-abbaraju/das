package com.picsauditing.employeeguard.entities;

import javax.persistence.Embeddable;
import java.util.Locale;

@Embeddable
public class Settings {

	private Locale locale = Locale.UK;

	public Settings() {
	}

	public Settings(Locale locale) {
		this.locale = locale;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
