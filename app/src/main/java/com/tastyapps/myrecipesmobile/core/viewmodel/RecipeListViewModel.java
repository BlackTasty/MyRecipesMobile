package com.tastyapps.myrecipesmobile.core.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.databinding.library.baseAdapters.BR;

import com.tastyapps.myrecipesmobile.core.recipes.Category;
import com.tastyapps.myrecipesmobile.core.recipes.Ingredient;
import com.tastyapps.myrecipesmobile.core.recipes.Recipe;

import java.util.List;

public class RecipeListViewModel extends BaseObservable {
    private ObservableList<Category> mCategories = new ObservableArrayList<>();
    private ObservableList<Ingredient> mIngredients = new ObservableArrayList<>();
    private ObservableList<Recipe> mRecipes = new ObservableArrayList<>();

    @Bindable
    public ObservableList<Category> getCategories() {
        return mCategories;
    }

    public void setCategories(List<Category> categories) {
        this.mCategories.addAll(categories);
        notifyPropertyChanged(BR.categories);
    }

    @Bindable
    public ObservableList<Ingredient> getIngredients() {
        return mIngredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.mIngredients.addAll(ingredients);
        notifyPropertyChanged(BR.ingredients);
    }

    @Bindable
    public ObservableList<Recipe> getRecipes() {
        return mRecipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.mRecipes.addAll(recipes);
        notifyPropertyChanged(BR.recipes);
    }
}
