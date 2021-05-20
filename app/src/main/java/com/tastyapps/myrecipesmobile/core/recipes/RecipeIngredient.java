package com.tastyapps.myrecipesmobile.core.recipes;

import com.google.gson.Gson;
import com.tastyapps.myrecipesmobile.core.util.EnumUtils;
import com.tastyapps.myrecipesmobile.core.util.NumberUtils;

public class RecipeIngredient {
    public Ingredient Ingredient;
    public double Amount;
    public MeasurementType MeasurementTypeReal;

    public int MeasurementType;

    public RecipeIngredient(Ingredient ingredient, double amount, MeasurementType measurementType) {
        Ingredient = ingredient;
        Amount = amount;
        MeasurementTypeReal = measurementType;
    }

    private RecipeIngredient(RecipeIngredient original) {
        Ingredient = original.Ingredient;
        Amount = original.Amount;
        MeasurementTypeReal = original.MeasurementTypeReal;
    }

    public static RecipeIngredient fromJson(String json) {
        RecipeIngredient recipeIngredient = new Gson().fromJson(json, RecipeIngredient.class);
        recipeIngredient.MeasurementTypeReal = EnumUtils.castIntToMeasurementType(recipeIngredient.MeasurementType);
        return recipeIngredient;
    }

    public RecipeIngredient fromServingRatio(double baseServings, double desiredServings) {
        final double ratio = desiredServings / baseServings;

        return new RecipeIngredient(Ingredient, NumberUtils.round(Amount * ratio, 2), MeasurementTypeReal);
    }

    public RecipeIngredient clone() {
        return new RecipeIngredient(this);
    }
}
