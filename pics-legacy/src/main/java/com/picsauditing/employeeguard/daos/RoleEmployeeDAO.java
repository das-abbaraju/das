package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.RoleEmployee;

import javax.persistence.TypedQuery;
import java.util.List;

public class RoleEmployeeDAO extends AbstractBaseEntityDAO<RoleEmployee> {

    public RoleEmployeeDAO() {
        this.type = RoleEmployee.class;
    }

    public RoleEmployee findByGroupAndEmployee(final Employee employee, final Role role) {
        if (employee == null || role == null) {
            return null;
        }

        TypedQuery<RoleEmployee> query = em.createQuery("FROM RoleEmployee re " +
                "WHERE re.employee = :employee " +
                "AND re.role = :role", RoleEmployee.class);
        query.setParameter("employee", employee);
        query.setParameter("role", role);
        return query.getSingleResult();
    }

    public List<RoleEmployee> findContractorEmployeeSiteAssignment(final int accountId) {
        TypedQuery<RoleEmployee> query = em.createQuery("SELECT re FROM RoleEmployee re " +
                "JOIN re.employee as e " +
                "WHERE e.accountId = :accountId", RoleEmployee.class);
        query.setParameter("accountId", accountId);
        return query.getResultList();
    }
}
