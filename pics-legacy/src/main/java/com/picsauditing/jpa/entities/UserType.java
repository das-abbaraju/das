package com.picsauditing.jpa.entities;

/**
 * Created with IntelliJ IDEA.
 * User: MDo
 * Date: 12/23/13
 * Time: 1:40 PM
 * To change this template use File | Settings | File Templates.
 */
public enum UserType {
    SafetyHealthAndEnvironmental("Safety, Health & Environmental"),
    ProcurementContracts("Procurement/Contracts"),
    Operations("Operations"),
    Maintenance("Maintenance"),
    Engineering("Engineering"),
    TechnicalServices("Technical Services"),
    Risk("Risk"),
    Security("Security");

    private String name;

    UserType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getI18nKey() {
        return "UserType." + this.toString();
    }
}
