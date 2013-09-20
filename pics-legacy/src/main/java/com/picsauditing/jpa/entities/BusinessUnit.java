package com.picsauditing.jpa.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

@Entity
@Table(name = "ref_sap_business_unit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class BusinessUnit implements Serializable {
	private int id;

    private String businessUnit;
	private String displayName;
	private String address;

	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Transient
	public String getAddressSingleLine() {
		String address = getAddress();
		String singleLine = address.replaceAll("\n", ", ");
		return singleLine;
	}
}
