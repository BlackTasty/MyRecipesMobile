package com.tastyapps.myrecipesmobile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;

import com.tastyapps.myrecipesmobile.core.events.OnClientConnectedEventListener;
import com.tastyapps.myrecipesmobile.core.events.OnRecipeItemClickedEventListener;
import com.tastyapps.myrecipesmobile.core.mobile.Client;
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

    private Client client;
    private boolean isReconnect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "Activity started (isReconnect: " + isReconnect + ")");

        actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.color.brown_700, null));
        }

        Log.d("MainActivity", "Client is set: " + (client != null) + ")");
        client = Client.getInstance();
        client.connect(this.getApplicationContext(), isReconnect);
        Activity current = this;

        client.setOnClientConnectedEventListener(new OnClientConnectedEventListener() {
            @Override
            public void onConnected() {
                Log.d("MainActivity", "Connection succeeded with MQTT server! (isUploadingImage: " + client.isUploadingImage + ")");
                Log.d("MainActivity", "Recipe guid: " + client.recipeGuid);
                Log.d("MainActivity", "Image file path: " + client.tempImageFilePath);
                Log.d("MainActivity", "Image set: " + (client.selectedImage != null));
                showRecipeView();

                if (client.isUploadingImage) {
                    if (client.tempImageFilePath != null) {
                        Log.d("MainActivity", "Image size reduced! Sending to server... (Guid: " + client.recipeGuid + ")");
                        byte[] imageBytes = ImageUtil.fileToByteArray(new File(client.tempImageFilePath));

                        Recipe recipe = RecipeStorage.getInstance().getRecipeByGuid(client.recipeGuid);
                        recipe.RecipeImage = new RecipeImage(imageBytes);
                        Client.getInstance().sendImage("recipes/upload/" + client.recipeGuid, imageBytes);
                    } else if (client.selectedImage != null) {
                        byte[] imageBytes = ImageUtil.bitmapToByteArray(Client.getInstance().selectedImage);

                        Log.d("MainActivity", "Image bytes length: " + imageBytes.length);
                        Recipe recipe = RecipeStorage.getInstance().getRecipeByGuid(client.recipeGuid);
                        recipe.RecipeImage = new RecipeImage(imageBytes);
                        Client.getInstance().sendImage("recipes/upload/" + client.recipeGuid, imageBytes);
                    }

                    Client.getInstance().isUploadingImage = false;
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
        /*if (!isBackPressed) {
            Client.getInstance().reconnect(this.getApplicationContext());
        }*/
    }

    @Override
    protected void onRestart() {
        isReconnect = true;
        super.onRestart();
    }

    private void showRecipeView() {
        if (savedInstanceState == null && !isReconnect) {

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.main_fragment_container, RecipeListFragment.newInstance(), null)
                    .commit();
        }

        RecipeStorage.getInstance().setOnRecipeItemClickedEventListener(new OnRecipeItemClickedEventListener() {
            @Override
            public void onItemClick(Recipe recipe) {
                Log.d("MainActivity", "Recipe item has been clicked, transitioning to RecipeViewFragment...");
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.main_fragment_container, RecipeViewFragment.newInstance(recipe.Guid), null)
                        .commit();

                actionBar.hide();
                isRecipeViewOpen = true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!isRecipeViewOpen) {
            isBackPressed = true;
            super.onBackPressed();
        } else {
            isBackPressed = false;
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.main_fragment_container, RecipeListFragment.newInstance(), null)
                    .commit();
            actionBar.show();
            isRecipeViewOpen = false;
        };
    }

    @Override
    protected void onStop() {
        if (!isBackPressed) {

        }
        Log.d("MainActivity", "Activity stopped");
        Client.getInstance().disconnect();
        super.onStop();
    }
}