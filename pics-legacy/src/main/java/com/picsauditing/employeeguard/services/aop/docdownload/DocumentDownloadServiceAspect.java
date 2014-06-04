package com.picsauditing.employeeguard.services.aop.docdownload;

import com.picsauditing.employeeguard.exceptions.DocumentViewAccessDeniedException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;


@Aspect
public class DocumentDownloadServiceAspect {

	@Before("execution(* com.picsauditing.employeeguard.services.ProfileDocumentService.getDocumentThumbnail(..))")
	public void chkPermissionsToViewDocumentThumbnail(JoinPoint joinPoint) throws DocumentViewAccessDeniedException {

		Object[] methodArgs = joinPoint.getArgs();

		if(methodArgs.length != 2) {
			throw new DocumentViewAccessDeniedException("Missing required information to check Document view permissions");
		}

		int documentId = (int)methodArgs[0];
		int skillId = 	(int)methodArgs[1];

		/**
		 Corporate "certification" Skill - Everyone BUT Contractor can view
		 Contractor "certification" skill - Everyone BUT Corporate/Site can view

		 Chain of responsibility pattern.
		 */
		EmployeeDocViewPerms employeeDocViewPerms = new EmployeeDocViewPerms();
		employeeDocViewPerms.attach(new CorpOpDocViewPerms()).attach(new ContractorDocViewPerms()).attach(new AnonymousUserDocViewPerms());

		employeeDocViewPerms.chkPermissions(documentId, skillId);

	}


}
