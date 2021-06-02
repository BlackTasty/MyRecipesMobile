package com.tastyapps.myrecipesmobile.storage;

import android.util.Log;

import com.tastyapps.myrecipesmobile.core.FilterObject;
import com.tastyapps.myrecipesmobile.core.events.OnRecipeItemClickedEventListener;
import com.tastyapps.myrecipesmobile.core.recipes.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeStorage {
    private static final RecipeStorage instance = new RecipeStorage();

    private OnRecipeItemClickedEventListener onRecipeItemClickedEventListener;

    private List<Recipe> allRecipes;
    private List<Recipe> filteredRecipes;
    private boolean isFiltered;

    private RecipeStorage() {
        allRecipes = new ArrayList<>();
        filteredRecipes = new ArrayList<>();
    }

    public static RecipeStorage getInstance() {
        return instance;
    }

    public void setOnRecipeItemClickedEventListener(OnRecipeItemClickedEventListener onRecipeItemClickedEventListener) {
        this.onRecipeItemClickedEventListener = onRecipeItemClickedEventListener;
    }

    public List<Recipe> getRecipes() {
        return !isFiltered ? allRecipes : filteredRecipes;
    }

    public Recipe getRecipeByGuid(String guid) {
        return allRecipes.stream().filter(x -> x.Guid == guid).findFirst().orElse(null);
    }

    public void setRecipes(List<Recipe> recipes) {
        this.allRecipes = recipes;
    }

    public boolean add(Recipe recipe) {
        Recipe old = stream().filter(x -> x.Guid.equals(recipe.Guid)).findFirst().orElse(null);
        if (old != null) {
            int index = allRecipes.indexOf(old);
            allRecipes.remove(old);
            allRecipes.add(index, recipe);
        } else {
            allRecipes.add(recipe);
        }

        return old != null;
    }

    public void clear() {
        allRecipes.clear();
        filteredRecipes.clear();
    }

    public Recipe get(int position) {
        return !isFiltered ? allRecipes.get(position) : filteredRecipes.get(position);
    }

    public int size() {
        return !isFiltered ? allRecipes.size() : filteredRecipes.size();
    }

    public Stream<Recipe> stream() {
        return allRecipes.stream();
    }

    public void filterRecipesByName(String recipeName) {
        isFiltered = !recipeName.equals("");
        if (isFiltered) {
            filteredRecipes = stream()
                    .filter(x -> x.Name.toLowerCase().contains(recipeName.toLowerCase()))
                    .collect(Collectors.toList());
        } else {
            filteredRecipes.clear();
        }
    }

    public void filterRecipesByCategory(FilterObject categoryFilter) {
        isFiltered = !categoryFilter.isDefault();
        if (isFiltered) {
            filteredRecipes = stream()
                    .filter(x -> x.Categories.stream().anyMatch(y -> y.Name.equals(categoryFilter.getName())))
                    .collect(Collectors.toList());
        } else {
            filteredRecipes.clear();
        }
    }

    public void filterRecipesByIngredient(List<FilterObject> ingredientsFilter) {
        isFiltered = !ingredientsFilter.isEmpty();
        if (isFiltered) {
            for (Recipe recipe : allRecipes) {
                boolean matchesIngredientSearch = true;
                for (FilterObject filterIngredient : ingredientsFilter) {
                    if (!recipe.Ingredients.stream().anyMatch(x -> x.Ingredient.Name.equals(filterIngredient.getName()))) {
                        matchesIngredientSearch = false;
                        break;
                    }
                }

                if (matchesIngredientSearch) {
                    filteredRecipes.add(recipe);
                }
            }
        } else {
            filteredRecipes.clear();
        }
    }

    public void onRecipeSelect(Recipe recipe) {
        Log.d("RecipeStorage", "onRecipeSelect fired");
        if (onRecipeItemClickedEventListener != null) {
            Log.d("RecipeStorage", "Re-firing event...");
            onRecipeItemClickedEventListener.onItemClick(recipe);
        }
    }
}
