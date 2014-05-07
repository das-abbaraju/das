package com.picsauditing.employeeguard.controllers.employee;

import com.google.gson.Gson;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.models.factories.EmployeeSkillsModelFactory;
import com.picsauditing.employeeguard.services.EmployeeSkillsModelService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import org.springframework.beans.factory.annotation.Autowired;

public class ProfileSkillsAction extends PicsRestActionSupport {

	@Autowired
	private EmployeeSkillsModelService employeeSkillsModelService;
	@Autowired
	private ProfileEntityService profileEntityService;

	public String skills() {
//		Profile profile = profileEntityService.findByAppUserId(permissions.getAppUserID());
//		EmployeeSkillsModelFactory.EmployeeSkillsModel employeeSkillsModel =
//				employeeSkillsModelService.buildEmployeeSkillsModel(profile);
//
//		jsonString = new Gson().toJson(employeeSkillsModel);

		jsonString = fakeJson();

		return JSON_STRING;
	}

	private String fakeJson() {
		return "{\n" +
				"  \"status\": \"Expired\",\n" +
				"  \"sites\": [\n" +
				"    {\n" +
				"      \"id\": 2,\n" +
				"      \"name\": \"BASF Houston Texas\",\n" +
				"      \"status\": \"Expired\",\n" +
				"      \"projects\": [\n" +
				"        {\n" +
				"          \"id\": 3,\n" +
				"          \"name\": \"Dynamic Reporting\",\n" +
				"          \"status\": \"Expired\",\n" +
				"          \"skills\": [\n" +
				"            {\n" +
				"              \"id\": 210,\n" +
				"              \"name\": \"Dynamic Reporting Skill\",\n" +
				"              \"status\": \"Expiring\"\n" +
				"            },\n" +
				"            {\n" +
				"              \"id\": 5,\n" +
				"              \"name\": \"Dynamic Reporting Skill 2\",\n" +
				"              \"status\": \"Completed\"\n" +
				"            }\n" +
				"          ]\n" +
				"        },\n" +
				"        {\n" +
				"          \"id\": 3,\n" +
				"          \"name\": \"Ninja Dojo\",\n" +
				"          \"status\": \"Expiring\",\n" +
				"          \"skills\": [\n" +
				"            {\n" +
				"              \"id\": 4,\n" +
				"              \"name\": \"Ninja Dojo Skill 4\",\n" +
				"              \"status\": \"Expired\"\n" +
				"            },\n" +
				"            {\n" +
				"              \"id\": 5,\n" +
				"              \"name\": \"Ninja Dojo Skill 5\",\n" +
				"              \"status\": \"Completed\"\n" +
				"            }\n" +
				"          ]\n" +
				"        }\n" +
				"      ],\n" +
				"      \"required\": {\n" +
				"        \"skills\": [\n" +
				"          {\n" +
				"            \"id\": 45,\n" +
				"            \"name\": \"BASF Site Skill 1\",\n" +
				"            \"status\": \"Expired\"\n" +
				"          },\n" +
				"          {\n" +
				"            \"id\": 10,\n" +
				"            \"name\": \"BASF Corp Skill 1\",\n" +
				"            \"status\": \"Expiring\"\n" +
				"          }\n" +
				"        ]\n" +
				"      }\n" +
				"    },\n" +
				"    {\n" +
				"      \"id\": 6,\n" +
				"      \"name\": \"PICS\",\n" +
				"      \"status\": \"Expiring\",\n" +
				"      \"required\": {\n" +
				"        \"skills\": [\n" +
				"          {\n" +
				"            \"id\": 4,\n" +
				"            \"name\": \"PICS Site skill\",\n" +
				"            \"status\": \"Expired\"\n" +
				"          },\n" +
				"          {\n" +
				"            \"id\": 25,\n" +
				"            \"name\": \"PICS Site skill 2\",\n" +
				"            \"status\": \"Expiring\"\n" +
				"          }\n" +
				"        ]\n" +
				"      }\n" +
				"    },\n" +
				"    {\n" +
				"      \"id\": 8,\n" +
				"      \"name\": \"Spectre\",\n" +
				"      \"status\": \"Completed\",\n" +
				"      \"projects\": [\n" +
				"        {\n" +
				"          \"id\": 32,\n" +
				"          \"name\": \"Volcano Base\",\n" +
				"          \"status\": \"Completed\",\n" +
				"          \"skills\": [\n" +
				"            {\n" +
				"              \"id\": 4,\n" +
				"              \"name\": \"Volcano Base Skill\",\n" +
				"              \"status\": \"Expired\"\n" +
				"            }\n" +
				"          ]\n" +
				"        }\n" +
				"      ]\n" +
				"    }\n" +
				"  ]\n" +
				"}";
	}

}
