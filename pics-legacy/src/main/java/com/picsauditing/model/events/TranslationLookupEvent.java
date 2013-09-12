package com.picsauditing.model.events;

import org.springframework.context.ApplicationEvent;

public class TranslationLookupEvent extends ApplicationEvent {

    public TranslationLookupEvent(Object source) {
        super(source);
    }

    public String toString() {
        return "TranslationLookupEvent";
    }
}
