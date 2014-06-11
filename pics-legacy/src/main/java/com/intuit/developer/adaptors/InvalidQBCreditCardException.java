package com.intuit.developer.adaptors;

public class InvalidQBCreditCardException extends Exception {

    public InvalidQBCreditCardException() {
        super();
    }

    public InvalidQBCreditCardException(String message) {
        super(message);
    }

    public InvalidQBCreditCardException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public InvalidQBCreditCardException(Throwable cause) {
        super(cause);
    }

    public InvalidQBCreditCardException(String message,
                                        Throwable cause,
                                        boolean enableSuppression,
                                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
