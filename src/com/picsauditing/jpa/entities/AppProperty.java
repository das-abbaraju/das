package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "app_properties")
public class AppProperty implements java.io.Serializable {

	private String property;
	private String value;

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

	public boolean valueEquals(Object comparisonValue) throws AppPropertyValueParseException {
		// TODO: Define a string standard for dates.
		if (comparisonValue instanceof String) {
			value.equals(comparisonValue);
		} else if (comparisonValue instanceof Integer) {
			Integer intValue = Integer.parseInt(value);
			return intValue.equals(comparisonValue);
		} else if (comparisonValue instanceof Boolean) {
			Boolean boolValue = Boolean.parseBoolean(value);
			return boolValue.equals(comparisonValue);
		} else {
			throw new AppPropertyValueParseException();
		}

		return false;
	}
}
