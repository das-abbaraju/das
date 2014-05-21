package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.*;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.TypedQuery;
import java.util.*;

public class AccountSkillDAO extends AbstractBaseEntityDAO<AccountSkill> {

	public AccountSkillDAO() {
		this.type = AccountSkill.class;
	}

	public List<AccountSkill> findByAccount(final int accountId) {
		TypedQuery<AccountSkill> query = em.createQuery("select s FROM AccountSkill s " +
				"WHERE s.accountId = :accountId", AccountSkill.class);

		query.setParameter("accountId", accountId);

		return query.getResultList();
	}

	public List<AccountSkill> findOptionalSkillsByAccount(final int accountId) {
		TypedQuery<AccountSkill> query = em.createQuery("select s FROM AccountSkill s " +
				"WHERE s.accountId = :accountId " +
				"AND s.ruleType = 'Optional'", AccountSkill.class);

		query.setParameter("accountId", accountId);

		return query.getResultList();
	}

	public List<AccountSkill> findOptionalSkillsByAccounts(final Collection<Integer> accountIds) {
		TypedQuery<AccountSkill> query = em.createQuery("select s FROM AccountSkill s " +
				"WHERE s.accountId IN (:accountIds) " +
				"AND s.ruleType = 'Optional'", AccountSkill.class);

		query.setParameter("accountIds", accountIds);

		return query.getResultList();
	}

	public List<AccountSkill> findByAccounts(final Collection<Integer> accountIds) {
		TypedQuery<AccountSkill> query = em.createQuery("select s FROM AccountSkill s " +
				"WHERE s.accountId IN ( :accountIds )", AccountSkill.class);

		query.setParameter("accountIds", accountIds);

		return query.getResultList();
	}

	public List<AccountSkill> findByProfile(final Profile profile) {
		TypedQuery<AccountSkill> query = em.createQuery("SELECT DISTINCT asp.skill " +
				"FROM AccountSkillProfile asp " +
				"WHERE asp.profile = :profile", AccountSkill.class);

		query.setParameter("profile", profile);

		return query.getResultList();
	}

	public AccountSkill findSkillByAccount(final int skillId, final int accountId) {
		TypedQuery<AccountSkill> query = em.createQuery("select s FROM AccountSkill s " +
				"WHERE s.accountId = :accountId " +
				"AND s.id = :skillId", AccountSkill.class);

		query.setParameter("accountId", accountId);
		query.setParameter("skillId", skillId);

		return query.getSingleResult();
	}

	public List<AccountSkill> findByIds(final Collection<Integer> skillIds) {
		TypedQuery<AccountSkill> query = em.createQuery("select s FROM AccountSkill s " +
				"WHERE s.id IN (:skillIds)", AccountSkill.class);

		query.setParameter("skillIds", skillIds);

		return query.getResultList();
	}

	public List<AccountSkill> findSkillsByAccountsAndIds(final List<Integer> accountIds, final List<Integer> skillIds) {
		TypedQuery<AccountSkill> query = em.createQuery("select s FROM AccountSkill s " +
				"WHERE s.accountId IN (:accountIds) " +
				"AND s.id IN (:skillIds)", AccountSkill.class);

		query.setParameter("skillIds", skillIds);
		query.setParameter("accountIds", accountIds);

		return query.getResultList();
	}

	public List<AccountSkill> findByRoles(final List<Role> roles) {
		TypedQuery<AccountSkill> query = em.createQuery("SELECT DISTINCT asr.skill " +
				"FROM AccountSkillRole asr " +
				"WHERE asr.role IN ( :roles ) ", AccountSkill.class);

		query.setParameter("roles", roles);

		return query.getResultList();
	}

