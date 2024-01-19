package com.example.odyssey.utils;

import android.content.Context;
import android.net.Uri;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileUploadUtils {
    public static MultipartBody.Part createRequestBody(Uri imageUri, Context context) throws IOException {
        File file = createImageFromUri(imageUri.getLastPathSegment(), imageUri, context);
        RequestBody requestFile = RequestBody.create(MultipartBody.FORM, file);
        return MultipartBody.Part.createFormData("image", file.getName(), requestFile);
    }

    private static File createImageFromUri(String name, Uri uri, Context context) throws IOException {
        InputStream stream = context.getContentResolver().openInputStream(uri);
        File file = File.createTempFile(name, ".png", context.getCacheDir());
        if (stream == null)
            throw new IOException("Could not open stream for uri: " + uri.toString());
        FileUtils.copyInputStreamToFile(stream, file);
        return file;
    }
}
