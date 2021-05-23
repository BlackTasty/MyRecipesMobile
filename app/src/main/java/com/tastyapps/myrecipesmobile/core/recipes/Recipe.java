package com.tastyapps.myrecipesmobile.core.recipes;

import com.tastyapps.myrecipesmobile.core.BaseData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Recipe extends BaseData {
    public List<RecipeIngredient> Ingredients;
    public List<String> PreparationSteps;
    public List<Category> Categories;

    public RecipeImage RecipeImage;
    public int Servings;

    public boolean isImageSet() {
        return RecipeImage != null && RecipeImage.Image != null;
    }

    public static Recipe fromJson(String json) {
        Recipe recipe = new Gson().fromJson(json, Recipe.class);
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
