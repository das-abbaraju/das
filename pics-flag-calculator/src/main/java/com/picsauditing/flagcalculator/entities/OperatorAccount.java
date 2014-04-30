package com.picsauditing.flagcalculator.entities;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.OperatorAccount")
@Table(name = "operators")
public class OperatorAccount extends Account {
    private OperatorAccount parent;
    private OperatorAccount inheritFlagCriteria;
    private YesNo canSeeInsurance = YesNo.No;
    private OshaType oshaType = OshaType.OSHA;
    private boolean primaryCorporate = false;

    private List<Facility> corporateFacilities = new ArrayList<>();
    private List<Facility> operatorFacilities = new ArrayList<>();
    private List<FlagCriteriaOperator> flagCriteria = new ArrayList<FlagCriteriaOperator>();
<<<<<<< HEAD
//    private List<JobSite> jobSites = new ArrayList<JobSite>();
//    private List<OperatorCompetency> competencies = new ArrayList<OperatorCompetency>();
//    private Set<Integer> visibleAuditTypes = null;
//    private List<Facility> linkedClients = new ArrayList<Facility>();
//    private List<Facility> linkedGeneralContractors = new ArrayList<Facility>();
//
//    public OperatorAccount() {
//        this.type = Account.OPERATOR_ACCOUNT_TYPE;
//        this.onsiteServices = true;
//        this.offsiteServices = true;
//        this.materialSupplier = true;
//        this.transportationServices = true;
//    }
//
//    public OperatorAccount(String name) {
//        this.name = name;
//        this.type = Account.OPERATOR_ACCOUNT_TYPE;
//        this.onsiteServices = true;
//        this.offsiteServices = true;
//        this.materialSupplier = true;
//        this.transportationServices = true;
//    }
//
//    @Transient
//    public String getFullName() {
//        if (Strings.isNullOrEmpty(dbaName)) {
//            return name;
//        }
//
//        return dbaName;
//    }
//
//    /**
//     * Yes, No, Multiple
//     */
//    @Column(nullable = false, length = 8)
//    @ReportField()
//    public String getDoContractorsPay() {
//        return this.doContractorsPay;
//    }
//
//    public void setDoContractorsPay(String doContractorsPay) {
//        this.doContractorsPay = doContractorsPay;
//    }
//
    @Type(type = "com.picsauditing.flagcalculator.entities.EnumMapperWithEmptyStrings", parameters = {@org.hibernate.annotations.Parameter(name = "enumClass", value = "com.picsauditing.flagcalculator.entities.YesNo")})
=======

    @Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = {@org.hibernate.annotations.Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.YesNo")})
>>>>>>> 7ae760b... US831 Deprecated old FDC
    @Column(name = "canSeeInsurance", nullable = false)
    @Enumerated(EnumType.STRING)
    public YesNo getCanSeeInsurance() {
        if (this.canSeeInsurance == null) {
            this.canSeeInsurance = YesNo.No;
        }
        return this.canSeeInsurance;
    }

    public void setCanSeeInsurance(YesNo canSeeInsurance) {
        this.canSeeInsurance = canSeeInsurance;
    }

    @Enumerated(EnumType.STRING)
    public OshaType getOshaType() {
        return oshaType;
    }

    public void setOshaType(OshaType oshaType) {
        this.oshaType = oshaType;
    }

    public boolean isPrimaryCorporate() {
        return primaryCorporate;
    }

    public void setPrimaryCorporate(boolean primaryCorporate) {
        this.primaryCorporate = primaryCorporate;
    }

    @ManyToOne
    @JoinColumn(name = "inheritFlagCriteria")
    public OperatorAccount getInheritFlagCriteria() {
        return inheritFlagCriteria;
    }

    public void setInheritFlagCriteria(OperatorAccount inheritFlagCriteria) {
        this.inheritFlagCriteria = inheritFlagCriteria;
    }

    @OneToMany(mappedBy = "operator", orphanRemoval = true)
    @Where(clause = "type IS NULL")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    public List<Facility> getCorporateFacilities() {
        return corporateFacilities;
    }

    public void setCorporateFacilities(List<Facility> corporateFacilities) {
        this.corporateFacilities = corporateFacilities;
    }

    /**
     * @return a list of all "associated" operator accounts associated via the
     *         facilities intersection table for example, BASF would contain
     *         BASF Port Arthur but not BASF Freeport Hub
     * @see getOperatorAccounts()
     */
    @OneToMany(mappedBy = "corporate")
    @Where(clause = "type IS NULL")
    public List<Facility> getOperatorFacilities() {
        return operatorFacilities;
    }

    public void setOperatorFacilities(List<Facility> operatorFacilities) {
        this.operatorFacilities = operatorFacilities;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentID", nullable = true)
    public OperatorAccount getParent() {
        return parent;
    }

    public void setParent(OperatorAccount parent) {
        this.parent = parent;
    }

    @OneToMany(mappedBy = "operator", cascade = {CascadeType.ALL})
    public List<FlagCriteriaOperator> getFlagCriteria() {
        return flagCriteria;
    }

    public void setFlagCriteria(List<FlagCriteriaOperator> flagCriteria) {
        this.flagCriteria = flagCriteria;
    }
}