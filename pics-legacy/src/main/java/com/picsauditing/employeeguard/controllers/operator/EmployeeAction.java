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

	public String show() {
		LiveIDEmployeeModelFactory.LiveIDEmployeeModel liveIDEmployeeModel = buildLiveIDEmployeeModel();

		jsonString = new Gson().toJson(liveIDEmployeeModel);

		return JSON_STRING;
	}

	private LiveIDEmployeeModelFactory.LiveIDEmployeeModel buildLiveIDEmployeeModel() {
		int siteId = permissions.getAccountId();
		return liveIDEmployeeService.buildLiveIDEmployee(id, siteId);
	}

	public String employeeData() {
		OperatorEmployeeModelFactory.OperatorEmployeeModel operatorEmployeeModel = operatorEmployeeModelService
				.buildModel(permissions.getAccountId(), getIdAsInt());

		jsonString = new Gson().toJson(operatorEmployeeModel);

		return JSON_STRING;
	}

}
