package com.picsauditing.service.i18n;

import com.picsauditing.util.Strings;

public class TranslationKeyValidator {

    public boolean validateKey(String key) {
        if (Strings.isEmpty(key) || key.contains(" ")) {
            return false;
        } else {
            return true;
        }
    }

}
