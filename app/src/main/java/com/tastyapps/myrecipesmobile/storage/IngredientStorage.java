package com.tastyapps.myrecipesmobile.storage;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.tastyapps.myrecipesmobile.R;
import com.tastyapps.myrecipesmobile.core.FilterObject;
import com.tastyapps.myrecipesmobile.core.events.OnRemoveFilterIngredientClickedEventListener;
import com.tastyapps.myrecipesmobile.core.recipes.Category;
import com.tastyapps.myrecipesmobile.core.recipes.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class IngredientStorage {
    private static final IngredientStorage instance = new IngredientStorage();

    private OnRemoveFilterIngredientClickedEventListener onRemoveFilterIngredientClickedEventListener;

    private List<Ingredient> ingredients;
    private ArrayAdapter<FilterObject> filterIngredients;

    public static IngredientStorage getInstance() {
        return instance;
    }
    
    private IngredientStorage() {
        ingredients = new ArrayList<>();
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setOnRemoveFilterIngredientClickedEventListener(OnRemoveFilterIngredientClickedEventListener onRemoveFilterIngredientClickedEventListener) {
        this.onRemoveFilterIngredientClickedEventListener = onRemoveFilterIngredientClickedEventListener;
    }

    public void initializeFilterList(Context appContext) {
        filterIngredients = new ArrayAdapter<>(appContext, R.layout.support_simple_spinner_dropdown_item, new ArrayList<>());
    }

    public ArrayAdapter<FilterObject> getFilterIngredients() {
        return filterIngredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        filterIngredients.clear();
        filterIngredients.add(new FilterObject("", true));
        for (Ingredient ingredient : ingredients) {
            FilterObject filterIngredient = new FilterObject(ingredient);
            long recipesWithIngredient = RecipeStorage.getInstance()
                    .stream()
                    .filter(x -> x.Ingredients.stream().anyMatch(y -> y.Ingredient.Name.equals(ingredient.Name)))
                    .count();

            filterIngredient.setCounted(Math.toIntExact(recipesWithIngredient));

            if (filterIngredient.getCounted() > 0) {
                filterIngredients.add(filterIngredient);
            }
        }
        filterIngredients.notifyDataSetChanged();
    }

    public void add(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    public void clear() {
        ingredients.clear();
        if (filterIngredients != null) {
            filterIngredients.clear();
        }
    }

    public Ingredient get(int position) {
        return ingredients.get(position);
    }

    public Stream<Ingredient> stream() {
        return ingredients.stream();
    }

    public void onRemoveFilterIngredient(FilterObject filterIngredient) {
        if (onRemoveFilterIngredientClickedEventListener != null) {
            onRemoveFilterIngredientClickedEventListener.onRemoveFilterIngredient(filterIngredient);
        }
    }
}
