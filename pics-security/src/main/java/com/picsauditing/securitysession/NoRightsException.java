package com.picsauditing.securitysession;

import com.picsauditing.access.OpType;

public class NoRightsException extends Exception {
//	private static final long serialVersionUID = 263711643706157627L;
//
	public NoRightsException(OpPerms opPerm, OpType oType) {
        // TODO: we should avoid translations
//		super(TranslationServiceFactory.getTranslationService().getText("Exception.NoRights",
//				TranslationActionSupport.getLocaleStatic(), oType, opPerm));
	}

//	public NoRightsException(String groupName) {
//		super(TranslationServiceFactory.getTranslationService().getText("Exception.NoRights.GroupName",
//				TranslationActionSupport.getLocaleStatic(), groupName));
//	}
//
}
