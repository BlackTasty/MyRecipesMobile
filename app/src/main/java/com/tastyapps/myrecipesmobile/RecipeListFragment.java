package com.tastyapps.myrecipesmobile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tastyapps.myrecipesmobile.adapters.RecipeAdapter;
import com.tastyapps.myrecipesmobile.core.events.OnRecipeItemClickedEventListener;
import com.tastyapps.myrecipesmobile.core.events.OnTopicReceivedEventListener;
import com.tastyapps.myrecipesmobile.core.mobile.Client;
import com.tastyapps.myrecipesmobile.core.recipes.Category;
import com.tastyapps.myrecipesmobile.core.recipes.Ingredient;
import com.tastyapps.myrecipesmobile.core.recipes.Recipe;
import com.tastyapps.myrecipesmobile.storage.RecipeStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeListFragment extends Fragment {

    private RecyclerView listRecipes;
    private RecipeAdapter recipeAdapter;

    private Client client;

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
        onRefreshList();
        return root;
    }

    /*@Override
    public void onResume() {
        super.onResume();
        initialize();
    }*/

    public void onRefreshList() {
        //TODO: Implement call to topic "/recipes" and show recipes in list
        client.sendMessage("recipes", null);
    }

    public void initialize(View root) {
        listRecipes = root.findViewById(R.id.list_recipes);
        listRecipes.setLayoutManager(new LinearLayoutManager(getActivity()));

        client = Client.getInstance();

        client.setOnTopicReceivedEventListener(new OnTopicReceivedEventListener() {
            @Override
            public void onRecipeReceived(Recipe recipe) {
                Log.d("RecipeListFragment", "Received recipe: " + recipe.Name);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (recipeAdapter == null) {
                            ArrayList<Recipe> recipes = new ArrayList<>();
                            recipes.add(recipe);
                            recipeAdapter = new RecipeAdapter(recipes);
                            listRecipes.setAdapter(recipeAdapter);
                        } else {
                            recipeAdapter.addRecipe(recipe);
                        }
                        recipeAdapter.notifyDataSetChanged();
                        client.subscribeTopic("recipes/img/" + recipe.Guid);
                        client.sendMessage("recipes/img", recipe.Guid);
                    }
                });
            }

            @Override
            public void onRecipeImageReceived(byte[] imageBytes, String recipeGuid) {
                Log.d("RecipeListFragment", "Received recipe image for GUID: " + recipeGuid);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recipeAdapter.addImageForRecipe(imageBytes, recipeGuid);
                        recipeAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onClearRecipes() {
                recipeAdapter.clearRecipes();
            }

            @Override
            public void onCategoriesReceived(List<Category> categories) {

            }

            @Override
            public void onIngredientsReceived(List<Ingredient> ingredients) {

            }

            @Override
            public void onSeasonCalendarDataReceived(List<Ingredient> seasonIngredients, String topic) {

            }
        });

        client.subscribeTopic("recipes");
    }
}