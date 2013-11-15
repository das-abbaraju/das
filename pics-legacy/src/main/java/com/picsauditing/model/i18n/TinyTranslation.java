package com.picsauditing.model.i18n;

import edu.emory.mathcs.backport.java.util.Collections;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TinyTranslation {
    public String text;
    public Set<String> usedOnPages = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void addToUsedOnPages(Collection<String> pagesUsedOn) {
        usedOnPages.addAll(pagesUsedOn);
    }
}
