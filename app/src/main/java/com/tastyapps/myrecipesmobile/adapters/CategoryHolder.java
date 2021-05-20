package com.tastyapps.myrecipesmobile.adapters;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.tastyapps.myrecipesmobile.R;
import com.tastyapps.myrecipesmobile.core.recipes.Category;

public class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Category category;

    private Chip chipTitle;

    public CategoryHolder(View itemView) {
        super(itemView);

        chipTitle = itemView.findViewById(R.id.card_title);
    }

    public void bindCategory(Category category) {
        this.category = category;

        chipTitle.setText(category.Name);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.card_title) {
            //TODO: Implement onclick for category chips
        }
    }
}
