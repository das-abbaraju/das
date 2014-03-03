package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.AccountSkillRoleDAO;
import com.picsauditing.employeeguard.daos.ProjectSkillDAO;
import com.picsauditing.employeeguard.daos.SiteSkillDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.util.Extractor;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class SkillEntityService implements EntityService<AccountSkill, Integer>, Searchable<AccountSkill> {

	@Autowired
	private AccountSkillDAO accountSkillDAO;
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
		return Utilities.convertToMapOfSets(projectSkillDAO.findByProjects(projects),
				new Utilities.EntityKeyValueConvertable<ProjectSkill, Project, AccountSkill>() {

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
		return Utilities.convertToMapOfSets(accountSkillRoleDAO.findSkillsByRoles(roles),
				new Utilities.EntityKeyValueConvertable<AccountSkillRole, Role, AccountSkill>() {

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

	/**
	 * This will return the Corporate and Site required skills.
	 *
	 * @param siteId
	 * @return
	 */
	public Set<AccountSkill> getSiteRequiredSkills(final int siteId, final List<Integer> accountIdsInAccountHierarchy) {
		List<Integer> accountIds = new ArrayList<>(accountIdsInAccountHierarchy);
		accountIds.add(siteId);

		return ExtractorUtil.extractSet(siteSkillDAO.findByAccountIds(accountIds), new Extractor<SiteSkill, AccountSkill>() {
			@Override
			public AccountSkill extract(SiteSkill siteSkill) {
				return siteSkill.getSkill();
			}
		});
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
		accountSkill = EntityHelper.setCreateAuditFields(accountSkill, entityAuditInfo);
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
}
