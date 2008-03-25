package com.picsauditing.jpa.entities;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "facilities")
public class Facility implements java.io.Serializable {

	private FacilityId id;

	@EmbeddedId
	@AttributeOverrides( { @AttributeOverride(name = "opId", column = @Column(name = "opID", nullable = false)),
			@AttributeOverride(name = "corporateId", column = @Column(name = "corporateID", nullable = false)) })
	public FacilityId getId() {
		return this.id;
	}

	public void setId(FacilityId id) {
		this.id = id;
	}
}
