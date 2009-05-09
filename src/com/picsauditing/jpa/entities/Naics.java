package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Naics {
	private String code;
	private float trir;

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

}
