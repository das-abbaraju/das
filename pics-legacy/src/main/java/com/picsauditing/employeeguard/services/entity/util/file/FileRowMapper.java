package com.picsauditing.employeeguard.services.entity.util.file;

import com.picsauditing.employeeguard.entities.BaseEntity;

public interface FileRowMapper<E extends BaseEntity> {

	boolean isHeader(String[] row);

	boolean isValid(String[] row);

	boolean isEmptyRow(String[] row);

	E mapToEntity(String[] fileRow);

}
