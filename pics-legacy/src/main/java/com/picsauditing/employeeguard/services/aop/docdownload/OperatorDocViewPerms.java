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

public class OperatorDocViewPerms implements DocViewable {
	private Logger log = LoggerFactory.getLogger(getClass());

	private DocViewable nextDocViewable;

	@Override
	public DocViewableStatus chkPermissions(int employeeId, int skillId) throws DocumentViewAccessDeniedException {

		AccountSkill accountSkill = fetchAccountSkill(employeeId, skillId);

		SessionInfoProvider sessionInfoProvider = SessionInfoProviderFactory.getSessionInfoProvider();
		Permissions permissions = sessionInfoProvider.getPermissions();
		if(permissions.isOperator() ){
			List<Integer> accountIds = fetchTopmostCorporateAccountIds(permissions);
			return checkPermissions(accountIds, accountSkill, permissions);
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

	private List<Integer> fetchTopmostCorporateAccountIds(final Permissions permissions){
		AccountService accountService = SpringUtils.getBean("AccountService");
		List<Integer> accountIds = accountService.getTopmostCorporateAccountIds(permissions.getAccountId());

		return accountIds;
	}

	private DocViewableStatus checkPermissions(List<Integer> accountIds, AccountSkill accountSkill, Permissions permissions) throws DocumentViewAccessDeniedException{
		if (accountIds.contains(accountSkill.getAccountId())) {
			log.debug("Document tied to Operator skill, is being requested by a Operator User - Allow");
			return DocViewableStatus.ALLOWED;
		}
		else{
			throw new DocumentViewAccessDeniedException(String.format("Permission Denied - Operator User is trying to view document tied to a NON Corporate/Site Skill - AccountId=[%d], SkillId=[%d], SkillAccountId=[%d]",permissions.getAccountId(), accountSkill.getId(), accountSkill.getAccountId()));

		}
	}
}