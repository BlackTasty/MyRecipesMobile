package com.tastyapps.myrecipesmobile.storage;

import android.util.Log;

import com.tastyapps.myrecipesmobile.core.events.OnRecipeItemClickedEventListener;
import com.tastyapps.myrecipesmobile.core.recipes.Recipe;
import com.tastyapps.myrecipesmobile.core.recipes.RecipeImage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class RecipeStorage {
    private static final RecipeStorage instance = new RecipeStorage();

    private OnRecipeItemClickedEventListener onRecipeItemClickedEventListener;

    private List<Recipe> recipes;

    private RecipeStorage() {
        recipes = new ArrayList<>();
    }

    public static RecipeStorage getInstance() {
        return instance;
    }

    public void setOnRecipeItemClickedEventListener(OnRecipeItemClickedEventListener onRecipeItemClickedEventListener) {
        this.onRecipeItemClickedEventListener = onRecipeItemClickedEventListener;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public Recipe getRecipeByGuid(String guid) {
        return recipes.stream().filter(x -> x.Guid == guid).findFirst().orElse(null);
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public boolean add(Recipe recipe) {
        Recipe old = stream().filter(x -> x.Guid.equals(recipe.Guid)).findFirst().orElse(null);
        if (old != null) {
            int index = recipes.indexOf(old);
            recipes.remove(old);
            recipes.add(index, recipe);
        } else {
            recipes.add(recipe);
        }

        return old != null;
    }

    public void clear() {
        recipes.clear();
    }

    public Recipe get(int position) {
        return recipes.get(position);
    }

    public int size() {
        return recipes != null ? recipes.size() : 0;
    }

    public Stream<Recipe> stream() {
        return recipes.stream();
    }

    public void onRecipeSelect(Recipe recipe) {
        Log.d("RecipeStorage", "onRecipeSelect fired");
        if (onRecipeItemClickedEventListener != null) {
            Log.d("RecipeStorage", "Re-firing event...");
            onRecipeItemClickedEventListener.onItemClick(recipe);
        }
    }
}
