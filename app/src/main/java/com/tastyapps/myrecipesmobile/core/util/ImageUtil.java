package com.tastyapps.myrecipesmobile.core.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {
    private static ImageUtil instance;

    public static final int REQUEST_PICTURE_FROM_GALLERY = 23;
    public static final int REQUEST_PICTURE_FROM_CAMERA = 24;
    public static final int REQUEST_CROP_PICTURE = 25;
    private static final String TAG = "ImageInputHelper";

    private File tempFileFromSource = null;
    private Uri tempUriFromSource = null;

    private File tempFileFromCrop = null;
    private Uri tempUriFromCrop = null;

    /**
     * Activity object that will be used while calling startActivityForResult(). Activity then will
     * receive the callbacks to its own onActivityResult() and is responsible of calling the
     * onActivityResult() of the ImageInputHelper for handling result and being notified.
     */
    private Activity mContext;

    /**
     * Fragment object that will be used while calling startActivityForResult(). Fragment then will
     * receive the callbacks to its own onActivityResult() and is responsible of calling the
     * onActivityResult() of the ImageInputHelper for handling result and being notified.
     */
    private Fragment fragment;

    /**
     * Listener instance for callbacks on user events. It must be set to be able to use
     * the ImageInputHelper object.
     */
    private ImageActionListener imageActionListener;

    public static ImageUtil getInstance(Fragment fragment) {
        if (instance == null) {
            instance = new ImageUtil(fragment);
        }

        return instance;
    }

    private ImageUtil(Fragment fragment) {
        this.fragment = fragment;
        this.mContext = fragment.getActivity();
    }

    public void setImageActionListener(ImageActionListener imageActionListener) {
        this.imageActionListener = imageActionListener;
    }

    public static byte[] fileToByteArray(File file) {
        byte bytes[] = new byte[(int) file.length()];
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("ImageUtil", "Converted file to byte array. Array size: " + bytes.length);

        return bytes;
    }

    public static byte[] imageUriToByteArray(Context context, Uri uri) {
        try {
            InputStream iStream = context.getContentResolver().openInputStream(uri);
            return getBytes(iStream);
        } catch (IOException e) {
            Log.d("ImageUtil", "Error parsing image uri to byte array!");
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    /**
     * Starts an intent for selecting image from gallery. The result is returned to the
     * onImageSelectedFromGallery() method of the ImageSelectionListener interface.
     */
    public void selectImageFromGallery() {
        checkListener();

        if (tempFileFromSource == null) {
            try {
                tempFileFromSource = File.createTempFile("choose", "png", mContext.getExternalCacheDir());
                tempUriFromSource = Uri.fromFile(tempFileFromSource);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ActivityResultLauncher<String[]> openImage = fragment.registerForActivityResult(new ActivityResultContracts.OpenDocument(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        // Handle the returned Uri
                    }
                });

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUriFromSource);
        /*if (fragment == null) {
            mContext.startActivityForResult(intent, REQUEST_PICTURE_FROM_GALLERY);
        } else {
            fragment.startActivityForResult(intent, REQUEST_PICTURE_FROM_GALLERY);
        }*/
    }

    /**
     * Starts an intent for cropping an image that is saved in the uri. The result is
     * returned to the onImageCropped() method of the ImageSelectionListener interface.
     *
     * @param uri     uri that contains the data of the image to crop
     * @param outputX width of the result image
     * @param outputY height of the result image
     * @param aspectX horizontal ratio value while cutting the image
     * @param aspectY vertical ratio value of while cutting the image
     */
    public void requestCropImage(Uri uri, int outputX, int outputY, int aspectX, int aspectY) {
        checkListener();

        if (tempFileFromCrop == null) {
            try {
                tempFileFromCrop = File.createTempFile("crop", "png", mContext.getExternalCacheDir());
                tempUriFromCrop = Uri.fromFile(tempFileFromCrop);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // open crop intent when user selects image
        final Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("output", tempUriFromCrop);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("scale", true);
        intent.putExtra("noFaceDetection", true);
        if (fragment == null) {
            mContext.startActivityForResult(intent, REQUEST_CROP_PICTURE);
        } else {
            fragment.startActivityForResult(intent, REQUEST_CROP_PICTURE);
        }
    }

    private void checkListener() {
        if (imageActionListener == null) {
            throw new RuntimeException("ImageSelectionListener must be set before calling openGalleryIntent(), openCameraIntent() or requestCropImage().");
        }
    }

    /**
     * Listener interface for receiving callbacks from the ImageInputHelper.
     */
    public interface ImageActionListener {
        void onImageSelectedFromGallery(Uri uri, File imageFile);

        void onImageTakenFromCamera(Uri uri, File imageFile);

        void onImageCropped(Uri uri, File imageFile);
    }
}
