package com.tastyapps.myrecipesmobile.core;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class FilterObject {
    private String name;
    private int counted;
    private BaseData data;

    public String getName() {
        return name;
    }

    public int getCounted() {
        return counted;
    }

    public BaseData getData() {
        return data;
    }

    public FilterObject(String name) {
        this.name = name;
        counted = 1;
    }

    public FilterObject(BaseData data) {
        this(data.Name);
        this.data = data;
    }

    @Override
    public @NotNull String toString() {
        return String.format(Locale.GERMAN, "%s (%d Rezepte)", name, counted);
    }
}
