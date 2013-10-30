package com.picsauditing.service.i18n;

import com.picsauditing.util.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationKeyValidator {
    private static Pattern pattern = Pattern.compile("^[a-zA-Z0-9.]+$");

    public boolean validateKey(String key) {
        if (Strings.isEmpty(key) || key.contains(" ") || containsInvalidCharacters(key)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean containsInvalidCharacters(String key) {
        Matcher matcher = pattern.matcher(key);
        return !matcher.matches();
    }

}
