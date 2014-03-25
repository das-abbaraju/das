package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountSkillGroup;
import com.picsauditing.employeeguard.entities.Group;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AccountSkillGroupDAO extends AbstractBaseEntityDAO<AccountSkillGroup> {
	public AccountSkillGroupDAO() {
		this.type = AccountSkillGroup.class;
	}

	public List<AccountSkillGroup> findByGroups(final Collection<Group> groups) {
		if (CollectionUtils.isEmpty(groups)) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkillGroup> query = em.createQuery("FROM AccountSkillGroup asg WHERE asg.group IN :groups", AccountSkillGroup.class);
		query.setParameter("groups", groups);

		return query.getResultList();
	}

	public List<AccountSkillGroup> findByContractorId(final int contractorId) {
		TypedQuery<AccountSkillGroup> query = em.createQuery("FROM AccountSkillGroup asg " +
				"WHERE asg.group.accountId = :contractorId", AccountSkillGroup.class);
		query.setParameter("contractorId", contractorId);

		return query.getResultList();
	}
}
