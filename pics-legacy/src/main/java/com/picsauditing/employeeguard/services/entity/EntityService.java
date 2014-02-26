package com.picsauditing.employeeguard.services.entity;

import java.util.Date;

public interface EntityService<ENTITY, ID> {

	ENTITY find(ID id);

	ENTITY save(ENTITY entity, int createdBy, Date createdDate);

	ENTITY update(ENTITY entity, int updatedBy, Date updatedDate);

	void delete(ENTITY entity);

	void deleteById(ID id);

}
