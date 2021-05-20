package com.tastyapps.myrecipesmobile.core.events;

import com.tastyapps.myrecipesmobile.core.recipes.Recipe;

public interface OnRecipeItemClickedEventListener {
    void onItemClick(Recipe recipe);
}
