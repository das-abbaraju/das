package com.picsauditing.jpa.entities;

import java.io.Serializable;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "ref_state")
public class State implements Serializable {
	private static final long serialVersionUID = -7010252482295453919L;

	protected String isoCode;
	protected String english;
	protected String french;

	protected Country country;
	protected User csr;

	public State() {
	}

	public State(String isoCode) {
		this.isoCode = isoCode;
	}

	@Id
	@Column(nullable = false, length = 2)
	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

	public String getEnglish() {
		return english;
	}

	public void setEnglish(String english) {
		this.english = english;
	}

	public String getFrench() {
		return french;
	}

	public void setFrench(String french) {
		this.french = french;
	}

	@ManyToOne
	@JoinColumn(name = "countryCode")
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "csrID")
	public User getCsr() {
		return csr;
	}

	public void setCsr(User csr) {
		this.csr = csr;
	}

	@Transient
	public String getName() {
		return english;
	}

	@Transient
	public String getName(Locale locale) {
		if (locale.getLanguage().equals("fr"))
			return french;
		return english;
	}

	@Override
	public String toString() {
		return isoCode;
	}

}
