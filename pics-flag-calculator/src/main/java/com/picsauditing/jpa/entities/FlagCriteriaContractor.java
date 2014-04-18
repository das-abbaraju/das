package com.picsauditing.jpa.entities;

@SuppressWarnings("serial")
//@Entity
//@Table(name = "flag_criteria_contractor")
public class FlagCriteriaContractor extends BaseTable /*implements Comparable<FlagCriteriaContractor>, UpdatableListItem*/ {

    private ContractorAccount contractor;
    private FlagCriteria criteria;
    private String answer;
//    private String answer2;
//    private boolean verified;

    public FlagCriteriaContractor() {
    }

    public FlagCriteriaContractor(ContractorAccount ca, FlagCriteria fc, String answer) {
        contractor = ca;
        criteria = fc;
        this.answer = answer;
        setAuditColumns(new User(User.SYSTEM));
    }

//    @ManyToOne
//    @JoinColumn(name = "conID", nullable = false)
    public ContractorAccount getContractor() {
        return contractor;
    }

//    public void setContractor(ContractorAccount contractorAccount) {
//        this.contractor = contractorAccount;
//    }
//
//    @ManyToOne
//    @JoinColumn(name = "criteriaID", nullable = false)
    public FlagCriteria getCriteria() {
        return criteria;
    }

//    public void setCriteria(FlagCriteria criteria) {
//        this.criteria = criteria;
//    }
//
//    @ReportField(importance = FieldImportance.Required)
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

//    public String getAnswer2() {
//        return answer2;
//    }
//
//    public void setAnswer2(String answer2) {
//        this.answer2 = answer2;
//    }
//
//    public boolean isVerified() {
//        return verified;
//    }
//
//    public void setVerified(boolean verified) {
//        this.verified = verified;
//    }
//
//    @Override
//    public boolean equals(Object other) {
//        FlagCriteriaContractor fcc = (FlagCriteriaContractor) other;
//
//        if (fcc.getId() > 0 && id > 0)
//            return super.equals(other);
//        if (!contractor.equals(fcc.getContractor()))
//            return false;
//        if (!criteria.equals(fcc.getCriteria()))
//            return false;
//        return true;
//    }
//
//    @Override
//    public String toString() {
//        return criteria.toString() + " (" + answer + ") for " + contractor.toString();
//    }
//
//    @Override
//    public void update(UpdatableListItem change) {
//        FlagCriteriaContractor fcc = (FlagCriteriaContractor) change;
//
//        if (!Strings.isEqualNullSafe(fcc.getAnswer(), answer)) {
//            answer = fcc.getAnswer();
//            setAuditColumns(new User(User.SYSTEM));
//        }
//
//        if (!Strings.isEmpty(fcc.getAnswer2()) && !fcc.getAnswer2().equals(answer2)) {
//            answer2 = fcc.getAnswer2();
//            setAuditColumns(new User(User.SYSTEM));
//        }
//        if (verified != fcc.isVerified()) {
//            verified = fcc.isVerified();
//            setAuditColumns(new User(User.SYSTEM));
//        }
//    }
//
//    @Override
//    public int compareTo(FlagCriteriaContractor o) {
//        int res = contractor.compareTo(o.contractor);
//        if (res == 0)
//            res = criteria.compareTo(o.criteria);
//        return res;
//    }
//
//    public static FlagCriteriaContractorBuilder builder() {
//        return new FlagCriteriaContractorBuilder();
//    }
}
