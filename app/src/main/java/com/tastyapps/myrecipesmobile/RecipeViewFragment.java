package com.tastyapps.myrecipesmobile;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.tastyapps.myrecipesmobile.adapters.PreparationStepAdapter;
import com.tastyapps.myrecipesmobile.adapters.RecipeIngredientAdapter;
import com.tastyapps.myrecipesmobile.core.ScrollLockedLinearLayoutManager;
import com.tastyapps.myrecipesmobile.core.recipes.Ingredient;
import com.tastyapps.myrecipesmobile.core.recipes.Recipe;
import com.tastyapps.myrecipesmobile.core.recipes.RecipeIngredient;
import com.tastyapps.myrecipesmobile.core.util.EnumUtils;
import com.tastyapps.myrecipesmobile.core.util.NumberUtils;
import com.tastyapps.myrecipesmobile.storage.RecipeStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeViewFragment extends Fragment implements ObservableScrollViewCallbacks {
    private TextWatcher textWatcherInputServings;
    private TextWatcher textWatcherInputHoverServings;

    private static final String ARG_GUID = "guid";

    private WindowManager windowManager;
    private boolean isInitialized;

    private Recipe recipe;
    private List<RecipeIngredient> originalRecipeIngredients;
    private int desiredServings;

    private boolean hoverIngredientListVisible;
    private boolean ingredientListVisible;

    private TextView txtName;
    private EditText inputServings;
    private EditText inputHoverServings;
    private ImageView imageRecipe;
    private RecyclerView listIngredients;
    private RecyclerView listHoverIngredients;
    private RecyclerView listSteps;
    private ObservableScrollView scroll;
    private Button toggleIngredientList;
    private TextView txtPreparationStepsTitle;
    private Button btnIncreaseServings;
    private Button btnDecreaseServings;

    private CardView containerIngredients;
    private CardView containerHoverIngredients;
    private LinearLayout containerSubIngredients;

    private RecipeIngredientAdapter recipeIngredientAdapter;
    private PreparationStepAdapter preparationStepAdapter;

    private Toolbar toolbar;

    private boolean servingsValueSet;

    public RecipeViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param guid Recipe guid.
     * @return A new instance of fragment RecipeViewFragment.
     */
    public static RecipeViewFragment newInstance(String guid) {
        RecipeViewFragment fragment = new RecipeViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GUID, guid);
        fragment.setArguments(args);
        return fragment;
    }

    public void releaseEventListeners() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            final String guid = getArguments().getString(ARG_GUID);
            recipe = RecipeStorage.getInstance()
                    .stream()
                    .filter(x -> x.Guid.equals(guid))
                    .findFirst()
                    .orElse(null);
            originalRecipeIngredients = recipe.cloneIngredients();
            desiredServings = recipe.Servings;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe_view, container, false);

        //TODO: Bind recipe name to toolbar title
        //txtName = view.findViewById(R.id.)
        inputServings = view.findViewById(R.id.input_servings);
        textWatcherInputServings = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateServingsFields(inputServings, inputHoverServings);
            }
        };
        inputServings.addTextChangedListener(textWatcherInputServings);

        inputHoverServings = view.findViewById(R.id.input_hover_servings);
        textWatcherInputHoverServings = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateServingsFields(inputHoverServings, inputServings);
            }
        };
        inputHoverServings.addTextChangedListener(textWatcherInputHoverServings);
        imageRecipe = view.findViewById(R.id.recipe_image);
        listIngredients = view.findViewById(R.id.list_ingredients);
        listHoverIngredients = view.findViewById(R.id.list_hover_ingredients);
        listSteps = view.findViewById(R.id.list_steps);
        scroll = view.findViewById(R.id.scroll_recipe);
        toggleIngredientList = view.findViewById(R.id.toggle_list_ingredient);
        toggleIngredientList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onToggleIngredientList();
            }
        });
        txtPreparationStepsTitle = view.findViewById(R.id.txt_preparationsteps_title);

        containerIngredients = view.findViewById(R.id.container_ingredients);
        containerHoverIngredients = view.findViewById(R.id.container_hover_ingredients);
        containerSubIngredients = view.findViewById(R.id.container_sub_ingredients);

        btnDecreaseServings = view.findViewById(R.id.btn_decrease_servings);
        btnDecreaseServings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NumberUtils.isValidInteger(inputServings.getText().toString())) {
                    inputServings.setText(String.valueOf(desiredServings > 1 ? desiredServings - 1 : 1));
                } else {
                    inputServings.setText("1");
                }
            }
        });
        btnIncreaseServings = view.findViewById(R.id.btn_increase_servings);
        btnIncreaseServings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NumberUtils.isValidInteger(inputServings.getText().toString())) {
                    inputServings.setText(String.valueOf(desiredServings + 1));
                } else {
                    inputServings.setText("1");
                }
            }
        });

        listSteps.setLayoutManager(new ScrollLockedLinearLayoutManager(getActivity()));
        listHoverIngredients.setLayoutManager(new ScrollLockedLinearLayoutManager(getActivity()));
        if (preparationStepAdapter == null) {
            preparationStepAdapter = new PreparationStepAdapter(recipe.PreparationSteps);
            listSteps.setAdapter(preparationStepAdapter);
        } else {
            preparationStepAdapter.notifyDataSetChanged();
        }
        listIngredients.setLayoutManager(new ScrollLockedLinearLayoutManager(getActivity()));
        if (recipeIngredientAdapter == null) {
            recipeIngredientAdapter = new RecipeIngredientAdapter(recipe.Ingredients);

            Log.d("TEST", String.valueOf(EnumUtils.castIntToMeasurementType(3)));
            Log.d("TEST", String.valueOf(EnumUtils.castIntToIngredientCategory(40)));
            Log.d("TEST", String.valueOf(EnumUtils.castIntToSeasonMonth(5)));
            Log.d("TEST", String.valueOf(EnumUtils.castIntToWareOriginType(2)));

            RecipeIngredient recipeIngredient = recipe.Ingredients.get(recipe.Ingredients.size() - 1);
            Log.d("RecipeViewFragment", "RecipeIngredient set: " + (recipeIngredient != null));
            Log.d("RecipeViewFragment", "Measurement: " + String.valueOf(recipeIngredient.MeasurementTypeReal) + "(RAW: "+ recipeIngredient.MeasurementType + ")");
            if (recipeIngredient != null) {
                Ingredient test = recipeIngredient.Ingredient;
                Log.d("RecipeViewFragment", "Ingredient name: " + test.Name);
                Log.d("RecipeViewFragment", "Measurement: " + String.valueOf(test.MeasurementTypeReal) + "(RAW: "+ test.MeasurementType + ")");
                Log.d("RecipeViewFragment", "Category: " + String.valueOf(test.IngredientCategoryReal) + "(RAW: "+ test.IngredientCategory + ")");
            }
            listIngredients.setAdapter(recipeIngredientAdapter);
            listHoverIngredients.setAdapter(recipeIngredientAdapter);
        } else {
            recipeIngredientAdapter.notifyDataSetChanged();
        }

        inputServings.setText(String.valueOf(recipe.Servings));
        if (recipe.RecipeImage != null && recipe.RecipeImage.Image != null) {
            imageRecipe.setImageBitmap(recipe.RecipeImage.Image);
        }

        windowManager = this.getActivity().getWindowManager();

        toolbar = view.findViewById(R.id.toolbar_recipe_view);

        scroll.setScrollViewCallbacks(this);

        isInitialized = true;
        return view;
    }

    public void onToggleIngredientList() {
        setIngredientListVisible(!ingredientListVisible);
    }


    /**
     * Applies text from a source EditText field to another (copyCat)
     * @param changeSource The EditText field that changed
     * @param copyCat The EditText field which copies the new text from changeSource (hence copyCat)
     */
    private void updateServingsFields(EditText changeSource, EditText copyCat) {
        // Set servingsValueSet flag to false to allow further changes and return
        if (servingsValueSet) {
            Log.d("RecipeViewFragment", "servingsValueSet flag is set to true, skipping...");
            servingsValueSet = false;
            return;
        }

        Editable newText = changeSource.getText();
        Log.d("RecipeViewFragment", "Old text: " + copyCat.getText().toString() + "; New text: " + newText.toString());
        // Set value to 1 on source if input is not a valid number

        // Set servingsValueSet flag to true to avoid "ping-pong" calls between changeSource and copyCat
        servingsValueSet = true;
        Log.d("RecipeViewFragment", "servingsValueSet flag set to true");
        // Calculate amount of ingredients for new servings amount
        if (NumberUtils.isValidInteger(newText.toString())) {
            desiredServings = Integer.parseInt(newText.toString());
            Log.d("RecipeViewFragment", "Calculating ingredient amount...");
            calculateIngredientAmount(recipe.Servings, desiredServings);
            Log.d("RecipeViewFragment", "Calculation done");
        }
        // Set text of changeSource on copyCat
        Log.d("RecipeViewFragment", "Updating text on copy cat...");
        copyCat.setText(newText);
    }

    private void setIngredientListVisible(boolean isVisible) {
        if (containerSubIngredients == null || toggleIngredientList == null) {
            return;
        }

        ingredientListVisible = isVisible;
        containerSubIngredients.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        toggleIngredientList.setCompoundDrawablesWithIntrinsicBounds(null,
                ResourcesCompat.getDrawable(getResources(), isVisible ? R.drawable.ic_chevron_up : R.drawable.ic_chevron_down, null),
                null,
                null);
    }

    private void calculateIngredientAmount(int baseServings, int desiredServings) {
        List<RecipeIngredient> recipeIngredients = new ArrayList<>();

        for (RecipeIngredient recipeIngredient : originalRecipeIngredients) {
            RecipeIngredient calculated = recipeIngredient.fromServingRatio(baseServings, desiredServings);
            recipeIngredients.add(calculated);
            Log.d("RecipeViewFragment", "Ingredient name: " + recipeIngredient.Ingredient.Name +
                    "; Original amount for " + baseServings + " servings: " + recipeIngredient.Amount);
            Log.d("RecipeViewFragment", "Calculated amount for " + desiredServings + " servings: " + calculated.Amount);
        }

        recipeIngredientAdapter.setRecipeIngredients(recipeIngredients);
        recipeIngredientAdapter.notifyDataSetChanged();
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        Log.d("RecipeViewFragment", "onScrollChanged() fired...");
        if (!isInitialized || txtPreparationStepsTitle == null || containerHoverIngredients == null ||
                containerIngredients == null) {
            Log.d("RecipeViewFragment", "Fragment not initialized!");
            return;
        }

        int[] location = new int[2];
        txtPreparationStepsTitle.getLocationInWindow(location);
        int pxY = location[1];

        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;

        int offset = 145;
        int dpY = (int) Math.ceil(pxY / logicalDensity) - offset;

        Log.d("RecipeViewFragment", "Target Y position : " + pxY + "px; " + dpY + "dp");

        if (dpY <= 0 && !hoverIngredientListVisible) {
            hoverIngredientListVisible = true;
            containerHoverIngredients.setVisibility(View.VISIBLE);
            Log.d("RecipeViewFragment", "Hover ingredient list visible");
        }
        else if (dpY > 0 && hoverIngredientListVisible) {
            hoverIngredientListVisible = false;
            containerHoverIngredients.setVisibility(View.GONE);
            setIngredientListVisible(false);
            Log.d("RecipeViewFragment", "Default ingredient list visible");
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}