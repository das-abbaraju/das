package com.picsauditing.auditbuilder.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ref_country")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class Country implements Serializable {
    protected String isoCode;
	protected Double corruptionPerceptionIndex;

	@Id
	@Column(nullable = false, length = 2)
	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

    @Column(name = "perceivedCorruption")
    public Double getCorruptionPerceptionIndex() {
        return this.corruptionPerceptionIndex;
    }

    public void setCorruptionPerceptionIndex(Double corruptionPerceptionIndex) {
        this.corruptionPerceptionIndex = corruptionPerceptionIndex;
    }
}