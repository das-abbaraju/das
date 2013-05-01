package com.picsauditing.validator;

import org.approvaltests.reporters.GenericDiffReporter;

public class ExamDiffReporter extends GenericDiffReporter {

	public ExamDiffReporter() {
		super("C:\\Program Files (x86)\\ExamDiff\\ExamDiff.exe", "Please install ExamDiff.");
	}

}
