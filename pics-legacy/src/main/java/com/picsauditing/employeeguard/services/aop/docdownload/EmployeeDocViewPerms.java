package com.picsauditing.employeeguard.services.aop.docdownload;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.exceptions.DocumentViewAccessDeniedException;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmployeeDocViewPerms implements DocViewable {
	private Logger log = LoggerFactory.getLogger(getClass());

	private DocViewable nextDocViewable;

	@Override
	public DocViewableStatus chkPermissions(int employeeId, int skillId) throws DocumentViewAccessDeniedException {

		int employeeAppUserId = fetchEmployeeAppUserId(employeeId, skillId);

		SessionInfoProvider sessionInfoProvider = SessionInfoProviderFactory.getSessionInfoProvider();
		int loggedInUserAppUserId = sessionInfoProvider.getAppUserId();

		if(isEmployeeLoggedIn(employeeAppUserId,loggedInUserAppUserId)){
			//TODO:Currently we dont check if employee is really assigned to this skill or not.
			return DocViewableStatus.ALLOWED;
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


	private boolean isEmployeeLoggedIn(int employeeAppUserId, int loggedInUserAppUserId){
		return employeeAppUserId==loggedInUserAppUserId;
	}

	private int fetchEmployeeAppUserId(int employeeId, int skillId) throws DocumentViewAccessDeniedException {

		EmployeeEntityService employeeEntityService = SpringUtils.getBean("EmployeeEntityService");
		Employee employee = employeeEntityService.find(employeeId);

		if(employee == null)
			throw new DocumentViewAccessDeniedException(String.format("Employee not found - employeeId=[%d], SkillId=[%d]", employeeId, skillId));

		Profile profile = employee.getProfile();

		if(profile==null )
			throw new DocumentViewAccessDeniedException(String.format("Profile not found - employeeId=[%d], SkillId=[%d]", employeeId, skillId));

		return profile.getUserId();
	}
}
