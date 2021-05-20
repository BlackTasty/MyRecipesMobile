package com.tastyapps.myrecipesmobile.core.recipes;

import com.tastyapps.myrecipesmobile.core.BaseData;
import com.tastyapps.myrecipesmobile.core.seasoncalendar.Season;
import com.google.gson.Gson;
import com.tastyapps.myrecipesmobile.core.util.EnumUtils;

import java.util.List;

public class Ingredient extends BaseData {
    public String ProductLink;
    public IngredientCategory IngredientCategoryReal;
    public MeasurementType MeasurementTypeReal;
    public List<Season> Seasons;

    public int IngredientCategory;
    public int MeasurementType;

    public static Ingredient fromJson(String json) {
        Ingredient ingredient = new Gson().fromJson(json, Ingredient.class);
        ingredient.IngredientCategoryReal = EnumUtils.castIntToIngredientCategory(ingredient.IngredientCategory);
        ingredient.MeasurementTypeReal = EnumUtils.castIntToMeasurementType(ingredient.MeasurementType);
        return ingredient;
    }
}
