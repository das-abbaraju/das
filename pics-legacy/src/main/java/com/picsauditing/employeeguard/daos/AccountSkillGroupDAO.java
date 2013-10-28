package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountSkillGroup;

import javax.persistence.TypedQuery;
import java.util.List;

public class AccountSkillGroupDAO extends BaseEntityDAO<AccountSkillGroup> {
	public AccountSkillGroupDAO() {
		this.type = AccountSkillGroup.class;
	}

	public List<AccountSkillGroup> findBySkill(int skillId) {
		TypedQuery<AccountSkillGroup> query = em.createQuery("FROM AccountSkillGroup asg WHERE asg.skill.id = :skillId", AccountSkillGroup.class);


		return query.getResultList();
	}
}
