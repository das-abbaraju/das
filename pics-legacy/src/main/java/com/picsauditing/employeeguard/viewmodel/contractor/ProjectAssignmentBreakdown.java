package com.picsauditing.employeeguard.viewmodel.contractor;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.Map;

public class ProjectAssignmentBreakdown {

    private Map<SkillStatus, Integer> statusRollup;

    public ProjectAssignmentBreakdown(Map<SkillStatus, Integer> statusRollup) {
        this.statusRollup = statusRollup;
    }

    public int getExpired() {
        return statusRollup.containsKey(SkillStatus.Expired) ? statusRollup.get(SkillStatus.Expired) : 0;
    }

    public int getExpiring() {
        return statusRollup.containsKey(SkillStatus.Expiring) ? statusRollup.get(SkillStatus.Expiring) : 0;
    }

    public int getPending() {
        return statusRollup.containsKey(SkillStatus.Pending) ? statusRollup.get(SkillStatus.Pending) : 0;
    }

    public int getComplete() {
        return statusRollup.containsKey(SkillStatus.Completed) ? statusRollup.get(SkillStatus.Completed) : 0;
    }
}
