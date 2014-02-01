package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.*;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.AccountSkillEmployeeBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleEmployeeBuilder;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.contractor.GroupEmployeesForm;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
import com.picsauditing.util.Strings;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class RoleService {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountSkillDAO accountSkillDAO;
    @Autowired
    private AccountSkillEmployeeDAO accountSkillEmployeeDAO;
    @Autowired
    private AccountSkillEmployeeService accountSkillEmployeeService;
    @Autowired
    private EmployeeDAO employeeDAO;
    @Autowired
    private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;
    @Autowired
    private RoleDAO roleDAO;
    @Autowired
    private RoleEmployeeDAO roleEmployeeDAO;
    @Autowired
    private SiteSkillDAO siteSkillDAO;

    public Role getRole(final String id, final int accountId) {
        return roleDAO.findRoleByAccount(NumberUtils.toInt(id), accountId);
    }

    public List<Role> getRolesForAccount(final int accountId) {
        return getRolesForAccounts(Arrays.asList(accountId));
    }

    public List<Role> getRolesForAccounts(final List<Integer> accountIds) {
        return roleDAO.findByAccounts(accountIds);
    }

    public Role update(GroupNameSkillsForm groupNameSkillsForm, String id, int accountId, int appUserId) {
        Role roleInDatabase = getRole(id, accountId);
        roleInDatabase.setName(groupNameSkillsForm.getName());
        roleInDatabase = roleDAO.save(roleInDatabase);

        List<AccountSkillRole> newAccountSkillRoles = new ArrayList<>();
        if (ArrayUtils.isNotEmpty(groupNameSkillsForm.getSkills())) {
            List<AccountSkill> skills = accountSkillDAO.findByIds(Arrays.asList(ArrayUtils.toObject(groupNameSkillsForm.getSkills())));
            for (AccountSkill accountSkill : skills) {
                newAccountSkillRoles.add(new AccountSkillRole(roleInDatabase, accountSkill));
            }
        }

        Date timestamp = new Date();
        List<AccountSkillRole> accountSkillGroups = IntersectionAndComplementProcess.intersection(
                newAccountSkillRoles,
                roleInDatabase.getSkills(),
                AccountSkillRole.COMPARATOR,
                new BaseEntityCallback(appUserId, timestamp));

        roleInDatabase.setSkills(accountSkillGroups);
        roleInDatabase = roleDAO.save(roleInDatabase);

        for (RoleEmployee roleEmployee : roleInDatabase.getEmployees()) {
            accountSkillEmployeeService.linkEmployeeToSkills(roleEmployee.getEmployee(), appUserId, timestamp);
        }

        return roleInDatabase;
    }

    public List<Role> search(final String searchTerm, final int accountId) {
        if (Strings.isEmpty(searchTerm) || accountId == 0) {
            return Collections.emptyList();
        }

        return roleDAO.search(searchTerm, accountId);
    }

    public List<Role> search(final String searchTerm, final List<Integer> accountIds) {
        if (Strings.isEmpty(searchTerm) || CollectionUtils.isEmpty(accountIds)) {
            return Collections.emptyList();
        }

        return roleDAO.search(searchTerm, accountIds);
    }

    public Role save(Role role, final int accountId, final int appUserId) {
        role.setAccountId(accountId);

        Date createdDate = new Date();
        EntityHelper.setCreateAuditFields(role, appUserId, createdDate);
        EntityHelper.setCreateAuditFields(role.getSkills(), appUserId, createdDate);
        EntityHelper.setCreateAuditFields(role.getEmployees(), appUserId, createdDate);

        role = roleDAO.save(role);
        accountSkillEmployeeService.linkEmployeesToSkill(role, appUserId);
        return role;
    }

    public Role update(GroupEmployeesForm roleEmployeesForm, String id, int accountId, int userId) {
        return null;
    }

    public Role getRole(final String id) {
        return roleDAO.find(NumberUtils.toInt(id));
    }

    public Map<Employee, Set<AccountSkill>> getEmployeeSkillsForSite(final int siteId, final int contractorId) {
        List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
        // find roles by this site that duplicate roles with the above corporate ids
        Map<Role, Role> siteRoleToCorporateRole = roleDAO.findSiteToCorporateRoles(corporateIds, siteId);

        List<Employee> employees = employeeDAO.findByAccount(contractorId);

        Map<Employee, Set<AccountSkill>> employeeSkills = new HashMap<>();
        for (Employee employee : employees) {
            addSkillsFromSiteRoles(siteId, employeeSkills, employee, siteRoleToCorporateRole);
            addSkillsFromSiteProjectRoles(siteId, employeeSkills, employee);
        }

        addSiteRequiredSkills(siteId, corporateIds, employeeSkills);

        return employeeSkills;
    }

    private void addSkillsFromSiteRoles(int siteId, Map<Employee, Set<AccountSkill>> employeeSkills, Employee employee, Map<Role, Role> siteRoleToCorporateRole) {
        for (RoleEmployee roleEmployee : employee.getRoles()) {
            if (roleEmployee.getRole().getAccountId() == siteId) {
                Role corporateRole = siteRoleToCorporateRole.get(roleEmployee.getRole());
                addRoleSkills(employeeSkills, employee, corporateRole.getSkills());
            }
        }
    }

    private void addSkillsFromSiteProjectRoles(int siteId, Map<Employee, Set<AccountSkill>> employeeSkills, Employee employee) {
        for (ProjectRoleEmployee projectRoleEmployee : employee.getProjectRoles()) {
            if (projectRoleEmployee.getProjectRole().getProject().getAccountId() == siteId) {
                addRoleSkills(employeeSkills, employee, projectRoleEmployee.getProjectRole().getRole().getSkills());
            }
        }
    }

    private void addRoleSkills(Map<Employee, Set<AccountSkill>> employeeSkills, Employee employee, List<AccountSkillRole> roleSkills) {
        for (AccountSkillRole accountSkillRole : roleSkills) {
            Utilities.addToMapOfKeyToSet(employeeSkills, employee, accountSkillRole.getSkill());
        }
    }

    private void addSiteRequiredSkills(int siteId, List<Integer> corporateIds, Map<Employee, Set<AccountSkill>> employeeSkills) {
        List<SiteSkill> siteSkills = siteSkillDAO.findByAccountId(siteId);
        List<SiteSkill> corporateSkills = siteSkillDAO.findByAccountIds(corporateIds);

        for (Map.Entry<Employee, Set<AccountSkill>> employeeSkillSet : employeeSkills.entrySet()) {
            for (SiteSkill siteSkill : siteSkills) {
                Utilities.addToMapOfKeyToSet(employeeSkills, employeeSkillSet.getKey(), siteSkill.getSkill());
            }

            for (SiteSkill corporateSkill : corporateSkills) {
                Utilities.addToMapOfKeyToSet(employeeSkills, employeeSkillSet.getKey(), corporateSkill.getSkill());
            }
        }
    }

    public Map<Role, Set<Employee>> getRoleAssignments(final int contractorId, final int siteId) {
        List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
        Map<Role, Role> siteToCorporateRoles = roleDAO.findSiteToCorporateRoles(corporateIds, siteId);

        List<RoleEmployee> roleEmployees = roleEmployeeDAO.findByContractorAndSiteId(contractorId, siteId);
        List<ProjectRoleEmployee> projectRoleEmployees = projectRoleEmployeeDAO.findByCorporateAndContractor(corporateIds, contractorId);

        Map<Role, Set<Employee>> roleAssignments = new HashMap<>();
        for (RoleEmployee roleEmployee : roleEmployees) {
            Role corporateRole = siteToCorporateRoles.get(roleEmployee.getRole());

            Utilities.addToMapOfKeyToSet(roleAssignments, corporateRole, roleEmployee.getEmployee());
        }

        for (ProjectRoleEmployee projectRoleEmployee : projectRoleEmployees) {
            Role role = projectRoleEmployee.getProjectRole().getRole();
            Utilities.addToMapOfKeyToSet(roleAssignments, role, projectRoleEmployee.getEmployee());
        }

        return roleAssignments;
    }

    public Map<Employee, Role> getEmployeeSiteRolesForRole(final int contractorId, final int siteId, final int corporateRoleId) {
        List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
        Role siteRole = roleDAO.findSiteRoleByCorporateRole(corporateIds, siteId, corporateRoleId);

        return Utilities.convertToMap(roleEmployeeDAO.findSiteRolesByContractorAndRoleId(contractorId, siteRole),
                new Utilities.EntityKeyValueConvertable<RoleEmployee, Employee, Role>() {
                    @Override
                    public Employee getKey(RoleEmployee entity) {
                        return entity.getEmployee();
                    }

                    @Override
                    public Role getValue(RoleEmployee entity) {
                        return entity.getRole();
                    }
                });
    }

    public Map<Role, Role> getSiteToCorporateRoles(int siteId) {
        List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);

        return roleDAO.findSiteToCorporateRoles(corporateIds, siteId);
    }

    public void removeSiteRolesFromEmployee(final int employeeId, final int siteId) {
        // FIXME
    }

    public void assignEmployeeToSite(final int siteId, final int roleId, final Employee employee, final int userId) {
        assignEmployeeToRole(siteId, roleId, employee, userId);
        updateEmployeeSkillsForRole(roleId, employee, userId);
    }

    private void assignEmployeeToRole(final int siteId, final int roleId, final Employee employee, final int userId) {
        List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
        Map<Role, Role> siteToCorporateRoleMap = roleDAO.findSiteToCorporateRoles(corporateIds, siteId);
        List<RoleEmployee> roleEmployees = buildRoleEmployees(siteToCorporateRoleMap, employee, userId);
        roleEmployeeDAO.save(roleEmployees);
    }

    private void updateEmployeeSkillsForRole(final int roleId, final Employee employee, final int userId) {
        Set<AccountSkill> allSkillsForJobRole = null;
        allSkillsForJobRole.addAll(null); // add in the Site required skills
        allSkillsForJobRole.addAll(null); // add in the Corporate required skills

        List<AccountSkill> employeeSkills = null;
        List<AccountSkillEmployee> accountSkillEmployees = buildAccountSkillEmployee(employee, allSkillsForJobRole,
                employeeSkills, userId);
        accountSkillEmployeeService.save(accountSkillEmployees);
    }

    private List<RoleEmployee> buildRoleEmployees(final Map<Role, Role> siteToCorporateRoleMap,
                                                  final Employee employee, final int userId) {
        if (MapUtils.isEmpty(siteToCorporateRoleMap)) {
            return Collections.emptyList();
        }

        Date createdDate = DateBean.today();
        List<RoleEmployee> roleEmployees = new ArrayList<>();
        for (Role role : siteToCorporateRoleMap.keySet()) {
            roleEmployees.add(new RoleEmployeeBuilder()
                    .role(role)
                    .employee(employee)
                    .createdBy(userId)
                    .createdDate(createdDate)
                    .build());
        }

        return roleEmployees;
    }

    private List<AccountSkillEmployee> buildAccountSkillEmployee(final Employee employee,
                                                                 final Set<AccountSkill> allSkillsForJobRole,
                                                                 final List<AccountSkill> employeeSkills,
                                                                 final int userId) {
        if (CollectionUtils.isEmpty(allSkillsForJobRole)) {
            return Collections.emptyList();
        }

        Date createdDate = DateBean.today();
        List<AccountSkillEmployee> accountSkillEmployees = new ArrayList<>();
        for (AccountSkill accountSkill : allSkillsForJobRole) {
            if (!employeeSkills.contains(accountSkill)) {
                accountSkillEmployees.add(new AccountSkillEmployeeBuilder()
                        .accountSkill(accountSkill)
                        .employee(employee)
                        .createdBy(userId)
                        .createdDate(createdDate)
                        .build());
            }
        }

        return accountSkillEmployees;
    }

    public void removeSiteSpecificRolesFromEmployee(final int employeeId, final int siteId) {
        deleteProjectRolesFromEmployee(employeeId, siteId);
        deleteSiteRolesFromEmployee(employeeId, siteId);

        // See if the site skills are being used elsewhere, prune the ones that aren't
        // If another site is tied to projects / roles that is under the same corporate, leave the corporate skills alone
        List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
        List<Integer> otherSiteIds = getOtherSiteIds(corporateIds, siteId);
        // Simplest case: this operator has no siblings -- delete all skills related to this site/corp
        if (CollectionUtils.isEmpty(otherSiteIds)) {
            List<AccountSkillEmployee> accountSkillEmployees =
                    accountSkillEmployeeDAO.findByEmployeeAndCorporateIds(employeeId, corporateIds);
            accountSkillEmployeeDAO.delete(accountSkillEmployees);
        } else {
            // Find all the skills that are not in use by any other operator and delete those
            // FIXME
        }
    }

    private void deleteProjectRolesFromEmployee(int employeeId, int siteId) {
        List<ProjectRoleEmployee> projectRoleEmployees = projectRoleEmployeeDAO.findByEmployeeAndSiteId(employeeId, siteId);
        projectRoleEmployeeDAO.delete(projectRoleEmployees);
    }

    private void deleteSiteRolesFromEmployee(int employeeId, int siteId) {
        List<RoleEmployee> roleEmployees = roleEmployeeDAO.findByEmployeeAndSiteId(employeeId, siteId);
        roleEmployeeDAO.delete(roleEmployees);
    }

    private List<Integer> getOtherSiteIds(List<Integer> corporateIds, int siteId) {
        List<Integer> otherSiteIds = accountService.getChildOperatorIds(corporateIds);
        Iterator<Integer> siteIdIterator = otherSiteIds.iterator();

        while (siteIdIterator.hasNext()) {
            int otherSiteId = siteIdIterator.next();
            if (siteId == otherSiteId) {
                siteIdIterator.remove();
            }
        }

        return otherSiteIds;
    }
}
