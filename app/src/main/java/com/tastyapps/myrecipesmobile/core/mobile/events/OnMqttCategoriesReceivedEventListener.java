package com.tastyapps.myrecipesmobile.core.mobile.events;

import com.tastyapps.myrecipesmobile.core.recipes.Category;

import java.util.List;

public interface OnMqttCategoriesReceivedEventListener {
    void onCategoriesReceived(List<Category> categories);
}
