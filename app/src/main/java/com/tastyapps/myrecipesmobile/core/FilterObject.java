package com.tastyapps.myrecipesmobile.core;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class FilterObject {
    private String name;
    private int counted;
    private BaseData data;
    private boolean isDefault;

    public String getName() {
        return name;
    }

    public int getCounted() {
        return counted;
    }

    public void setCounted(int counted) {
        this.counted = counted;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public BaseData getData() {
        return data;
    }

    public FilterObject(String name, boolean isDefault) {
        this.name = name;
        counted = 0;
        this.isDefault = isDefault;
    }

    public FilterObject(BaseData data) {
        this(data.Name, false);
        this.data = data;
    }

    @Override
    public @NotNull String toString() {
        return !isDefault ? String.format(Locale.GERMAN, "%s (%d Rezepte)", name, counted) : name;
    }
}
