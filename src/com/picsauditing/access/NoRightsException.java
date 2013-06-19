package com.picsauditing.access;

import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.service.i18n.TranslationServiceFactory;

public class NoRightsException extends Exception {
	private static final long serialVersionUID = 263711643706157627L;

	public NoRightsException(OpPerms opPerm, OpType oType) {
		super(TranslationServiceFactory.getTranslationService().getText("Exception.NoRights",
				TranslationActionSupport.getLocaleStatic(), oType, opPerm));
	}

	public NoRightsException(String groupName) {
		super(TranslationServiceFactory.getTranslationService().getText("Exception.NoRights.GroupName",
				TranslationActionSupport.getLocaleStatic(), groupName));
	}

}
