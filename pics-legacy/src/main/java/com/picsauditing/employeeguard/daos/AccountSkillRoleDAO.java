package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountSkillRole;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;

import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

public class AccountSkillRoleDAO extends AbstractBaseEntityDAO<AccountSkillRole> {

    public AccountSkillRoleDAO() {
        this.type = AccountSkillRole.class;
    }

    public List<AccountSkillRole> findSkillsByRole(final Role role) {
        TypedQuery query = em.createQuery("SELECT asr FROM AccountSkillRole asr " +
                "JOIN asr.role AS r " +
                "WHERE r = :role", AccountSkillRole.class);
        query.setParameter("role", role);
        return query.getResultList();
    }

	public List<AccountSkillRole> findByEmployee(final Employee employee) {
		if (employee == null) {
			return Collections.emptyList();
		}

		// Employee to project roles to roles to account skill roles
		TypedQuery<AccountSkillRole> query = em.createQuery("SELECT asr FROM AccountSkillRole asr " +
				"JOIN asr.role r " +
				"JOIN r.employees re " +
				"WHERE re.employee = :employee", AccountSkillRole.class);

		query.setParameter("employee", employee);

		return query.getResultList();
	}
}
