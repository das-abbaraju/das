package com.picsauditing;

import java.util.List;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class PicsRunListener extends RunListener {
	List<Failure> failures;

	public PicsRunListener(List<Failure> failures) {
		super();
		this.failures = failures;
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		super.testFailure(failure);
		// Do the specific PICS run method here
		failures.add(failure);
	}
}
