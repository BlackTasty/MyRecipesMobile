package com.tastyapps.myrecipesmobile.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tastyapps.myrecipesmobile.R;

public class PreparationStepHolder extends RecyclerView.ViewHolder {
    private TextView mStepNumber;
    private TextView mStepDescription;

    public PreparationStepHolder(View itemView) {
        super(itemView);

        mStepNumber = itemView.findViewById(R.id.txt_stepNumber);
        mStepDescription = itemView.findViewById(R.id.txt_stepDescription);
    }

    public void setPreparationStep(String description, int step) {
        mStepDescription.setText(description);
        String stepText = String.valueOf(step) + ".";
        mStepNumber.setText(stepText);
    }
}
