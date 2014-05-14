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
//		TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase " +
//				"WHERE ase.employee = :employee AND ase.skill IN (:skills)", AccountSkillEmployee.class);

		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfle asp " +
				"JOIN asp.profile p " +
				"JOIN p.employees e " +
				"JOIN asp.skill s " +
				"WHERE e = :employee AND s IN (:skills)", AccountSkillProfile.class);

		query.setParameter("employee", employee);
		query.setParameter("skills", skills);

		return query.getResultList();
	}

	public List<AccountSkillProfile> findByAccountAndEmployee(final Employee employee) {
//		TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase " +
//				"WHERE ase.employee = :employee " +
//				"AND ase.skill.accountId = :accountId", AccountSkillEmployee.class);

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
//		TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase " +
//				"WHERE ase.employee.profile = :profile " +
//				"AND ase.skill = :skill", AccountSkillEmployee.class);

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
//		TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase " +
//				"WHERE ase.employee.profile = :profile", AccountSkillEmployee.class);

		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"WHERE p = :profile", AccountSkillProfile.class);

		query.setParameter("profile", profile);

		return query.getResultList();
	}

	public List<AccountSkillProfile> findByProfileDocument(final ProfileDocument profileDocument) {
//		TypedQuery<AccountSkillEmployee> query = em.createQuery("SELECT ase FROM AccountSkillEmployee ase " +
//				"WHERE ase.profileDocument = :profileDocument", AccountSkillEmployee.class);

		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"WHERE asp.profileDocument = :profileDocument", AccountSkillProfile.class);

		query.setParameter("profileDocument", profileDocument);

		return query.getResultList();
	}

	public List<AccountSkillProfile> findByEmployeeAccount(final int accountId) {
//		TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase " +
//				"WHERE ase.employee.accountId = :accountId", AccountSkillEmployee.class);

		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"JOIN p.employees e " +
				"WHERE e.accountId = :accountId", AccountSkillProfile.class);

		query.setParameter("accountId", accountId);

		return query.getResultList();
	}

	public List<AccountSkillProfile> findByEmployeesAndSkills(final Collection<Employee> employees,
															  final Collection<AccountSkill> accountSkills) {
//		TypedQuery<AccountSkillEmployee> query = em.createQuery("FROM AccountSkillEmployee ase " +
//				"WHERE ase.employee IN (:employees) " +
//				"AND ase.skill IN (:skills)", AccountSkillEmployee.class);

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
//		String s = "SELECT DISTINCT ase FROM AccountSkillEmployee ase " +
//				"JOIN ase.skill s " +
//				"JOIN s.roles asr " +
//				"JOIN asr.role r " +
//				"JOIN r.projects pr " +
//				"JOIN pr.employees pre " +
//				"JOIN pre.employee e " +
//				"WHERE pr.project = :project AND e.accountId = :accountId";
//
//		TypedQuery<AccountSkillEmployee> query = em.createQuery(s, AccountSkillEmployee.class);

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

	public List<AccountSkillProfile> findByContractorAndRole(final int contractorId, final int roleId) {
		if (contractorId == 0 || roleId == 0) {
			return Collections.emptyList();
		}

//		String s = "SELECT DISTINCT ase FROM AccountSkillEmployee ase " +
//				"JOIN ase.skill s " +
//				"JOIN s.roles asr " +
//				"JOIN asr.role r " +
//				"JOIN ase.employee e " +
//				"WHERE e.accountId = :contractorId " +
//				"AND r.id = :roleId";
//		TypedQuery<AccountSkillEmployee> query = em.createQuery(s, AccountSkillEmployee.class);

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

	public List<AccountSkillProfile> findByEmployeeAndCorporateIds(final int employeeId, final List<Integer> corporateIds) {
		if (employeeId == 0 || CollectionUtils.isEmpty(corporateIds)) {
			return Collections.emptyList();
		}

//		String s = "SELECT DISTINCT ase " +
//				"FROM AccountSkillEmployee ase " +
//				"JOIN ase.employee e " +
//				"JOIN ase.skill s " +
//				"WHERE e.id = :employeeId " +
//				"AND s.accountId IN (:corporateIds)";
//		TypedQuery<AccountSkillEmployee> query = em.createQuery(s, AccountSkillEmployee.class);

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

//		Query query = em.createQuery("DELETE FROM AccountSkillEmployee ase WHERE ase.id IN (:ids)");

		Query query = em.createQuery("DELETE FROM AccountSkillProfile asp WHERE asp.id IN (:ids)");

		query.setParameter("ids", ids);

		query.executeUpdate();
	}

	public List<AccountSkillProfile> getProjectRoleSkillsForContractorsAndSite(final Set<Integer> contractorIds,
																			   final int siteId) {
//		TypedQuery<AccountSkillEmployee> query = em.createQuery("SELECT ase " +
//				"FROM AccountSkillEmployee ase " +
//				"JOIN ase.employee e " +
//				"JOIN ase.skill s " +
//				"JOIN s.roles asr " +
//				"JOIN asr.role r " +
//				"JOIN r.projects pr " +
//				"JOIN pr.project p " +
//				"WHERE e.accountId IN (:contractorIds) " +
//				"AND p.accountId = :siteId", AccountSkillEmployee.class);

		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"JOIN p.employees e " +
				"JOIN asp.skill s " +
				"JOIN s.roles asr " +
				"JOIN asr.role r " +
				"JOIN r.projects pr " +
				"JOIN pr.project p " +
				"WHERE e.accountId IN (:contractorIds) " +
				"AND p.accountId = :siteId", AccountSkillProfile.class);

		query.setParameter("contractorIds", contractorIds);
		query.setParameter("siteId", siteId);

		return query.getResultList();
	}

	public List<AccountSkillProfile> getProjectSkillsForContractorsAndSite(final Set<Integer> contractorIds,
																		   final int siteId) {
//		TypedQuery<AccountSkillEmployee> query = em.createQuery("SELECT ase " +
//				"FROM AccountSkillEmployee ase " +
//				"JOIN ase.employee e " +
//				"JOIN ase.skill s " +
//				"JOIN s.projects ps " +
//				"JOIN ps.project p " +
//				"WHERE e.accountId IN (:contractorIds) " +
//				"AND p.accountId = :siteId", AccountSkillEmployee.class);

		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"JOIN p.employees e " +
				"JOIN asp.skill s " +
				"JOIN s.projects ps " +
				"JOIN ps.project p " +
				"WHERE e.accountId IN (:contractorIds) " +
				"AND p.accountId = :siteId", AccountSkillProfile.class);

		query.setParameter("contractorIds", contractorIds);
		query.setParameter("siteId", siteId);

		return query.getResultList();
	}

	public List<AccountSkillProfile> getRoleSkillsForContractorsAndRoles(final Set<Integer> contractorIds,
																		 final Collection<Role> roles) {
//		TypedQuery<AccountSkillEmployee> query = em.createQuery("SELECT ase " +
//				"FROM AccountSkillEmployee ase " +
//				"JOIN ase.employee e " +
//				"JOIN ase.skill s " +
//				"JOIN s.roles sr " +
//				"WHERE e.accountId IN (:contractorIds) " +
//				"AND sr.role IN (:roles)", AccountSkillEmployee.class);

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

	public List<AccountSkillProfile> getSiteSkillsForContractorsAndSites(final Set<Integer> contractorIds,
																		 final Set<Integer> siteAndCorporateIds) {
//		TypedQuery<AccountSkillEmployee> query = em.createQuery("SELECT ase " +
//				"FROM AccountSkillEmployee ase " +
//				"JOIN ase.employee e " +
//				"JOIN ase.skill s " +
//				"JOIN s.sites ss " +
//				"WHERE e.accountId IN (:contractorIds) " +
//				"AND ss.siteId IN (:siteAndCorporateIds)", AccountSkillEmployee.class);

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
//		Query query = em.createQuery("DELETE FROM AccountSkillEmployee ase " +
//				"WHERE ase.profileDocument = :profileDocument");

		Query query = em.createQuery("DELETE FROM AccountSkillProfile asp " +
				"WHERE asp.profileDocument = :profileDocument");

		query.setParameter("profileDocument", profileDocument);

		query.executeUpdate();
	}

	public List<AccountSkillProfile> findBySkillAndProfile(AccountSkill accountSkill, Profile profile) {
//		TypedQuery<AccountSkillEmployee> query = em.createQuery("SELECT ase FROM AccountSkillEmployee ase " +
//				"JOIN ase.employee e " +
//				"JOIN e.profile p " +
//				"WHERE ase.skill = :skill AND p = :profile", AccountSkillEmployee.class);

		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"JOIN p.employees e " +
				"WHERE asp.skill = :skill AND p = :profile", AccountSkillProfile.class);

		query.setParameter("skill", accountSkill);
		query.setParameter("profile", profile);

		return query.getResultList();
	}

	public List<AccountSkillProfile> findBySkillIdAndProfile(final int skillId, final Profile profile) {
//		TypedQuery<AccountSkillEmployee> query = em.createQuery("SELECT ase FROM AccountSkillEmployee ase " +
//				"JOIN ase.employee e " +
//				"JOIN e.profile p " +
//				"WHERE ase.skill.id = :skillId AND p = :profile", AccountSkillEmployee.class);

		TypedQuery<AccountSkillProfile> query = em.createQuery("SELECT asp FROM AccountSkillProfile asp " +
				"JOIN asp.profile p " +
				"JOIN p.employee e " +
				"WHERE asp.skill.id = :skillId AND p = :profile", AccountSkillProfile.class);

		query.setParameter("skillId", skillId);
		query.setParameter("profile", profile);

		return query.getResultList();
	}
}
