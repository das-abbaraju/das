package com.picsauditing.employeeguard.services.aop.docdownload;

import com.picsauditing.employeeguard.daos.ProfileDocumentDAO;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.employeeguard.exceptions.DocumentViewAccessDeniedException;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmployeeDocViewPerms implements DocViewable {
	private Logger log = LoggerFactory.getLogger(getClass());

	private DocViewable nextDocViewable;

	@Override
	public DocViewableStatus chkPermissions(int documentId, int skillId) throws DocumentViewAccessDeniedException {

		int documentOwnerAppUserId = fetchDocumentOwnerAppUserId(documentId, skillId);

		SessionInfoProvider sessionInfoProvider = SessionInfoProviderFactory.getSessionInfoProvider();
		int loggedInUserAppUserId = sessionInfoProvider.getAppUserId();

		if(loggedInUserAppUserId == documentOwnerAppUserId){
			log.debug("Owner of document requesting to view it - Allow");
			return DocViewableStatus.ALLOWED;
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

	private int fetchDocumentOwnerAppUserId(int documentId, int skillId) throws DocumentViewAccessDeniedException {
		ProfileDocumentDAO profileDocumentDAO = SpringUtils.getBean("ProfileDocumentDAO");
		ProfileDocument profileDocument = profileDocumentDAO.find(documentId);

		if(profileDocument==null)
			throw new DocumentViewAccessDeniedException(String.format("Document not found - documentId=[%d], SkillId=[%d]",documentId, skillId));

		return profileDocument.getProfile().getUserId();
	}
}
