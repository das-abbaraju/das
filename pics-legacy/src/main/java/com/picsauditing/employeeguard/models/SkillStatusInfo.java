package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.status.SkillStatus;

public interface SkillStatusInfo {

	SkillStatus getStatus();

	void setStatus(SkillStatus skillStatus);

}
