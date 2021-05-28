package com.tastyapps.myrecipesmobile.core.recipes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.content.res.AppCompatResources;

import com.tastyapps.myrecipesmobile.R;

public class RecipeImage {
    //public String ImageBytes;
    public Bitmap Image;

    public RecipeImage(byte[] imageBytes) {
        Image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    public RecipeImage(Context context) {
        Image = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image);
    }

    /*public static RecipeImage fromJson(String json) {
        RecipeImage recipeImage = new Gson().fromJson(json, RecipeImage.class);

        if (recipeImage.ImageBytes != null) {
            byte[] imageBytes = Base64.decodeFast(recipeImage.ImageBytes);

            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
            recipeImage.Image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            recipeImage.ImageBytes = null;
        }

        return recipeImage;
    }*/
}
