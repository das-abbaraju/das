
package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.process.EmployeeSiteStatusProcess;
import com.picsauditing.employeeguard.process.EmployeeSiteStatusResult;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LiveIDEmployeeModelFactory {

  private Logger logger = LoggerFactory.getLogger(getClass());

  public LiveIDEmployeeModelFactory() {

  }

  public LiveIDEmployeeModel prepareLiveIDEmployeeModel(RequiredSkills required, List<ProjectStatusModel> projects, List<RoleStatusModel> roles) {

    return new LiveIDEmployeeModel(required,projects,roles);
  }

  public class LiveIDEmployeeModel {

    private LiveIDEmployeeModel(){

    }

    private LiveIDEmployeeModel(RequiredSkills required, List<ProjectStatusModel> projects, List<RoleStatusModel> roles) {
      this.required = required;
      this.projects = projects;
      this.roles = roles;
    }

    private RequiredSkills required;
    private List<ProjectStatusModel> projects;
    private List<RoleStatusModel> roles;

    public RequiredSkills getRequired() {
      return required;
    }

    public void setRequired(RequiredSkills required) {
      this.required = required;
    }

    public List<ProjectStatusModel> getProjects() {
      return projects;
    }

    public void setProjects(List<ProjectStatusModel> projects) {
      this.projects = projects;
    }

    public List<RoleStatusModel> getRoles() {
      return roles;
    }

    public void setRoles(List<RoleStatusModel> roles) {
      this.roles = roles;
    }
  }

}//--  LiveIDEmployeeModelFactory

