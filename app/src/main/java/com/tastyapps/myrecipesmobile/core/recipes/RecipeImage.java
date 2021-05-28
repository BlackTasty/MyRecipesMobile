package com.tastyapps.myrecipesmobile.core.recipes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;

import androidx.appcompat.content.res.AppCompatResources;

import com.tastyapps.myrecipesmobile.R;
import com.tastyapps.myrecipesmobile.core.util.ImageUtil;

public class RecipeImage {
    public byte[] ImageBytes;
    public Bitmap Image;
    public String RecipeGuid;

    public Bitmap getImage() {
        if (Image == null) {
            return null;
        } else if (!Image.isRecycled()) {
            return Image;
        } else {
            return ImageBytes != null ? getBitmapFromBytes(ImageBytes) : null;
        }
    }

    public RecipeImage(byte[] imageBytes) {
        Image = getBitmapFromBytes(imageBytes);
        this.ImageBytes = imageBytes;
    }

    public RecipeImage(Context context) {
        Image = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image);
        ImageBytes = ImageUtil.bitmapToByteArray(Image);
    }

    public void recycle() {
        if (Image != null) {
            Image.recycle();
        }
    }

    private Bitmap getBitmapFromBytes(byte[] imageBytes) {
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
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
