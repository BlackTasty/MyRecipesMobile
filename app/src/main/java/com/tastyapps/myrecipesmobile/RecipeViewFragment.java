package com.tastyapps.myrecipesmobile;

import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tastyapps.myrecipesmobile.adapters.PreparationStepAdapter;
import com.tastyapps.myrecipesmobile.adapters.RecipeIngredientAdapter;
import com.tastyapps.myrecipesmobile.core.ScrollLockedLinearLayoutManager;
import com.tastyapps.myrecipesmobile.core.mobile.Client;
import com.tastyapps.myrecipesmobile.core.recipes.Ingredient;
import com.tastyapps.myrecipesmobile.core.recipes.Recipe;
import com.tastyapps.myrecipesmobile.core.recipes.RecipeIngredient;
import com.tastyapps.myrecipesmobile.core.util.EnumUtils;
import com.tastyapps.myrecipesmobile.core.util.ImageUtil;
import com.tastyapps.myrecipesmobile.core.util.NumberUtils;
import com.tastyapps.myrecipesmobile.storage.RecipeStorage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeViewFragment extends Fragment implements ObservableScrollViewCallbacks {
    private TextWatcher textWatcherInputServings;
    private TextWatcher textWatcherInputHoverServings;
    private ImageUtil imageUtil;

    private ActivityResultLauncher<Uri> imageFromCamera;
    private File tempImageFile;
    private ActivityResultLauncher<String[]> imageFromGallery;
    private String[] tempSelectedImage;

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
    private Button btnUploadImage;

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
        registerLaunchers();
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

    private void registerLaunchers() {
        imageFromCamera = registerForActivityResult(new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if (result) {
                            Client.getInstance().sendImage("recipes/upload/" + recipe.Guid,
                                    ImageUtil.fileToByteArray(tempImageFile));
                        }
                    }
                });

        imageFromGallery = registerForActivityResult(new ActivityResultContracts.OpenDocument(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        Log.d("RecipeViewFragment", "tempImage array size: " + (tempSelectedImage != null ? tempSelectedImage.length : 0));
                        if (tempSelectedImage != null && tempSelectedImage.length > 0) {
                            Log.d("RecipeViewFragment", "tempImage array first item: " + tempSelectedImage[0]);
                            if (result != null) {
                                Client.getInstance().sendImage("recipes/upload/" + recipe.Guid,
                                        ImageUtil.imageUriToByteArray(getContext(), result));
                            }
                        }
                    }
                });
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

        btnUploadImage = view.findViewById(R.id.btn_upload_image);
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUploadImageSheetDialog();
            }
        });

        boolean hasCamera = this.getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        btnUploadImage.setVisibility(!hasCamera || recipe.isImageSet() ? View.GONE : View.VISIBLE);

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

    private void takePhoto() {
        tempImageFile = createImageFile();
        Log.d("RecipeViewFragment", "Created temp file in path: " + tempImageFile.getAbsolutePath());
        Log.d("RecipeViewFragment", "File exists: " + tempImageFile.exists());

        Uri tempImageUri = FileProvider.getUriForFile(this.getContext(),
                getActivity().getApplicationContext().getPackageName() + ".provider", tempImageFile);

        imageFromCamera.launch(tempImageUri);
    }

    private void uploadFromGallery() {
        imageFromGallery.launch(tempSelectedImage);
    }

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.GERMAN).format(new Date());
        String imageFileName = timeStamp + "_" + recipe.Name;
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            Log.d("RecipeViewFragment", "Error creating image!");
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        //currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void showUploadImageSheetDialog() {
        if (this.getContext() == null) {
            return;
        }

        imageUtil = ImageUtil.getInstance(this);
        /*imageUtil.setImageActionListener(new ImageUtil.ImageActionListener() {
            @Override
            public void onImageSelectedFromGallery(Uri uri, File imageFile) {
                Log.d("RecipeViewFragment", "Uploading image from gallery to server");
                Client.getInstance().sendImage("recipes/upload/" + recipe.Guid, ImageUtil.fileToByteArray(imageFile));
            }

            @Override
            public void onImageTakenFromCamera(Uri uri, File imageFile) {
                Log.d("RecipeViewFragment", "Uploading image from camera to server");
                Client.getInstance().sendImage("recipes/upload/" + recipe.Guid, ImageUtil.fileToByteArray(imageFile));
            }

            @Override
            public void onImageCropped(Uri uri, File imageFile) {

            }
        });*/

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this.getContext());
        bottomSheetDialog.setContentView(R.layout.layout_sheet_upload_image);

        AppCompatTextView btnUploadFromGallery = bottomSheetDialog.findViewById(R.id.btn_upload_from_gallery);
        btnUploadFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFromGallery();
                //imageUtil.selectImageFromGallery();
            }
        });
        AppCompatTextView btnUploadFromCamera = bottomSheetDialog.findViewById(R.id.btn_upload_from_camera);
        btnUploadFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
                //imageUtil.takePhotoWithCamera();
            }
        });


        bottomSheetDialog.show();
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