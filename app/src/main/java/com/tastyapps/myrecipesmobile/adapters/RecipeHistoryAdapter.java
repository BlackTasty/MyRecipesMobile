package com.tastyapps.myrecipesmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tastyapps.myrecipesmobile.R;
import com.tastyapps.myrecipesmobile.core.FilterObject;
import com.tastyapps.myrecipesmobile.core.recipes.Recipe;
import com.tastyapps.myrecipesmobile.storage.RecipeStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeHistoryAdapter extends RecyclerView.Adapter<RecipeHistoryHolder> {
    private List<Recipe> history;
    private Context context;

    public RecipeHistoryAdapter() {
        refreshInternal();
    }

    public RecipeHistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.layout_history_listitem, parent, false);

        RecipeHistoryHolder holder = new RecipeHistoryHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecipeHistoryHolder holder, int position) {
        final Recipe recipe = history.get(position);
        holder.bindRecipe(recipe);
    }

    @Override
    public int getItemCount() {
        return history != null ? history.size() : 0;
    }

    public List<Recipe> getRecipes() {
        return history;
    }

    public void setRecipes(List<Recipe> history) {
        this.history = history;
    }

    public void refresh() {
        refreshInternal();
        notifyDataSetChanged();
    }

    private void refreshInternal() {
        history = RecipeStorage.getInstance().stream()
                .sorted((x, y) -> y.LastAccessDate.compareTo(x.LastAccessDate))
                .limit(10)
                .collect(Collectors.toList());
    }
}
