package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountSkillRole;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AccountSkillRoleDAO extends AbstractBaseEntityDAO<AccountSkillRole> {

    public AccountSkillRoleDAO() {
        this.type = AccountSkillRole.class;
    }

	public List<AccountSkillRole> findSkillsByRoles(final Collection<Role> roles) {
		TypedQuery query = em.createQuery("SELECT asr FROM AccountSkillRole asr " +
				"JOIN asr.role AS r " +
				"WHERE r IN (:roles)", AccountSkillRole.class);
		query.setParameter("roles", roles);
		return query.getResultList();
	}

	public List<AccountSkillRole> findProjectRoleSkillsByEmployee(final Employee employee) {
		if (employee == null) {
			return Collections.emptyList();
		}

		// Employee to project roles to roles to account skill roles
		TypedQuery<AccountSkillRole> query = em.createQuery("SELECT asr FROM Employee e " +
				"JOIN e.projectRoles pre " +
				"JOIN pre.projectRole pr " +
				"JOIN pr.role r " +
				"JOIN r.skills asr " +
				"WHERE e = :employee", AccountSkillRole.class);

		query.setParameter("employee", employee);

		return query.getResultList();
	}

}
