package com.tastyapps.myrecipesmobile.core.mobile.events;

import com.tastyapps.myrecipesmobile.core.recipes.Ingredient;

import java.util.List;

public interface OnMqttSeasonCalendarDataReceivedEventListener {
    void onSeasonCalendarDataReceived(List<Ingredient> seasonIngredients, String topic);
}
