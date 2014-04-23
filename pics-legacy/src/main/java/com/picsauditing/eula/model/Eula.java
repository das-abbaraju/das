package com.picsauditing.eula.model;

import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.Country;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "eula")
public class Eula extends BaseTable {
    private String name;
    private int versionNumber;
    private Country country;
    private String eulaBody;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    @ManyToOne
    @JoinColumn(name = "isoCode")
    public Country getCountry() {
        return country;
    }

    public void setCountry(Country isoCode) {
        this.country = isoCode;
    }

    public String getEulaBody() {
        return eulaBody;
    }

    public void setEulaBody(String eulaBody) {
        this.eulaBody = eulaBody;
    }
}
