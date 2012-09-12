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
	public static final String LC_COR_TOGGLE = "Toggle.LcCor";

	private String property;
	private String value;
	private Date ticklerDate;

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

	public boolean valueEquals(Object comparisonValue) throws AppPropertyValueParseException {
		// TODO: Define a string standard for dates.
		try {
			if (comparisonValue instanceof String) {
				value.equals(comparisonValue);
			} else if (comparisonValue instanceof Integer) {
				Integer intValue = Integer.parseInt(value);
				return intValue.equals(comparisonValue);
			} else if (comparisonValue instanceof Boolean) {
				Boolean boolValue = Boolean.parseBoolean(value);
				return boolValue.equals(comparisonValue);
			} else {
				throw new AppPropertyValueParseException("comparisonValue class "
						+ comparisonValue.getClass().getSimpleName() + " is not supported.");
			}

		} catch (Exception parseException) {
			throw new AppPropertyValueParseException(parseException);
		}

		return false;
	}
}
