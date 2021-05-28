package com.tastyapps.myrecipesmobile;

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
import android.widget.Toolbar;

import com.tastyapps.myrecipesmobile.adapters.RecipeAdapter;
import com.tastyapps.myrecipesmobile.core.events.OnTopicReceivedEventListener;
import com.tastyapps.myrecipesmobile.core.mobile.MqttClient;
import com.tastyapps.myrecipesmobile.core.recipes.Category;
import com.tastyapps.myrecipesmobile.core.recipes.Ingredient;
import com.tastyapps.myrecipesmobile.core.recipes.Recipe;
import com.tastyapps.myrecipesmobile.storage.CategoryStorage;
import com.tastyapps.myrecipesmobile.storage.IngredientStorage;
import com.tastyapps.myrecipesmobile.storage.RecipeStorage;

import java.util.List;

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

    private MqttClient MQTTClient;

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
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefreshList();
    }

    public void onRefreshList() {
        StringBuilder loadedRecipeChecksums = null;
        if (RecipeStorage.getInstance().size() > 0) {
            loadedRecipeChecksums = new StringBuilder();
            for (Recipe recipe : RecipeStorage.getInstance().getRecipes()) {
                loadedRecipeChecksums.append(loadedRecipeChecksums.toString().equals("") ? recipe.Checksum : ";" + recipe.Checksum);
            }
        }
        MQTTClient.sendMessage("recipes", loadedRecipeChecksums != null ? loadedRecipeChecksums.toString() : null);

        Log.d("RecipeListFragment", "RecipeAdapter is set: " + (recipeAdapter != null));
        if (recipeAdapter != null) {
            recipeAdapter.notifyDataSetChanged();
        } else {
            recipeAdapter = new RecipeAdapter();
            listRecipes.setAdapter(recipeAdapter);
        }
    }

    public void initialize(View root) {
        listRecipes = root.findViewById(R.id.list_recipes);
        listRecipes.setLayoutManager(new LinearLayoutManager(getActivity()));

        toolbarRecipeView = root.findViewById(R.id.toolbar_recipe_view);

        MQTTClient = MQTTClient.getInstance();
        Context context = this.getContext();

        MQTTClient.setOnTopicReceivedEventListener(new OnTopicReceivedEventListener() {
            @Override
            public void onRecipeReceived(Recipe recipe) {
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
            }

            @Override
            public void onRecipeImageReceived(byte[] imageBytes, String recipeGuid) {
                Log.d("RecipeListFragment", "Received recipe image for GUID: " + recipeGuid);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //recipeAdapter.addImageForRecipe(imageBytes, recipeGuid);
                        recipeAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onClearRecipes() {
                //recipeAdapter.clearRecipes();
            }

            @Override
            public void onCategoriesReceived(List<Category> categories) {
                CategoryStorage.getInstance().setCategories(categories);
            }

            @Override
            public void onIngredientsReceived(List<Ingredient> ingredients) {
                IngredientStorage.getInstance().setIngredients(ingredients);
            }

            @Override
            public void onSeasonCalendarDataReceived(List<Ingredient> seasonIngredients, String topic) {

            }
        });

        MQTTClient.subscribeTopic("recipes");
        MQTTClient.subscribeTopic("categories");
        MQTTClient.subscribeTopic("ingredients");
    }
}