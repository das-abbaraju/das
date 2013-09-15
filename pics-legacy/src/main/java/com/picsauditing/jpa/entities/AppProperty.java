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

	public static final String SYSTEM_MESSAGE = "PICS.showSystemMessage";
	public static final String BETA_LEVEL = "BETA_maxLevel";
	public static final String QB_JAXB_ENCODING = "QuickBooks.JAXB.ASCII.Encoding";
	public static final String QB_AXIS_ENCODING = "QuickBooks.Axis.ASCII.Encoding";
	public static final String SAP_BIZ_UNITS_ENABLED = "SAP.BusinessUnits.Enabled";
	public static final String VERSION_MAJOR = "VERSION.major";
	public static final String VERSION_MINOR = "VERSION.minor";
	public static final String SSIP_CLIENT_SITE_ID_LIST_KEY = "Ssip.ClientSite.Ids";
	public static final String SAP_BIZ_UNITS_SET_SYNC_TRUE_ENABLED = "SAP.BusinessUnits.SetSyncTrue.Enabled";


	private String property;
	private String value;

    private String description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Temporal(TemporalType.DATE)
	public Date getTicklerDate() {
		return ticklerDate;
	}

	public void setTicklerDate(Date ticklerDate) {
		this.ticklerDate = ticklerDate;
	}

}