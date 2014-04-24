package com.picsauditing.employeeguard.controllers.helper;

import com.picsauditing.util.Strings;
import com.picsauditing.web.SessionInfoProviderFactory;

/**
 * Instead of having this class, add a parameter and ID on the end of the URL.
 */
public class RefererHelper {

	public static final String EMPLOYEE_SKILL_ID = "employee_skill_id";
	public static final String EMPLOYEE_SKILL_REFERER_URL = "employee/skill/";

	public static void saveSkillRefererIdToSession() {
		String referer = SessionInfoProviderFactory.getSessionInfoProvider().getReferer();

		if (Strings.isNotEmpty(referer) && referer.contains(EMPLOYEE_SKILL_REFERER_URL)) {
			int idIndex = referer.indexOf(EMPLOYEE_SKILL_REFERER_URL) + EMPLOYEE_SKILL_REFERER_URL.length();

			SessionInfoProviderFactory.getSessionInfoProvider().putInSession(EMPLOYEE_SKILL_ID, referer.substring(idIndex));
		}
	}

	public static boolean sessionHasSkillId() {
		return SessionInfoProviderFactory.getSessionInfoProvider().getSession().containsKey(EMPLOYEE_SKILL_ID);
	}

	public static int getSkillIdFromSession() {
		return (int) SessionInfoProviderFactory.getSessionInfoProvider().getSession().get(EMPLOYEE_SKILL_ID);
	}

}
