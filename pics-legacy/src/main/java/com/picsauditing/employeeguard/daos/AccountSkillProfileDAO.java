package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AccountSkillProfileDAO extends AbstractBaseEntityDAO<AccountSkillProfile> {

	public AccountSkillProfileDAO() {
		this.type = AccountSkillProfile.class;
	}

	public List<AccountSkillProfile> findByEmployeeAndSkills(final Employee employee,
															 final Collection<AccountSkill> skills) {
		if (employee.getProfile() == null) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"JOIN p.employees e " +
				"JOIN asp.skill s " +
				"WHERE e = :employee AND s IN (:skills)", AccountSkillProfile.class);

		query.setParameter("employee", employee);
		query.setParameter("skills", skills);

		return query.getResultList();
	}

	public List<AccountSkillProfile> findByContractorCreatedSkillsForEmployee(final Employee employee) {
		if (employee.getProfile() == null) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"JOIN p.employees e " +
				"JOIN asp.skill s " +
				"WHERE e = :employee " +
				"AND s.accountId = :accountId", AccountSkillProfile.class);

		query.setParameter("employee", employee);
		query.setParameter("accountId", employee.getAccountId());

		return query.getResultList();
	}

	public AccountSkillProfile findByProfileAndSkill(final Profile profile, final AccountSkill skill) {
		if (profile == null) {
			return null;
		}

		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"WHERE p = :profile " +
				"AND asp.skill = :skill", AccountSkillProfile.class);

		query.setParameter("profile", profile);
		query.setParameter("skill", skill);

		try {
			return query.getSingleResult();
		} catch (NoResultException | NonUniqueResultException e) {
			return null;
		}
	}

	public List<AccountSkillProfile> findByProfile(final Profile profile) {
		if (profile == null) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"WHERE p = :profile", AccountSkillProfile.class);

		query.setParameter("profile", profile);

		return query.getResultList();
	}

	public List<AccountSkillProfile> findByProfileDocument(final ProfileDocument profileDocument) {
		if (profileDocument == null) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"WHERE asp.profileDocument = :profileDocument", AccountSkillProfile.class);

		query.setParameter("profileDocument", profileDocument);

		return query.getResultList();
	}

	public List<AccountSkillProfile> findByEmployeeAccount(final int accountId) {
		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"JOIN p.employees e " +
				"WHERE e.accountId = :accountId", AccountSkillProfile.class);

		query.setParameter("accountId", accountId);

		return query.getResultList();
	}

	public List<AccountSkillProfile> findByEmployeesAndSkills(final Collection<Employee> employees,
															  final Collection<AccountSkill> accountSkills) {
		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"JOIN p.employees e " +
				"WHERE e IN (:employees) " +
				"AND asp.skill IN (:skills)", AccountSkillProfile.class);

		query.setParameter("employees", employees);
		query.setParameter("skills", accountSkills);

		return query.getResultList();
	}

	public List<AccountSkillProfile> findByProjectAndContractor(final Project project, final int accountId) {
		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT DISTINCT asp FROM AccountSkillProfile asp " +
				"JOIN asp.skill s " +
				"JOIN s.roles asr " +
				"JOIN asr.role r " +
				"JOIN r.projects pr " +
				"JOIN pr.employees pre " +
				"JOIN pre.employee e " +
				"WHERE pr.project = :project AND e.accountId = :accountId", AccountSkillProfile.class);

		query.setParameter("project", project);
		query.setParameter("accountId", accountId);

		return query.getResultList();
	}

	public List<AccountSkillProfile> findDistinctByContractorAndRole(final int contractorId, final int roleId) {
		if (contractorId == 0 || roleId == 0) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT DISTINCT asp FROM AccountSkillProfile asp " +
				"JOIN asp.skill s " +
				"JOIN s.roles asr " +
				"JOIN asr.role r " +
				"JOIN asp.profile p " +
				"JOIN p.employees e " +
				"WHERE e.accountId = :contractorId " +
				"AND r.id = :roleId", AccountSkillProfile.class);

		query.setParameter("contractorId", contractorId);
		query.setParameter("roleId", roleId);

		return query.getResultList();
	}

	public List<AccountSkillProfile> findDistinctByEmployeeAndCorporateIds(final int employeeId, final List<Integer> corporateIds) {
		if (employeeId == 0 || CollectionUtils.isEmpty(corporateIds)) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT DISTINCT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"JOIN p.employees e " +
				"JOIN asp.skill s " +
				"WHERE e.id = :employeeId " +
				"AND s.accountId IN (:corporateIds)", AccountSkillProfile.class);

		query.setParameter("employeeId", employeeId);
		query.setParameter("corporateIds", corporateIds);

		return query.getResultList();
	}

	@Transactional(propagation = Propagation.NESTED)
	public void deleteByIds(final Collection<Integer> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return;
		}

		Query query = em.createQuery("DELETE FROM AccountSkillProfile asp WHERE asp.id IN (:ids)");

		query.setParameter("ids", ids);

		query.executeUpdate();
	}

	public List<AccountSkillProfile> getProjectRoleSkillsForContractorsAndSite(final Set<Integer> contractorIds,
																			   final int siteId) {
		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"JOIN p.employees e " +
				"JOIN asp.skill s " +
				"JOIN s.roles asr " +
				"JOIN asr.role r " +
				"JOIN r.projects projs " +
				"JOIN projs.project proj " +
				"WHERE e.accountId IN (:contractorIds) " +
				"AND proj.accountId = :siteId", AccountSkillProfile.class);

		query.setParameter("contractorIds", contractorIds);
		query.setParameter("siteId", siteId);

		return query.getResultList();
	}

	public List<AccountSkillProfile> getProjectReqdSkillsForContractorsAndSite(final Set<Integer> contractorIds,
																			   final int siteId) {
		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"JOIN p.employees e " +
				"JOIN asp.skill s " +
				"JOIN s.projects projs " +
				"JOIN projs.project proj " +
				"WHERE e.accountId IN (:contractorIds) " +
				"AND proj.accountId = :siteId", AccountSkillProfile.class);

		query.setParameter("contractorIds", contractorIds);
		query.setParameter("siteId", siteId);

		return query.getResultList();
	}

	public List<AccountSkillProfile> getRoleSkillsForContractorsAndRoles(final Set<Integer> contractorIds,
																		 final Collection<Role> roles) {
		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"JOIN p.employees e " +
				"JOIN asp.skill s " +
				"JOIN s.roles sr " +
				"WHERE e.accountId IN (:contractorIds) " +
				"AND sr.role IN (:roles)", AccountSkillProfile.class);

		query.setParameter("contractorIds", contractorIds);
		query.setParameter("roles", roles);

		return query.getResultList();
	}

	public List<AccountSkillProfile> getSiteReqdSkillsForContractorsAndSites(final Set<Integer> contractorIds,
																			 final Set<Integer> siteAndCorporateIds) {
		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"JOIN p.employees e " +
				"JOIN asp.skill s " +
				"JOIN s.sites ss " +
				"WHERE e.accountId IN (:contractorIds) " +
				"AND ss.siteId IN (:siteAndCorporateIds)", AccountSkillProfile.class);

		query.setParameter("contractorIds", contractorIds);
		query.setParameter("siteAndCorporateIds", siteAndCorporateIds);

		return query.getResultList();
	}

	@Transactional(propagation = Propagation.NESTED)
	public void delete(final ProfileDocument profileDocument) {
		Query query = em.createQuery("DELETE FROM AccountSkillProfile asp " +
				"WHERE asp.profileDocument = :profileDocument");

		query.setParameter("profileDocument", profileDocument);

		query.executeUpdate();
	}

	public AccountSkillProfile findBySkillAndProfile(AccountSkill accountSkill, Profile profile) {
		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"WHERE asp.skill = :skill AND p = :profile", AccountSkillProfile.class);

		query.setParameter("skill", accountSkill);
		query.setParameter("profile", profile);

		try {
			return query.getSingleResult();
		} catch (NoResultException | NonUniqueResultException e) {
			return null;
		}

	}

	public List<AccountSkillProfile> findBySkillIdAndProfile(final int skillId, final Profile profile) {
		if (profile == null) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"WHERE asp.skill.id = :skillId AND p = :profile", AccountSkillProfile.class);

		query.setParameter("skillId", skillId);
		query.setParameter("profile", profile);

		return query.getResultList();
	}
}
