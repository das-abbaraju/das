package com.picsauditing.actions;

import com.picsauditing.jpa.entities.User;

class AccountRecoveryMessages {

    static final String EMAIL_SENT_HEADER_KEY = "AccountRecovery.EmailSentHeader";
    static final String RECOVERY_EMAIL_SENT_KEY = "AccountRecovery.EmailSent";
    static final String USERNAME_EMAIL_SENT_KEY = "AccountRecovery.EmailSent2";
    static final String EMAIL_RESET_ERROR_KEY = "AccountRecovery.error.ResetEmailError";
    static final String NO_USERNAME_ERROR_KEY = "AccountRecovery.error.NoUserName";
    static final String EMAIL_NOT_FOUND_KEY = "AccountRecovery.error.EmailNotFound";
    static final String NO_EMAIL_SENT_ERROR_KEY = "AccountRecovery.error.NoEmailSent";
    static final String BLANK_USERNAME_INPUT_ERROR_KEY = "AccountRecovery.error.NoUserName";
    static final String USER_NOT_FOUND_ERROR_KEY = "AccountRecovery.error.UserNotFound";
    static final String USER_NOT_ACTIVE_ERROR_KEY = "AccountRecovery.error.UserNotActive";
    static final String USERNAME_NOT_FOUND_ERROR_KEY = "AccountRecovery.error.UserNameNotFound";

    final private PicsActionSupport parent;

    AccountRecoveryMessages(PicsActionSupport parent) {
        this.parent = parent;
    }

    private void message(String messageKey) {
        parent.addActionMessage(parent.getText(messageKey));
    }

    private void error(String messageKey) {
        parent.addActionError(parent.getText(messageKey));
    }

    private void setHeader() {
        parent.setActionMessageHeader(parent.getText(EMAIL_SENT_HEADER_KEY));
    }

    private void messageWithSentHeader(String key) {
        setHeader();
        message(key);
    }

    void addNoUserNameError() { error(NO_USERNAME_ERROR_KEY); }
    void addUserNotFoundError() { error(USER_NOT_FOUND_ERROR_KEY); }
    void addUsernameNotFoundError() { error(USERNAME_NOT_FOUND_ERROR_KEY); }
    void addUserEmailNotFoundError() { error(EMAIL_NOT_FOUND_KEY); }
    void addUserNotActiveError() { error(USER_NOT_ACTIVE_ERROR_KEY); }
    void addNoEmailSentError() { error(NO_EMAIL_SENT_ERROR_KEY); }
    void addBlankUsernameSubmittedError() { error(BLANK_USERNAME_INPUT_ERROR_KEY); }
    void addUsernameEmailSentMessage() { messageWithSentHeader(USERNAME_EMAIL_SENT_KEY); }

    void failedEmailSend() { error(EMAIL_RESET_ERROR_KEY); }
    void successfulEmailSendTo(User user) {
        setHeader();
        parent.addActionMessage(parent.getTextParameterized(RECOVERY_EMAIL_SENT_KEY, user.getEmail()));
    }

}
