package com.picsauditing.rbic;

import com.picsauditing.exception.PicsException;

public class InvalidAuditDataAnswer extends PicsException {
    public InvalidAuditDataAnswer(String message, Exception cause) {
        super(message, cause);
    }
}
