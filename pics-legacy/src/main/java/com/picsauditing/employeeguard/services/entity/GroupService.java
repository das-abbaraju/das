package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.AccountGroupDAO;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GroupService implements EntityService<Group, Integer>, Searchable<Group> {

	@Autowired
	private AccountGroupDAO accountGroupDAO;

	/* All Find Methods */

	@Override
	public Group find(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		return accountGroupDAO.find(id);
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
	public Group save(Group group, final int createdBy, final Date createdDate) {
		group = EntityHelper.setCreateAuditFields(group, createdBy, createdDate);
		return accountGroupDAO.save(group);
	}

	/* All Update Methods */

	@Override
	public Group update(final Group group, final int updatedBy, final Date updatedDate) {
		Group groupToUpDate = find(group.getId());

		groupToUpDate.setName(group.getName());
		groupToUpDate.setDescription(group.getDescription());
		groupToUpDate = EntityHelper.setUpdateAuditFields(group, updatedBy, updatedDate);

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
}
