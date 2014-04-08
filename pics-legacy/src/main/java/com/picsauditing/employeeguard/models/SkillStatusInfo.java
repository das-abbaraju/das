package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

public interface SkillStatusInfo {

	SkillStatus getStatus();

	void setStatus(SkillStatus skillStatus);

}
