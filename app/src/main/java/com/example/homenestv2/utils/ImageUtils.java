package com.example.homenestv2.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
    private static final int MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MAX_IMAGE_DIMENSION = 2048; // 2048px

    public static boolean isValidImage(Context context, Uri imageUri) {
        try {
            // Check file size
            long fileSize = getFileSize(context, imageUri);
            if (fileSize > MAX_IMAGE_SIZE) {
                return false;
            }

            // Check file type
            String mimeType = context.getContentResolver().getType(imageUri);
            if (mimeType == null || !mimeType.startsWith("image/")) {
                return false;
            }

            // Check image dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            return options.outWidth <= MAX_IMAGE_DIMENSION && options.outHeight <= MAX_IMAGE_DIMENSION;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static File compressImage(Context context, Uri imageUri) throws IOException {
        // Create a temporary file
        File outputFile = File.createTempFile("compressed_", ".jpg", context.getCacheDir());

        // Decode the image
        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();

        // Calculate the compression ratio
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float ratio = Math.min(
                (float) MAX_IMAGE_DIMENSION / width,
                (float) MAX_IMAGE_DIMENSION / height
        );

        if (ratio < 1) {
            // Resize the bitmap
            int newWidth = Math.round(width * ratio);
            int newHeight = Math.round(height * ratio);
            bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        }

        // Compress and save the bitmap
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
        outputStream.close();
        bitmap.recycle();

        return outputFile;
    }

    private static long getFileSize(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        long size = inputStream.available();
        inputStream.close();
        return size;
    }

    public static String getFileExtension(Context context, Uri uri) {
        String extension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(context.getContentResolver().getType(uri));
        return extension != null ? extension : "jpg";
    }
} 