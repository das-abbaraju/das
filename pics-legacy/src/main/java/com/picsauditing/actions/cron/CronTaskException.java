package com.picsauditing.actions.cron;

import com.picsauditing.exception.PicsException;

public class CronTaskException extends PicsException {

	public CronTaskException(String message, Throwable cause) {
		super(message, cause);
	}

    public CronTaskException(String message) {
        super(message);
    }
}
