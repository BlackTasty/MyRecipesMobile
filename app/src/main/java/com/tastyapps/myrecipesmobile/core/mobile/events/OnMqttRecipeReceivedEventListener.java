package com.tastyapps.myrecipesmobile.core.mobile.events;

import com.tastyapps.myrecipesmobile.core.recipes.Recipe;

public interface OnMqttRecipeReceivedEventListener {
    void onRecipeReceived(Recipe recipe);
}
