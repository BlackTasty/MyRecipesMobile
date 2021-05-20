package com.tastyapps.myrecipesmobile.core.seasoncalendar;

import com.google.gson.Gson;
import com.tastyapps.myrecipesmobile.core.util.EnumUtils;

import java.util.Hashtable;
import java.util.Map;

public class Season {
    public SeasonMonth SeasonMonthBeginReal;

    public SeasonMonth SeasonMonthEndReal;
    public WareOriginType OriginTypeReal;
    public boolean WholeYear;

    public Map<SeasonMonth, WareOriginType> ActiveSeasonsReal;

    public int SeasonMonthBegin;
    public int SeasonMonthEnd;
    public int OriginType;

    private Map<Integer, Integer> ActiveSeasons;

    public static Season fromJson(String json) {
        Season season = new Gson().fromJson(json, Season.class);
        season.SeasonMonthBeginReal = EnumUtils.castIntToSeasonMonth(season.SeasonMonthBegin);
        season.SeasonMonthEndReal = EnumUtils.castIntToSeasonMonth(season.SeasonMonthEnd);
        season.OriginTypeReal = EnumUtils.castIntToWareOriginType(season.OriginType);
        season.ActiveSeasonsReal = castToRealActiveSeasons(season.ActiveSeasons);

        return season;
    }

    private static Map<SeasonMonth, WareOriginType> castToRealActiveSeasons(Map<Integer, Integer> rawDictionary) {
        Map<SeasonMonth, WareOriginType> realDictionary = new Hashtable<>();
        for (Map.Entry<Integer, Integer> entry : rawDictionary.entrySet()) {
            SeasonMonth seasonMonth = EnumUtils.castIntToSeasonMonth(entry.getKey());
            WareOriginType wareOriginType = EnumUtils.castIntToWareOriginType(entry.getValue());

            realDictionary.put(seasonMonth, wareOriginType);
        }

        return realDictionary;
    }
}
