package com.picsauditing.employeeguard.services.entity;

import java.util.Date;
import java.util.List;

public interface EntityService<ENTITY, ID> {

	ENTITY find(ID id);

	List<ENTITY> search(String searchTerm, int accountId);

	ENTITY save(ENTITY entity, int createdBy, Date createdDate);

	ENTITY update(ENTITY entity, int updatedBy, Date updatedDate);

	void delete(ENTITY entity);

	void deleteById(ID id);

}
