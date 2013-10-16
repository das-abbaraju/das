package com.picsauditing.employeeguard.services.calculator;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;

import java.util.Date;

public class ExpirationCalculator {
	public static Date calculateExpirationDate(AccountSkillEmployee accountSkillEmployee) {
		if (accountSkillEmployee.getSkill().getSkillType().isCertification()) {
			return calculateExpirationDateForCertificate(accountSkillEmployee);
		} else {
			return calculateExpirationDateForTraining(accountSkillEmployee);
		}
	}

	private static Date calculateExpirationDateForTraining(AccountSkillEmployee accountSkillEmployee) {
		int intervalPeriod = accountSkillEmployee.getSkill().getIntervalPeriod();

		Date now = new Date();
		switch (accountSkillEmployee.getSkill().getIntervalType()) {
			case DAY:
				return DateBean.addDays(now, intervalPeriod);
			case WEEK:
				return DateBean.addDays(now, intervalPeriod * 7);
			case MONTH:
				return DateBean.addMonths(now, intervalPeriod);
			case YEAR:
				return DateBean.addMonths(now, intervalPeriod * 12);
			case NOT_APPLICABLE:
				return DateBean.getEndOfTime();
			default:
				throw new IllegalArgumentException();
		}
	}

	private static Date calculateExpirationDateForCertificate(AccountSkillEmployee accountSkillEmployee) {
		return accountSkillEmployee.getProfileDocument().getEndDate();
	}
}
