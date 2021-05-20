package com.tastyapps.myrecipesmobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tastyapps.myrecipesmobile.R;
import com.tastyapps.myrecipesmobile.core.recipes.Recipe;
import com.tastyapps.myrecipesmobile.core.recipes.RecipeIngredient;

import java.util.List;

public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientHolder> {
    private List<RecipeIngredient> recipeIngredients;

    public List<RecipeIngredient> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public RecipeIngredientAdapter(List<RecipeIngredient> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    @Override
    public RecipeIngredientHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.layout_ingredient_listitem, parent, false);
        return new RecipeIngredientHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeIngredientHolder holder, int position) {
        final RecipeIngredient recipeIngredient = recipeIngredients.get(position);

        holder.bindRecipeIngredient(recipeIngredient);
    }

    @Override
    public int getItemCount() {
        return recipeIngredients != null ? recipeIngredients.size() : 0;
    }

    public void add(RecipeIngredient recipeIngredient) {
        recipeIngredients.add(recipeIngredient);
    }

    public void clear() {
        recipeIngredients.clear();
    }
}
