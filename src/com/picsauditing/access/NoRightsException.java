package com.picsauditing.access;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.actions.TranslationActionSupport;

public class NoRightsException extends Exception {
	private static final long serialVersionUID = 263711643706157627L;

	public NoRightsException(OpPerms opPerm, OpType oType) {
		super(I18nCache.getInstance().getText("Exception.NoRights", TranslationActionSupport.getLocaleStatic(), oType,
				opPerm));
	}

	public NoRightsException(String groupName) {
		super(I18nCache.getInstance().getText("Exception.NoRights.GroupName", TranslationActionSupport.getLocaleStatic(), groupName));
	}
}
