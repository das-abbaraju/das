package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.AccountGroupDAO;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.services.models.EntityAuditInfo;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

public class GroupServiceEntityAuditInfo implements TestEntityService<Group, Integer> {

	@Autowired
	private AccountGroupDAO accountGroupDAO;

	@Override
	public Group find(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		return accountGroupDAO.find(id);
	}

	@Override
	public List<Group> search(final String searchTerm, final int accountId) {
		if (Strings.isEmpty(searchTerm) || accountId == 0) {
			return Collections.emptyList();
		}

		return accountGroupDAO.search(searchTerm, accountId);
	}

	@Override
	public Group save(Group group, final EntityAuditInfo entityAuditInfo) {
		group = EntityHelper.setCreateAuditFields(group, entityAuditInfo.getUserId(), entityAuditInfo.getTimestamp());
		return accountGroupDAO.save(group);
	}

	@Override
	public Group update(final Group group, final EntityAuditInfo entityAuditInfo) {
		Group groupToUpDate = find(group.getId());

		groupToUpDate.setName(group.getName());
		groupToUpDate.setDescription(group.getDescription());
		groupToUpDate = EntityHelper.setUpdateAuditFields(group, entityAuditInfo.getUserId(),
				entityAuditInfo.getTimestamp());

		return accountGroupDAO.save(groupToUpDate);
	}

	@Override
	public void delete(final Group group) {
		if (group == null) {
			throw new NullPointerException("group cannot be null.");
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
}
