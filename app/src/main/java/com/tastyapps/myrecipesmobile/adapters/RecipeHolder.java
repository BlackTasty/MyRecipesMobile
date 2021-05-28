package com.tastyapps.myrecipesmobile.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignContent;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.tastyapps.myrecipesmobile.R;
import com.tastyapps.myrecipesmobile.core.recipes.Recipe;
import com.tastyapps.myrecipesmobile.core.recipes.RecipeImage;
import com.tastyapps.myrecipesmobile.storage.RecipeStorage;

public class RecipeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Recipe recipe;

    private TextView mName;
    private ImageView mRecipeImage;
    private RecyclerView mCategories;

    private CategoryAdapter categoryAdapter;

    public RecipeHolder(View itemView) {
        super(itemView);

        itemView.setOnClickListener(this);

        mName = itemView.findViewById(R.id.recipe_item_name);
        mRecipeImage = itemView.findViewById(R.id.recipe_item_image);
        mCategories = itemView.findViewById(R.id.recipe_item_categories);


        FlexboxLayoutManager flexLayoutManager = new FlexboxLayoutManager(itemView.getContext());
        flexLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        flexLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexLayoutManager.setAlignItems(AlignItems.FLEX_START);
        mCategories.setLayoutManager(flexLayoutManager);
    }

    public void bindRecipe(Recipe recipe) {
        this.recipe = recipe;
        mName.setText(recipe.Name);

        if (recipe.RecipeImage != null && recipe.RecipeImage.getImage() != null) {
            mRecipeImage.setImageBitmap(recipe.RecipeImage.getImage());
        }

        if (categoryAdapter == null) {
            categoryAdapter = new CategoryAdapter(recipe.Categories);
            mCategories.setAdapter(categoryAdapter);
        } else {
            categoryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        RecipeStorage.getInstance().onRecipeSelect(recipe);
    }
}
