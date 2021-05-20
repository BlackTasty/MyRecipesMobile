package com.tastyapps.myrecipesmobile.core.events;

import com.tastyapps.myrecipesmobile.core.recipes.Category;
import com.tastyapps.myrecipesmobile.core.recipes.Ingredient;
import com.tastyapps.myrecipesmobile.core.recipes.Recipe;

import java.util.List;

public interface OnTopicReceivedEventListener {
    void onRecipeReceived(Recipe recipe);

    void onRecipeImageReceived(byte[] imageBytes, String recipeGuid);

    void onClearRecipes();

    void onCategoriesReceived(List<Category> categories);

    void onIngredientsReceived(List<Ingredient> ingredients);

    void onSeasonCalendarDataReceived(List<Ingredient> seasonIngredients, String topic);
}
