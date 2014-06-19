package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.models.MFileManager;
import com.picsauditing.employeeguard.services.entity.DocumentEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class EmployeeFileService {

	@Autowired
	private DocumentEntityService documentEntityService;

	public Set<MFileManager.MFile> findEmployeeFiles(final int appUserId) {
		return new MFileManager().copyBasicInfo(documentEntityService.findDocumentsForAppUser(appUserId));
	}
}
