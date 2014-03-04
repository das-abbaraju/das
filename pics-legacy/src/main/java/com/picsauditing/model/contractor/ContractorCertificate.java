package com.picsauditing.model.contractor;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.ContractorAccount;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_certificate")
public class ContractorCertificate {
    private int id;
    private ContractorAccount contractor;
    private Date issueDate;
    private Date expirationDate;
    private CertificateType certificateType;
    private CertificationMethod certificationMethod;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "contractorID")
    public ContractorAccount getContractor() {
        return contractor;
    }

    public void setContractor(ContractorAccount contractor) {
        this.contractor = contractor;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Enumerated(EnumType.STRING)
    @Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = {
    @org.hibernate.annotations.Parameter(name = "enumClass", value = "com.picsauditing.model.contractor.CertificateType"),
    @org.hibernate.annotations.Parameter(name = "identifierMethod", value = "getDbValue"),
    @org.hibernate.annotations.Parameter(name = "valueOfMethod", value = "fromDbValue")})
    public CertificateType getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
    }

    @Enumerated(EnumType.STRING)
    @Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = {
            @org.hibernate.annotations.Parameter(name = "enumClass", value = "com.picsauditing.model.contractor.CertificationMethod"),
            @org.hibernate.annotations.Parameter(name = "identifierMethod", value = "getDbValue"),
            @org.hibernate.annotations.Parameter(name = "valueOfMethod", value = "fromDbValue")})
    public CertificationMethod getCertificationMethod() {
        return certificationMethod;
    }

    public void setCertificationMethod(CertificationMethod certificationMethod) {
        this.certificationMethod = certificationMethod;
    }

    @Transient
    public String getIssueDateString() {
        return DateBean.format(issueDate, "yyyy-MM-dd");    // jstl date format doesn't work with iText
    }

    @Transient
    public String getExpirationDateString() {
        return DateBean.format(expirationDate, "yyyy-MM-dd");
    }

    @Override
    public String toString() {
        return "ContractorCertificate{" +
                "id=" + id +
                ", contractor=" + contractor +
                ", issueDate=" + issueDate +
                ", expirationDate=" + expirationDate +
                ", certificateType=" + certificateType +
                ", certificationMethod=" + certificationMethod +
                '}';
    }

    public static ContractorCertificateBuilder builder() {
        return new ContractorCertificateBuilder();
    }

    public static class ContractorCertificateBuilder {
        private ContractorCertificate contractorCertificate = new ContractorCertificate();

        public ContractorCertificate build() {
            return contractorCertificate;
        }

        public ContractorCertificateBuilder contractor(ContractorAccount contractor) {
            contractorCertificate.setContractor(contractor);
            return this;
        }

        public ContractorCertificateBuilder issueDate(Date issueDate) {
            contractorCertificate.setIssueDate(issueDate);
            return this;
        }

        public ContractorCertificateBuilder expirationDate(Date expirationDate) {
            contractorCertificate.setExpirationDate(expirationDate);
            return this;
        }

        public ContractorCertificateBuilder certificateType(CertificateType certificateType) {
            contractorCertificate.setCertificateType(certificateType);
            return this;
        }

        public ContractorCertificateBuilder certificationMethod(CertificationMethod certificationMethod) {
            contractorCertificate.setCertificationMethod(certificationMethod);
            return this;
        }
    }
}
