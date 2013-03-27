package com.picsauditing.selenium;

import com.picsauditing.access.ApiRequired;
import com.picsauditing.actions.PicsApiSupport;
import com.picsauditing.security.KeyCode;
import org.apache.struts2.interceptor.ParameterAware;
import org.json.simple.JSONObject;


public class SeleniumKey extends PicsApiSupport implements ParameterAware {
    private static final String UniqueKeyPrefix = "Selenium";
    protected String type;
    protected String namePart;
    protected KeyCode keyCode = new KeyCode(8);



    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }


    public String getNamePart() {
        return this.namePart;
    }

    public void setNamePart(String namePart) {
        this.namePart = namePart;
    }

    private String shortName() {
        return UniqueKeyPrefix + this.getType().substring(0,1).toUpperCase() + keyCode.getKey().toUpperCase();
    }
    private Object fullName() {
        return UniqueKeyPrefix + " " + this.getNamePart() + " " + this.getType() + " " + keyCode.getKey().toUpperCase();
    }

    @Override
    @ApiRequired
    // TODO When the permission is actually available on live... @RequiredPermission(value = OpPerms.SeleniumTest)
    public String execute() {
        keyCode.generateRandom();
        json = new JSONObject();
        json.put("shortName", this.shortName());       // SeleniumC1A2B3C4D
        json.put("fullName", this.fullName());         // Selenium Acme Contractor 1A2B3C4D
        return JSON;
    }

}
