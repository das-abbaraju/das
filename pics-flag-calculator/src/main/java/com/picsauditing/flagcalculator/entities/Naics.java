package com.picsauditing.flagcalculator.entities;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.Naics")
@Table(name = "naics")
public class Naics implements java.io.Serializable {
	private String code;
	private float dart;
<<<<<<< HEAD
//
	@Id
	@Column(nullable = false, length = 6)
//	@ReportField(importance = FieldImportance.Required)
=======

>>>>>>> 7ae760b... US831 Deprecated old FDC
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		if (code != null) {
			code = code.trim();
		}
		this.code = code;
	}

	public float getDart() {
		return dart;
	}

	public void setDart(float dart) {
		this.dart = dart;
	}
}