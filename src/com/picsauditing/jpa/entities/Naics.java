package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.picsauditing.report.annotations.ReportField;
import com.picsauditing.report.tables.FieldCategory;

@SuppressWarnings("serial")
@Entity
public class Naics implements java.io.Serializable {
	private String code;
	private float trir;
	private float lwcr;

	@Id
	@Column(nullable = false, length = 6)
	@ReportField(category = FieldCategory.Classification)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@ReportField(category = FieldCategory.CompanyStatistics)
	public float getTrir() {
		return trir;
	}

	public void setTrir(float trir) {
		this.trir = trir;
	}

	@ReportField(category = FieldCategory.CompanyStatistics)
	public float getLwcr() {
		return lwcr;
	}

	public void setLwcr(float lwcr) {
		this.lwcr = lwcr;
	}
}