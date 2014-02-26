package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.RoleDAO;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

public class RoleService implements EntityService<Role, Integer>, Searchable<Role> {

	@Autowired
	private RoleDAO roleDAO;

	/* All Find Methods */

	@Override
	public Role find(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		return roleDAO.find(id);
	}

	/* All Search Methods */

	@Override
	public List<Role> search(final String searchTerm, final int accountId) {
		if (Strings.isEmpty(searchTerm) || accountId == 0) {
			return Collections.emptyList();
		}

		return roleDAO.search(searchTerm, accountId);
	}

	/* All Save Methods */

	@Override
	public Role save(Role role, final EntityAuditInfo entityAuditInfo) {
		role = EntityHelper.setCreateAuditFields(role, entityAuditInfo);
		return roleDAO.save(role);
	}

	/* All Update Methods */

	@Override
	public Role update(final Role role, final EntityAuditInfo entityAuditInfo) {
		Role roleToUpdate = find(role.getId());

		roleToUpdate.setName(role.getName());
		roleToUpdate.setDescription(role.getDescription());
		roleToUpdate = EntityHelper.setUpdateAuditFields(roleToUpdate, entityAuditInfo);

		return roleDAO.save(roleToUpdate);
	}

	/* All Delete Methods */

	@Override
	public void delete(final Role role) {
		if (role == null) {
			throw new NullPointerException("role cannot be null");
		}

		roleDAO.delete(role);
	}

	@Override
	public void deleteById(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		Role role = find(id);
		delete(role);
	}
}
