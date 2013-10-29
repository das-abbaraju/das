package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
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

public class AccountSkillEmployeeDAO extends BaseEntityDAO<AccountSkillEmployee> {

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

	public List<AccountSkillEmployee> findByEmployeesAndSkill(List<Employee> employees, AccountSkill skill) {
		if (CollectionUtils.isNotEmpty(employees) && skill != null) {
			TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase WHERE ase.employee IN (:employees) AND ase.skill = :skill", AccountSkillEmployee.class);
			query.setParameter("employees", employees);
			query.setParameter("skill", skill);
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
		TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase WHERE ase.employee = :employee " +
				"AND ase.skill.deletedBy = 0 AND ase.skill.deletedDate = NULL", AccountSkillEmployee.class);
		query.setParameter("employee", employee);

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
}