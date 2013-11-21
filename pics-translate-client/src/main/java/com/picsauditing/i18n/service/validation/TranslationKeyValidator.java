package com.picsauditing.i18n.service.validation;

public class TranslationKeyValidator {

    public boolean validateKey(String key) {
        if (isEmptyString(key) || key.contains(" ")) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isEmptyString(String value) {
        if (value == null) {
            return true;
        }
        value = value.trim();
        return value.length() == 0;
    }

}
