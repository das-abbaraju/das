package com.picsauditing.auditbuilder.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.auditbuilder.entities.AppTranslation")
@Table(name = "app_translation")
public class AppTranslation extends BaseTable implements java.io.Serializable {

	private String value;

	@Column(name = "msgValue", nullable = false)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}