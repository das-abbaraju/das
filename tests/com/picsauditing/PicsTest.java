package com.picsauditing;

import junit.framework.TestCase;

import org.junit.BeforeClass;

public abstract class PicsTest extends TestCase {
	@BeforeClass
	static public void before() {
		System.setProperty("pics.ftpDir", "P:\\Development\\ftp_dir");
	}
}
