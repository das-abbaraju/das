package com.picsauditing.employeeguard.services.aop.docdownload;

import com.picsauditing.access.Permissions;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.exceptions.DocumentViewAccessDeniedException;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnonymousUserDocViewPerms implements DocViewable {
	private Logger log = LoggerFactory.getLogger(getClass());

	private DocViewable nextDocViewable;

	@Override
	public DocViewableStatus chkPermissions(int employeeId, int skillId) throws DocumentViewAccessDeniedException {
		SkillEntityService skillEntityService = SpringUtils.getBean("SkillEntityService");
		AccountSkill accountSkill = skillEntityService.find(skillId);

		SessionInfoProvider sessionInfoProvider = SessionInfoProviderFactory.getSessionInfoProvider();
		Permissions permissions = sessionInfoProvider.getPermissions();

		String PermDeniedForUnknownReason=String.format("Not expected to reach here - Permission denied to view document for unknown reason -  AccountId=[%d], SkillId=[%d], SkillAccountId=[%d]",permissions.getAccountId(), skillId, accountSkill.getAccountId());

		log.warn(PermDeniedForUnknownReason);
		throw new DocumentViewAccessDeniedException(PermDeniedForUnknownReason);

	}

	@Override
	public DocViewable attach(DocViewable docViewable) {
		this.nextDocViewable = docViewable;

		return docViewable;
	}
}
