package com.tastyapps.myrecipesmobile.core.seasoncalendar;

public enum SeasonMonth {
    January(0),
    February(1),
    March(2),
    April(3),
    May(4),
    June(5),
    July(6),
    August(7),
    September(8),
    October(9),
    November(10),
    December(11);

    private int numVal;

    SeasonMonth(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
