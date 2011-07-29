package com.picsauditing.importpqf;

import com.picsauditing.jpa.entities.AuditType;

public class ImportComplyWorks extends ImportPqf {

	@Override
	public int getAuditType() {
		return AuditType.COMPLYWORKS_PQF;
	}

}
