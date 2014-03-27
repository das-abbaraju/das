package com.picsauditing.jpa.entities;


import com.picsauditing.model.account.AddressVerificationStatus;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "address_verification")
public class AddressVerification extends BaseTable {
    private Date verificationDate;
    private AddressVerificationStatus status;
    private String entityType;

    public Date getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(Date verificationDate) {
        this.verificationDate = verificationDate;
    }

    @Enumerated(EnumType.STRING)
    @Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = {
            @org.hibernate.annotations.Parameter(name = "enumClass", value = "com.picsauditing.model.account.AddressVerificationStatus"),
            @org.hibernate.annotations.Parameter(name = "identifierMethod", value = "getDbValue"),
            @org.hibernate.annotations.Parameter(name = "valueOfMethod", value = "fromDbValue")})
    public AddressVerificationStatus getStatus() {
        return status;
    }

    public void setStatus(AddressVerificationStatus status) {
        this.status = status;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
}
