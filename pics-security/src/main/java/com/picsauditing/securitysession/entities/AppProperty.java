package com.picsauditing.securitysession.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "app_properties")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "app_properties")
public class AppProperty implements java.io.Serializable {
//
//	public static final String SYSTEM_MESSAGE = "PICS.showSystemMessage";
//	public static final String BETA_LEVEL = "BETA_maxLevel";
//	public static final String QB_JAXB_ENCODING = "QuickBooks.JAXB.ASCII.Encoding";
//	public static final String QB_AXIS_ENCODING = "QuickBooks.Axis.ASCII.Encoding";
	public static final String VERSION_MAJOR = "VERSION.major";
	public static final String VERSION_MINOR = "VERSION.minor";
//	public static final String SSIP_CLIENT_SITE_ID_LIST_KEY = "Ssip.ClientSite.Ids";
//	public static final String EG_WELCOME_EMAIL_TEMPLATE_ID = "EG_WELCOME_EMAIL_TEMPLATE";
//	public static final String EG_BETA_FEEDBACK_EMAIL_TEMPLATE_ID = "EG_BETA_FEEDBACK_EMAIL_TEMPLATE";
//	public static final String EMAIL_FROM_INFO_AT_PICSAUDITING = "EMAIL_FROM_INFO_AT_PICSAUDITING";
//	public static final String EMAIL_TO_EG_FEEDBACK = "EMAIL_TO_EG_FEEDBACK";
//
//	public static final String AUTH_SERVICE_HOST = "AuthServiceHost";
//	public static final String AUTH_SERVICE_HOST_PORT = "AuthServiceHostPort";
//
//	private String property;
	private String value;

//    private String description;
//	private Date ticklerDate;
//
//	public AppProperty() {
//	}
//
//	public AppProperty(String property, String value) {
//		this.property = property;
//		this.value = value;
//	}
//
//	@Id
//	@Column(nullable = false)
//	public String getProperty() {
//		return property;
//	}
//
//	public void setProperty(String property) {
//		this.property = property;
//	}
//
	@Column(nullable = false)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    @Temporal(TemporalType.DATE)
//	public Date getTicklerDate() {
//		return ticklerDate;
//	}
//
//	public void setTicklerDate(Date ticklerDate) {
//		this.ticklerDate = ticklerDate;
//	}
//
}
