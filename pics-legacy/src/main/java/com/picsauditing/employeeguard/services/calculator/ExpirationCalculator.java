package com.picsauditing.employeeguard.services.calculator;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.AccountSkillProfile;

import java.util.Date;

public class ExpirationCalculator {
	public static Date calculateExpirationDate(AccountSkillProfile accountSkillProfile) {
		if (accountSkillProfile.getSkill().getSkillType().isCertification()) {
			return calculateExpirationDateForCertificate(accountSkillProfile);
		} else {
			return calculateExpirationDateForTraining(accountSkillProfile);
		}
	}

	private static Date calculateExpirationDateForTraining(AccountSkillProfile accountSkillProfile) {
		int intervalPeriod = accountSkillProfile.getSkill().getIntervalPeriod();

		Date now = new Date();
		switch (accountSkillProfile.getSkill().getIntervalType()) {
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
			case NO_EXPIRATION:
				return DateBean.getEndOfTime();
			default:
				throw new IllegalArgumentException();
		}
	}

	private static Date calculateExpirationDateForCertificate(AccountSkillProfile accountSkillProfile) {
		return accountSkillProfile.getProfileDocument().getEndDate();
	}
}
