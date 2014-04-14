package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class AccountSkillEmployeeDAO extends AbstractBaseEntityDAO<AccountSkillEmployee> {

	private static final Logger LOG = LoggerFactory.getLogger(AccountSkillEmployeeDAO.class);

	public AccountSkillEmployeeDAO() {
		this.type = AccountSkillEmployee.class;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void save(List<AccountSkillEmployee> accountSkillEmployees) {
		for (AccountSkillEmployee accountSkillEmployee : accountSkillEmployees) {
			super.persistOrMerge(accountSkillEmployee);
		}

		em.flush();
	}

	public List<AccountSkillEmployee> findBySkill(AccountSkill skill) {
		if (skill != null) {
			TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase WHERE ase.skill = :skill AND ase.employee.deletedBy = 0 AND ase.employee.deletedDate IS NULL", AccountSkillEmployee.class);
			query.setParameter("skill", skill);
			return query.getResultList();
		}

		return Collections.emptyList();
	}

	public List<AccountSkillEmployee> findBySkills(List<AccountSkill> skills) {
		if (CollectionUtils.isNotEmpty(skills)) {
			TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase WHERE ase.skill IN (:skills) AND ase.employee.deletedBy = 0 AND ase.employee.deletedDate IS NULL", AccountSkillEmployee.class);
			query.setParameter("skills", skills);
			return query.getResultList();
		}

		return Collections.emptyList();
	}

	public List<AccountSkillEmployee> findByEmployeeAndSkills(Employee employee, Collection<AccountSkill> skills) {
		if (CollectionUtils.isNotEmpty(skills) && employee != null) {
			TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase WHERE ase.employee = :employee AND ase.skill IN (:skills)", AccountSkillEmployee.class);
			query.setParameter("employee", employee);
			query.setParameter("skills", skills);
			return query.getResultList();
		}

		return Collections.emptyList();
	}

	public List<AccountSkillEmployee> findByAccountAndEmployee(Employee employee) {
		TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase " +
				"WHERE ase.employee = :employee " +
				"AND ase.skill.accountId = :accountId", AccountSkillEmployee.class);
		query.setParameter("employee", employee);
		query.setParameter("accountId", employee.getAccountId());

		return query.getResultList();
	}

	public AccountSkillEmployee findByProfileAndSkill(final Profile profile, final AccountSkill skill) {
		TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase WHERE ase.employee.profile = :profile AND ase.skill = :skill", AccountSkillEmployee.class);
		query.setParameter("profile", profile);
		query.setParameter("skill", skill);

		AccountSkillEmployee result = null;
		try {
			result = query.getSingleResult();
		} catch (NoResultException | NonUniqueResultException e) {
			LOG.error("Error searching for profile and skill", e);
		}

		return result;
	}

	public List<AccountSkillEmployee> findByProfile(final Profile profile) {
		if (profile == null) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase " +
				"JOIN " +
				"WHERE ase.employee.profile = :profile", AccountSkillEmployee.class);

		query.setParameter("profile", profile);

		return query.getResultList();
	}

	public List<AccountSkillEmployee> findByEmployeeAccount(final int accountId) {
		if (accountId == 0) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase WHERE ase.employee.accountId = :accountId", AccountSkillEmployee.class);
		query.setParameter("accountId", accountId);
		return query.getResultList();
	}

	public List<AccountSkillEmployee> findByEmployeesAndSkills(final Collection<Employee> employees,
															   final Collection<AccountSkill> accountSkills) {
		TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase " +
				"WHERE ase.employee IN (:employees) " +
				"AND ase.skill IN (:skills)", AccountSkillEmployee.class);

		query.setParameter("employees", employees);
		query.setParameter("skills", accountSkills);

		return query.getResultList();
	}

	public List<AccountSkillEmployee> findByProjectAndContractor(Project project, int accountId) {
		String s = "SELECT DISTINCT ase FROM AccountSkillEmployee ase " +
				"JOIN ase.skill s " +
				"JOIN s.roles asr " +
				"JOIN asr.role r " +
				"JOIN r.projects pr " +
				"JOIN pr.employees pre " +
				"JOIN pre.employee e " +
				"WHERE pr.project = :project AND e.accountId = :accountId";
		TypedQuery<AccountSkillEmployee> query = em.createQuery(s, AccountSkillEmployee.class);
		query.setParameter("project", project);
		query.setParameter("accountId", accountId);
		return query.getResultList();
	}

	public List<AccountSkillEmployee> findByContractorAndRole(final int contractorId, final int roleId) {
		if (contractorId == 0 || roleId == 0) {
			return Collections.emptyList();
		}

		String s = "SELECT DISTINCT ase FROM AccountSkillEmployee ase " +
				"JOIN ase.skill s " +
				"JOIN s.roles asr " +
				"JOIN asr.role r " +
				"JOIN ase.employee e " +
				"WHERE e.accountId = :contractorId " +
				"AND r.id = :roleId";
		TypedQuery<AccountSkillEmployee> query = em.createQuery(s, AccountSkillEmployee.class);
		query.setParameter("contractorId", contractorId);
		query.setParameter("roleId", roleId);
		return query.getResultList();
	}

	public List<AccountSkillEmployee> findByEmployeeAndCorporateIds(final int employeeId, final List<Integer> corporateIds) {
		if (employeeId == 0 || CollectionUtils.isEmpty(corporateIds)) {
			return Collections.emptyList();
		}

		String s = "SELECT DISTINCT ase " +
				"FROM AccountSkillEmployee ase " +
				"JOIN ase.employee e " +
				"JOIN ase.skill s " +
				"WHERE e.id = :employeeId " +
				"AND s.accountId IN (:corporateIds)";
		TypedQuery<AccountSkillEmployee> query = em.createQuery(s, AccountSkillEmployee.class);
		query.setParameter("employeeId", employeeId);
		query.setParameter("corporateIds", corporateIds);
		return query.getResultList();
	}

	@Transactional(propagation = Propagation.NESTED)
	public void deleteByIds(Collection<Integer> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return;
		}

		Query query = em.createQuery("DELETE FROM AccountSkillEmployee ase WHERE ase.id IN (:ids)");
		query.setParameter("ids", ids);
		query.executeUpdate();
	}

	public List<AccountSkillEmployee> getProjectRoleSkillsForContractorsAndSite(final Set<Integer> contractorIds,
																				final int siteId) {
		TypedQuery<AccountSkillEmployee> query = em.createQuery("SELECT ase " +
				"FROM AccountSkillEmployee ase " +
				"JOIN ase.employee e " +
				"JOIN ase.skill s " +
				"JOIN s.roles asr " +
				"JOIN asr.role r " +
				"JOIN r.projects pr " +
				"JOIN pr.project p " +
				"WHERE e.accountId IN (:contractorIds) " +
				"AND p.accountId = :siteId", AccountSkillEmployee.class);
		query.setParameter("contractorIds", contractorIds);
		query.setParameter("siteId", siteId);

		return query.getResultList();
	}

	public List<AccountSkillEmployee> getProjectSkillsForContractorsAndSite(final Set<Integer> contractorIds,
																			final int siteId) {
		TypedQuery<AccountSkillEmployee> query = em.createQuery("SELECT ase " +
				"FROM AccountSkillEmployee ase " +
				"JOIN ase.employee e " +
				"JOIN ase.skill s " +
				"JOIN s.projects ps " +
				"JOIN ps.project p " +
				"WHERE e.accountId IN (:contractorIds) " +
				"AND p.accountId = :siteId", AccountSkillEmployee.class);
		query.setParameter("contractorIds", contractorIds);
		query.setParameter("siteId", siteId);

		return query.getResultList();
	}

	public List<AccountSkillEmployee> getRoleSkillsForContractorsAndRoles(final Set<Integer> contractorIds,
																		  final Collection<Role> roles) {
		TypedQuery<AccountSkillEmployee> query = em.createQuery("SELECT ase " +
				"FROM AccountSkillEmployee ase " +
				"JOIN ase.employee e " +
				"JOIN ase.skill s " +
				"JOIN s.roles sr " +
				"WHERE e.accountId IN (:contractorIds) " +
				"AND sr.role IN (:roles)", AccountSkillEmployee.class);
		query.setParameter("contractorIds", contractorIds);
		query.setParameter("roles", roles);

		return query.getResultList();
	}

	public List<AccountSkillEmployee> getSiteSkillsForContractorsAndSites(final Set<Integer> contractorIds,
																		  final Set<Integer> siteAndCorporateIds) {
		TypedQuery<AccountSkillEmployee> query = em.createQuery("SELECT ase " +
				"FROM AccountSkillEmployee ase " +
				"JOIN ase.employee e " +
				"JOIN ase.skill s " +
				"JOIN s.sites ss " +
				"WHERE e.accountId IN (:contractorIds) " +
				"AND ss.siteId IN (:siteAndCorporateIds)", AccountSkillEmployee.class);
		query.setParameter("contractorIds", contractorIds);
		query.setParameter("siteAndCorporateIds", siteAndCorporateIds);

		return query.getResultList();
	}
}
