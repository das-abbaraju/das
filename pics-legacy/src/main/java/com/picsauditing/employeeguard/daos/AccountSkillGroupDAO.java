package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillGroup;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.Profile;

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

	public Collection<AccountSkillGroup> findByProfile(final Profile profile) {
		TypedQuery<AccountSkillGroup> query = em.createQuery("SELECT asg FROM AccountSkillGroup asg " +
				"JOIN asg.group g " +
				"JOIN g.employees ge " +
				"JOIN ge.employee e " +
				"JOIN e.profile p " +
				"WHERE p = :profile", AccountSkillGroup.class);

		query.setParameter("profile", profile);

		return query.getResultList();
	}
}
