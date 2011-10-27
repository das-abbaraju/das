package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@SuppressWarnings("serial")
@Entity
public class Naics implements java.io.Serializable {
	private String code;
	private float trir;
	private float lwcr;

	@Id
	@Column(nullable = false, length = 6)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public float getTrir() {
		return trir;
	}

	public void setTrir(float trir) {
		this.trir = trir;
	}

	public float getLwcr() {
		return lwcr;
	}

	public void setLwcr(float lwcr) {
		this.lwcr = lwcr;
	}
}
