package com.picsauditing.jpa.entities;

public enum CountryBusinessSize {

    US(10),
    UK(5),
    CA(-1),
    GB(-1),
    OTHERS(-1);

    private int smallBusinessEmployeeCount;

    private CountryBusinessSize(int smallBusinessEmployeeCount) {
        this.smallBusinessEmployeeCount = smallBusinessEmployeeCount;
    }

    public int getSmallBusinessEmployeeCount() {
        return smallBusinessEmployeeCount;
    }

    public static boolean isSmallBusiness(String isoCode, int numberOfEmployees) {
        if (isoCode == null) {
            throw new IllegalArgumentException("isoCode can't be null!");
        }
        if (numberOfEmployees <= 0) {
            throw new IllegalArgumentException("numberOfEmployees must be > 0!");
        }
        CountryBusinessSize countryBusinessSize = CountryBusinessSize.valueOf(isoCode);
        return numberOfEmployees <= countryBusinessSize.getSmallBusinessEmployeeCount();
    }
}
