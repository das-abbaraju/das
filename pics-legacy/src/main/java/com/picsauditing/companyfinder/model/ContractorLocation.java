package com.picsauditing.companyfinder.model;

import com.picsauditing.companyfinder.model.builder.ContractorLocationBuilder;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_location")
public class ContractorLocation extends BaseTable {

    private ContractorAccount contractor;
    private double latitude;
    private double longitude;

    @ManyToOne
    @JoinColumn(name = "conId", nullable = false, updatable = false)
    public ContractorAccount getContractor() {
        return contractor;
    }

    public void setContractor(ContractorAccount contractor) {
        this.contractor = contractor;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public static ContractorLocationBuilder builder() {
        return new ContractorLocationBuilder();
    }
}
