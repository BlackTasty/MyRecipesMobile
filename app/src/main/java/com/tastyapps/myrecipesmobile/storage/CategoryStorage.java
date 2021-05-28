package com.tastyapps.myrecipesmobile.storage;

import android.util.Log;

import com.tastyapps.myrecipesmobile.core.events.OnCategoryItemClickedEventListener;
import com.tastyapps.myrecipesmobile.core.recipes.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CategoryStorage {
    private static final CategoryStorage instance = new CategoryStorage();

    private OnCategoryItemClickedEventListener onCategoryItemClickedEventListener;

    private List<Category> categories;

    public static CategoryStorage getInstance() {
        return instance;
    }

    private CategoryStorage() {
        categories = new ArrayList<>();
    }

    public void setOnCategoryItemClickedEventListener(OnCategoryItemClickedEventListener onCategoryItemClickedEventListener) {
        this.onCategoryItemClickedEventListener = onCategoryItemClickedEventListener;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void add(Category category) {
        categories.add(category);
    }

    public void clear() {
        categories.clear();
    }

    public Category get(int position) {
        return categories.get(position);
    }

    public Stream<Category> stream() {
        return categories.stream();
    }

    public void onCategorySelect(Category category) {
        if (onCategoryItemClickedEventListener != null) {
            onCategoryItemClickedEventListener.onItemClick(category);
        }
    }
}
