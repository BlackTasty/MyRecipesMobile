package com.tastyapps.myrecipesmobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tastyapps.myrecipesmobile.R;

import java.util.List;

public class PreparationStepAdapter extends RecyclerView.Adapter<PreparationStepHolder> {
    List<String> preparationSteps;

    public PreparationStepAdapter(List<String> preparationSteps) {
        this.preparationSteps = preparationSteps;
    }

    @Override
    public PreparationStepHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.layout_preparationstep_listitem, parent, false);
        return new PreparationStepHolder(view);
    }

    @Override
    public void onBindViewHolder(PreparationStepHolder holder, int position) {
        final String preparationStep = preparationSteps.get(position);
        holder.setPreparationStep(preparationStep, position + 1);
    }

    @Override
    public int getItemCount() {
        return preparationSteps != null ? preparationSteps.size() : 0;
    }
}
