package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountGroupEmployee;
import com.picsauditing.employeeguard.entities.Profile;

import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

public class AccountGroupEmployeeDAO extends BaseEntityDAO<AccountGroupEmployee> {

    public AccountGroupEmployeeDAO() {
        this.type = AccountGroupEmployee.class;
    }

	public List<AccountGroupEmployee> findByProfile(final Profile profile) {
		if (profile == null) {
			return Collections.emptyList();
		}

		TypedQuery<AccountGroupEmployee> query = em.createQuery("FROM AccountGroupEmployee age WHERE age.employee.profile = :profile", AccountGroupEmployee.class);
		query.setParameter("profile", profile);
		return query.getResultList();
	}
}
