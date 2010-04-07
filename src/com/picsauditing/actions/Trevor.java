package com.picsauditing.actions;

import java.io.File;

import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {

	private String fileFrom;
	private String fileTo;

	@Override
	public String execute() {

		if (button != null) {
			if (Strings.isEmpty(fileFrom)) {
				addActionError("You must specify a source file");
				return SUCCESS;
			}
			if (Strings.isEmpty(fileTo)) {
				addActionError("You must specify a destination file");
				return SUCCESS;
			}

			File sourceFile = new File(fileFrom);
			if (!sourceFile.exists()) {
				addActionError(fileFrom + " does not exist");
				return SUCCESS;
			}

			File destinationFile = new File(fileTo);
			try {
				sourceFile.renameTo(destinationFile);
			} catch (Exception e) {
				addActionError("Failed to copy file: " + e.getMessage());
				output = e.getStackTrace().toString();
			}
		}
		return SUCCESS;
	}

	public String getFileFrom() {
		return fileFrom;
	}

	public void setFileFrom(String fileFrom) {
		this.fileFrom = fileFrom;
	}

	public String getFileTo() {
		return fileTo;
	}

	public void setFileTo(String fileTo) {
		this.fileTo = fileTo;
	}

}
