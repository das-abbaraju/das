package com.picsauditing.messaging;

public class MessagePublisherService {

    private Publisher csrAssignmentSinglePublisher;
    private Publisher flagChangePublisher;
    private Publisher emailRequestPublisher;

    public Publisher getCsrAssignmentSinglePublisher() {
        return csrAssignmentSinglePublisher;
    }

    public void setCsrAssignmentSinglePublisher(Publisher csrAssignmentSinglePublisher) {
        this.csrAssignmentSinglePublisher = csrAssignmentSinglePublisher;
    }

    public Publisher getFlagChangePublisher() {
        return flagChangePublisher;
    }

    public void setFlagChangePublisher(Publisher flagChangePublisher) {
        this.flagChangePublisher = flagChangePublisher;
    }

    public Publisher getEmailRequestPublisher() {
        return emailRequestPublisher;
    }

    public void setEmailRequestPublisher(Publisher emailRequestPublisher) {
        this.emailRequestPublisher = emailRequestPublisher;
    }
}
