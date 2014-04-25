package com.picsauditing.employeeguard.controllers.operator;

import com.google.gson.Gson;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.models.factories.LiveIDEmployeeModelFactory;
import com.picsauditing.employeeguard.models.factories.OperatorEmployeeModelFactory;
import com.picsauditing.employeeguard.services.LiveIDEmployeeService;
import com.picsauditing.employeeguard.services.OperatorEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;

public class EmployeeAction extends PicsRestActionSupport {

    @Autowired
    private OperatorEmployeeService operatorEmployeeModelService;
    @Autowired
    private LiveIDEmployeeService liveIDEmployeeService;

    private int siteId;

    public String show() {
        return convertToJson(buildLiveIDEmployeeModel(permissions.getAccountId()));
    }

    public String corporateEmployeeLiveId() {
        return convertToJson(buildLiveIDEmployeeModel(siteId));
    }

    private LiveIDEmployeeModelFactory.LiveIDEmployeeModel buildLiveIDEmployeeModel(final int siteId) {
        return liveIDEmployeeService.buildLiveIDEmployee(id, siteId);
    }

    public String employeeData() {
        return convertToJson(buildOperatorEmployeeModel(permissions.getAccountId()));
    }

    public String corporateEmployeeData() {
        return convertToJson(buildOperatorEmployeeModel(siteId));
    }

    private OperatorEmployeeModelFactory.OperatorEmployeeModel buildOperatorEmployeeModel(final int siteId) {
        return operatorEmployeeModelService.buildModel(permissions.getAccountId(), getIdAsInt());
    }

    private String convertToJson(final Object model) {
        jsonString = new Gson().toJson(model);

        return JSON_STRING;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }
}
