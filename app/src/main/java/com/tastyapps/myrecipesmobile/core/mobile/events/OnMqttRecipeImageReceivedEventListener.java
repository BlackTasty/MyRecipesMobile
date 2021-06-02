package com.tastyapps.myrecipesmobile.core.mobile.events;

public interface OnMqttRecipeImageReceivedEventListener {
    void onRecipeImageReceived(byte[] imageBytes, String recipeGuid);
}
