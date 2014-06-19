package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.AccountGroupDAO;
import com.picsauditing.employeeguard.daos.AccountGroupEmployeeDAO;
import com.picsauditing.employeeguard.daos.AccountSkillGroupDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class GroupEntityService implements EntityService<Group, Integer>, Searchable<Group> {

	@Autowired
	private AccountGroupDAO accountGroupDAO;
	@Autowired
	private AccountGroupEmployeeDAO accountGroupEmployeeDAO;
	@Autowired
	private AccountSkillGroupDAO accountSkillGroupDAO;

	/* All Find Methods */

	@Override
	public Group find(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		return accountGroupDAO.find(id);
	}

	public Map<Employee, Set<Group>> getEmployeeGroups(final Collection<Employee> employees) {
		if (CollectionUtils.isEmpty(employees)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(accountGroupEmployeeDAO.findByEmployees(employees),
				new PicsCollectionUtil.EntityKeyValueConvertable<GroupEmployee, Employee, Group>() {
					@Override
					public Employee getKey(GroupEmployee entity) {
						return entity.getEmployee();
					}

					@Override
					public Group getValue(GroupEmployee entity) {
						return entity.getGroup();
					}
				});
	}

	public Map<Integer, Set<Group>> getGroupsByContractorId(final Collection<Employee> employees) {
		if (CollectionUtils.isEmpty(employees)) {
			return Collections.emptyMap();
		}

		List<Group> groups = accountGroupDAO.findGroupsByEmployees(employees);
		if (CollectionUtils.isEmpty(groups)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(groups,
				new PicsCollectionUtil.MapConvertable<Integer, Group>() {
					@Override
					public Integer getKey(Group group) {
						return group.getAccountId();
					}
				});
	}

	/* All Search Methods */

	@Override
	public List<Group> search(final String searchTerm, final int accountId) {
		if (Strings.isEmpty(searchTerm) || accountId == 0) {
			return Collections.emptyList();
		}

		return accountGroupDAO.search(searchTerm, accountId);
	}

	/* All Save Methods */

	@Override
	public Group save(Group group, final EntityAuditInfo entityAuditInfo) {
		group = EntityHelper.setCreateAuditFields(group, entityAuditInfo);
		return accountGroupDAO.save(group);
	}

	/* All Update Methods */

	@Override
	public Group update(final Group group, final EntityAuditInfo entityAuditInfo) {
		Group groupToUpDate = find(group.getId());

		groupToUpDate.setName(group.getName());
		groupToUpDate.setDescription(group.getDescription());
		groupToUpDate = EntityHelper.setUpdateAuditFields(group, entityAuditInfo);

		return accountGroupDAO.save(groupToUpDate);
	}

	/* All Delete Methods */

	@Override
	public void delete(final Group group) {
		if (group == null) {
			throw new NullPointerException("group cannot be null");
		}

		accountGroupDAO.delete(group);
	}

	@Override
	public void deleteById(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		Group group = find(id);
		delete(group);
	}

	public Map<Group, Set<AccountSkill>> getGroupSkillsForProfile(final Profile profile) {
		return PicsCollectionUtil.convertToMapOfSets(accountSkillGroupDAO.findByProfile(profile),
				new PicsCollectionUtil.EntityKeyValueConvertable<AccountSkillGroup, Group, AccountSkill>() {
					@Override
					public Group getKey(AccountSkillGroup accountSkillGroup) {
						return accountSkillGroup.getGroup();
					}

					@Override
					public AccountSkill getValue(AccountSkillGroup accountSkillGroup) {
						return accountSkillGroup.getSkill();
					}
				});
	}

	public List<Group> findGroupsForContractor(int accountId) {
		return accountGroupDAO.findByAccount(accountId);
	}
}
