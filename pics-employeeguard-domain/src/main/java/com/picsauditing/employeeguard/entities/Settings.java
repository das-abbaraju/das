package com.picsauditing.employeeguard.entities;

import com.picsauditing.database.domain.Identifiable;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Embeddable
public class Settings {

	private Locale locale=Locale.UK;

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
