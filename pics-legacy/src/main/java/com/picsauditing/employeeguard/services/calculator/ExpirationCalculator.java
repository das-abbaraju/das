package com.picsauditing.employeeguard.services.calculator;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;

import java.util.Date;

public class ExpirationCalculator {
	public static Date calculateExpirationDate(AccountSkillEmployee accountSkillEmployee) {
		if (accountSkillEmployee == null || accountSkillEmployee.getSkill() == null)
			return null;

		if (accountSkillEmployee.getSkill().getSkillType().isCertification()) {
			return calculateExpirationDateForCertificate(accountSkillEmployee);
		} else {
			return calculateExpirationDateForTraining(accountSkillEmployee);
		}
	}

	private static Date calculateExpirationDateForTraining(AccountSkillEmployee accountSkillEmployee) {
		int intervalPeriod = accountSkillEmployee.getSkill().getIntervalPeriod();

		Date trainingSkillDocUpdated = accountSkillEmployee.getStartDate();

		switch (accountSkillEmployee.getSkill().getIntervalType()) {
			case DAY:
				return DateBean.addDays(trainingSkillDocUpdated, intervalPeriod);
			case WEEK:
				return DateBean.addDays(trainingSkillDocUpdated, intervalPeriod * 7);
			case MONTH:
				return DateBean.addMonths(trainingSkillDocUpdated, intervalPeriod);
			case YEAR:
				return DateBean.addMonths(trainingSkillDocUpdated, intervalPeriod * 12);
			case NOT_APPLICABLE:
				return DateBean.getEndOfTime();
			case NO_EXPIRATION:
				return DateBean.getEndOfTime();
			default:
				throw new IllegalArgumentException();
		}
	}

	private static Date calculateExpirationDateForCertificate(AccountSkillEmployee accountSkillEmployee) {
		if (accountSkillEmployee.getProfileDocument() == null)
			return null;

		return accountSkillEmployee.getProfileDocument().getEndDate();
	}
}
