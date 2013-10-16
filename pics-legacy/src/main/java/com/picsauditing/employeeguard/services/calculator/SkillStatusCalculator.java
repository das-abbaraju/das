package com.picsauditing.employeeguard.services.calculator;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;

import java.util.Date;

public class SkillStatusCalculator {

	public static SkillStatus calculateStatusFromSkill(AccountSkillEmployee accountSkillEmployee) {
		Date endDate = accountSkillEmployee.getEndDate();
        Date today = new Date(); // TODO: Maybe create method in DateBean for today?

		if (endDate == null || endDate.before(today)) {
			return SkillStatus.Expired;
		} else if (endDate.before(DateBean.addMonths(new Date(), 1))) {
			return SkillStatus.Expiring;
		} else {
			return SkillStatus.Complete;
		}
	}
}
