package com.picsauditing.access;

public class NoRightsException extends Exception {
	private static final long serialVersionUID = 263711643706157627L;
	public NoRightsException(OpPerms opPerm, OpType oType) {
		super("You do not have the "+oType+" "+opPerm+" right");
	}
}
