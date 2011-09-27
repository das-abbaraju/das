package com.picsauditing.importpqf;

import com.picsauditing.jpa.entities.AuditType;

public class ImportPqfIsnUs extends ImportPqfIsn {
	@Override
	public int getAuditType() {
		return AuditType.ISN_US_PQF;
	}
}
