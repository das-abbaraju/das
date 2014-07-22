package com.picsauditing.employeeguard.models.operations;

import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;

public interface MAttachContractor {
	public MAttachContractor attachContractor() throws ReqdInfoMissingException;

}
