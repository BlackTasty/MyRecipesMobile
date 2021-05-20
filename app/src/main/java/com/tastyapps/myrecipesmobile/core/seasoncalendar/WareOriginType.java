package com.tastyapps.myrecipesmobile.core.seasoncalendar;

public enum WareOriginType {
    Unset(0),
    Warehouse(1),
    Fresh(2);

    private int numVal;

    WareOriginType(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
