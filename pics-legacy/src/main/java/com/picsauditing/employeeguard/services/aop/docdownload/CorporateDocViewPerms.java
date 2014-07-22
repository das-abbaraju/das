package com.picsauditing.employeeguard.services.aop.docdownload;

import com.picsauditing.access.Permissions;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.exceptions.DocumentViewAccessDeniedException;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CorporateDocViewPerms implements DocViewable {
	private Logger log = LoggerFactory.getLogger(getClass());

	private DocViewable nextDocViewable;

	@Override
	public DocViewableStatus chkPermissions(int employeeId, int skillId) throws DocumentViewAccessDeniedException {

		AccountSkill accountSkill = fetchAccountSkill(employeeId, skillId);

		SessionInfoProvider sessionInfoProvider = SessionInfoProviderFactory.getSessionInfoProvider();
		Permissions permissions = sessionInfoProvider.getPermissions();
		if(permissions.isCorporate() ){

			return checkPermissions(accountSkill, permissions);
		}
		else if(nextDocViewable!=null){
			return nextDocViewable.chkPermissions(employeeId, skillId);
		}


		return DocViewableStatus.UNKNOWN;
	}

	@Override
	public DocViewable attach(DocViewable docViewable) {
		this.nextDocViewable = docViewable;

		return docViewable;
	}

	private AccountSkill fetchAccountSkill(int employeeId, int skillId) throws DocumentViewAccessDeniedException {
		SkillEntityService skillEntityService = SpringUtils.getBean("SkillEntityService");
		AccountSkill accountSkill = skillEntityService.find(skillId);

		if(accountSkill==null)
			throw new DocumentViewAccessDeniedException(String.format("Skill not found - employeeId=[%d], SkillId=[%d]", employeeId, skillId));

		return accountSkill;
	}

	private DocViewableStatus checkPermissions(AccountSkill accountSkill, Permissions permissions) throws DocumentViewAccessDeniedException{
		int loggedInAccountId = permissions.getAccountId();
		int skillAccountId = accountSkill.getAccountId();
		if (loggedInAccountId==skillAccountId) {
			log.debug("Document tied to a Corporate skill, is being requested by a Corporate User - Allow");
			return DocViewableStatus.ALLOWED;
		}
		else{
			throw new DocumentViewAccessDeniedException(String.format("Permission Denied - Corporate User is trying to view document tied to a Skill not owned by this Corporate - Logged In Corp AccountId=[%d], SkillAccountId=[%d], SkillId=[%d]",loggedInAccountId, skillAccountId, accountSkill.getId()));

		}
	}
}
