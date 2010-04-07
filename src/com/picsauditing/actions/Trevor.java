package com.picsauditing.actions;

import java.io.File;

import com.picsauditing.util.FileUtils;

@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {

	@Override
	public String execute() throws Exception {

		File sourceFile = new File("/var/pics/test");
		FileUtils.moveFile(sourceFile, "/var/pics/www_files/");
		return SUCCESS;
	}

}
