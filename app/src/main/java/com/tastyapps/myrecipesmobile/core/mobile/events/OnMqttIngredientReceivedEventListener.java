package com.tastyapps.myrecipesmobile.core.mobile.events;

import com.tastyapps.myrecipesmobile.core.recipes.Ingredient;

import java.util.List;

public interface OnMqttIngredientReceivedEventListener {
    void onIngredientsReceived(List<Ingredient> ingredients);
}
