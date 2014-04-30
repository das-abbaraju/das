package com.picsauditing.flagcalculator.entities;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.Naics")
@Table(name = "naics")
public class Naics implements java.io.Serializable {
	private String code;
//	private float trir;
//	private float lwcr;
	private float dart;
//
	@Id
	@Column(nullable = false, length = 6)
//	@ReportField(importance = FieldImportance.Required)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		if (code != null) {
			code = code.trim();
		}
		this.code = code;
	}

//	public float getTrir() {
//		return trir;
//	}
//
//	public void setTrir(float trir) {
//		this.trir = trir;
//	}
//
//	public float getLwcr() {
//		return lwcr;
//	}
//
//	public void setLwcr(float lwcr) {
//		this.lwcr = lwcr;
//	}
//
	public float getDart() {
		return dart;
	}

	public void setDart(float dart) {
		this.dart = dart;
	}
}