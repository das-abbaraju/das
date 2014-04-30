package com.picsauditing.flagcalculator.entities;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.Account")
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.JOINED)
public class Account extends BaseTable {

    protected String name;
    protected AccountStatus status = AccountStatus.Pending;
    private Naics naics;
    protected String type;

    @Column(name = "name", nullable = false, length = 50)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "naics")
    public Naics getNaics() {
        return naics;
    }

    public void setNaics(Naics naics) {
        this.naics = naics;
    }

    @Type(type = "com.picsauditing.flagcalculator.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.flagcalculator.entities.AccountStatus") })
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    /**
     * Contractor, Operator, Admin, Corporate
     *
     * @return
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}