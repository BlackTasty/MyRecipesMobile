package com.tastyapps.myrecipesmobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tastyapps.myrecipesmobile.R;
import com.tastyapps.myrecipesmobile.core.FilterObject;

import java.util.ArrayList;
import java.util.List;

public class IngredientFilterAdapter extends RecyclerView.Adapter<IngredientFilterHolder> {
    private List<FilterObject> ingredientFilters;

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public IngredientFilterHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.layout_chip_ingredientfilter, parent, false);
        return new IngredientFilterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull IngredientFilterHolder holder, int position) {
        final FilterObject ingredientFilter = ingredientFilters.get(position);

        holder.bindFilterIngredient(ingredientFilter);
    }

    @Override
    public int getItemCount() {
        return ingredientFilters != null ? ingredientFilters.size() : 0;
    }

    public List<FilterObject> getIngredientFilters() {
        return ingredientFilters;
    }

    public IngredientFilterAdapter() {
        this.ingredientFilters = new ArrayList<>();
    }

    public void add(FilterObject filterIngredient) {
        ingredientFilters.add(filterIngredient);
        notifyDataSetChanged();
    }

    public void remove(FilterObject filterIngredient) {
        ingredientFilters.remove(filterIngredient);
        notifyDataSetChanged();
    }
}
