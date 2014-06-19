package com.picsauditing.employeeguard.controllers.restful;

import com.google.gson.GsonBuilder;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.services.EmployeeFileService;
import org.springframework.beans.factory.annotation.Autowired;

public class FilesAction extends PicsRestActionSupport {

	@Autowired
	private EmployeeFileService employeeFileService;

	public String index() {
		jsonString = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(
				employeeFileService.findEmployeeFiles(permissions.getAppUserID()));

		return JSON_STRING;
	}
}
