package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

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

	public List<AccountSkillEmployee> findByEmployeeAndSkills(Employee employee, List<AccountSkill> skills) {
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

		TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase WHERE ase.employee.profile = :profile", AccountSkillEmployee.class);
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

	public List<AccountSkillEmployee> findByEmployeesAndSkills(final List<Employee> employees, final List<AccountSkill> accountSkills) {
		if (CollectionUtils.isEmpty(employees) || CollectionUtils.isEmpty(accountSkills)) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase WHERE ase.employee IN (:employees) AND ase.skill IN (:skills)", AccountSkillEmployee.class);
		query.setParameter("employees", employees);
		query.setParameter("skills", accountSkills);
		return query.getResultList();
	}

	public List<AccountSkillEmployee> findByEmployeeAccountAndSkills(final int accountId, final List<AccountSkill> accountSkills) {
		if (accountId == 0 || CollectionUtils.isEmpty(accountSkills)) {
			return Collections.emptyList();
		}

		TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase WHERE ase.employee.accountId = :accountId AND ase.skill IN :skills", AccountSkillEmployee.class);
		query.setParameter("accountId", accountId);
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
}
