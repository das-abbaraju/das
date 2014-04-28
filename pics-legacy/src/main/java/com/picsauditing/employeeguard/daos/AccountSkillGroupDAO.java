package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountSkillGroup;
import com.picsauditing.employeeguard.entities.Group;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;

public class AccountSkillGroupDAO extends AbstractBaseEntityDAO<AccountSkillGroup> {

	public AccountSkillGroupDAO() {
		this.type = AccountSkillGroup.class;
	}

	public List<AccountSkillGroup> findByGroups(final Collection<Group> groups) {
		TypedQuery<AccountSkillGroup> query = em.createQuery("FROM AccountSkillGroup asg " +
				"WHERE asg.group IN :groups", AccountSkillGroup.class);

		query.setParameter("groups", groups);

		return query.getResultList();
	}
}
