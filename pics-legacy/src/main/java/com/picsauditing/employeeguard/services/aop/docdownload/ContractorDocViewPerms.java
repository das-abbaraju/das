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

import java.util.List;

public class ContractorDocViewPerms implements DocViewable {
	private Logger log = LoggerFactory.getLogger(getClass());

	private DocViewable nextDocViewable;

	@Override
	public DocViewableStatus chkPermissions(int documentId, int skillId) throws DocumentViewAccessDeniedException {

		AccountSkill accountSkill = fetchAccountSkill(documentId, skillId);

		SessionInfoProvider sessionInfoProvider = SessionInfoProviderFactory.getSessionInfoProvider();
		Permissions permissions = sessionInfoProvider.getPermissions();
		if(permissions.isContractor() ){
			return chkPermissions(accountSkill, permissions);
		}
		else if(nextDocViewable!=null){
			return nextDocViewable.chkPermissions(documentId, skillId);
		}


		return DocViewableStatus.UNKNOWN;
	}

	@Override
	public DocViewable attach(DocViewable docViewable) {
		this.nextDocViewable = docViewable;

		return docViewable;
	}


	private DocViewableStatus chkPermissions(AccountSkill accountSkill, Permissions permissions) throws DocumentViewAccessDeniedException {
		if(accountSkill.getAccountId() == permissions.getAccountId()){
			log.debug("Document tied to a Contractor skill, is being requested by a Contractor User - Allow");
			return DocViewableStatus.ALLOWED;
		}
		else{
			throw new DocumentViewAccessDeniedException(String.format("Permission Denied - Corporate/Site User is trying to view document tied to a NON Corporate/Site Skill - AccountId=[%d], SkillId=[%d], SkillAccountId=[%d]",permissions.getAccountId(), accountSkill.getId(), accountSkill.getAccountId()));
		}

	}

	private AccountSkill fetchAccountSkill(int documentId, int skillId) throws DocumentViewAccessDeniedException {
		SkillEntityService skillEntityService = SpringUtils.getBean("SkillEntityService");
		AccountSkill accountSkill = skillEntityService.find(skillId);
		if(accountSkill==null)
			throw new DocumentViewAccessDeniedException(String.format("Skill not found - documentId=[%d], SkillId=[%d]",documentId, skillId));

		return accountSkill;
	}
}
