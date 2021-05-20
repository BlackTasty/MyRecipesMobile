package com.tastyapps.myrecipesmobile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toolbar;

import com.tastyapps.myrecipesmobile.core.events.OnRecipeItemClickedEventListener;
import com.tastyapps.myrecipesmobile.core.mobile.Client;
import com.tastyapps.myrecipesmobile.core.recipes.Recipe;
import com.tastyapps.myrecipesmobile.storage.RecipeStorage;

public class MainActivity extends AppCompatActivity {
    private boolean isRecipeViewOpen;

    private ActionBar actionBar;
    private boolean isBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {

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
    protected void onStart() {
        super.onStart();

        actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.color.brown_700, null));
        }


        /*if (!isBackPressed) {
            Client.getInstance().reconnect(this.getApplicationContext());
        }*/
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
        Client.getInstance().disconnect();
        super.onStop();
    }
}