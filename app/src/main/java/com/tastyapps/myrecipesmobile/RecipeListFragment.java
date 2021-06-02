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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.tastyapps.myrecipesmobile.adapters.IngredientFilterAdapter;
import com.tastyapps.myrecipesmobile.adapters.RecipeAdapter;
import com.tastyapps.myrecipesmobile.core.FilterObject;
import com.tastyapps.myrecipesmobile.core.events.OnCategoryItemClickedEventListener;
import com.tastyapps.myrecipesmobile.core.events.OnRemoveFilterIngredientClickedEventListener;
import com.tastyapps.myrecipesmobile.core.mobile.MqttClient;
import com.tastyapps.myrecipesmobile.core.recipes.Category;
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
    private RadioGroup groupFilterBy;
    private EditText inputSearch;
    private Spinner spinnerCategories;
    private Spinner spinnerIngredients;
    private RecyclerView listFilterIngredients;
    private IngredientFilterAdapter ingredientFilterAdapter;
    private RadioButton rbSearchCategories;

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
        mqttClient.sendMessage("categories", null);
        mqttClient.sendMessage("ingredients", null);

        Log.d("RecipeListFragment", "RecipeAdapter is set: " + (recipeAdapter != null));
        if (recipeAdapter != null) {
            recipeAdapter.notifyDataSetChanged();
        } else {
            recipeAdapter = new RecipeAdapter();
            listRecipes.setAdapter(recipeAdapter);
        }
    }

    public void initialize(View root) {
        Activity activity = this.getActivity();

        btnRefresh = root.findViewById(R.id.btn_recipes_refresh);
        btnRefresh.setOnClickListener(view -> onRefreshList());

        btnSearch = root.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)
                    root.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                int radioButtonId = groupFilterBy.getCheckedRadioButtonId();
                View activeRadioButton = groupFilterBy.findViewById(radioButtonId);

                switch (groupFilterBy.indexOfChild(activeRadioButton)) {
                    case 0: // Selection is "Search by recipe name"
                        recipeAdapter.filterRecipesByName(inputSearch.getText().toString());
                        break;
                    case 1: // Selection is "Search by ingredients"
                        recipeAdapter.filterRecipesByIngredient(ingredientFilterAdapter.getIngredientFilters());
                        break;
                    case 2: // Selection is "Search by category"
                        recipeAdapter.filterRecipesByCategory((FilterObject)spinnerCategories.getSelectedItem());
                        break;
                }
            }
        });
        groupFilterBy = root.findViewById(R.id.group_filter_by);
        groupFilterBy.setOnCheckedChangeListener((radioGroup, i) -> {
            int radioButtonId = radioGroup.getCheckedRadioButtonId();
            View activeRadioButton = radioGroup.findViewById(radioButtonId);

            int index = radioGroup.indexOfChild(activeRadioButton);

            inputSearch.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
            spinnerIngredients.setVisibility(index == 1 ? View.VISIBLE : View.GONE);
            listFilterIngredients.setVisibility(index == 1 ? View.VISIBLE : View.GONE);
            spinnerCategories.setVisibility(index == 2 ? View.VISIBLE : View.GONE);
        });

        rbSearchCategories = root.findViewById(R.id.rb_search_category);

        inputSearch = root.findViewById(R.id.input_recipeName);
        spinnerIngredients = root.findViewById(R.id.spinner_ingredients);
        spinnerIngredients.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                FilterObject selectedIngredient = (FilterObject)spinnerIngredients.getSelectedItem();

                if (!selectedIngredient.isDefault()) {
                    ingredientFilterAdapter.add(selectedIngredient);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerCategories = root.findViewById(R.id.spinner_categories);
        listFilterIngredients = root.findViewById(R.id.list_filter_ingredients);

        FlexboxLayoutManager flexLayoutManager = new FlexboxLayoutManager(getContext());
        flexLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        flexLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexLayoutManager.setAlignItems(AlignItems.FLEX_START);
        listFilterIngredients.setLayoutManager(flexLayoutManager);
        if (ingredientFilterAdapter == null) {
            ingredientFilterAdapter = new IngredientFilterAdapter();
            listFilterIngredients.setAdapter(ingredientFilterAdapter);
        } else {
            ingredientFilterAdapter.notifyDataSetChanged();
        }

        txtLastRefresh = root.findViewById(R.id.txt_recipes_last_refresh);

        listRecipes = root.findViewById(R.id.list_recipes);
        listRecipes.setLayoutManager(new LinearLayoutManager(getActivity()));

        toolbarRecipeView = root.findViewById(R.id.toolbar_recipe_view);

        mqttClient = MqttClient.getInstance();
        Context context = this.getContext();
        CategoryStorage.getInstance().initializeFilterList(context);
        spinnerCategories.setAdapter(CategoryStorage.getInstance().getFilterCategories());
        CategoryStorage.getInstance().setOnCategoryItemClickedEventListener(new OnCategoryItemClickedEventListener() {
            @Override
            public void onItemClick(Category category) {
                rbSearchCategories.setChecked(true);
                spinnerCategories.setSelection(CategoryStorage.getInstance().getFilterCategoryIndex(category));
            }
        });

        IngredientStorage.getInstance().initializeFilterList(context);
        spinnerIngredients.setAdapter(IngredientStorage.getInstance().getFilterIngredients());
        IngredientStorage.getInstance().setOnRemoveFilterIngredientClickedEventListener(filterIngredient -> ingredientFilterAdapter.remove(filterIngredient));

        mqttClient.setOnRecipeReceivedEventListener(recipe -> {
            Log.d("RecipeListFragment", "Received recipe: " + recipe.Name);

            getActivity().runOnUiThread(() -> {
                if (recipeAdapter == null) {
                    recipeAdapter = new RecipeAdapter();
                    listRecipes.setAdapter(recipeAdapter);
                }
                recipeAdapter.notifyDataSetChanged();
            });
        });

        mqttClient.setOnRecipeImageReceivedEventListener((imageBytes, recipeGuid) -> {
            Log.d("RecipeListFragment", "Received recipe image for GUID: " + recipeGuid);
            getActivity().runOnUiThread(() -> recipeAdapter.notifyDataSetChanged());
        });

        mqttClient.setOnCategoriesReceivedEventListener(categories -> {
            getActivity().runOnUiThread(() -> CategoryStorage.getInstance().setCategories(categories));
        });
        mqttClient.setOnIngredientReceivedEventListener(ingredients -> {
            getActivity().runOnUiThread(() -> IngredientStorage.getInstance().setIngredients(ingredients));
        });
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