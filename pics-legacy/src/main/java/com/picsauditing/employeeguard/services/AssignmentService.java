package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.AccountSkillRoleDAO;
import com.picsauditing.employeeguard.daos.ProjectRoleEmployeeDAO;
import com.picsauditing.employeeguard.daos.RoleEmployeeDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.forms.operator.OperatorEmployeeProjectAssignment;
import com.picsauditing.employeeguard.forms.operator.OperatorProjectAssignmentMatrix;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.Extractor;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.employeeguard.util.ListUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class AssignmentService {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountSkillEmployeeService accountSkillEmployeeService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private FormBuilderFactory formBuilderFactory;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRoleService projectRoleService;
    @Autowired
    private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;
    @Autowired
    private RoleEmployeeDAO roleEmployeeDAO;
    @Autowired
    private SkillUsageLocator skillUsageLocator;
    @Autowired
    private AccountSkillRoleDAO accountSkillRoleDAO;

    public OperatorProjectAssignmentMatrix buildOperatorProjectAssignmentMatrix(final Project project, final int assignmentId, final int roleId) {
        List<AccountModel> contractors = accountService.getContractors(assignmentId);

        List<Employee> employees;
        if (roleId > 0) {
            employees = projectRoleEmployeeDAO.findByProjectAndRoleId(project, roleId);
        } else {
            employees = projectRoleEmployeeDAO.findByProject(project);
        }

        List<AccountSkill> accountSkills = projectService.getRequiredSkills(project);
        extractSkillsFromProjectRoles(project, accountSkills, roleId);
        List<AccountSkillEmployee> accountSkillEmployees = accountSkillEmployeeService.findByEmployeesAndSkills(employees, accountSkills);

        List<Role> jobRoles = ExtractorUtil.extractList(project.getRoles(), ProjectRole.ROLE_EXTRACTOR);
        jobRoles = ListUtil.removeDuplicatesAndSort(jobRoles);
        List<RoleInfo> roleInfos = formBuilderFactory.getRoleInfoFactory().build(jobRoles);

        Map<Integer, AccountModel> accountModels = getIdToAccountModel(contractors);
        List<OperatorEmployeeProjectAssignment> operatorEmployeeProjectAssignments =
                formBuilderFactory.getOperatorEmployeeProjectAssignmentFactory().buildList(employees, accountSkillEmployees, accountSkills, accountModels, jobRoles);
        if (roleId == 0) {
            filterDownToAssignment(operatorEmployeeProjectAssignments);
        }

        Collections.sort(operatorEmployeeProjectAssignments);

        return buildAssignmentMatrix(accountSkills, roleInfos, operatorEmployeeProjectAssignments);
    }

    private void filterDownToAssignment(List<OperatorEmployeeProjectAssignment> operatorEmployeeProjectAssignments) {
        for (OperatorEmployeeProjectAssignment operatorEmployeeProjectAssignment : operatorEmployeeProjectAssignments) {
            SkillStatus worstStatus = getWorstStatus(operatorEmployeeProjectAssignment.getSkillStatuses());

            if (worstStatus == null) {
                operatorEmployeeProjectAssignment.setSkillStatuses(Collections.<SkillStatus>emptyList());
            } else {
                operatorEmployeeProjectAssignment.setSkillStatuses(Arrays.asList(worstStatus));
            }
        }
    }

    private SkillStatus getWorstStatus(List<SkillStatus> skillStatuses) {
        if (CollectionUtils.isEmpty(skillStatuses)) {
            return null;
        }

        SkillStatus worst = SkillStatus.Complete;
        for (SkillStatus skillStatus : skillStatuses) {
            if (skillStatus.compareTo(worst) < 0) {
                worst = skillStatus;
            }
        }

        return worst;
    }

    private void extractSkillsFromProjectRoles(final Project project, final List<AccountSkill> accountSkills, final int roleId) {
        for (ProjectRole jobRole : project.getRoles()) {
            if (jobRole.getRole().getId() == roleId) {
                for (AccountSkillRole accountSkillRole : jobRole.getRole().getSkills()) {
                    accountSkills.add(accountSkillRole.getSkill());
                }
            }
        }
    }

    private List<Integer> getContractorIds(List<AccountModel> contractors) {
        return ExtractorUtil.extractList(contractors, new Extractor<AccountModel, Integer>() {
            @Override
            public Integer extract(AccountModel accountModel) {
                return accountModel.getId();
            }
        });
    }

    private Map<Integer, AccountModel> getIdToAccountModel(List<AccountModel> contractors) {
        Map<Integer, AccountModel> accountModels = new HashMap<>();
        for (AccountModel accountModel : contractors) {
            accountModels.put(accountModel.getId(), accountModel);
        }
        return accountModels;
    }

    private OperatorProjectAssignmentMatrix buildAssignmentMatrix(List<AccountSkill> accountSkills, List<RoleInfo> roleInfos, List<OperatorEmployeeProjectAssignment> operatorEmployeeProjectAssignments) {
        OperatorProjectAssignmentMatrix operatorProjectAssignmentMatrix = new OperatorProjectAssignmentMatrix();
        operatorProjectAssignmentMatrix.setAssignments(operatorEmployeeProjectAssignments);
        operatorProjectAssignmentMatrix.setRoles(roleInfos);
        operatorProjectAssignmentMatrix.setSkillNames(extractNamesFromSkills(accountSkills));
//		operatorProjectAssignmentMatrix.setEmployeeRoles(operatorProjectService.sumEmployeeRolesForProject(permissions.getAccountId(), project));
        return operatorProjectAssignmentMatrix;
    }

    private List<String> extractNamesFromSkills(final List<AccountSkill> accountSkills) {
        if (CollectionUtils.isEmpty(accountSkills)) {
            return Collections.emptyList();
        }

        List<String> skillNames = new ArrayList<>();
        for (AccountSkill accountSkill : accountSkills) {
            skillNames.add(accountSkill.getName());
        }

        return ListUtil.removeDuplicatesAndSort(skillNames);
    }

    public Map<Role, Set<Employee>> getAssignmentsForContractorEmployees(final int accountId) {
        return Utilities.convertToMapOfSets(roleEmployeeDAO.findContractorEmployeeSiteAssignment(accountId),
                new Utilities.EntityKeyValueConvertable<RoleEmployee, Role, Employee>() {

                    @Override
                    public Role getKey(RoleEmployee roleEmployee) {
                        return roleEmployee.getRole();
                    }

                    @Override
                    public Employee getValue(RoleEmployee roleEmployee) {
                        return roleEmployee.getEmployee();
                    }
                });
    }

    public Map<Employee, Set<Role>> getContractorEmployeeAssignments(final int accountId) {
        return Utilities.convertToMapOfSets(roleEmployeeDAO.findContractorEmployeeSiteAssignment(accountId),
                new Utilities.EntityKeyValueConvertable<RoleEmployee, Employee, Role>() {

                    @Override
                    public Employee getKey(RoleEmployee roleEmployee) {
                        return roleEmployee.getEmployee();
                    }

                    @Override
                    public Role getValue(RoleEmployee roleEmployee) {
                        return roleEmployee.getRole();
                    }
                });
    }

    public Map<Employee, Set<AccountSkill>> getAllContractorEmployeeAssignmentSkillsForSite(final int contractorId, int siteId) {
        List<SkillUsage> skillUsages = getEmployeeSkillsForSite(siteId, null);

        Map<Employee, Set<AccountSkill>> skillMap = new HashMap<>();
        for (SkillUsage skillUsage : skillUsages) {
            HashSet<AccountSkill> skills = new HashSet<>();
            skills.addAll(skillUsage.getCorporateRequiredSkills().keySet());
            skills.addAll(skillUsage.getSiteRequiredSkills().keySet());
            skills.addAll(skillUsage.getSiteRequiredSkills().keySet());

            skillMap.put(skillUsage.getEmployee(), skills);
        }

        return skillMap;
    }

    private List<SkillUsage> getEmployeeSkillsForSite(final int siteId, final List<Employee> employees) {
        return null;
    }

    public Map<Employee, Set<AccountSkill>> getContractorEmployeeAssignmentSkillsForRole(final int contractorId, final Role role) {
        Map<Employee, Set<Role>> employeeRoles =

        Map<Role, Set<AccountSkill>> roleSkills = getRoleSkills(role);

        return null;
    }

    private Map<Role, Set<AccountSkill>> getRoleSkills(final Role role) {
          return  Utilities.convertToMapOfSets(accountSkillRoleDAO.findSkillsByRole(role),
                new Utilities.EntityKeyValueConvertable<AccountSkillRole, Role, AccountSkill>() {

                    @Override
                    public Role getKey(AccountSkillRole accountSkillRole) {
                        return accountSkillRole.getRole();
                    }

                    @Override
                    public AccountSkill getValue(AccountSkillRole accountSkillRole) {
                        return accountSkillRole.getSkill();
                    }
                });
    }
}
