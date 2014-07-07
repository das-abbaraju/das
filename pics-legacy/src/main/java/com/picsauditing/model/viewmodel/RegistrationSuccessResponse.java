package com.picsauditing.model.viewmodel;

import com.picsauditing.model.viewmodel.builder.RegistrationSuccessResponseBuilder;
import com.picsauditing.service.addressverifier.AddressResponseHolder;
import com.picsauditing.service.addressverifier.ResultStatus;
import com.picsauditing.struts.controller.forms.RegistrationForm;

import java.util.List;

public class RegistrationSuccessResponse {
    private List<User> users;
    private ResultStatus addressVerificationResultStatus;
    private RegistrationForm registrationForm;
    private AddressResponseHolder addressResponseHolder;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public ResultStatus getAddressVerificationResultStatus() {
        return addressVerificationResultStatus;
    }

    public void setAddressVerificationResultStatus(ResultStatus addressVerificationResultStatus) {
        this.addressVerificationResultStatus = addressVerificationResultStatus;
    }

    public RegistrationForm getRegistrationForm() {
        return registrationForm;
    }

    public void setRegistrationForm(RegistrationForm registrationForm) {
        this.registrationForm = registrationForm;
    }

    public AddressResponseHolder getAddressResponseHolder() {
        return addressResponseHolder;
    }

    public void setAddressResponseHolder(AddressResponseHolder addressResponseHolder) {
        this.addressResponseHolder = addressResponseHolder;
    }

    public static RegistrationSuccessResponseBuilder builder() {
        return new RegistrationSuccessResponseBuilder();
    }
}
