package com.tastyapps.myrecipesmobile.core.recipes;

import com.tastyapps.myrecipesmobile.core.BaseData;
import com.google.gson.Gson;

public class Category extends BaseData {

    public static Category fromJson(String json) {
        return new Gson().fromJson(json, Category.class);
    }
}
