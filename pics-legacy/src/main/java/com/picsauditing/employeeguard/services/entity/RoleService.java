package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.RoleDAO;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RoleService implements EntityService<Role, Integer> {

	@Autowired
	private RoleDAO roleDAO;

	@Override
	public Role find(Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null.");
		}

		return roleDAO.find(id);
	}

	@Override
	public List<Role> search(String searchTerm, int accountId) {
		if (Strings.isEmpty(searchTerm) || accountId == 0) {
			return Collections.emptyList();
		}

		return roleDAO.search(searchTerm, accountId);
	}

	@Override
	public Role save(Role role, final int createdBy, final Date createdDate) {
		role = EntityHelper.setCreateAuditFields(role, createdBy, createdDate);
		return roleDAO.save(role);
	}

	@Override
	public Role update(final Role role, final int updatedBy, final Date updatedDate) {
		Role roleToUpdate = find(role.getId());

		roleToUpdate.setName(role.getName());
		roleToUpdate.setDescription(role.getDescription());
		roleToUpdate = EntityHelper.setUpdateAuditFields(roleToUpdate, updatedBy, updatedDate);

		return roleDAO.save(roleToUpdate);
	}

	@Override
	public void delete(Role role) {
		if (role == null) {
			throw new NullPointerException("role cannot be null.");
		}

		roleDAO.delete(role);
	}

	@Override
	public void deleteById(Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		Role role = find(id);
		delete(role);
	}
}
