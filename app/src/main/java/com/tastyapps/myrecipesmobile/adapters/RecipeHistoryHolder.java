package com.tastyapps.myrecipesmobile.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.tastyapps.myrecipesmobile.R;
import com.tastyapps.myrecipesmobile.core.recipes.Recipe;
import com.tastyapps.myrecipesmobile.storage.RecipeStorage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class RecipeHistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Recipe recipe;

    private TextView mName;
    private TextView mLastAccess;

    private CategoryAdapter categoryAdapter;

    public RecipeHistoryHolder(View itemView) {
        super(itemView);

        itemView.setOnClickListener(this);

        mName = itemView.findViewById(R.id.history_item_name);
        mLastAccess = itemView.findViewById(R.id.history_last_access);
    }

    public void bindRecipe(Recipe recipe) {
        this.recipe = recipe;
        mName.setText(recipe.Name);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN);
        mLastAccess.setText(dateFormat.format(recipe.LastAccessDate));
    }

    @Override
    public void onClick(View view) {
        RecipeStorage.getInstance().onRecipeSelect(recipe);
    }
}
