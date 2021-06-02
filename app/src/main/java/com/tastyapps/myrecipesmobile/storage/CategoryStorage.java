package com.tastyapps.myrecipesmobile.storage;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.tastyapps.myrecipesmobile.R;
import com.tastyapps.myrecipesmobile.core.FilterObject;
import com.tastyapps.myrecipesmobile.core.events.OnCategoryItemClickedEventListener;
import com.tastyapps.myrecipesmobile.core.recipes.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CategoryStorage {
    private static final CategoryStorage instance = new CategoryStorage();

    private OnCategoryItemClickedEventListener onCategoryItemClickedEventListener;

    private ArrayAdapter<FilterObject> filterCategories;
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

    public void initializeFilterList(Context appContext) {
        filterCategories = new ArrayAdapter<>(appContext, R.layout.support_simple_spinner_dropdown_item, new ArrayList<>());
    }

    public List<Category> getCategories() {
        return categories;
    }

    public ArrayAdapter<FilterObject> getFilterCategories() {
        return filterCategories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        filterCategories.clear();
        filterCategories.add(new FilterObject("", true));
        for (Category category : categories) {
            FilterObject filterCategory = new FilterObject(category);
            long recipesWithCategory = RecipeStorage.getInstance()
                    .stream()
                    .filter(x -> x.Categories.stream().anyMatch(y -> y.Name.equals(category.Name)))
                    .count();

            filterCategory.setCounted(Math.toIntExact(recipesWithCategory));
            if (filterCategory.getCounted() > 0) {
                filterCategories.add(filterCategory);
            }
        }
        filterCategories.notifyDataSetChanged();
    }

    public void add(Category category) {
        categories.add(category);
    }

    public void clear() {
        categories.clear();
        if (filterCategories != null) {
            filterCategories.clear();
        }
    }

    public Category get(int position) {
        return categories.get(position);
    }

    public Stream<Category> stream() {
        return categories.stream();
    }

    public int getFilterCategoryIndex(Category category) {
        for (int i = 0; i < filterCategories.getCount(); i++) {
            FilterObject current = filterCategories.getItem(i);

            if (!current.isDefault() && current.getName().equals(category.Name)) {
                return i;
            }
        }

        return 0;
    }

    public void onCategorySelect(Category category) {
        if (onCategoryItemClickedEventListener != null) {
            onCategoryItemClickedEventListener.onItemClick(category);
        }
    }
}
