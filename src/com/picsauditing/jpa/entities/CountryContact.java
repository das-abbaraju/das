package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.*;

@Entity
@Table(name = "country_contact")
public class CountryContact extends BaseTable {
	protected Country country;
    protected String csrPhone;
    protected String csrFax;
    protected String csrEmail;
    protected String csrAddress;
    protected String csrCity;
    protected CountrySubdivision csrCountrySubdivision;
    protected String csrZip;
    protected String isrPhone;
    protected String isrFax;
    protected String isrEmail;
    protected String isrAddress;
    protected String isrCity;
    protected CountrySubdivision isrCountrySubdivision;
    protected String isrZip;
    protected BusinessUnit businessUnit;

    @ManyToOne
    @JoinColumn(name="businessUnitID")
    public BusinessUnit getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(BusinessUnit businessUnit) {
        this.businessUnit = businessUnit;
    }

    @Column(name = "isrZip", length = 15)
    public String getIsrZip() {
        return isrZip;
    }

    public void setIsrZip(String isrZip) {
        this.isrZip = isrZip;
    }

    @ManyToOne
    @JoinColumn(name = "isrCountrySubdivision")
    public CountrySubdivision getIsrCountrySubdivision() {
        return isrCountrySubdivision;
    }

    public void setIsrCountrySubdivision(CountrySubdivision isrCountrySubdivision) {
        this.isrCountrySubdivision = isrCountrySubdivision;
    }

    @Column(name = "isrCity", length = 35)
    public String getIsrCity() {
        return isrCity;
    }

    public void setIsrCity(String isrCity) {
        this.isrCity = isrCity;
    }

    @Column(name = "isrAddress", length = 50)
    public String getIsrAddress() {
        return isrAddress;
    }

    public void setIsrAddress(String isrAddress) {
        this.isrAddress = isrAddress;
    }

    @Column(name = "isrEmail", length = 100)
    public String getIsrEmail() {
        return isrEmail;
    }

    public void setIsrEmail(String isrEmail) {
        this.isrEmail = isrEmail;
    }

    @Column(name = "isrFax", length = 20)
    public String getIsrFax() {
        return isrFax;
    }

    public void setIsrFax(String isrFax) {
        this.isrFax = isrFax;
    }

    @Column(name = "isrPhone", length = 25)
    public String getIsrPhone() {
        return isrPhone;
    }

    public void setIsrPhone(String isrPhone) {
        this.isrPhone = isrPhone;
    }

    @Column(name = "csrZip", length = 15)
    public String getCsrZip() {
        return csrZip;
    }

    public void setCsrZip(String csrZip) {
        this.csrZip = csrZip;
    }

    @ManyToOne
    @JoinColumn(name = "csrCountrySubdivision")
    public CountrySubdivision getCsrCountrySubdivision() {
        return csrCountrySubdivision;
    }

    public void setCsrCountrySubdivision(CountrySubdivision csrCountrySubdivision) {
        this.csrCountrySubdivision = csrCountrySubdivision;
    }

    @Column(name = "csrCity", length = 35)
    public String getCsrCity() {
        return csrCity;
    }

    public void setCsrCity(String csrCity) {
        this.csrCity = csrCity;
    }

    @Column(name = "csrAddress", length = 50)
    public String getCsrAddress() {
        return csrAddress;
    }

    public void setCsrAddress(String csrAddress) {
        this.csrAddress = csrAddress;
    }

    @Column(name = "csrEmail", length = 100)
    public String getCsrEmail() {
        return csrEmail;
    }

    public void setCsrEmail(String csrEmail) {
        this.csrEmail = csrEmail;
    }

    @Column(name = "csrFax", length = 20)
    public String getCsrFax() {
        return csrFax;
    }

    public void setCsrFax(String csrFax) {
        this.csrFax = csrFax;
    }

    @Column(name = "csrPhone", length = 25)
    public String getCsrPhone() {
        return csrPhone;
    }

    public void setCsrPhone(String csrPhone) {
        this.csrPhone = csrPhone;
    }

    @OneToOne
    @JoinColumn(name = "country")
    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

}
