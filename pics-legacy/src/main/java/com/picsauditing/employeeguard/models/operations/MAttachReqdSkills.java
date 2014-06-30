package com.picsauditing.employeeguard.models.operations;

import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;

public interface MAttachReqdSkills {
	public MAttachReqdSkills attachReqdSkills() throws ReqdInfoMissingException;

}
