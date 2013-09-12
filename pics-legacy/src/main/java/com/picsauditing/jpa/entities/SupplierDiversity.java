package com.picsauditing.jpa.entities;

public enum SupplierDiversity implements Translatable, ReportEnum {
    SmallBusiness(2340),WomenOwned(2373),DisabledVeteranOwned(3543),UnionPersonnel(66),NonUnionPersonnel(77),
    AboriginalOwned(9672),AboriginalEmployees(9675);

    private int value;

    <E extends Enum<E>> SupplierDiversity(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String getI18nKey() {
        return "Filters.status." + name();
    }

    @Override
    public String getI18nKey(String property) {
        return getI18nKey() + "." + property;
    }
}