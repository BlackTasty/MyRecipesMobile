package com.tastyapps.myrecipesmobile.core.recipes;

import android.util.Log;

import com.tastyapps.myrecipesmobile.core.BaseData;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Recipe extends BaseData {
    public List<RecipeIngredient> Ingredients;
    public List<String> PreparationSteps;
    public List<Category> Categories;
    public Date LastAccessDate;

    public RecipeImage RecipeImage;
    public int Servings;

    public String Checksum;
    public String LastAccessDateRaw;


    public boolean isImageSet() {
        return RecipeImage != null && RecipeImage.getImage() != null;
    }

    public static Recipe fromJson(String json) {
        Recipe recipe = new Gson().fromJson(json, Recipe.class);
        try {
            recipe.LastAccessDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.GERMAN).parse(recipe.LastAccessDateRaw);
            Log.d("Recipe", "Last access date: " + recipe.LastAccessDate);
        } catch (ParseException ex) {
            Log.d("Recipe","Parsing date failed!");
            ex.printStackTrace();
        }
        return recipe;
    }

    public List<RecipeIngredient> cloneIngredients() {
        if (Ingredients == null) {
            return new ArrayList<>();
        }

        List<RecipeIngredient> clone = new ArrayList<>();

        for (RecipeIngredient recipeIngredient : Ingredients) {
            clone.add(recipeIngredient.clone());
        }

        return clone;
    }
}
