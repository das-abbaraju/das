// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.controller;

import java.util.List;

import com.klark.user.model.BeneficiaryDTO;
import com.klark.user.model.ReceivedMessage;
import com.klark.user.model.ScheduleorUnscheduleMessage;
import com.klark.user.model.User;

/**
 * Description here!
 * 
 * 
 * @author
 */

public class AccountView {

    private User user;
    private int countReceivedMsgs;
    private int countBeneficiaries;

    private int countOutGoingMsgs;

    public int getCountOutGoingMsgs() {
        return countOutGoingMsgs;
    }

    public void setCountOutGoingMsgs(int countOutGoingMsgs) {
        this.countOutGoingMsgs = countOutGoingMsgs;
    }

    private List<String> subjectLines;

    private List<ReceivedMessage> receivedMsgs;

    private List<ScheduleorUnscheduleMessage> outGoingMsgs;

    public List<ScheduleorUnscheduleMessage> getOutGoingMsgs() {
        return outGoingMsgs;
    }

    public void setOutGoingMsgs(List<ScheduleorUnscheduleMessage> outGoingMsgs) {
        this.outGoingMsgs = outGoingMsgs;
    }

    public List<ReceivedMessage> getReceivedMsgs() {
        return receivedMsgs;
    }

    public void setReceivedMsgs(List<ReceivedMessage> receivedMsgs) {
        this.receivedMsgs = receivedMsgs;
    }

    private List<ScheduleorUnscheduleMessage> scheduledMsgs;

    public List<ScheduleorUnscheduleMessage> getScheduledMsgs() {
        return scheduledMsgs;
    }

    public void setScheduledMsgs(List<ScheduleorUnscheduleMessage> scheduledMsgs) {
        this.scheduledMsgs = scheduledMsgs;
    }

    private List<BeneficiaryDTO> beneficiaries;

    public List<BeneficiaryDTO> getBeneficiaries() {
        return beneficiaries;
    }

    public void setBeneficiaries(List<BeneficiaryDTO> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getCountReceivedMsgs() {
        return countReceivedMsgs;
    }

    public void setCountReceivedMsgs(int countReceivedMsgs) {
        this.countReceivedMsgs = countReceivedMsgs;
    }

    public int getCountBeneficiaries() {
        return countBeneficiaries;
    }

    public void setCountBeneficiaries(int countBeneficiaries) {
        this.countBeneficiaries = countBeneficiaries;
    }

    public List<String> getSubjectLines() {
        return subjectLines;
    }

    public void setSubjectLines(List<String> subjectLines) {
        this.subjectLines = subjectLines;
    }

}
