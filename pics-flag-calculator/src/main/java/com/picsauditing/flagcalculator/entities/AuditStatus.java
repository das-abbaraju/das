package com.picsauditing.flagcalculator.entities;

public enum AuditStatus {
    Pending,
    Incomplete,
    Submitted,
    Resubmit,
    Resubmitted,
    Complete,
    PendingClientApproval,
    Approved,
    NotApplicable,
    Expired;

    /**
     * Pending, Incomplete, Submitted, Resubmitted, Complete, Approved,
     * NotApplicable
     *
     * @param o
     * @return
     */
    public boolean before(AuditStatus o) {
        return this.ordinal() < o.ordinal();
    }

    /**
     * Pending, Incomplete, Submitted, Resubmitted, Complete, Approved,
     * NotApplicable
     *
     * @param o
     * @return
     */
    public boolean after(AuditStatus o) {
        return this.ordinal() > o.ordinal();
    }

    /**
     * Inclusive between
     *
     * @param start
     * @param end
     * @return
     */
    public boolean between(AuditStatus start, AuditStatus end) {
        return start.ordinal() <= this.ordinal() && this.ordinal() <= end.ordinal();
    }

    public boolean isSubmitted() {
        return this.equals(Submitted);
    }

    public boolean isComplete() {
        return this.equals(Complete);
    }
}