	public List<AccountSkill> search(final String searchTerm, final int accountId) {
		TypedQuery<AccountSkill> query = em.createQuery("select s FROM AccountSkill s " +
				"WHERE s.accountId = :accountId " +
				"AND (s.name LIKE :searchTerm " +
				"OR s.description LIKE :searchTerm)", AccountSkill.class);

		query.setParameter("accountId", accountId);
		query.setParameter("searchTerm", "%" + searchTerm + "%");

		return query.getResultList();
	}

	public void delete(final int id, final int accountId) {
		AccountSkill skill = findSkillByAccount(id, accountId);
		super.delete(skill);
	}

	public List<AccountSkill> findRequiredByContractorId(final int contractorId) {
		return findRequiredByContractorIds(Arrays.asList(contractorId));
	}

	public List<AccountSkill> findRequiredByContractorIds(final Collection<Integer> contractorIds) {
		TypedQuery<AccountSkill> query = em.createQuery("select s FROM AccountSkill s " +
				"WHERE s.accountId IN (:contractorIds) " +
				"AND s.ruleType = 'Required'", AccountSkill.class);

		query.setParameter("contractorIds", contractorIds);

		return query.getResultList();
	}

	public List<AccountSkill> findByEmployee(final Employee employee) {
		TypedQuery<AccountSkill> query = em.createQuery("SELECT s FROM Employee e " +
				"JOIN e.profile.skills eps " +
				"JOIN eps.skill s " +
				"WHERE e = :employee", AccountSkill.class);

		query.setParameter("employee", employee);

		return query.getResultList();
	}

	public List<AccountSkill> search(final String searchTerm, final List<Integer> accountIds) {
		TypedQuery<AccountSkill> query = em.createQuery("select s FROM AccountSkill s WHERE s.accountId IN (:accountIds) " +
				"AND (s.name LIKE :searchTerm " +
				"OR s.description LIKE :searchTerm)", AccountSkill.class);

		query.setParameter("accountIds", accountIds);
		query.setParameter("searchTerm", "%" + searchTerm + "%");

		return query.getResultList();
	}

	public List<AccountSkill> findBySiteAssignments(final Collection<Integer> siteIds, final Employee employee) {
		TypedQuery<AccountSkill> query = em.createQuery("SELECT DISTINCT rs.skill " +
				"FROM SiteAssignment sa " +
				"JOIN sa.role r " +
				"JOIN r.skills rs " +
				"WHERE sa.siteId IN (:siteIds) " +
				"AND sa.employee = :employee", AccountSkill.class);

		query.setParameter("siteIds", siteIds);
		query.setParameter("employee", employee);

		return query.getResultList();
	}

	public List<AccountSkill> findSiteAndCorporateRequiredSkills(final Collection<Integer> siteAndCorporateIds) {
		TypedQuery<AccountSkill> query = em.createQuery("SELECT skill FROM SiteSkill siteSkill " +
				"JOIN siteSkill.skill skill " +
				"WHERE siteSkill.siteId IN (:siteAndCorporateIds)", AccountSkill.class);

		query.setParameter("siteAndCorporateIds", siteAndCorporateIds);

		return query.getResultList();
	}

	public List<AccountSkill> findProjectRequiredSkills(final Collection<Project> projects) {
		TypedQuery<AccountSkill> query = em.createQuery("SELECT skill FROM ProjectSkill projectSkill " +
				"JOIN projectSkill.project project " +
				"JOIN projectSkill.skill skill " +
				"WHERE project IN (:projects)", AccountSkill.class);

		query.setParameter("projects", projects);

		return query.getResultList();
	}

	public List<AccountSkill> findGroupSkillsForEmployee(final Employee employee) {
		TypedQuery<AccountSkill> query = em.createQuery("SELECT DISTINCT s FROM AccountSkillGroup asg " +
				"JOIN asg.skill s " +
				"JOIN asg.group g " +
				"JOIN g.employees age " +
				"JOIN age.employee e " +
				"WHERE e = :employee", AccountSkill.class);

		query.setParameter("employee", employee);

		return query.getResultList();
	}
}
