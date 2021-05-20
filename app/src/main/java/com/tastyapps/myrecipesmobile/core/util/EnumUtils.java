package com.tastyapps.myrecipesmobile.core.util;

import com.tastyapps.myrecipesmobile.core.recipes.IngredientCategory;
import com.tastyapps.myrecipesmobile.core.recipes.MeasurementType;
import com.tastyapps.myrecipesmobile.core.seasoncalendar.SeasonMonth;
import com.tastyapps.myrecipesmobile.core.seasoncalendar.WareOriginType;

public class EnumUtils {
    public static IngredientCategory castIntToIngredientCategory(int val) {
        //return IngredientCategory.values()[val];

        switch (val) {
            case -1:
                return IngredientCategory.All;
            case 1:
                return IngredientCategory.Vegetables;
            case 2:
                return IngredientCategory.Fruits;
            case 3:
                return IngredientCategory.Nuts;
            case 5:
                return IngredientCategory.Poultry;
            case 6:
                return IngredientCategory.Beef;
            case 7:
                return IngredientCategory.Pork;
            case 8:
                return IngredientCategory.Venison;
            case 9:
                return IngredientCategory.Lamb;
            case 10:
                return IngredientCategory.Meat;
            case 11:
                return IngredientCategory.Sausages;
            case 12:
                return IngredientCategory.Fish;
            case 13:
                return IngredientCategory.Pasta;
            case 14:
                return IngredientCategory.Rice;
            case 15:
                return IngredientCategory.Dairy;
            case 21:
                return IngredientCategory.Herbs;
            case 22:
                return IngredientCategory.Spices;
            case 23:
                return IngredientCategory.Seeds;
            case 30:
                return IngredientCategory.Liquid;
            case 31:
                return IngredientCategory.Alcohol;
            case 35:
                return IngredientCategory.Sauces;
            case 36:
                return IngredientCategory.Creme;
            case 40:
                return IngredientCategory.Bread;
            case 41:
                return IngredientCategory.Flour;
            case 42:
                return IngredientCategory.Powder;
            case 43:
                return IngredientCategory.Oil;
            case 200:
                return IngredientCategory.Other;
            default:
                return IngredientCategory.Unset;
        }
    }

    public static MeasurementType castIntToMeasurementType(int val) {
        return MeasurementType.values()[val];

        /*switch (val) {
            case 1:
                return MeasurementType.Milliliters;
            case 2:
                return MeasurementType.Gram;
            case 3:
                return MeasurementType.Pinch;
            case 4:
                return MeasurementType.Tablespoon;
            case 5:
                return MeasurementType.Teaspoon;
            case 6:
                return MeasurementType.Piece;
            default:
                return MeasurementType.Package;
        }*/
    }

    public static SeasonMonth castIntToSeasonMonth(int val) {
        return SeasonMonth.values()[val];

        /*switch (val) {
            case 1:
                return SeasonMonth.February;
            case 2:
                return SeasonMonth.March;
            case 3:
                return SeasonMonth.April;
            case 4:
                return SeasonMonth.May;
            case 5:
                return SeasonMonth.June;
            case 6:
                return SeasonMonth.July;
            case 7:
                return SeasonMonth.August;
            case 8:
                return SeasonMonth.September;
            case 9:
                return SeasonMonth.October;
            case 10:
                return SeasonMonth.November;
            case 11:
                return SeasonMonth.December;
            default:
                return SeasonMonth.January;
        }*/
    }

    public static WareOriginType castIntToWareOriginType(int val) {
        return WareOriginType.values()[val];

        /*switch (val) {
            case 1:
                return WareOriginType.Warehouse;
            case 2:
                return WareOriginType.Fresh;
            default:
                return WareOriginType.Unset;
        }*/
    }

    public static String getMeasurementTypeName(MeasurementType measurementType) {
        switch (measurementType) {
            case Gram:
                return "g";
            case Piece:
                return "Stk";
            case Pinch:
                return "Prise";
            case Package:
                return "Pk";
            case Teaspoon:
                return "TL";
            case Tablespoon:
                return "EL";
            case Milliliters:
                return "ml";
            default:
                return "-";
        }
    }

    public static String getSeasonMonthName(SeasonMonth seasonMonth) {
        switch (seasonMonth) {
            case January:
                return "Jänner";
            case February:
                return "Februar";
            case March:
                return "März";
            case April:
                return "April";
            case May:
                return "Mai";
            case June:
                return "Juni";
            case July:
                return "Juli";
            case August:
                return "August";
            case September:
                return "September";
            case October:
                return "Oktober";
            case November:
                return "November";
            case December:
                return "Dezember";
            default:
                return "-";
        }
    }

    public static String getWareOriginTypeName(WareOriginType wareOriginType) {
        switch (wareOriginType) {
            case Fresh:
                return "Erntezeit";
            case Warehouse:
                return "Lagerware";
            default:
                return "Ungesetzt";
        }
    }
}
