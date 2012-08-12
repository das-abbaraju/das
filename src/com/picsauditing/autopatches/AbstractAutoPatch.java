package com.picsauditing.autopatches;

import com.tacitknowledge.util.migration.MigrationTaskSupport;

public abstract class AbstractAutoPatch extends MigrationTaskSupport {

	public AbstractAutoPatch() {
		super();
	}

	@Override
	public Integer getLevel() {
	    String name = getName();
	    name = name.replaceAll("^Patch(\\d*).*$", "$1");
		return new Integer(name);
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

}