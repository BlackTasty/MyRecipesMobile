package com.tastyapps.myrecipesmobile.core;

import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;

public class ImageFileProvider extends FileProvider {
    @Override
    public ParcelFileDescriptor openFile(@NonNull @NotNull Uri uri, @NonNull @NotNull String mode) throws FileNotFoundException {
        return super.openFile(uri, mode);
    }
}
