package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.Role;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

public class AccountSkillDAO extends AbstractBaseEntityDAO<AccountSkill> {

	public AccountSkillDAO() {
		this.type = AccountSkill.class;
	}

	public List<AccountSkill> findByAccount(int accountId) {
		TypedQuery<AccountSkill> query = em.createQuery("FROM AccountSkill s WHERE s.accountId = :accountId", AccountSkill.class);
		query.setParameter("accountId", accountId);
		return query.getResultList();
	}

	public List<AccountSkill> findOptionalSkillsByAccount(final int accountId) {
		if (accountId == 0) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkill> query = em.createQuery("FROM AccountSkill s WHERE s.accountId = :accountId AND s.ruleType = 'Optional'", AccountSkill.class);
		query.setParameter("accountId", accountId);
		return query.getResultList();
	}

	public List<AccountSkill> findOptionalSkillsByAccounts(final List<Integer> accountIds) {
		if (CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkill> query = em.createQuery("FROM AccountSkill s WHERE s.accountId IN (:accountIds) AND s.ruleType = 'Optional'", AccountSkill.class);
		query.setParameter("accountIds", accountIds);
		return query.getResultList();
	}

	public List<AccountSkill> findByAccounts(List<Integer> accountIds) {
		if (CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkill> query = em.createQuery("FROM AccountSkill s WHERE s.accountId IN ( :accountIds )", AccountSkill.class);
		query.setParameter("accountIds", accountIds);
		return query.getResultList();
	}

	public List<AccountSkill> findByProfile(final Profile profile) {
		if (profile == null) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkill> query = em.createQuery("SELECT DISTINCT ase.skill FROM AccountSkillEmployee ase WHERE ase.employee.profile = :profile", AccountSkill.class);
		query.setParameter("profile", profile);
		return query.getResultList();
	}

	public AccountSkill findSkillByAccount(int skillId, int accountId) {
		TypedQuery<AccountSkill> query = em.createQuery("FROM AccountSkill s WHERE s.accountId = :accountId AND s.id = :skillId", AccountSkill.class);
		query.setParameter("accountId", accountId);
		query.setParameter("skillId", skillId);
		return query.getSingleResult();
	}

	// TODO Deprecate, use accountID for security?
	public List<AccountSkill> findByIds(List<Integer> skillIds) {
		TypedQuery<AccountSkill> query = em.createQuery("FROM AccountSkill s WHERE s.id IN (:skillIds)", AccountSkill.class);
		query.setParameter("skillIds", skillIds);
		return query.getResultList();
	}

	public List<AccountSkill> findSkillsByAccountAndIds(final int accountId, final List<Integer> skillIds) {
		if (accountId <= 0 || CollectionUtils.isEmpty(skillIds)) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkill> query = em.createQuery("FROM AccountSkill s WHERE s.accountId = :accountId AND s.id IN (:skillIds)", AccountSkill.class);
		query.setParameter("skillIds", skillIds);
		query.setParameter("accountId", accountId);
		return query.getResultList();
	}

	public List<AccountSkill> findSkillsByAccountsAndIds(final List<Integer> accountIds, final List<Integer> skillIds) {
		if (CollectionUtils.isEmpty(accountIds) || CollectionUtils.isEmpty(skillIds)) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkill> query = em.createQuery("FROM AccountSkill s WHERE s.accountId IN (:accountIds) AND s.id IN (:skillIds)", AccountSkill.class);
		query.setParameter("skillIds", skillIds);
		query.setParameter("accountIds", accountIds);
		return query.getResultList();
	}

	public List<AccountSkill> findByGroups(final List<Group> groups) {
		if (CollectionUtils.isEmpty(groups)) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkill> query = em.createQuery("SELECT DISTINCT asg.skill FROM AccountSkillGroup asg WHERE asg.group IN ( :groups ) ", AccountSkill.class);
		query.setParameter("groups", groups);
		return query.getResultList();
	}

    public List<AccountSkill> findByRoles(final List<Role> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }

        TypedQuery<AccountSkill> query = em.createQuery("SELECT DISTINCT asr.skill FROM AccountSkillRole asr WHERE asr.group IN ( :roles ) ", AccountSkill.class);
        query.setParameter("roles", roles);
        return query.getResultList();
    }

	public List<AccountSkill> search(final String searchTerm, final int accountId) {
		TypedQuery<AccountSkill> query = em.createQuery("FROM AccountSkill s WHERE s.accountId = :accountId " +
				"AND (s.name LIKE :searchTerm " +
				"OR s.description LIKE :searchTerm)", AccountSkill.class);
		query.setParameter("accountId", accountId);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		return query.getResultList();
	}

	public void delete(int id, int accountId) {
		AccountSkill skill = findSkillByAccount(id, accountId);
		super.delete(skill);
	}

	public List<AccountSkill> findRequiredByAccount(final int accountId) {
		TypedQuery<AccountSkill> query = em.createQuery("FROM AccountSkill s WHERE s.accountId = :accountId AND s.ruleType = 'Required'", AccountSkill.class);
		query.setParameter("accountId", accountId);
		return query.getResultList();
	}

	public List<AccountSkill> findRequiredByAccounts(final List<Integer> accountIds) {
		TypedQuery<AccountSkill> query = em.createQuery("FROM AccountSkill s WHERE s.accountId IN (:accountIds) AND s.ruleType = 'Required'", AccountSkill.class);
		query.setParameter("accountIds", accountIds);
		return query.getResultList();
	}

	public List<AccountSkill> search(final String searchTerm, final List<Integer> accountIds) {
		TypedQuery<AccountSkill> query = em.createQuery("FROM AccountSkill s WHERE s.accountId IN (:accountIds) " +
				"AND (s.name LIKE :searchTerm " +
				"OR s.description LIKE :searchTerm)", AccountSkill.class);
		query.setParameter("accountIds", accountIds);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		return query.getResultList();
	}
}
