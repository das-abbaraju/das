package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.*;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.exceptions.NoCorporateForOperatorException;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.util.Extractor;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class SkillEntityService implements EntityService<AccountSkill, Integer>, Searchable<AccountSkill> {

	@Autowired
	private AccountSkillDAO accountSkillDAO;
	@Autowired
	private AccountSkillProfileDAO accountSkillProfileDAO;
	@Autowired
	private AccountSkillGroupDAO accountSkillGroupDAO;
	@Autowired
	private AccountSkillRoleDAO accountSkillRoleDAO;
	@Autowired
	private ProjectSkillDAO projectSkillDAO;
	@Autowired
	private SiteSkillDAO siteSkillDAO;

	/* All Find Methods */

	@Override
	public AccountSkill find(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		return accountSkillDAO.find(id);
	}

	public Map<Project, Set<AccountSkill>> getRequiredSkillsForProjects(final Collection<Project> projects) {
		if (CollectionUtils.isEmpty(projects)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(projectSkillDAO.findByProjects(projects),
				new PicsCollectionUtil.EntityKeyValueConvertable<ProjectSkill, Project, AccountSkill>() {

					@Override
					public Project getKey(ProjectSkill projectSkill) {
						return projectSkill.getProject();
					}

					@Override
					public AccountSkill getValue(ProjectSkill projectSkill) {
						return projectSkill.getSkill();
					}
				});
	}

	public Map<Role, Set<AccountSkill>> getSkillsForRoles(final Collection<Role> roles) {
		if (CollectionUtils.isEmpty(roles)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(accountSkillRoleDAO.findSkillsByRoles(roles),
				new PicsCollectionUtil.EntityKeyValueConvertable<AccountSkillRole, Role, AccountSkill>() {

					@Override
					public Role getKey(AccountSkillRole accountSkillRole) {
						return accountSkillRole.getRole();
					}

					@Override
					public AccountSkill getValue(AccountSkillRole accountSkillRole) {
						return accountSkillRole.getSkill();
					}
				});
	}

	public Map<Employee, Set<AccountSkill>> getGroupSkillsForEmployees(final Map<Employee, Set<Group>> employeesToGroups) {
		Map<Group, Set<AccountSkill>> groupSkills = getGroupSkills(employeesToGroups);

		return getEmployeeSkillsByGroups(employeesToGroups, groupSkills);
	}

	private Map<Group, Set<AccountSkill>> getGroupSkills(final Map<Employee, Set<Group>> employeesToGroups) {
		if (MapUtils.isEmpty(employeesToGroups)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(
				accountSkillGroupDAO.findByGroups(PicsCollectionUtil.flattenCollectionOfCollection(employeesToGroups.values())),

				new PicsCollectionUtil.EntityKeyValueConvertable<AccountSkillGroup, Group, AccountSkill>() {
					@Override
					public Group getKey(AccountSkillGroup entity) {
						return entity.getGroup();
					}

					@Override
					public AccountSkill getValue(AccountSkillGroup entity) {
						return entity.getSkill();
					}
				});
	}

	private Map<Employee, Set<AccountSkill>> getEmployeeSkillsByGroups(Map<Employee, Set<Group>> employeesToGroups, Map<Group, Set<AccountSkill>> groupSkills) {
		Map<Employee, Set<AccountSkill>> employeeToSkills = new HashMap<>();
		for (Employee employee : employeesToGroups.keySet()) {
			for (Group group : employeesToGroups.get(employee)) {
				if (!employeeToSkills.containsKey(employee)) {
					employeeToSkills.put(employee, new HashSet<AccountSkill>());
				}

				employeeToSkills.get(employee).addAll(groupSkills.get(group));
			}
		}
		return employeeToSkills;
	}

	/**
	 * This will return the Corporate and Site required skills.
	 *
	 * @param siteId
	 * @return
	 */
	public Set<AccountSkill> getSiteRequiredSkills(final int siteId, final Collection<Integer> accountIdsInAccountHierarchy) {
		if (CollectionUtils.isEmpty(accountIdsInAccountHierarchy)) {
			throw new NoCorporateForOperatorException(siteId + " client site does not have a corporate");
		}

		List<Integer> accountIds = new ArrayList<>(accountIdsInAccountHierarchy);
		accountIds.add(siteId);

		return ExtractorUtil.extractSet(siteSkillDAO.findByAccountIds(accountIds), new Extractor<SiteSkill, AccountSkill>() {
			@Override
			public AccountSkill extract(SiteSkill siteSkill) {
				return siteSkill.getSkill();
			}
		});
	}

	public Set<AccountSkill> getSiteAndCorporateRequiredSkills(final int siteId, final Collection<Integer> corporateAccountIds) {
		Set<Integer> siteAndParentAccountIds = new HashSet<>(corporateAccountIds);
		siteAndParentAccountIds.add(siteId);

		if (CollectionUtils.isEmpty(siteAndParentAccountIds)) {
			return Collections.emptySet();
		}

		return new HashSet<>(accountSkillDAO.findSiteAndCorporateRequiredSkills(siteAndParentAccountIds));
	}

	public Map<Integer, Set<AccountSkill>> getSiteRequiredSkills(final Collection<Integer> accountIds) {
		if (CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(
				siteSkillDAO.findByAccountIds(accountIds),
				new PicsCollectionUtil.EntityKeyValueConvertable<SiteSkill, Integer, AccountSkill>() {
					@Override
					public Integer getKey(SiteSkill entity) {
						return entity.getSiteId();
					}

					@Override
					public AccountSkill getValue(SiteSkill entity) {
						return entity.getSkill();
					}
				});
	}

	public Map<Integer, Set<AccountSkill>> getSiteAssignmentSkills(final Map<Integer, Set<Role>> siteRoles) {
		if (MapUtils.isEmpty(siteRoles)) {
			return Collections.emptyMap();
		}

		Set<Role> roles = PicsCollectionUtil.mergeCollectionOfCollections(siteRoles.values());

		Map<Role, Set<AccountSkill>> roleSkills = getSkillsForRoles(roles);

		Map<Integer, Set<AccountSkill>> siteAssignmentSkills = new HashMap<>();
		for (Map.Entry<Integer, Set<Role>> entry : siteRoles.entrySet()) {
			int siteId = entry.getKey();

			if (!siteAssignmentSkills.containsKey(siteId)) {
				siteAssignmentSkills.put(siteId, new HashSet<AccountSkill>());
			}

			for (Role role : entry.getValue()) {
				if (roleSkills.containsKey(role)) {
					siteAssignmentSkills.get(siteId).addAll(roleSkills.get(role));
				}
			}
		}

		return siteAssignmentSkills;
	}

	/**
	 * Corporate and site required skills. Not including project skills.
	 *
	 * @param projects
	 * @param siteToCorporates
	 * @return
	 */
	public Map<Project, Set<AccountSkill>> getSiteRequiredSkillsByProjects(final Collection<Project> projects,
																		   final Map<Integer, Set<Integer>> siteToCorporates) {
		if (CollectionUtils.isEmpty(projects) || MapUtils.isEmpty(siteToCorporates)) {
			return Collections.emptyMap();
		}

		Map<Project, Set<AccountSkill>> siteRequiredSkills = new HashMap<>();
		for (Project project : projects) {
			int siteId = project.getAccountId();

			siteRequiredSkills.put(project, getSiteRequiredSkills(siteId, siteToCorporates.get(siteId)));
		}

		return siteRequiredSkills;
	}

	public Map<Group, Set<AccountSkill>> getSkillsForGroups(final Set<Group> groups) {
		if (CollectionUtils.isEmpty(groups)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(
				accountSkillGroupDAO.findByGroups(groups),

				new PicsCollectionUtil.EntityKeyValueConvertable<AccountSkillGroup, Group, AccountSkill>() {

					@Override
					public Group getKey(AccountSkillGroup entity) {
						return entity.getGroup();
					}

					@Override
					public AccountSkill getValue(AccountSkillGroup entity) {
						return entity.getSkill();
					}
				});
	}

	public Set<AccountSkill> getRequiredSkillsForContractor(final int contractorId) {
		return new HashSet<>(accountSkillDAO.findRequiredByContractorId(contractorId));
	}

	public Map<Employee, Set<AccountSkill>> getRequiredSkillsForContractor(final int contractorId,
																		   final Collection<Employee> employees) {
		if (CollectionUtils.isEmpty(employees)) {
			return Collections.emptyMap();
		}

		List<AccountSkill> requiredSkills = accountSkillDAO.findRequiredByContractorId(contractorId);
		if (CollectionUtils.isEmpty(requiredSkills) || CollectionUtils.isEmpty(employees)) {
			return Collections.emptyMap();
		}

		Map<Employee, Set<AccountSkill>> employeeRequiredSkillsForContractor = new HashMap<>();
		for (Employee employee : employees) {
			if (!employeeRequiredSkillsForContractor.containsKey(employee)) {
				employeeRequiredSkillsForContractor.put(employee, new HashSet<AccountSkill>());
			}

			employeeRequiredSkillsForContractor.get(employee).addAll(requiredSkills);
		}

		return employeeRequiredSkillsForContractor;
	}

	public Map<Integer, Set<AccountSkill>> getRequiredSkillsForContractor(final Collection<Integer> contractorIds) {
		return PicsCollectionUtil.convertToMapOfSets(
				accountSkillDAO.findRequiredByContractorIds(contractorIds),

				new PicsCollectionUtil.MapConvertable<Integer, AccountSkill>() {

					@Override
					public Integer getKey(AccountSkill accountSkill) {
						return accountSkill.getAccountId();
					}
				}
		);
	}

	public Set<AccountSkill> getGroupSkillsForEmployee(final Employee employee) {
		return new HashSet<>(accountSkillDAO.findGroupSkillsForEmployee(employee));
	}

	public Set<AccountSkill> getSkillsForRole(final Role role) {
		return new HashSet<>(accountSkillDAO.findByRoles(Arrays.asList(role)));
	}

	/* All search related methods */

	@Override
	public List<AccountSkill> search(final String searchTerm, final int accountId) {
		if (Strings.isNotEmpty(searchTerm)) {
			return accountSkillDAO.search(searchTerm, accountId);
		}

		return Collections.emptyList();
	}

	/* All Save Operations */

	@Override
	public AccountSkill save(AccountSkill accountSkill, final EntityAuditInfo entityAuditInfo) {
		EntityHelper.setCreateAuditFields(accountSkill, entityAuditInfo);
		EntityHelper.setCreateAuditFields(accountSkill.getRoles(), entityAuditInfo);
		EntityHelper.setCreateAuditFields(accountSkill.getGroups(), entityAuditInfo);

		return accountSkillDAO.save(accountSkill);
	}

	/* All Update Operations */

	@Override
	public AccountSkill update(final AccountSkill accountSkill, final EntityAuditInfo entityAuditInfo) {
		AccountSkill accountSkillToUpdate = find(accountSkill.getId());

		accountSkillToUpdate.setName(accountSkill.getName());
		accountSkillToUpdate.setDescription(accountSkill.getDescription());
		accountSkillToUpdate.setSkillType(accountSkill.getSkillType());
		accountSkillToUpdate.setIntervalType(accountSkill.getIntervalType());
		accountSkillToUpdate.setIntervalPeriod(accountSkill.getIntervalPeriod());
		accountSkillToUpdate.setRuleType(accountSkill.getRuleType());

		accountSkillToUpdate = EntityHelper.setUpdateAuditFields(accountSkillToUpdate, entityAuditInfo);

		return accountSkillDAO.save(accountSkillToUpdate);
	}

	/* All Delete Operations */

	@Override
	public void delete(final AccountSkill accountSkill) {
		if (accountSkill == null) {
			throw new NullPointerException("accountSkill cannot be null");
		}

		accountSkillDAO.delete(accountSkill);
	}

	@Override
	public void deleteById(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		AccountSkill accountSkill = find(id);
		delete(accountSkill);
	}

	public List<AccountSkill> findSkillsForCorpSite(final List<Integer> accountIds) {
		if (CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		return accountSkillDAO.findByAccounts(accountIds);
	}

	public List<AccountSkill> findReqdSkillsForCorpSite(int siteId) {
		return siteSkillDAO.findReqdSkillsForCorpOp(siteId);
	}

	public Map<Integer,AccountSkill> findReqdSkillsForCorpSiteMap(int siteId) {
		List<AccountSkill> skills= siteSkillDAO.findReqdSkillsForCorpOp(siteId);
		Map<Integer,AccountSkill> map = PicsCollectionUtil.convertToMap(skills,

						new PicsCollectionUtil.MapConvertable<Integer, AccountSkill>() {

							@Override
							public Integer getKey(AccountSkill skill) {
								return skill.getId();
							}
						});


		return map;
	}

}
