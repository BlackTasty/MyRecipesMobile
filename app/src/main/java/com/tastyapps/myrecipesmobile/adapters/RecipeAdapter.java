package com.tastyapps.myrecipesmobile.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tastyapps.myrecipesmobile.R;
import com.tastyapps.myrecipesmobile.core.FilterObject;
import com.tastyapps.myrecipesmobile.core.recipes.Recipe;
import com.tastyapps.myrecipesmobile.core.recipes.RecipeImage;
import com.tastyapps.myrecipesmobile.storage.RecipeStorage;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeHolder> {
    private RecipeStorage recipeStorage;
    private Context context;

    public RecipeAdapter() {
        this.recipeStorage = RecipeStorage.getInstance();
    }

    public RecipeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.layout_recipe_listitem, parent, false);

        RecipeHolder holder = new RecipeHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecipeHolder holder, int position) {
        final Recipe recipe = recipeStorage.get(position);
        holder.bindRecipe(recipe);
    }

    @Override
    public int getItemCount() {
        return recipeStorage != null ? recipeStorage.size() : 0;
    }

    public List<Recipe> getRecipes() {
        return recipeStorage.getRecipes();
    }

    public void setRecipes(List<Recipe> recipeStorage) {
        this.recipeStorage.setRecipes(recipeStorage);
    }

    public void filterRecipesByName(String recipeName) {
        this.recipeStorage.filterRecipesByName(recipeName);
        notifyDataSetChanged();
    }

    public void filterRecipesByCategory(FilterObject categoryFilter) {
        this.recipeStorage.filterRecipesByCategory(categoryFilter);
        notifyDataSetChanged();
    }

    public void filterRecipesByIngredient(List<FilterObject> ingredientsFilter) {
        this.recipeStorage.filterRecipesByIngredient(ingredientsFilter);
        notifyDataSetChanged();
    }

    /*public void addImageForRecipe(byte[] imageBytes, String guid) {
        Recipe recipe = recipeStorage.stream()
                .filter(x -> x.Guid.equals(guid))
                .findFirst()
                .orElse(null);
        if (recipe != null) {
            Log.d("RecipeAdapter", "Adding image to recipe: " + recipe.Name);
            recipe.RecipeImage = new RecipeImage(imageBytes);
        }
    }*/

    public void clearRecipes() {
        recipeStorage.clear();
    }
}
