package com.tastyapps.myrecipesmobile.core.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Observable;

import com.tastyapps.myrecipesmobile.core.recipes.Recipe;

public class RecipeViewViewModel extends BaseObservable {
    private Recipe mRecipe;

    public Recipe getRecipe() {
        return mRecipe;
    }

    public void setRecipe(Recipe recipe) {
        this.mRecipe = recipe;
    }
}
