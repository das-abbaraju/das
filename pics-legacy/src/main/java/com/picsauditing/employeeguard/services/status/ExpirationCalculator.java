package com.picsauditing.employeeguard.services.status;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.AccountSkillProfile;

import java.util.Date;

public class ExpirationCalculator {

	public static Date calculateExpirationDate(AccountSkillProfile accountSkillProfile) {
		if (accountSkillProfile == null || accountSkillProfile.getSkill() == null)
			return null;

		if (accountSkillProfile.getSkill().getSkillType().isCertification()) {
			return calculateExpirationDateForCertificate(accountSkillProfile);
		} else {
			return calculateExpirationDateForTraining(accountSkillProfile);
		}
	}

	private static Date calculateExpirationDateForTraining(AccountSkillProfile accountSkillProfile) {
		int intervalPeriod = accountSkillProfile.getSkill().getIntervalPeriod();

		Date trainingSkillDocUpdated = accountSkillProfile.getStartDate();

		switch (accountSkillProfile.getSkill().getIntervalType()) {
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

	private static Date calculateExpirationDateForCertificate(AccountSkillProfile accountSkillProfile) {
		if (accountSkillProfile.getProfileDocument() == null) {
			return null;
		}

		return accountSkillProfile.getProfileDocument().getEndDate();
	}
}
