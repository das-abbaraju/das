package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@Table(name = "app_properties")
public class AppProperty implements java.io.Serializable {
	
	public static final String LIVECHAT = "PICS.liveChat";
	public static final String SYSTEM_MESSAGE = "PICS.showSystemMessage";
	public static final String BETA_LEVEL = "BETA_maxLevel";
	public static final String QB_JAXB_ENCODING = "QuickBooks.JAXB.ASCII.Encoding";
	public static final String QB_AXIS_ENCODING = "QuickBooks.Axis.ASCII.Encoding";
	public static final String FULLY_SUPPORTED_LANGUAGES = "PICS.fullySupportedLanguages";

	private String property;
	private String value;
	private Date ticklerDate;

	public AppProperty() {
	}

	public AppProperty(String property, String value) {
		this.property = property;
		this.value = value;
	}

	@Id
	@Column(nullable = false)
	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	@Column(nullable = false)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Temporal(TemporalType.DATE)
	public Date getTicklerDate() {
		return ticklerDate;
	}

	public void setTicklerDate(Date ticklerDate) {
		this.ticklerDate = ticklerDate;
	}

}
