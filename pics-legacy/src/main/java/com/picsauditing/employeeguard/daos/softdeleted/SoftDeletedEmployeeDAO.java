package com.picsauditing.employeeguard.daos.softdeleted;

import com.picsauditing.employeeguard.daos.AbstractBaseEntityDAO;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;

public class SoftDeletedEmployeeDAO extends AbstractBaseEntityDAO<SoftDeletedEmployee> {
	public SoftDeletedEmployeeDAO() {
		this.type = SoftDeletedEmployee.class;
	}
}
