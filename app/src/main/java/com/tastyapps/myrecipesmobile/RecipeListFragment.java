package com.tastyapps.myrecipesmobile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import com.tastyapps.myrecipesmobile.adapters.RecipeAdapter;
import com.tastyapps.myrecipesmobile.core.mobile.MqttClient;
import com.tastyapps.myrecipesmobile.core.recipes.Recipe;
import com.tastyapps.myrecipesmobile.storage.CategoryStorage;
import com.tastyapps.myrecipesmobile.storage.IngredientStorage;
import com.tastyapps.myrecipesmobile.storage.RecipeStorage;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeListFragment extends Fragment {

    private RecyclerView listRecipes;
    private RecipeAdapter recipeAdapter;
    private Toolbar toolbarRecipeView;
    private Button btnSearch;

    private Timer refreshTimer;
    private Button btnRefresh;
    private TextView txtLastRefresh;
    private boolean isRefreshing;
    private long dateOfLastRefresh;
    private Activity activity;

    private MqttClient mqttClient;

    public RecipeListFragment() {
        // Required empty public constructor
    }

    public static RecipeListFragment newInstance() {
        RecipeListFragment fragment = new RecipeListFragment();
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
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        initialize(root);

        refreshTimer = new Timer();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        activity = getActivity();
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer = new Timer();
        }
        onRefreshList();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onRefreshList() {
        setRefreshing(true);
        StringBuilder loadedRecipeChecksums = null;
        if (RecipeStorage.getInstance().size() > 0) {
            loadedRecipeChecksums = new StringBuilder();
            for (Recipe recipe : RecipeStorage.getInstance().getRecipes()) {
                loadedRecipeChecksums.append(loadedRecipeChecksums.toString().equals("") ? recipe.Checksum : ";" + recipe.Checksum);
            }
        }
        mqttClient.sendMessage("recipes", loadedRecipeChecksums != null ? loadedRecipeChecksums.toString() : null);

        Log.d("RecipeListFragment", "RecipeAdapter is set: " + (recipeAdapter != null));
        if (recipeAdapter != null) {
            recipeAdapter.notifyDataSetChanged();
        } else {
            recipeAdapter = new RecipeAdapter();
            listRecipes.setAdapter(recipeAdapter);
        }
    }

    public void initialize(View root) {
        btnRefresh = root.findViewById(R.id.btn_recipes_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRefreshList();
            }
        });
        txtLastRefresh = root.findViewById(R.id.txt_recipes_last_refresh);

        listRecipes = root.findViewById(R.id.list_recipes);
        listRecipes.setLayoutManager(new LinearLayoutManager(getActivity()));

        toolbarRecipeView = root.findViewById(R.id.toolbar_recipe_view);

        mqttClient = mqttClient.getInstance();
        Context context = this.getContext();

        mqttClient.setOnRecipeReceivedEventListener(recipe -> {
            Log.d("RecipeListFragment", "Received recipe: " + recipe.Name);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (recipeAdapter == null) {
                        recipeAdapter = new RecipeAdapter();
                        listRecipes.setAdapter(recipeAdapter);
                    }
                    recipeAdapter.notifyDataSetChanged();

                    //MqttClient.subscribeTopic("recipes/img/" + recipe.Guid);
                    //MqttClient.sendMessage("recipes/img", recipe.Guid);
                }
            });
        });

        mqttClient.setOnRecipeImageReceivedEventListener((imageBytes, recipeGuid) -> {
            Log.d("RecipeListFragment", "Received recipe image for GUID: " + recipeGuid);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //recipeAdapter.addImageForRecipe(imageBytes, recipeGuid);
                    recipeAdapter.notifyDataSetChanged();
                }
            });
        });

        mqttClient.setOnCategoriesReceivedEventListener(categories -> CategoryStorage.getInstance().setCategories(categories));
        mqttClient.setOnIngredientReceivedEventListener(ingredients -> IngredientStorage.getInstance().setIngredients(ingredients));
        mqttClient.setOnRecipeTransferFinishEventListener(() -> setRefreshing(false));

        mqttClient.subscribeTopic("recipes");
        mqttClient.subscribeTopic("recipes/finish");
        mqttClient.subscribeTopic("categories");
        mqttClient.subscribeTopic("ingredients");
    }

    private void setRefreshing(boolean isRefreshing) {
        btnRefresh.setEnabled(!isRefreshing);

        if (!isRefreshing) {
            Log.d("RecipeListFragment", "Refresh done, updating timer data");
            dateOfLastRefresh = Calendar.getInstance().getTimeInMillis();
            Log.d("RecipeListFragment", "RefreshTime (ms): " + dateOfLastRefresh);

            refreshTimer = new Timer();
            refreshTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    long secondsSinceRefresh = (Calendar.getInstance().getTimeInMillis() - dateOfLastRefresh) / 1000;

                    activity.runOnUiThread(() -> {
                        if (secondsSinceRefresh < 10) {
                            txtLastRefresh.setText("Zuletzt aktualisiert: jetzt");
                        } else if (secondsSinceRefresh < 60) {
                            txtLastRefresh.setText("Zuletzt aktualisiert: vor " + secondsSinceRefresh + " Sekunden");
                        } else {
                            int minutesSinceRefresh = (int)Math.floor(secondsSinceRefresh / 60);
                            if (minutesSinceRefresh > 1) {
                                txtLastRefresh.setText("Zuletzt aktualisiert: vor " + minutesSinceRefresh + " Minuten");
                            } else {
                                txtLastRefresh.setText("Zuletzt aktualisiert: vor 1 Minute");
                            }
                        }
                    });
                }
            }, 0, 10000);
        } else {
            Log.d("RecipeListFragment", "Refreshing list, cancelling timer");
            txtLastRefresh.setText("Zuletzt aktualisiert: Lädt...");
            refreshTimer.cancel();
        }
    }
}