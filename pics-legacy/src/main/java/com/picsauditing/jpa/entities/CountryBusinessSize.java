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

    public void setSmallBusinessEmployeeCount(int smallBusinessEmployeeCount) {
        this.smallBusinessEmployeeCount = smallBusinessEmployeeCount;
    }
}
