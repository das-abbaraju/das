package com.picsauditing.autopatches;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.util.migration.MigrationContext;
import com.tacitknowledge.util.migration.MigrationException;
import com.tacitknowledge.util.migration.MigrationTaskSupport;

public class Patch_00020 extends MigrationTaskSupport {
	final static Logger logger = LoggerFactory.getLogger(Patch_00020.class);

	@Override
	public Integer getLevel() {
		return new Integer(00020);
	}
	@Override
	public void up(MigrationContext context) throws MigrationException {
		logger.trace("Entered Patch_00020.up()");
		logger.info("Some actual patch code would go here.");
	}
}
