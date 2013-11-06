package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountGroup;
import com.picsauditing.employeeguard.entities.AccountSkillGroup;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

public class AccountSkillGroupDAO extends BaseEntityDAO<AccountSkillGroup> {
	public AccountSkillGroupDAO() {
		this.type = AccountSkillGroup.class;
	}

	public List<AccountSkillGroup> findByGroups(final List<AccountGroup> groups) {
		if (CollectionUtils.isEmpty(groups)) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkillGroup> query = em.createQuery("FROM AccountSkillGroup asg WHERE asg.group IN :groups", AccountSkillGroup.class);
		query.setParameter("groups", groups);

		return query.getResultList();
	}
}
