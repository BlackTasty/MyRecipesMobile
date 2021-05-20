package com.tastyapps.myrecipesmobile.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tastyapps.myrecipesmobile.R;
import com.tastyapps.myrecipesmobile.core.recipes.RecipeIngredient;
import com.tastyapps.myrecipesmobile.core.util.EnumUtils;
import com.tastyapps.myrecipesmobile.core.util.NumberUtils;

public class RecipeIngredientHolder extends RecyclerView.ViewHolder {
    RecipeIngredient ingredient;

    TextView txtIngredientName;
    TextView txtIngredientAmount;
    TextView txtIngredientAmountType;

    public RecipeIngredientHolder(View itemView) {
        super(itemView);

        txtIngredientAmount = itemView.findViewById(R.id.ingredient_amount);
        txtIngredientAmountType = itemView.findViewById(R.id.ingredient_amount_type);
        txtIngredientName = itemView.findViewById(R.id.ingredient_name);
    }

    public void bindRecipeIngredient(RecipeIngredient ingredient) {
        this.ingredient = ingredient;

        txtIngredientName.setText(ingredient.Ingredient.Name);
        txtIngredientAmount.setText(String.valueOf(NumberUtils.round(ingredient.Amount, 1)).replace(".0", ""));
        txtIngredientAmountType.setText(EnumUtils.getMeasurementTypeName(ingredient.MeasurementTypeReal));
    }
}
