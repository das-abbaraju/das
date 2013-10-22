package com.picsauditing.employeeguard.daos.softdeleted;

import com.picsauditing.employeeguard.daos.BaseEntityDAO;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;

public class SoftDeletedEmployeeDAO extends BaseEntityDAO<SoftDeletedEmployee> {
	public SoftDeletedEmployeeDAO() {
		this.type = SoftDeletedEmployee.class;
	}
}
