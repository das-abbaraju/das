package com.picsauditing.i18n.model;

import java.util.Date;

public class TranslationLookupData {
    private String localeRequest;
    private String localeResponse;
    private String msgKey;
    private String environment;
    private String pageName;
    private Date requestDate;
    private boolean retrievedByWildcard = false;

    public String getMsgKey() {
        return msgKey;
    }

    public void setMsgKey(String msgKey) {
        this.msgKey = msgKey;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getLocaleRequest() {
        return localeRequest;
    }

    public void setLocaleRequest(String localeRequest) {
        this.localeRequest = localeRequest;
    }

    public String getLocaleResponse() {
        return localeResponse;
    }

    public void setLocaleResponse(String localeResponse) {
        this.localeResponse = localeResponse;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public boolean isRetrievedByWildcard() {
        return retrievedByWildcard;
    }

    public void setRetrievedByWildcard(boolean retrievedByWildcard) {
        this.retrievedByWildcard = retrievedByWildcard;
    }
}
