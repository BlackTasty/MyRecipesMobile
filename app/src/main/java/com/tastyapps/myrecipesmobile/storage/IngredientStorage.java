package com.tastyapps.myrecipesmobile.storage;

import com.tastyapps.myrecipesmobile.core.recipes.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class IngredientStorage {
    private static final IngredientStorage instance = new IngredientStorage();

    private List<Ingredient> ingredients;

    public static IngredientStorage getInstance() {
        return instance;
    }
    
    private IngredientStorage() {
        ingredients = new ArrayList<>();
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void add(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    public void clear() {
        ingredients.clear();
    }

    public Ingredient get(int position) {
        return ingredients.get(position);
    }

    public Stream<Ingredient> stream() {
        return ingredients.stream();
    }
}
