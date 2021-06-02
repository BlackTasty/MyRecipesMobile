package com.tastyapps.myrecipesmobile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.tastyapps.myrecipesmobile.core.events.OnClientConnectedEventListener;
import com.tastyapps.myrecipesmobile.core.events.OnRecipeItemClickedEventListener;
import com.tastyapps.myrecipesmobile.core.mobile.MqttClient;
import com.tastyapps.myrecipesmobile.core.recipes.Recipe;
import com.tastyapps.myrecipesmobile.core.recipes.RecipeImage;
import com.tastyapps.myrecipesmobile.core.util.ImageUtil;
import com.tastyapps.myrecipesmobile.storage.RecipeStorage;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private boolean isRecipeViewOpen;
    private Bundle savedInstanceState;

    private ActionBar actionBar;
    private boolean isBackPressed;
    private BottomNavigationView bottomNav;
    private NavController navController;

    private MqttClient MQTTClient;
    private boolean isReconnect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.savedInstanceState = savedInstanceState;

        // Bottom nav setup
        bottomNav = findViewById(R.id.main_bottom_nav);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(R.id.menu_history, R.id.menu_recipes, R.id.menu_seasoncalendar)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "Activity started (isReconnect: " + isReconnect + ")");

        actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.color.brown_700, null));
        }

        Log.d("MainActivity", "MqttClient is set: " + (MQTTClient != null) + ")");
        MQTTClient = MQTTClient.getInstance();
        MQTTClient.connect(this.getApplicationContext(), isReconnect);
        Activity current = this;

        MQTTClient.setOnClientConnectedEventListener(new OnClientConnectedEventListener() {
            @Override
            public void onConnected() {
                Log.d("MainActivity", "Connection succeeded with MQTT server! (isUploadingImage: " + MQTTClient.isUploadingImage + ")");
                Log.d("MainActivity", "Recipe guid: " + MQTTClient.recipeGuid);
                Log.d("MainActivity", "Image file path: " + MQTTClient.tempImageFilePath);
                Log.d("MainActivity", "Image set: " + (MQTTClient.selectedImage != null));
                showRecipeView();

                if (MQTTClient.isUploadingImage) {
                    if (MQTTClient.tempImageFilePath != null) {
                        Log.d("MainActivity", "Image size reduced! Sending to server... (Guid: " + MQTTClient.recipeGuid + ")");
                        byte[] imageBytes = ImageUtil.fileToByteArray(new File(MQTTClient.tempImageFilePath));

                        Recipe recipe = RecipeStorage.getInstance().getRecipeByGuid(MQTTClient.recipeGuid);
                        recipe.RecipeImage = new RecipeImage(imageBytes);
                        MQTTClient.getInstance().sendImage("recipes/upload/" + MQTTClient.recipeGuid, imageBytes);
                    } else if (MQTTClient.selectedImage != null) {
                        byte[] imageBytes = ImageUtil.bitmapToByteArray(MQTTClient.getInstance().selectedImage);

                        Log.d("MainActivity", "Image bytes length: " + imageBytes.length);
                        Recipe recipe = RecipeStorage.getInstance().getRecipeByGuid(MQTTClient.recipeGuid);
                        recipe.RecipeImage = new RecipeImage(imageBytes);
                        MQTTClient.getInstance().sendImage("recipes/upload/" + MQTTClient.recipeGuid, imageBytes);
                    }

                    MQTTClient.getInstance().isUploadingImage = false;
                }
            }

            @Override
            public void onFail(Throwable ex) {
                Toast.makeText(current, "Verbindung fehlgeschlagen!", Toast.LENGTH_LONG).show();
                Log.d("MainActivity", "Connection failed with MQTT server!");
                ex.printStackTrace();
                current.finish();
            }
        });

        RecipeStorage.getInstance().setOnRecipeItemClickedEventListener(new OnRecipeItemClickedEventListener() {
            @Override
            public void onItemClick(Recipe recipe) {
                Log.d("MainActivity", "Recipe item has been clicked, transitioning to RecipeViewFragment...");
                Bundle bundle = new Bundle();
                bundle.putString("guid", recipe.Guid);
                navController.navigate(R.id.action_menu_recipes_to_recipeViewFragment, bundle);

                /*getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.nav_host_main, RecipeViewFragment.newInstance(recipe.Guid), null)
                        .commit();*/

                actionBar.hide();
                isRecipeViewOpen = true;
            }
        });
        /*if (!isBackPressed) {
            MqttClient.getInstance().reconnect(this.getApplicationContext());
        }*/
    }

    @Override
    protected void onRestart() {
        isReconnect = true;
        super.onRestart();
    }

    private void showRecipeView() {
        if (savedInstanceState == null && !isReconnect) {
            bottomNav.setSelectedItemId(R.id.menu_recipes);
            /*getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.main_fragment_container, RecipeListFragment.newInstance(), null)
                    .commit();*/
        }
    }

    @Override
    public void onBackPressed() {
        if (!isRecipeViewOpen) {
            isBackPressed = true;
            super.onBackPressed();
        } else {
            isBackPressed = false;
            bottomNav.setSelectedItemId(R.id.menu_recipes);
            /*getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.main_fragment_container, RecipeListFragment.newInstance(), null)
                    .commit();*/
            actionBar.show();
            isRecipeViewOpen = false;
        };
    }

    @Override
    protected void onStop() {
        Log.d("MainActivity", "Activity stopped");
        MQTTClient.getInstance().disconnect();
        super.onStop();
    }
}