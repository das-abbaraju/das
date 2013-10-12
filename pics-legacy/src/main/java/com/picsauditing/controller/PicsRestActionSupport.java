package com.picsauditing.controller;

import com.picsauditing.actions.PicsActionSupport;

public class PicsRestActionSupport extends PicsActionSupport {

	private static final long serialVersionUID = -5217323549235443366L;

	// Constants for View Names
	public static final String CREATE = "create";
	public static final String LIST = "list";
	public static final String EDIT = "edit";
	public static final String SHOW = "show";

	protected String id;

	public final String getId() {
		return id;
	}

	public final void setId(String id) {
		this.id = id;
	}

}
