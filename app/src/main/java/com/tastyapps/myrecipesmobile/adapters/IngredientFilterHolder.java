package com.tastyapps.myrecipesmobile.adapters;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.tastyapps.myrecipesmobile.R;
import com.tastyapps.myrecipesmobile.core.FilterObject;
import com.tastyapps.myrecipesmobile.storage.IngredientStorage;

public class IngredientFilterHolder extends RecyclerView.ViewHolder {
    private FilterObject filterIngredient;

    private Chip chipTitle;

    public IngredientFilterHolder(View itemView) {
        super(itemView);

        chipTitle = itemView.findViewById(R.id.chip_ingredient_name);
        chipTitle.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IngredientStorage.getInstance().onRemoveFilterIngredient(filterIngredient);
            }
        });
    }

    public void bindFilterIngredient(FilterObject filterIngredient) {
        this.filterIngredient = filterIngredient;

        chipTitle.setText(filterIngredient.getName());
    }
}
