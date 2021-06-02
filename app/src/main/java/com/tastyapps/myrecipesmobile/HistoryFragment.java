package com.tastyapps.myrecipesmobile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tastyapps.myrecipesmobile.adapters.RecipeHistoryAdapter;

public class HistoryFragment extends Fragment {
    private RecyclerView listHistory;
    private RecipeHistoryAdapter recipeHistoryAdapter;

    public HistoryFragment() {
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        listHistory = root.findViewById(R.id.list_history);
        listHistory.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (recipeHistoryAdapter == null) {
            recipeHistoryAdapter = new RecipeHistoryAdapter();
            listHistory.setAdapter(recipeHistoryAdapter);
        } else {
            recipeHistoryAdapter.refresh();
        }


        return root;
    }
}