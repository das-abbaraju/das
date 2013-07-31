package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.picsauditing.jpa.entities.builders.TimeZoneBuilder;

@Entity
@Table(name = "time_zone")
public class TimeZoneListByCountry {
	private int id;
	private String countryCode;
	private String timeZoneName;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(nullable = false)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Column(name="country_code", nullable = false)
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	@Column(name="zone_name", nullable = false)
	public String getTimeZoneName() {
		return timeZoneName;
	}
	public void setTimeZoneName(String timeZoneName) {
		this.timeZoneName = timeZoneName;
	}
	public static TimeZoneBuilder builder() {
		return new TimeZoneBuilder();
	}

}
