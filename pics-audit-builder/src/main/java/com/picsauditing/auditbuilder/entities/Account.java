package com.picsauditing.auditbuilder.entities;

import com.picsauditing.auditbuilder.service.AccountService;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
@Entity
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.JOINED)
public class Account extends BaseTable implements Comparable<Account> {
    public static List<Integer> PICS_CORPORATE = Collections.unmodifiableList(
        Arrays.asList(4, 5, 6, 7, 8, 9, 10, 11));

    public static final String ADMIN_ACCOUNT_TYPE = "Admin";

    protected String name;
    protected AccountStatus status = AccountStatus.Pending;
    protected String type;
    protected boolean onsiteServices = false;
    protected boolean offsiteServices = false;
    protected boolean materialSupplier = false;
    protected boolean transportationServices = false;

    @Column(name = "name", nullable = false, length = 50)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Type(type = "com.picsauditing.auditbuilder.entities.EnumMapperWithEmptyStrings", parameters = {
            @Parameter(name = "enumClass", value = "com.picsauditing.auditbuilder.entities.AccountStatus")})
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isOnsiteServices() {
        return onsiteServices;
    }

    public void setOnsiteServices(boolean onsiteServices) {
        this.onsiteServices = onsiteServices;
    }

    public boolean isOffsiteServices() {
        return offsiteServices;
    }

    public void setOffsiteServices(boolean offsiteServices) {
        this.offsiteServices = offsiteServices;
    }

    public boolean isMaterialSupplier() {
        return materialSupplier;
    }

    public void setMaterialSupplier(boolean materialSupplier) {
        this.materialSupplier = materialSupplier;
    }

    public boolean isTransportationServices() {
        return transportationServices;
    }

    public void setTransportationServices(boolean transportationServices) {
        this.transportationServices = transportationServices;
    }

    @Override
    public int compareTo(Account o) {
        if (o.getId() == id) {
            return 0;
        }
        if (!o.getType().equals(type)) {
            if (this.getType().equals(Account.ADMIN_ACCOUNT_TYPE)) {
                return -1;
            }
            if (AccountService.isContractor(this)) {
                return 1;
            }
            if (AccountService.isCorporate(this)) {
                if (o.getType().equals(Account.ADMIN_ACCOUNT_TYPE)) {
                    return 1;
                } else {
                    return -1;
                }
            }
            if (AccountService.isOperator(this)) {
                if (AccountService.isContractor(o)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
        return name.compareToIgnoreCase(o.getName());
    }
}